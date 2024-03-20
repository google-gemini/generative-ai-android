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
  val blockReason: BlockReason? = null,
  val safetyRatings: List<SafetyRating>? = null,
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
  val finishReason: FinishReason? = null,
  val safetyRatings: List<SafetyRating>? = null,
  val citationMetadata: CitationMetadata? = null
)

@Serializable
data class CitationMetadata
@OptIn(ExperimentalSerializationApi::class)
constructor(@JsonNames("citations") val citationSources: List<CitationSources>)

@Serializable
data class CitationSources(
  val startIndex: Int,
  val endIndex: Int,
  val uri: String,
  val license: String
)

@Serializable
data class SafetyRating(
  val category: HarmCategory,
  val probability: HarmProbability,
  val blocked: Boolean? = null // TODO(): any reason not to default to false?
)

@Serializable(HarmProbabilitySerializer::class)
enum class HarmProbability {
  UNKNOWN,
  @SerialName("HARM_PROBABILITY_UNSPECIFIED") UNSPECIFIED,
  NEGLIGIBLE,
  LOW,
  MEDIUM,
  HIGH
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
data class GRpcError(
  val code: Int,
  val message: String,
)
