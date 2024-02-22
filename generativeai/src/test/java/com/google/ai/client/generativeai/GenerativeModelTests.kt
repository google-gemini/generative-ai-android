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

import com.google.ai.client.generativeai.type.BetaGenAiAPI
import com.google.ai.client.generativeai.type.FunctionCallPart
import com.google.ai.client.generativeai.type.ParameterDeclaration
import com.google.ai.client.generativeai.type.TwoParameterFunction
import com.google.ai.client.generativeai.type.defineFunction
import com.google.ai.client.generativeai.util.commonTest
import com.google.ai.client.generativeai.util.createResponses
import com.google.ai.client.generativeai.util.prepareStreamingResponse
import io.kotest.matchers.shouldBe
import io.ktor.utils.io.close
import io.ktor.utils.io.writeFully
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Test

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

  //
  //
  // FOR DEV PURPOSES ONLY
  //
  //

  fun myfun(a: Int, b: Int): String {
    return (a + b).toString()
  }

  @OptIn(BetaGenAiAPI::class)
  @Test
  fun `calling test`(): Unit = runBlocking {
    //    val f =
    //      FunctionBuilder("sum", "add two numbers together")
    //        .intParam("a", "First number to add together")
    //        .intParam("b", "Second number to add together")
    //        .build(::myfun)

    val f =
      defineFunction(
        "sum",
        "add two numbers together",
        ParameterDeclaration.int("a", "First number to add together"),
        ParameterDeclaration.int("b", "Second number to add together")
      ) { a, b ->
        (a + b).toString()
      }

    val x = f as TwoParameterFunction<Any?, Any?>
    val p = FunctionCallPart("sum", mapOf("a" to "2", "b" to "3"))
    val q = x(p)

    q shouldBe "5"
  }
}
