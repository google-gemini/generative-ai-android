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

@file:OptIn(ExperimentalSerializationApi::class)

package com.google.ai.client.generativeai.common.server

import com.google.ai.client.generativeai.common.shared.Content
import com.google.ai.client.generativeai.common.shared.HarmCategory
import com.google.ai.client.generativeai.common.util.FirstOrdinalSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

object BlockReasonSerializer :
  KSerializer<BlockReason> by FirstOrdinalSerializer(BlockReason::class)

object HarmProbabilitySerializer :
  KSerializer<HarmProbability> by FirstOrdinalSerializer(HarmProbability::class)

object FinishReasonSerializer :
  KSerializer<FinishReason> by FirstOrdinalSerializer(FinishReason::class)

@Serializable
data class PromptFeedback(
  // TODO() should default to UNSPECIFIED, but that would be an unexpected change for consumers null
  // checking block reason to see if their prompt was blocked
  val blockReason: BlockReason? = null,
  val safetyRatings: List<SafetyRating> = emptyList(),
)

@Serializable(BlockReasonSerializer::class)
enum class BlockReason {
  UNKNOWN,
  @SerialName("BLOCKED_REASON_UNSPECIFIED") UNSPECIFIED,
  SAFETY,
  OTHER
}

@Serializable
data class Candidate(
  val content: Content? = null,
  // TODO() should default to UNSPECIFIED, but that would be an unexpected change for consumers
  // checking if their finish reason is anything other than STOP
  val finishReason: FinishReason? = null,
  val safetyRatings: List<SafetyRating> = emptyList(),
  val citationMetadata: CitationMetadata? = null,
  val groundingMetadata: GroundingMetadata? = null,
)

@Serializable
data class CitationMetadata(
  @JsonNames("citations") val citationSources: List<CitationSources> = emptyList()
)

@Serializable
data class CitationSources(
  val startIndex: Int? = null,
  val endIndex: Int? = null,
  val uri: String? = null,
  val license: String? = null,
)

@Serializable
data class SafetyRating(
  val category: HarmCategory = HarmCategory.UNSPECIFIED,
  val probability: HarmProbability = HarmProbability.UNSPECIFIED,
  val blocked: Boolean = false,
  val probabilityScore: Float = 0f,
  val severity: HarmSeverity = HarmSeverity.UNSPECIFIED,
  val severityScore: Float = 0f,
)

@Serializable
data class GroundingMetadata(
  val webSearchQueries: List<String> = emptyList(),
  val searchEntryPoint: SearchEntryPoint? = null,
  val retrievalQueries: List<String> = emptyList(),
  val groundingAttribution: List<GroundingAttribution> = emptyList(),
)

@Serializable
data class SearchEntryPoint(val renderedContent: String = "", val sdkBlob: String = "")

// TODO() Has a different definition for labs vs vertex. May need to split into diff types in future
// (when labs supports it)
@Serializable
data class GroundingAttribution(val segment: Segment? = null, val confidenceScore: Float? = null)

@Serializable data class Segment(val startIndex: Int = 0, val endIndex: Int = 0)

@Serializable(HarmProbabilitySerializer::class)
enum class HarmProbability {
  UNKNOWN,
  @SerialName("HARM_PROBABILITY_UNSPECIFIED") UNSPECIFIED,
  NEGLIGIBLE,
  LOW,
  MEDIUM,
  HIGH
}

@Serializable
enum class HarmSeverity {
  UNKNOWN,
  @SerialName("HARM_SEVERITY_UNSPECIFIED") UNSPECIFIED,
  @SerialName("HARM_SEVERITY_NEGLIGIBLE") NEGLIGIBLE,
  @SerialName("HARM_SEVERITY_LOW") LOW,
  @SerialName("HARM_SEVERITY_MEDIUM") MEDIUM,
  @SerialName("HARM_SEVERITY_HIGH") HIGH
}

@Serializable(FinishReasonSerializer::class)
enum class FinishReason {
  UNKNOWN,
  @SerialName("FINISH_REASON_UNSPECIFIED") UNSPECIFIED,
  STOP,
  MAX_TOKENS,
  SAFETY,
  RECITATION,
  OTHER
}

@Serializable
data class GRpcError(val code: Int, val message: String, val details: List<GRpcErrorDetails>)

@Serializable data class GRpcErrorDetails(val reason: String? = null)
