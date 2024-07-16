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
import com.google.ai.client.generativeai.type.FunctionType
import com.google.ai.client.generativeai.type.Schema
import com.google.ai.client.generativeai.type.generationConfig

// Set up your API Key
// ====================
//
// To use the Gemini API, you'll need an API key. To learn more, see
// the "Set up your API Key section" in the [Gemini API
// quickstart](https://ai.google.dev/gemini-api/docs/quickstart?lang=android#set-up-api-key).

suspend fun json_controlled_generation() {
    // [START json_controlled_generation]
    val jsonSchema = Schema(
        name = "recipes",
        description = "List of recipes",
        type = FunctionType.ARRAY,
        items = Schema(
            name = "recipe",
            description = "A recipe",
            type = FunctionType.OBJECT,
            properties = mapOf(
                "recipeName" to Schema(
                    name = "recipeName",
                    description = "Name of the recipe",
                    type = FunctionType.STRING,
                    nullable = false
                ),
            ),
            required = listOf("recipeName")
        ),
    )

    val generativeModel =
        GenerativeModel(
            // Specify a Gemini model appropriate for your use case
            modelName = "gemini-1.5-pro",
            // Access your API key as a Build Configuration variable (see "Set up your API key" above)
            apiKey = BuildConfig.apiKey,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = jsonSchema
            })

    val prompt = "List a few popular cookie recipes."
    val response = generativeModel.generateContent(prompt)
    print(response.text)

    // [END json_controlled_generation]
}

suspend fun json_no_schema() {
    // [START json_no_schema]
    val generativeModel =
        GenerativeModel(
            // Specify a Gemini model appropriate for your use case
            modelName = "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key" above)
            apiKey = BuildConfig.apiKey,
            generationConfig = generationConfig {
                responseMimeType = "application/json"
            })

    val prompt = """
           List a few popular cookie recipes using this JSON schema:
           Recipe = {'recipeName': string}
           Return: Array<Recipe>
    """.trimIndent()
    val response = generativeModel.generateContent(prompt)
    print(response.text)

    // [END json_no_schema]
}
