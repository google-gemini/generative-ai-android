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
package dev.shreyaspatil.ai.client.generativeai.internal.api.server

import dev.shreyaspatil.ai.client.generativeai.internal.api.shared.Content
import dev.shreyaspatil.ai.client.generativeai.internal.api.shared.HarmCategory
import dev.shreyaspatil.ai.client.generativeai.internal.util.SerializableEnum
import dev.shreyaspatil.ai.client.generativeai.internal.util.enumSerializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

internal object BlockReasonSerializer :
    KSerializer<BlockReason> by enumSerializer(BlockReason.entries)

internal object HarmProbabilitySerializer :
    KSerializer<HarmProbability> by enumSerializer(HarmProbability.entries)

internal object FinishReasonSerializer :
    KSerializer<FinishReason> by enumSerializer(FinishReason.entries)

@Serializable
internal data class PromptFeedback(
    val blockReason: BlockReason? = null,
    val safetyRatings: List<SafetyRating>? = null,
)

@Serializable(BlockReasonSerializer::class)
internal enum class BlockReason(override val serialName: String) : SerializableEnum<BlockReason> {
    UNKNOWN("UNKNOWN"),
    UNSPECIFIED("BLOCKED_REASON_UNSPECIFIED"),
    SAFETY("SAFETY"),
    OTHER("OTHER"),
}

@Serializable
internal data class Candidate(
    val content: Content? = null,
    val finishReason: FinishReason? = null,
    val safetyRatings: List<SafetyRating>? = null,
    val citationMetadata: CitationMetadata? = null,
)

@Serializable internal data class CitationMetadata(val citationSources: List<CitationSources>)

@Serializable
internal data class CitationSources(
    val startIndex: Int,
    val endIndex: Int,
    val uri: String,
    val license: String,
)

@Serializable
internal data class SafetyRating(
    val category: HarmCategory,
    val probability: HarmProbability,
    val blocked: Boolean? = null,
)

@Serializable(HarmProbabilitySerializer::class)
internal enum class HarmProbability(override val serialName: String) :
    SerializableEnum<HarmProbability> {
    UNKNOWN("UNKNOWN"),
    UNSPECIFIED("HARM_PROBABILITY_UNSPECIFIED"),
    NEGLIGIBLE("NEGLIGIBLE"),
    LOW("LOW"),
    MEDIUM("MEDIUM"),
    HIGH("HIGH"),
}

@Serializable(FinishReasonSerializer::class)
internal enum class FinishReason(override val serialName: String) : SerializableEnum<FinishReason> {
    UNKNOWN("UNKNOWN"),
    UNSPECIFIED("FINISH_REASON_UNSPECIFIED"),
    STOP("STOP"),
    MAX_TOKENS("MAX_TOKENS"),
    SAFETY("SAFETY"),
    RECITATION("RECITATION"),
    OTHER("OTHER"),
}

@Serializable
internal data class GRpcError(
    val code: Int,
    val message: String,
)
