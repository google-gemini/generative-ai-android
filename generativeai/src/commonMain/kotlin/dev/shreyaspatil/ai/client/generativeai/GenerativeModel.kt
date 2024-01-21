/*
 * Copyright ${YEAR} Shreyas Patil
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
package dev.shreyaspatil.ai.client.generativeai

import dev.shreyaspatil.ai.client.generativeai.internal.api.APIController
import dev.shreyaspatil.ai.client.generativeai.internal.api.CountTokensRequest
import dev.shreyaspatil.ai.client.generativeai.internal.api.GenerateContentRequest
import dev.shreyaspatil.ai.client.generativeai.internal.util.toInternal
import dev.shreyaspatil.ai.client.generativeai.internal.util.toPublic
import dev.shreyaspatil.ai.client.generativeai.type.Content
import dev.shreyaspatil.ai.client.generativeai.type.CountTokensResponse
import dev.shreyaspatil.ai.client.generativeai.type.FinishReason
import dev.shreyaspatil.ai.client.generativeai.type.GenerateContentResponse
import dev.shreyaspatil.ai.client.generativeai.type.GenerationConfig
import dev.shreyaspatil.ai.client.generativeai.type.GoogleGenerativeAIException
import dev.shreyaspatil.ai.client.generativeai.type.PlatformImage
import dev.shreyaspatil.ai.client.generativeai.type.PromptBlockedException
import dev.shreyaspatil.ai.client.generativeai.type.ResponseStoppedException
import dev.shreyaspatil.ai.client.generativeai.type.SafetySetting
import dev.shreyaspatil.ai.client.generativeai.type.SerializationException
import dev.shreyaspatil.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlin.jvm.JvmOverloads

/**
 * A facilitator for a given multimodal model (eg; Gemini).
 *
 * @property modelName name of the model in the backend
 * @property apiKey authentication key for interacting with the backend
 * @property generationConfig configuration parameters to use for content generation
 * @property safetySettings the safety bounds to use during alongside prompts during content
 *   generation
 */
class GenerativeModel
internal constructor(
    val modelName: String,
    val apiKey: String,
    val generationConfig: GenerationConfig? = null,
    val safetySettings: List<SafetySetting>? = null,
    private val controller: APIController,
) {

    @JvmOverloads
    constructor(
        modelName: String,
        apiKey: String,
        generationConfig: GenerationConfig? = null,
        safetySettings: List<SafetySetting>? = null,
    ) : this(modelName, apiKey, generationConfig, safetySettings, APIController(apiKey, modelName))

    /**
     * Generates a response from the backend with the provided [Content]s.
     *
     * @param prompt A group of [Content]s to send to the model.
     * @return A [GenerateContentResponse] after some delay. Function should be called within a
     *   suspend context to properly manage concurrency.
     */
    suspend fun generateContent(vararg prompt: Content): GenerateContentResponse =
        try {
            controller.generateContent(constructRequest(*prompt)).toPublic().validate()
        } catch (e: Throwable) {
            throw GoogleGenerativeAIException.from(e)
        }

    /**
     * Generates a streaming response from the backend with the provided [Content]s.
     *
     * @param prompt A group of [Content]s to send to the model.
     * @return A [Flow] which will emit responses as they are returned from the model.
     */
    fun generateContentStream(vararg prompt: Content): Flow<GenerateContentResponse> =
        controller
            .generateContentStream(constructRequest(*prompt))
            .map { it.toPublic().validate() }
            .catch { throw GoogleGenerativeAIException.from(it) }

    /**
     * Generates a response from the backend with the provided text represented [Content].
     *
     * @param prompt The text to be converted into a single piece of [Content] to send to the model.
     * @return A [GenerateContentResponse] after some delay. Function should be called within a
     *   suspend context to properly manage concurrency.
     */
    suspend fun generateContent(prompt: String): GenerateContentResponse =
        generateContent(content { text(prompt) })

    /**
     * Generates a streaming response from the backend with the provided text represented [Content].
     *
     * @param prompt The text to be converted into a single piece of [Content] to send to the model.
     * @return A [Flow] which will emit responses as they are returned from the model.
     */
    fun generateContentStream(prompt: String): Flow<GenerateContentResponse> =
        generateContentStream(content { text(prompt) })

    /**
     * Generates a response from the backend with the provided bitmap represented [Content].
     *
     * @param prompt The bitmap to be converted into a single piece of [Content] to send to the model.
     * @return A [GenerateContentResponse] after some delay. Function should be called within a
     *   suspend context to properly manage concurrency.
     */
    suspend fun generateContent(prompt: PlatformImage): GenerateContentResponse =
        generateContent(content { image(prompt) })

    /**
     * Generates a streaming response from the backend with the provided bitmap represented [Content].
     *
     * @param prompt The bitmap to be converted into a single piece of [Content] to send to the model.
     * @return A [Flow] which will emit responses as they are returned from the model.
     */
    fun generateContentStream(prompt: PlatformImage): Flow<GenerateContentResponse> =
        generateContentStream(content { image(prompt) })

    /** Creates a chat instance which internally tracks the ongoing conversation with the model */
    fun startChat(history: List<Content> = emptyList()): Chat = Chat(this, history.toMutableList())

    /**
     * Counts the number of tokens used in a prompt.
     *
     * @param prompt A group of [Content]s to count tokens of.
     * @return A [CountTokensResponse] containing the number of tokens in the prompt.
     */
    suspend fun countTokens(vararg prompt: Content): CountTokensResponse {
        return controller.countTokens(constructCountTokensRequest(*prompt)).toPublic()
    }

    /**
     * Counts the number of tokens used in a prompt.
     *
     * @param prompt The text to be converted to a single piece of [Content] to count the tokens of.
     * @return A [CountTokensResponse] containing the number of tokens in the prompt.
     */
    suspend fun countTokens(prompt: String): CountTokensResponse {
        return countTokens(content { text(prompt) })
    }

    /**
     * Counts the number of tokens used in a prompt.
     *
     * @param prompt The image to be converted to a single piece of [Content] to count the tokens of.
     * @return A [CountTokensResponse] containing the number of tokens in the prompt.
     */
    suspend fun countTokens(prompt: PlatformImage): CountTokensResponse {
        return countTokens(content { image(prompt) })
    }

    private fun constructRequest(vararg prompt: Content) =
        GenerateContentRequest(
            modelName,
            prompt.map { it.toInternal() },
            safetySettings?.map { it.toInternal() },
            generationConfig?.toInternal(),
        )

    private fun constructCountTokensRequest(vararg prompt: Content) =
        CountTokensRequest(modelName, prompt.map { it.toInternal() })

    private fun GenerateContentResponse.validate() = apply {
        if (candidates.isEmpty() && promptFeedback == null) {
            throw SerializationException("Error deserializing response, found no valid fields")
        }
        promptFeedback?.blockReason?.let { throw PromptBlockedException(this) }
        candidates
            .mapNotNull { it.finishReason }
            .firstOrNull { it != FinishReason.STOP }
            ?.let { throw ResponseStoppedException(this) }
    }
}
