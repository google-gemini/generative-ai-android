/*
 * Copyright 2024 Google LLC
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

import com.google.ai.client.generativeai.type.Candidate
import com.google.ai.client.generativeai.type.FunctionCallPart
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.content
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.Test

internal class GenerateContentResponseTest {

  @Test
  fun `generate response should pull all functions requests`() {
    val response =
      GenerateContentResponse(
        candidates =
          listOf(
            Candidate(
              content {
                part(FunctionCallPart("blah", mapOf()))
                part(FunctionCallPart("blah2", mapOf()))
                text("This is a textPart")
              },
              listOf(),
              listOf(),
              null
            )
          ),
        null,
        null
      )

    response.functionCalls shouldHaveSize 2
  }

  @Test
  fun `generate response should get strings even if they are not the first part`() {
    val response =
      GenerateContentResponse(
        candidates =
          listOf(
            Candidate(
              content {
                part(FunctionCallPart("blah", mapOf()))
                part(FunctionCallPart("blah2", mapOf()))
                text("This is a textPart")
              },
              listOf(),
              listOf(),
              null
            )
          ),
        null,
        null
      )

    response.text shouldBe "This is a textPart"
  }

  @Test
  fun `generate response should get strings and concatenate them together`() {
    val response =
      GenerateContentResponse(
        candidates =
          listOf(
            Candidate(
              content {
                part(FunctionCallPart("blah", mapOf()))
                part(FunctionCallPart("blah2", mapOf()))
                text("This is a textPart")
                text("This is another textPart")
              },
              listOf(),
              listOf(),
              null
            )
          ),
        null,
        null
      )

    response.text shouldBe "This is a textPart This is another textPart"
  }
}
