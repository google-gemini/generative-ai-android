/*
 * Copyright 2024 Shreyas Patil
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
package dev.shreyaspatil.ai.client.generativeai

import dev.shreyaspatil.ai.client.generativeai.util.commonTest
import dev.shreyaspatil.ai.client.generativeai.util.createResponses
import dev.shreyaspatil.ai.client.generativeai.util.prepareStreamingResponse
import io.kotest.matchers.shouldBe
import io.ktor.utils.io.close
import io.ktor.utils.io.writeFully
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.withTimeout
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

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
}
