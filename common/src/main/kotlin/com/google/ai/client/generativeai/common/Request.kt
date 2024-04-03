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

package com.google.ai.client.generativeai.common

import com.google.ai.client.generativeai.common.client.GenerationConfig
import com.google.ai.client.generativeai.common.client.Tool
import com.google.ai.client.generativeai.common.shared.Content
import com.google.ai.client.generativeai.common.shared.SafetySetting
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

sealed interface Request

@Serializable
data class GenerateContentRequest(
  @Transient val model: String? = null,
  val contents: List<Content>,
  @SerialName("safety_settings") val safetySettings: List<SafetySetting>? = null,
  @SerialName("generation_config") val generationConfig: GenerationConfig? = null,
  val tools: List<Tool>? = null,
) : Request

@Serializable
data class CountTokensRequest(@Transient val model: String? = null, val contents: List<Content>) :
  Request
