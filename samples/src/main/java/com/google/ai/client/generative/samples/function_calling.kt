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
import com.google.ai.client.generativeai.type.FunctionResponsePart
import com.google.ai.client.generativeai.type.InvalidStateException
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.Tool
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.defineFunction
import org.json.JSONObject


// Set up your API Key
// ====================
//
// To use the Gemini API, you'll need an API key. To learn more, see
// the "Set up your API Key section" in the [Gemini API
// quickstart](https://ai.google.dev/gemini-api/docs/quickstart?lang=android#set-up-api-key).

suspend fun functionCalling() {
    // [START function_calling]
    fun multiply(a: Double, b: Double) = a * b

    val multiplyDefinition = defineFunction(
        name = "multiply",
        description = "returns the product of the provided numbers.",
        parameters = listOf(
        Schema.double("a", "First number"),
        Schema.double("b", "Second number")
        )
    )

    val generativeModel =
        GenerativeModel(
            // Specify a Gemini model appropriate for your use case
            modelName = "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key" above)
            apiKey = BuildConfig.apiKey,
            // List the functions definitions you want to make available to the model
            tools = listOf(Tool(listOf(multiplyDefinition)))
        )

    val chat = generativeModel.startChat()
    val prompt = "I have 57 cats, each owns 44 mittens, how many mittens is that in total?"

    // Send the message to the generative model
    var response = chat.sendMessage(prompt)

    // Check if the model responded with a function call
    response.functionCalls.first { it.name == "multiply" }.apply {
        val a: String by args
        val b: String by args

        val result = JSONObject(mapOf("result" to multiply(a.toDouble(), b.toDouble())))
        response = chat.sendMessage(
            content(role = "function") {
                part(FunctionResponsePart("multiply", result))
            }
        )
    }

    // Whenever the model responds with text, show it in the UI
    response.text?.let { modelResponse ->
        println(modelResponse)
    }
    // [END function_calling]
}
