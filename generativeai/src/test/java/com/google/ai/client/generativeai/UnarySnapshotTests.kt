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

import com.google.ai.client.generativeai.type.BlockReason
import com.google.ai.client.generativeai.type.FinishReason
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.InvalidAPIKeyException
import com.google.ai.client.generativeai.type.PromptBlockedException
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.SerializationException
import com.google.ai.client.generativeai.type.ServerException
import com.google.ai.client.generativeai.type.UnsupportedUserLocationException
import com.google.ai.client.generativeai.util.goldenUnaryFile
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe
import io.ktor.http.HttpStatusCode
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.withTimeout
import org.junit.Test

internal class UnarySnapshotTests {
  private val testTimeout = 5.seconds

  @Test
  fun `short reply`() =
    goldenUnaryFile("success-basic-reply-short.json") {
      withTimeout(testTimeout) {
        val response = model.generateContent()

        response.candidates.isEmpty() shouldBe false
        response.candidates.first().finishReason shouldBe FinishReason.STOP
        response.candidates.first().content.parts.isEmpty() shouldBe false
        response.candidates.first().safetyRatings.isEmpty() shouldBe false
      }
    }

  @Test
  fun `long reply`() =
    goldenUnaryFile("success-basic-reply-long.json") {
      withTimeout(testTimeout) {
        val response = model.generateContent()

        response.candidates.isEmpty() shouldBe false
        response.candidates.first().finishReason shouldBe FinishReason.STOP
        response.candidates.first().content.parts.isEmpty() shouldBe false
        response.candidates.first().safetyRatings.isEmpty() shouldBe false
      }
    }

  @Test
  fun `unknown enum`() =
    goldenUnaryFile("success-unknown-enum.json") {
      withTimeout(testTimeout) {
        val response = model.generateContent()

        response.candidates.first { it.safetyRatings.any { it.category == HarmCategory.UNKNOWN } }
      }
    }

  @Test
  fun `prompt blocked for safety`() =
    goldenUnaryFile("failure-prompt-blocked-safety.json") {
      withTimeout(testTimeout) {
        shouldThrow<PromptBlockedException> { model.generateContent() } should
          {
            it.response.promptFeedback?.blockReason shouldBe BlockReason.SAFETY
          }
      }
    }

  @Test
  fun `empty content`() =
    goldenUnaryFile("failure-empty-content.json") {
      withTimeout(testTimeout) { shouldThrow<SerializationException> { model.generateContent() } }
    }

  @Test
  fun `http error`() =
    goldenUnaryFile("failure-http-error.json", HttpStatusCode.PreconditionFailed) {
      withTimeout(testTimeout) { shouldThrow<ServerException> { model.generateContent() } }
    }

  @Test
  fun `user location error`() =
    goldenUnaryFile("failure-unsupported-user-location.json", HttpStatusCode.PreconditionFailed) {
      withTimeout(testTimeout) {
        shouldThrow<UnsupportedUserLocationException> { model.generateContent() }
      }
    }

  @Test
  fun `stopped for safety`() =
    goldenUnaryFile("failure-finish-reason-safety.json") {
      withTimeout(testTimeout) {
        val exception = shouldThrow<ResponseStoppedException> { model.generateContent() }
        exception.response.candidates.first().finishReason shouldBe FinishReason.SAFETY
      }
    }

  @Test
  fun `citation returns correctly`() =
    goldenUnaryFile("success-citations.json") {
      withTimeout(testTimeout) {
        val response = model.generateContent()

        response.candidates.isEmpty() shouldBe false
        response.candidates.first().citationMetadata.isNotEmpty() shouldBe true
      }
    }

  @Test
  fun `citation returns correctly when using alternative name`() =
    goldenUnaryFile("success-citations-altname.json") {
      withTimeout(testTimeout) {
        val response = model.generateContent()

        response.candidates.isEmpty() shouldBe false
        response.candidates.first().citationMetadata.isNotEmpty() shouldBe true
      }
    }

  @Test
  fun `invalid response`() =
    goldenUnaryFile("failure-invalid-response.json") {
      withTimeout(testTimeout) { shouldThrow<SerializationException> { model.generateContent() } }
    }

  @Test
  fun `malformed content`() =
    goldenUnaryFile("failure-malformed-content.json") {
      withTimeout(testTimeout) { shouldThrow<SerializationException> { model.generateContent() } }
    }

  @Test
  fun `invalid api key`() =
    goldenUnaryFile("failure-api-key.json", HttpStatusCode.BadRequest) {
      withTimeout(testTimeout) { shouldThrow<InvalidAPIKeyException> { model.generateContent() } }
    }

  @Test
  fun `image rejected`() =
    goldenUnaryFile("failure-image-rejected.json", HttpStatusCode.BadRequest) {
      withTimeout(testTimeout) { shouldThrow<ServerException> { model.generateContent() } }
    }

  @Test
  fun `unknown model`() =
    goldenUnaryFile("failure-unknown-model.json", HttpStatusCode.NotFound) {
      withTimeout(testTimeout) { shouldThrow<ServerException> { model.generateContent() } }
    }
}
