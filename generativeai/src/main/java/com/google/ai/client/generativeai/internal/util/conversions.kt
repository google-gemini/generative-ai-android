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

package com.google.ai.client.generativeai.internal.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.ai.client.generativeai.common.CountTokensResponse
import com.google.ai.client.generativeai.common.GenerateContentResponse
import com.google.ai.client.generativeai.common.RequestOptions
import com.google.ai.client.generativeai.common.client.GenerationConfig
import com.google.ai.client.generativeai.common.client.Schema
import com.google.ai.client.generativeai.common.server.BlockReason
import com.google.ai.client.generativeai.common.server.Candidate
import com.google.ai.client.generativeai.common.server.CitationSources
import com.google.ai.client.generativeai.common.server.FinishReason
import com.google.ai.client.generativeai.common.server.HarmProbability
import com.google.ai.client.generativeai.common.server.PromptFeedback
import com.google.ai.client.generativeai.common.server.SafetyRating
import com.google.ai.client.generativeai.common.shared.Blob
import com.google.ai.client.generativeai.common.shared.BlobPart
import com.google.ai.client.generativeai.common.shared.Content
import com.google.ai.client.generativeai.common.shared.FunctionCall
import com.google.ai.client.generativeai.common.shared.FunctionCallPart
import com.google.ai.client.generativeai.common.shared.FunctionResponse
import com.google.ai.client.generativeai.common.shared.FunctionResponsePart
import com.google.ai.client.generativeai.common.shared.HarmBlockThreshold
import com.google.ai.client.generativeai.common.shared.HarmCategory
import com.google.ai.client.generativeai.common.shared.Part
import com.google.ai.client.generativeai.common.shared.SafetySetting
import com.google.ai.client.generativeai.common.shared.TextPart
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.CitationMetadata
import com.google.ai.client.generativeai.type.FunctionDeclaration
import com.google.ai.client.generativeai.type.GenerativeBeta
import com.google.ai.client.generativeai.type.ImagePart
import com.google.ai.client.generativeai.type.SerializationException
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import java.io.ByteArrayOutputStream
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.json.JSONObject

private const val BASE_64_FLAGS = Base64.NO_WRAP

internal fun com.google.ai.client.generativeai.type.RequestOptions.toInternal() =
  RequestOptions(timeout, apiVersion)

internal fun com.google.ai.client.generativeai.type.Content.toInternal() =
  Content(this.role, this.parts.map { it.toInternal() })

internal fun com.google.ai.client.generativeai.type.Part.toInternal(): Part {
  return when (this) {
    is com.google.ai.client.generativeai.type.TextPart -> TextPart(text)
    is ImagePart -> BlobPart(Blob("image/jpeg", encodeBitmapToBase64Png(image)))
    is com.google.ai.client.generativeai.type.BlobPart ->
      BlobPart(Blob(mimeType, Base64.encodeToString(blob, BASE_64_FLAGS)))
    is com.google.ai.client.generativeai.type.FunctionCallPart ->
      FunctionCallPart(FunctionCall(name, args.orEmpty()))
    is com.google.ai.client.generativeai.type.FunctionResponsePart ->
      FunctionResponsePart(FunctionResponse(name, response.toInternal()))
    else ->
      throw SerializationException(
        "The given subclass of Part (${javaClass.simpleName}) is not supported in the serialization yet."
      )
  }
}

internal fun com.google.ai.client.generativeai.type.SafetySetting.toInternal() =
  SafetySetting(harmCategory.toInternal(), threshold.toInternal())

internal fun com.google.ai.client.generativeai.type.GenerationConfig.toInternal() =
  GenerationConfig(
    temperature = temperature,
    topP = topP,
    topK = topK,
    candidateCount = candidateCount,
    maxOutputTokens = maxOutputTokens,
    stopSequences = stopSequences,
  )

internal fun com.google.ai.client.generativeai.type.HarmCategory.toInternal() =
  when (this) {
    com.google.ai.client.generativeai.type.HarmCategory.HARASSMENT -> HarmCategory.HARASSMENT
    com.google.ai.client.generativeai.type.HarmCategory.HATE_SPEECH -> HarmCategory.HATE_SPEECH
    com.google.ai.client.generativeai.type.HarmCategory.SEXUALLY_EXPLICIT ->
      HarmCategory.SEXUALLY_EXPLICIT
    com.google.ai.client.generativeai.type.HarmCategory.DANGEROUS_CONTENT ->
      HarmCategory.DANGEROUS_CONTENT
    com.google.ai.client.generativeai.type.HarmCategory.UNKNOWN -> HarmCategory.UNKNOWN
  }

internal fun BlockThreshold.toInternal() =
  when (this) {
    BlockThreshold.NONE -> HarmBlockThreshold.BLOCK_NONE
    BlockThreshold.ONLY_HIGH -> HarmBlockThreshold.BLOCK_ONLY_HIGH
    BlockThreshold.MEDIUM_AND_ABOVE -> HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE
    BlockThreshold.LOW_AND_ABOVE -> HarmBlockThreshold.BLOCK_LOW_AND_ABOVE
    BlockThreshold.UNSPECIFIED -> HarmBlockThreshold.UNSPECIFIED
  }

@GenerativeBeta
internal fun Tool.toInternal() =
  com.google.ai.client.generativeai.common.client.Tool(functionDeclarations.map { it.toInternal() })

@GenerativeBeta
internal fun FunctionDeclaration.toInternal() =
  com.google.ai.client.generativeai.common.client.FunctionDeclaration(
    name,
    description,
    Schema(
      properties = getParameters().associate { it.name to it.toInternal() },
      required = getParameters().map { it.name },
      type = "OBJECT",
    ),
  )

internal fun <T> com.google.ai.client.generativeai.type.Schema<T>.toInternal(): Schema =
  Schema(
    type.name,
    description,
    format,
    enum,
    properties?.mapValues { it.value.toInternal() },
    required,
    items?.toInternal(),
  )

internal fun JSONObject.toInternal() = Json.decodeFromString<JsonObject>(toString())

internal fun Candidate.toPublic(): com.google.ai.client.generativeai.type.Candidate {
  val safetyRatings = safetyRatings?.map { it.toPublic() }.orEmpty()
  val citations = citationMetadata?.citationSources?.map { it.toPublic() }.orEmpty()
  val finishReason = finishReason.toPublic()

  return com.google.ai.client.generativeai.type.Candidate(
    this.content?.toPublic() ?: content("model") {},
    safetyRatings,
    citations,
    finishReason,
  )
}

internal fun Content.toPublic(): com.google.ai.client.generativeai.type.Content =
  com.google.ai.client.generativeai.type.Content(role, parts.map { it.toPublic() })

internal fun Part.toPublic(): com.google.ai.client.generativeai.type.Part {
  return when (this) {
    is TextPart -> com.google.ai.client.generativeai.type.TextPart(text)
    is BlobPart -> {
      val data = Base64.decode(inlineData.data, BASE_64_FLAGS)
      if (inlineData.mimeType.contains("image")) {
        ImagePart(decodeBitmapFromImage(data))
      } else {
        com.google.ai.client.generativeai.type.BlobPart(inlineData.mimeType, data)
      }
    }
    is FunctionCallPart ->
      com.google.ai.client.generativeai.type.FunctionCallPart(
        functionCall.name,
        functionCall.args.orEmpty(),
      )
    is FunctionResponsePart ->
      com.google.ai.client.generativeai.type.FunctionResponsePart(
        functionResponse.name,
        functionResponse.response.toPublic(),
      )
    else ->
      throw SerializationException(
        "Unsupported part type \"${javaClass.simpleName}\" provided. This model may not be supported by this SDK."
      )
  }
}

internal fun CitationSources.toPublic() =
  CitationMetadata(startIndex = startIndex, endIndex = endIndex, uri = uri, license = license)

internal fun SafetyRating.toPublic() =
  com.google.ai.client.generativeai.type.SafetyRating(category.toPublic(), probability.toPublic())

internal fun PromptFeedback.toPublic(): com.google.ai.client.generativeai.type.PromptFeedback {
  val safetyRatings = safetyRatings?.map { it.toPublic() }.orEmpty()
  return com.google.ai.client.generativeai.type.PromptFeedback(
    blockReason?.toPublic(),
    safetyRatings,
  )
}

internal fun FinishReason?.toPublic() =
  when (this) {
    null -> null
    FinishReason.MAX_TOKENS -> com.google.ai.client.generativeai.type.FinishReason.MAX_TOKENS
    FinishReason.RECITATION -> com.google.ai.client.generativeai.type.FinishReason.RECITATION
    FinishReason.SAFETY -> com.google.ai.client.generativeai.type.FinishReason.SAFETY
    FinishReason.STOP -> com.google.ai.client.generativeai.type.FinishReason.STOP
    FinishReason.OTHER -> com.google.ai.client.generativeai.type.FinishReason.OTHER
    FinishReason.UNSPECIFIED -> com.google.ai.client.generativeai.type.FinishReason.UNSPECIFIED
    FinishReason.UNKNOWN -> com.google.ai.client.generativeai.type.FinishReason.UNKNOWN
  }

internal fun HarmCategory.toPublic() =
  when (this) {
    HarmCategory.HARASSMENT -> com.google.ai.client.generativeai.type.HarmCategory.HARASSMENT
    HarmCategory.HATE_SPEECH -> com.google.ai.client.generativeai.type.HarmCategory.HATE_SPEECH
    HarmCategory.SEXUALLY_EXPLICIT ->
      com.google.ai.client.generativeai.type.HarmCategory.SEXUALLY_EXPLICIT
    HarmCategory.DANGEROUS_CONTENT ->
      com.google.ai.client.generativeai.type.HarmCategory.DANGEROUS_CONTENT
    HarmCategory.UNKNOWN -> com.google.ai.client.generativeai.type.HarmCategory.UNKNOWN
  }

internal fun HarmProbability.toPublic() =
  when (this) {
    HarmProbability.HIGH -> com.google.ai.client.generativeai.type.HarmProbability.HIGH
    HarmProbability.MEDIUM -> com.google.ai.client.generativeai.type.HarmProbability.MEDIUM
    HarmProbability.LOW -> com.google.ai.client.generativeai.type.HarmProbability.LOW
    HarmProbability.NEGLIGIBLE -> com.google.ai.client.generativeai.type.HarmProbability.NEGLIGIBLE
    HarmProbability.UNSPECIFIED ->
      com.google.ai.client.generativeai.type.HarmProbability.UNSPECIFIED
    HarmProbability.UNKNOWN -> com.google.ai.client.generativeai.type.HarmProbability.UNKNOWN
  }

internal fun BlockReason.toPublic() =
  when (this) {
    BlockReason.UNSPECIFIED -> com.google.ai.client.generativeai.type.BlockReason.UNSPECIFIED
    BlockReason.SAFETY -> com.google.ai.client.generativeai.type.BlockReason.SAFETY
    BlockReason.OTHER -> com.google.ai.client.generativeai.type.BlockReason.OTHER
    BlockReason.UNKNOWN -> com.google.ai.client.generativeai.type.BlockReason.UNKNOWN
  }

internal fun GenerateContentResponse.toPublic() =
  com.google.ai.client.generativeai.type.GenerateContentResponse(
    candidates?.map { it.toPublic() }.orEmpty(),
    promptFeedback?.toPublic(),
  )

internal fun CountTokensResponse.toPublic() =
  com.google.ai.client.generativeai.type.CountTokensResponse(totalTokens)

internal fun JsonObject.toPublic() = JSONObject(toString())

private fun encodeBitmapToBase64Png(input: Bitmap): String {
  ByteArrayOutputStream().let {
    input.compress(Bitmap.CompressFormat.JPEG, 80, it)
    return Base64.encodeToString(it.toByteArray(), BASE_64_FLAGS)
  }
}

private fun decodeBitmapFromImage(input: ByteArray) =
  BitmapFactory.decodeByteArray(input, 0, input.size)
