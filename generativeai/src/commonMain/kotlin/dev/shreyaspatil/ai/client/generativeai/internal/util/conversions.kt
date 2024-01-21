/*
 * Copyright ${YEAR} Shreyas Patil
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
@file:OptIn(ExperimentalEncodingApi::class, ExperimentalEncodingApi::class)

package dev.shreyaspatil.ai.client.generativeai.internal.util

import dev.shreyaspatil.ai.client.generativeai.internal.api.CountTokensResponse
import dev.shreyaspatil.ai.client.generativeai.internal.api.GenerateContentResponse
import dev.shreyaspatil.ai.client.generativeai.internal.api.client.GenerationConfig
import dev.shreyaspatil.ai.client.generativeai.internal.api.server.BlockReason
import dev.shreyaspatil.ai.client.generativeai.internal.api.server.Candidate
import dev.shreyaspatil.ai.client.generativeai.internal.api.server.CitationSources
import dev.shreyaspatil.ai.client.generativeai.internal.api.server.FinishReason
import dev.shreyaspatil.ai.client.generativeai.internal.api.server.HarmProbability
import dev.shreyaspatil.ai.client.generativeai.internal.api.server.PromptFeedback
import dev.shreyaspatil.ai.client.generativeai.internal.api.server.SafetyRating
import dev.shreyaspatil.ai.client.generativeai.internal.api.shared.Blob
import dev.shreyaspatil.ai.client.generativeai.internal.api.shared.BlobPart
import dev.shreyaspatil.ai.client.generativeai.internal.api.shared.Content
import dev.shreyaspatil.ai.client.generativeai.internal.api.shared.HarmBlockThreshold
import dev.shreyaspatil.ai.client.generativeai.internal.api.shared.HarmCategory
import dev.shreyaspatil.ai.client.generativeai.internal.api.shared.Part
import dev.shreyaspatil.ai.client.generativeai.internal.api.shared.SafetySetting
import dev.shreyaspatil.ai.client.generativeai.internal.api.shared.TextPart
import dev.shreyaspatil.ai.client.generativeai.type.BlockThreshold
import dev.shreyaspatil.ai.client.generativeai.type.CitationMetadata
import dev.shreyaspatil.ai.client.generativeai.type.ImagePart
import dev.shreyaspatil.ai.client.generativeai.type.PlatformImage
import dev.shreyaspatil.ai.client.generativeai.type.SerializationException
import dev.shreyaspatil.ai.client.generativeai.type.content
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

internal fun dev.shreyaspatil.ai.client.generativeai.type.Content.toInternal() =
    Content(this.role, this.parts.map { it.toInternal() })

internal fun dev.shreyaspatil.ai.client.generativeai.type.Part.toInternal(): Part {
    return when (this) {
        is dev.shreyaspatil.ai.client.generativeai.type.TextPart -> TextPart(text)
        is ImagePart -> BlobPart(Blob("image/png", image.asBase64()))
        is dev.shreyaspatil.ai.client.generativeai.type.BlobPart ->
            BlobPart(Blob(mimeType, Base64.Mime.encode(blob)))
        else ->
            throw SerializationException(
                "The given subclass of Part ($this) is not supported in the serialization yet.",
            )
    }
}

internal fun dev.shreyaspatil.ai.client.generativeai.type.SafetySetting.toInternal() =
    SafetySetting(harmCategory.toInternal(), threshold.toInternal())

internal fun dev.shreyaspatil.ai.client.generativeai.type.GenerationConfig.toInternal() =
    GenerationConfig(
        temperature = temperature,
        topP = topP,
        topK = topK,
        candidateCount = candidateCount,
        maxOutputTokens = maxOutputTokens,
        stopSequences = stopSequences,
    )

internal fun dev.shreyaspatil.ai.client.generativeai.type.HarmCategory.toInternal() =
    when (this) {
        dev.shreyaspatil.ai.client.generativeai.type.HarmCategory.HARASSMENT -> HarmCategory.HARASSMENT
        dev.shreyaspatil.ai.client.generativeai.type.HarmCategory.HATE_SPEECH ->
            HarmCategory.HATE_SPEECH
        dev.shreyaspatil.ai.client.generativeai.type.HarmCategory.SEXUALLY_EXPLICIT ->
            HarmCategory.SEXUALLY_EXPLICIT
        dev.shreyaspatil.ai.client.generativeai.type.HarmCategory.DANGEROUS_CONTENT ->
            HarmCategory.DANGEROUS_CONTENT
        dev.shreyaspatil.ai.client.generativeai.type.HarmCategory.UNKNOWN -> HarmCategory.UNKNOWN
    }

internal fun BlockThreshold.toInternal() =
    when (this) {
        BlockThreshold.NONE -> HarmBlockThreshold.BLOCK_NONE
        BlockThreshold.ONLY_HIGH -> HarmBlockThreshold.BLOCK_ONLY_HIGH
        BlockThreshold.MEDIUM_AND_ABOVE -> HarmBlockThreshold.BLOCK_MEDIUM_AND_ABOVE
        BlockThreshold.LOW_AND_ABOVE -> HarmBlockThreshold.BLOCK_LOW_AND_ABOVE
        BlockThreshold.UNSPECIFIED -> HarmBlockThreshold.UNSPECIFIED
    }

internal fun Candidate.toPublic(): dev.shreyaspatil.ai.client.generativeai.type.Candidate {
    val safetyRatings = safetyRatings?.map { it.toPublic() }.orEmpty()
    val citations = citationMetadata?.citationSources?.map { it.toPublic() }.orEmpty()
    val finishReason = finishReason.toPublic()

    return dev.shreyaspatil.ai.client.generativeai.type.Candidate(
        this.content?.toPublic() ?: content("model") {},
        safetyRatings,
        citations,
        finishReason,
    )
}

internal fun Content.toPublic(): dev.shreyaspatil.ai.client.generativeai.type.Content =
    dev.shreyaspatil.ai.client.generativeai.type.Content(role, parts.map { it.toPublic() })

@OptIn(ExperimentalEncodingApi::class)
internal fun Part.toPublic(): dev.shreyaspatil.ai.client.generativeai.type.Part {
    return when (this) {
        is TextPart -> dev.shreyaspatil.ai.client.generativeai.type.TextPart(text)
        is BlobPart -> {
            val data = Base64.decode(inlineData.data)
            if (inlineData.mimeType.contains("image")) {
                ImagePart(PlatformImage(data))
            } else {
                dev.shreyaspatil.ai.client.generativeai.type.BlobPart(inlineData.mimeType, data)
            }
        }
    }
}

internal fun CitationSources.toPublic() =
    CitationMetadata(startIndex = startIndex, endIndex = endIndex, uri = uri, license = license)

internal fun SafetyRating.toPublic() =
    dev.shreyaspatil.ai.client.generativeai.type.SafetyRating(
        category.toPublic(),
        probability.toPublic(),
    )

internal fun PromptFeedback.toPublic(): dev.shreyaspatil.ai.client.generativeai.type.PromptFeedback {
    val safetyRatings = safetyRatings?.map { it.toPublic() }.orEmpty()
    return dev.shreyaspatil.ai.client.generativeai.type.PromptFeedback(
        blockReason?.toPublic(),
        safetyRatings,
    )
}

internal fun FinishReason?.toPublic() =
    when (this) {
        null -> null
        FinishReason.MAX_TOKENS -> dev.shreyaspatil.ai.client.generativeai.type.FinishReason.MAX_TOKENS
        FinishReason.RECITATION -> dev.shreyaspatil.ai.client.generativeai.type.FinishReason.RECITATION
        FinishReason.SAFETY -> dev.shreyaspatil.ai.client.generativeai.type.FinishReason.SAFETY
        FinishReason.STOP -> dev.shreyaspatil.ai.client.generativeai.type.FinishReason.STOP
        FinishReason.OTHER -> dev.shreyaspatil.ai.client.generativeai.type.FinishReason.OTHER
        FinishReason.UNSPECIFIED ->
            dev.shreyaspatil.ai.client.generativeai.type.FinishReason.UNSPECIFIED
        FinishReason.UNKNOWN -> dev.shreyaspatil.ai.client.generativeai.type.FinishReason.UNKNOWN
    }

internal fun HarmCategory.toPublic() =
    when (this) {
        HarmCategory.HARASSMENT -> dev.shreyaspatil.ai.client.generativeai.type.HarmCategory.HARASSMENT
        HarmCategory.HATE_SPEECH ->
            dev.shreyaspatil.ai.client.generativeai.type.HarmCategory.HATE_SPEECH
        HarmCategory.SEXUALLY_EXPLICIT ->
            dev.shreyaspatil.ai.client.generativeai.type.HarmCategory.SEXUALLY_EXPLICIT
        HarmCategory.DANGEROUS_CONTENT ->
            dev.shreyaspatil.ai.client.generativeai.type.HarmCategory.DANGEROUS_CONTENT
        HarmCategory.UNKNOWN -> dev.shreyaspatil.ai.client.generativeai.type.HarmCategory.UNKNOWN
    }

internal fun HarmProbability.toPublic() =
    when (this) {
        HarmProbability.HIGH -> dev.shreyaspatil.ai.client.generativeai.type.HarmProbability.HIGH
        HarmProbability.MEDIUM -> dev.shreyaspatil.ai.client.generativeai.type.HarmProbability.MEDIUM
        HarmProbability.LOW -> dev.shreyaspatil.ai.client.generativeai.type.HarmProbability.LOW
        HarmProbability.NEGLIGIBLE ->
            dev.shreyaspatil.ai.client.generativeai.type.HarmProbability.NEGLIGIBLE
        HarmProbability.UNSPECIFIED ->
            dev.shreyaspatil.ai.client.generativeai.type.HarmProbability.UNSPECIFIED
        HarmProbability.UNKNOWN -> dev.shreyaspatil.ai.client.generativeai.type.HarmProbability.UNKNOWN
    }

internal fun BlockReason.toPublic() =
    when (this) {
        BlockReason.UNSPECIFIED -> dev.shreyaspatil.ai.client.generativeai.type.BlockReason.UNSPECIFIED
        BlockReason.SAFETY -> dev.shreyaspatil.ai.client.generativeai.type.BlockReason.SAFETY
        BlockReason.OTHER -> dev.shreyaspatil.ai.client.generativeai.type.BlockReason.OTHER
        BlockReason.UNKNOWN -> dev.shreyaspatil.ai.client.generativeai.type.BlockReason.UNKNOWN
    }

internal fun GenerateContentResponse.toPublic() =
    dev.shreyaspatil.ai.client.generativeai.type.GenerateContentResponse(
        candidates?.map { it.toPublic() }.orEmpty(),
        promptFeedback?.toPublic(),
    )

internal fun CountTokensResponse.toPublic() =
    dev.shreyaspatil.ai.client.generativeai.type.CountTokensResponse(totalTokens)
