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

package com.google.ai.client.generativeai.common

import com.google.ai.client.generativeai.common.client.GenerationConfig
import com.google.ai.client.generativeai.common.client.Tool
import com.google.ai.client.generativeai.common.client.ToolConfig
import com.google.ai.client.generativeai.common.shared.Content
import com.google.ai.client.generativeai.common.shared.SafetySetting
import com.google.ai.client.generativeai.common.util.fullModelName
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable

sealed interface Request

@Serializable
data class GenerateContentRequest(
  val model: String,
  val contents: List<Content>,
  val safetySettings: List<SafetySetting> = emptyList(),
  val generationConfig: GenerationConfig? = null,
  val tools: List<Tool> = emptyList(),
  val toolConfig: ToolConfig? = null,
  val systemInstruction: Content? = null,
) : Request

@Serializable
data class CountTokensRequest(
  val model: String,
  val contents: List<Content> = emptyList(),
  val tools: List<Tool> = emptyList(),
  val generateContentRequest: GenerateContentRequest? = null,
  val systemInstruction: Content? = null,
) : Request {
  companion object {
    fun forGenAI(request: GenerateContentRequest) =
      CountTokensRequest(fullModelName(request.model), request.contents, emptyList(), request)

    fun forVertexAI(request: GenerateContentRequest) =
      CountTokensRequest(
        fullModelName(request.model),
        request.contents,
        request.tools,
        null,
        request.systemInstruction,
      )
  }
}
