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

package com.google.ai.client.generativeai.internal.api

import com.google.ai.client.generativeai.internal.api.client.GenerationConfig
import com.google.ai.client.generativeai.internal.api.client.Tool
import com.google.ai.client.generativeai.internal.api.shared.Content
import com.google.ai.client.generativeai.internal.api.shared.SafetySetting
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal sealed interface Request

@Serializable
internal data class GenerateContentRequest(
  val model: String,
  val contents: List<Content>,
  @SerialName("safety_settings") val safetySettings: List<SafetySetting>? = null,
  @SerialName("generation_config") val generationConfig: GenerationConfig? = null,
  val tools: List<Tool>? = null
) : Request

@Serializable
internal data class CountTokensRequest(val model: String, val contents: List<Content>) : Request
