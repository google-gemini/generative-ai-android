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

package com.google.ai.client.generative.samples

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Tool

// Set up your API Key
// ====================
//
// To use the Gemini API, you'll need an API key. To learn more, see
// the "Set up your API Key section" in the [Gemini API
// quickstart](https://ai.google.dev/gemini-api/docs/quickstart?lang=android#set-up-api-key).

suspend fun codeExecutionBasic() {
    // [START code_execution_basic]

    val model = GenerativeModel(
        // Specify a Gemini model appropriate for your use case
        modelName = "gemini-1.5-pro",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = BuildConfig.apiKey,
        tools = listOf(Tool.CODE_EXECUTION)
    )

    val response = model.generateContent("What is the sum of the first 50 prime numbers?")

    // Each `part` either contains `text`, `executable_code` or an `execution_result`
    println(response.candidates[0].content.parts.joinToString("\n"))

    // Alternatively, you can use the `text` accessor joins the parts into a markdown compatible
    // text representation
    println(response.text)
    // [END code_execution_basic]
}

suspend fun codeExecutionChat() {
    // [START code_execution_chat]

    val model = GenerativeModel(
        // Specify a Gemini model appropriate for your use case
        modelName = "gemini-1.5-pro",
        // Access your API key as a Build Configuration variable (see "Set up your API key" above)
        apiKey = BuildConfig.apiKey,
        tools = listOf(Tool.CODE_EXECUTION)
    )

    val chat = model.startChat()

    val response = chat.sendMessage("What is the sum of the first 50 prime numbers?")

    // Each `part` either contains `text`, `executable_code` or an `execution_result`
    println(response.candidates[0].content.parts.joinToString("\n"))

    // Alternatively, you can use the `text` accessor which joins the parts into a markdown compatible
    // text representation
    println(response.text)
    // [END code_execution_chat]
}