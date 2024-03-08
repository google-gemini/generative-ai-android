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

import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.RequestTimeoutException
import com.google.ai.client.generativeai.util.commonTest
import com.google.ai.client.generativeai.util.createGenerativeModel
import com.google.ai.client.generativeai.util.createResponses
import com.google.ai.client.generativeai.util.doBlocking
import com.google.ai.client.generativeai.util.prepareStreamingResponse
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.TextContent
import io.ktor.http.headersOf
import io.ktor.utils.io.ByteChannel
import io.ktor.utils.io.close
import io.ktor.utils.io.writeFully
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.withTimeout
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

internal class GenerativeModelTests {
  private val testTimeout = 5.seconds

  @Test
  fun `(generateContentStream) emits responses as they come in`() = commonTest {
    val response = createResponses("The", " world", " is", " a", " beautiful", " place!")
    val bytes = prepareStreamingResponse(response)

    bytes.forEach { channel.writeFully(it) }
    val responses = model.generateContentStream()

    withTimeout(testTimeout) {
      responses.collect {
        it.candidates.isEmpty() shouldBe false
        channel.close()
      }
    }
  }

  @Test
  fun `(generateContent) respects a custom timeout`() =
    commonTest(requestOptions = RequestOptions(2.seconds)) {
      shouldThrow<RequestTimeoutException> {
        withTimeout(testTimeout) { model.generateContent("d") }
      }
    }
}

@RunWith(Parameterized::class)
internal class ModelNamingTests(private val modelName: String, private val actualName: String) {

  @Test
  fun `request should include right model name`() = doBlocking {
    val channel = ByteChannel(autoFlush = true)
    val mockEngine = MockEngine {
      respond(channel, HttpStatusCode.OK, headersOf(HttpHeaders.ContentType, "application/json"))
    }
    prepareStreamingResponse(createResponses("Random")).forEach { channel.writeFully(it) }
    val model =
      createGenerativeModel(modelName, "super_cool_test_key", RequestOptions(), mockEngine)

    withTimeout(5.seconds) {
      model.generateContentStream("sample content").collect {
        it.candidates.isEmpty() shouldBe false
        channel.close()
      }
    }

    mockEngine.requestHistory.first().url.encodedPath shouldContain actualName
    (mockEngine.requestHistory.first().body as TextContent).text shouldContain "role"
  }

  companion object {
    @JvmStatic
    @Parameterized.Parameters
    fun data() =
      listOf(
        arrayOf("gemini-pro", "models/gemini-pro"),
        arrayOf("x/gemini-pro", "x/gemini-pro"),
        arrayOf("models/gemini-pro", "models/gemini-pro"),
        arrayOf("/modelname", "/modelname"),
        arrayOf("modifiedNaming/mymodel", "modifiedNaming/mymodel"),
      )
  }
}
