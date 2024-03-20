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

package com.google.ai.sample

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.FunctionType
import com.google.ai.client.generativeai.type.GenerativeBeta
import com.google.ai.client.generativeai.type.ParameterDeclaration
import com.google.ai.client.generativeai.type.ParameterDeclaration.Companion.int
import com.google.ai.client.generativeai.type.ParameterDeclaration.Companion.str
import com.google.ai.client.generativeai.type.RequestOptions
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.defineFunction
import com.google.ai.client.generativeai.type.generationConfig
import com.google.ai.sample.feature.chat.ChatViewModel
import com.google.ai.sample.feature.multimodal.PhotoReasoningViewModel
import com.google.ai.sample.feature.text.SummarizeViewModel
import org.json.JSONObject

@OptIn(GenerativeBeta::class)
val GenerativeViewModelFactory = object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        viewModelClass: Class<T>,
        extras: CreationExtras
    ): T {
        val config = generationConfig {
            temperature = 0.7f
        }

        return with(viewModelClass) {
            when {
                isAssignableFrom(SummarizeViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-pro` AI model
                    // for text generation
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-1.0-pro",
                        apiKey = BuildConfig.apiKey,
                        generationConfig = config
                    )
                    SummarizeViewModel(generativeModel)
                }

                isAssignableFrom(PhotoReasoningViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-pro-vision` AI model
                    // for multimodal text generation
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-1.0-pro-vision-latest",
                        apiKey = BuildConfig.apiKey,
                        generationConfig = config
                    )
                    PhotoReasoningViewModel(generativeModel)
                }

                isAssignableFrom(ChatViewModel::class.java) -> {
                    // Initialize a GenerativeModel with the `gemini-pro` AI model for chat
                    val generativeModel = GenerativeModel(
                        modelName = "gemini-1.0-pro",
                        apiKey = BuildConfig.apiKey,
                        generationConfig = config,
                        requestOptions = RequestOptions(apiVersion = "v1beta"),
                        tools = listOf(
                            Tool(
                                listOf(
                                    defineFunction(
                                        "getWeather",
                                        "gets the weather at a given city",
                                        str("city", "the city to get the weather in")
                                    ) { city -> JSONObject(mapOf("result" to "Sunny")) }
                                )
                            )
                        )
                    )
                    ChatViewModel(generativeModel)
                }

                else ->
                    throw IllegalArgumentException("Unknown ViewModel class: ${viewModelClass.name}")
            }
        } as T
    }
}
