/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ai.client.generativeai

import com.google.ai.client.generativeai.common.APIController
import com.google.ai.client.generativeai.common.GenerateContentRequest as GenerateContentRequest_Common
import com.google.ai.client.generativeai.common.GenerateContentResponse as GenerateContentResponse_Common
import com.google.ai.client.generativeai.common.InvalidAPIKeyException as InvalidAPIKeyException_Common
import com.google.ai.client.generativeai.common.UnsupportedUserLocationException as UnsupportedUserLocationException_Common
import com.google.ai.client.generativeai.common.UsageMetadata as UsageMetadata_Common
import com.google.ai.client.generativeai.common.server.Candidate as Candidate_Common
import com.google.ai.client.generativeai.common.server.CitationMetadata as CitationMetadata_Common
import com.google.ai.client.generativeai.common.server.CitationSources
import com.google.ai.client.generativeai.common.shared.Content as Content_Common
import com.google.ai.client.generativeai.common.shared.TextPart as TextPart_Common
import com.google.ai.client.generativeai.type.Candidate
import com.google.ai.client.generativeai.type.CitationMetadata
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.InvalidAPIKeyException
import com.google.ai.client.generativeai.type.PromptFeedback
import com.google.ai.client.generativeai.type.TextPart
import com.google.ai.client.generativeai.type.UnsupportedUserLocationException
import com.google.ai.client.generativeai.type.UsageMetadata
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.equality.shouldBeEqualToUsingFields
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import org.junit.Test

internal class GenerativeModelTests {

  private val apiKey: String = "api_key"
  private val mockApiController = mockk<APIController>()

  @Test
  fun `generateContent request succeeds`() = doBlocking {
    val model = GenerativeModel("gemini-pro-1.0", apiKey, controller = mockApiController)
    coEvery {
      mockApiController.generateContent(
        GenerateContentRequest_Common(
          "gemini-pro-1.0",
          contents = listOf(Content_Common(parts = listOf(TextPart_Common("Why's the sky blue?"))))
        )
      )
    } returns
      GenerateContentResponse_Common(
        listOf(
          Candidate_Common(
            content =
              Content_Common(
                parts = listOf(TextPart_Common("I'm still learning how to answer this question"))
              ),
            finishReason = null,
            safetyRatings = listOf(),
            citationMetadata =
              CitationMetadata_Common(
                listOf(
                  CitationSources(
                    endIndex = 100,
                    uri = "http://www.example.com",
                  )
                )
              )
          )
        ),
        usageMetadata = UsageMetadata_Common(promptTokenCount = 10)
      )

    val expectedResponse =
      GenerateContentResponse(
        listOf(
          Candidate(
            Content(parts = listOf(TextPart("I'm still learning how to answer this question"))),
            safetyRatings = listOf(),
            citationMetadata =
              listOf(
                CitationMetadata(
                  startIndex = 0,
                  endIndex = 100,
                  uri = "http://www.example.com",
                  license = null
                )
              ),
            finishReason = null
          )
        ),
        PromptFeedback(null, listOf()),
        UsageMetadata(10, 0, 0 /* default to 0*/)
      )

    val response = model.generateContent("Why's the sky blue?")

    response.shouldBeEqualToUsingFields(expectedResponse, GenerateContentResponse::text)
    response.candidates shouldHaveSize expectedResponse.candidates.size
    response.candidates[0].shouldBeEqualToUsingFields(
      expectedResponse.candidates[0],
      Candidate::finishReason,
      Candidate::safetyRatings
    )
    response.candidates[0]
      .citationMetadata[0]
      .shouldBeEqualToUsingFields(
        expectedResponse.candidates[0].citationMetadata[0],
        CitationMetadata::startIndex,
        CitationMetadata::endIndex,
        CitationMetadata::uri,
        CitationMetadata::license,
      )
  }

  @Test
  fun `generateContent throws exception`() = doBlocking {
    val model = GenerativeModel("gemini-pro-1.0", apiKey, controller = mockApiController)
    coEvery {
      mockApiController.generateContent(
        GenerateContentRequest_Common(
          "gemini-pro-1.0",
          contents = listOf(Content_Common(parts = listOf(TextPart_Common("Why's the sky blue?"))))
        )
      )
    } throws InvalidAPIKeyException_Common("exception message")

    shouldThrow<InvalidAPIKeyException> { model.generateContent("Why's the sky blue?") }
  }

  @Test
  fun `generateContentStream throws exception`() = doBlocking {
    val model = GenerativeModel("gemini-pro-1.0", apiKey, controller = mockApiController)
    coEvery {
      mockApiController.generateContentStream(
        GenerateContentRequest_Common(
          "gemini-pro-1.0",
          contents = listOf(Content_Common(parts = listOf(TextPart_Common("Why's the sky blue?"))))
        )
      )
    } returns flow { throw UnsupportedUserLocationException_Common() }

    shouldThrow<UnsupportedUserLocationException> {
      model.generateContentStream("Why's the sky blue?").collect {}
    }
  }
}

internal fun doBlocking(block: suspend CoroutineScope.() -> Unit) {
  runBlocking(block = block)
}
