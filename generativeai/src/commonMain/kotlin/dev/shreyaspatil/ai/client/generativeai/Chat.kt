/*
 * Copyright 2024 Shreyas Patil
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

import dev.shreyaspatil.ai.client.generativeai.type.BlobPart
import dev.shreyaspatil.ai.client.generativeai.type.Content
import dev.shreyaspatil.ai.client.generativeai.type.GenerateContentResponse
import dev.shreyaspatil.ai.client.generativeai.type.ImagePart
import dev.shreyaspatil.ai.client.generativeai.type.InvalidStateException
import dev.shreyaspatil.ai.client.generativeai.type.PlatformImage
import dev.shreyaspatil.ai.client.generativeai.type.TextPart
import dev.shreyaspatil.ai.client.generativeai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

/**
 * Representation of a back and forth interaction with a model.
 *
 * Handles the capturing and storage of the communication with the model, providing methods for
 * further interaction.
 *
 * @param model the model to use for the interaction
 * @property history the previous interactions with the model
 */
class Chat(private val model: GenerativeModel, val history: MutableList<Content> = ArrayList()) {

    /**
     * Generates a response from the backend with the provided [Content], and any previous ones
     * sent/returned from this chat.
     *
     * @param prompt A [Content] to send to the model.
     * @throws InvalidStateException if the prompt is not coming from the 'user' role
     */
    suspend fun sendMessage(prompt: Content): GenerateContentResponse {
        prompt.assertComesFromUser()

        val response = model.generateContent(*history.toTypedArray(), prompt)

        history.add(prompt)
        history.add(response.candidates.first().content)

        return response
    }

    /**
     * Generates a response from the backend with the provided text represented [Content].
     *
     * @param prompt The text to be converted into a single piece of [Content] to send to the model.
     */
    suspend fun sendMessage(prompt: String): GenerateContentResponse {
        val content = content("user") { text(prompt) }
        return sendMessage(content)
    }

    /**
     * Generates a response from the backend with the provided image represented [Content].
     *
     * @param prompt The image to be converted into a single piece of [Content] to send to the model.
     */
    suspend fun sendMessage(prompt: PlatformImage): GenerateContentResponse {
        val content = content("user") { image(prompt) }
        return sendMessage(content)
    }

    /**
     * Generates a streaming response from the backend with the provided [Content]s.
     *
     * @param prompt A [Content] to send to the model.
     * @return A [Flow] which will emit responses as they are returned from the model.
     * @throws InvalidStateException if the prompt is not coming from the 'user' role
     */
    fun sendMessageStream(prompt: Content): Flow<GenerateContentResponse> {
        prompt.assertComesFromUser()

        val flow = model.generateContentStream(*history.toTypedArray(), prompt)
        val bitmaps = LinkedHashSet<PlatformImage>()
        val blobs = LinkedHashSet<BlobPart>()
        val text = StringBuilder()

        /**
         * TODO: revisit when images and blobs are returned. This will cause issues with how things are
         *   structured in the response. eg; a text/image/text response will be (incorrectly)
         *   represented as image/text
         */
        return flow
            .onEach {
                for (part in it.candidates.first().content.parts) {
                    when (part) {
                        is TextPart -> text.append(part.text)
                        is ImagePart -> bitmaps.add(part.image)
                        is BlobPart -> blobs.add(part)
                    }
                }
            }
            .onCompletion {
                if (it == null) {
                    val content =
                        content("model") {
                            for (bitmap in bitmaps) {
                                image(bitmap)
                            }
                            for (blob in blobs) {
                                blob(blob.mimeType, blob.blob)
                            }
                            if (text.isNotBlank()) {
                                text(text.toString())
                            }
                        }

                    history.add(prompt)
                    history.add(content)
                }
            }
    }

    /**
     * Generates a streaming response from the backend with the provided [Content]s.
     *
     * @param prompt A [Content] to send to the model.
     * @return A [Flow] which will emit responses as they are returned from the model.
     */
    fun sendMessageStream(prompt: String): Flow<GenerateContentResponse> {
        val content = content("user") { text(prompt) }
        return sendMessageStream(content)
    }

    /**
     * Generates a streaming response from the backend with the provided [Content]s.
     *
     * @param prompt A [Content] to send to the model.
     * @return A [Flow] which will emit responses as they are returned from the model.
     */
    fun sendMessageStream(prompt: PlatformImage): Flow<GenerateContentResponse> {
        val content = content("user") { image(prompt) }
        return sendMessageStream(content)
    }

    private fun Content.assertComesFromUser() {
        if (role != "user") {
            throw InvalidStateException("Chat prompts should come from the 'user' role.")
        }
    }
}
