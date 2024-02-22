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

package com.google.ai.client.generativeai

import android.graphics.Bitmap
import com.google.ai.client.generativeai.type.GenerativeBeta
import com.google.ai.client.generativeai.type.BlobPart
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.FunctionCallPart
import com.google.ai.client.generativeai.type.FunctionResponsePart
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.ImagePart
import com.google.ai.client.generativeai.type.InvalidStateException
import com.google.ai.client.generativeai.type.TextPart
import com.google.ai.client.generativeai.type.content
import java.util.LinkedList
import java.util.concurrent.Semaphore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.transform

/**
 * Representation of a back and forth interaction with a model.
 *
 * Handles the capturing and storage of the communication with the model, providing methods for
 * further interaction.
 *
 * Note: This object is not thread-safe, and calling [sendMessage] multiple times without waiting
 * for a response will throw an [InvalidStateException].
 *
 * @param model the model to use for the interaction
 * @property history the previous interactions with the model
 */
@OptIn(GenerativeBeta::class)
class Chat(private val model: GenerativeModel, val history: MutableList<Content> = ArrayList()) {
  private var lock = Semaphore(1)

  /**
   * Generates a response from the backend with the provided [Content], and any previous ones
   * sent/returned from this chat.
   *
   * @param prompt A [Content] to send to the model.
   * @throws InvalidStateException if the prompt is not coming from the 'user' role
   * @throws InvalidStateException if the [Chat] instance has an active request.
   */
  suspend fun sendMessage(inputPrompt: Content): GenerateContentResponse {
    inputPrompt.assertComesFromUser()
    attemptLock()
    var response: GenerateContentResponse
    var prompt = inputPrompt
    val tempHistory = LinkedList<Content>()
    try {
      while (true) {
        response =
          model.generateContent(*history.toTypedArray(), *tempHistory.toTypedArray(), prompt)
        val responsePart = response.candidates.first().content.parts.first()

        tempHistory.add(prompt)
        tempHistory.add(response.candidates.first().content)
        if (responsePart is FunctionCallPart) {
          val output = model.executeFunction(responsePart)
          prompt = Content("function", listOf(FunctionResponsePart(responsePart.name, output)))
        } else {
          break
        }
      }
      history.addAll(tempHistory)
      return response
    } finally {
      lock.release()
    }
  }

  /**
   * Generates a response from the backend with the provided text represented [Content].
   *
   * @param prompt The text to be converted into a single piece of [Content] to send to the model.
   * @throws InvalidStateException if the [Chat] instance has an active request.
   */
  suspend fun sendMessage(prompt: String): GenerateContentResponse {
    val content = content("user") { text(prompt) }
    return sendMessage(content)
  }

  /**
   * Generates a response from the backend with the provided image represented [Content].
   *
   * @param prompt The image to be converted into a single piece of [Content] to send to the model.
   * @throws InvalidStateException if the [Chat] instance has an active request.
   */
  suspend fun sendMessage(prompt: Bitmap): GenerateContentResponse {
    val content = content("user") { image(prompt) }
    return sendMessage(content)
  }

  /**
   * Generates a streaming response from the backend with the provided [Content]s.
   *
   * @param prompt A [Content] to send to the model.
   * @return A [Flow] which will emit responses as they are returned from the model.
   * @throws InvalidStateException if the prompt is not coming from the 'user' role
   * @throws InvalidStateException if the [Chat] instance has an active request.
   */
  fun sendMessageStream(prompt: Content): Flow<GenerateContentResponse> {
    prompt.assertComesFromUser()
    attemptLock()

    val flow = model.generateContentStream(*history.toTypedArray(), prompt)
    val tempHistory = LinkedList<Content>()
    tempHistory.add(prompt)
    /**
     * TODO: revisit when images and blobs are returned. This will cause issues with how things are
     *   structured in the response. eg; a text/image/text response will be (incorrectly)
     *   represented as image/text
     */
    return flow
      .transform { response -> automaticFunctionExecutingTransform(this, tempHistory, response) }
      .onCompletion {
        lock.release()
        if (it == null) {
          history.addAll(tempHistory)
        }
      }
  }

  /**
   * Generates a streaming response from the backend with the provided [Content]s.
   *
   * @param prompt A [Content] to send to the model.
   * @return A [Flow] which will emit responses as they are returned from the model.
   * @throws InvalidStateException if the [Chat] instance has an active request.
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
   * @throws InvalidStateException if the [Chat] instance has an active request.
   */
  fun sendMessageStream(prompt: Bitmap): Flow<GenerateContentResponse> {
    val content = content("user") { image(prompt) }
    return sendMessageStream(content)
  }

  private fun Content.assertComesFromUser() {
    if (role != "user") {
      throw InvalidStateException("Chat prompts should come from the 'user' role.")
    }
  }

  private suspend fun automaticFunctionExecutingTransform(
    transformer: FlowCollector<GenerateContentResponse>,
    tempHistory: LinkedList<Content>,
    response: GenerateContentResponse
  ) {
    for (part in response.candidates.first().content.parts) {
      when (part) {
        is TextPart -> {
          transformer.emit(response)
          addTextToHistory(tempHistory, part)
        }
        is ImagePart -> {
          transformer.emit(response)
          tempHistory.add(Content("model", listOf(part)))
        }
        is BlobPart -> {
          transformer.emit(response)
          tempHistory.add(Content("model", listOf(part)))
        }
        is FunctionCallPart -> {
          val functionCall =
            response.candidates.first().content.parts.first { it is FunctionCallPart }
              as FunctionCallPart
          val output = model.executeFunction(functionCall)
          val functionResponse =
            Content("function", listOf(FunctionResponsePart(functionCall.name, output)))
          tempHistory.add(response.candidates.first().content)
          tempHistory.add(functionResponse)
          model
            .generateContentStream(*history.toTypedArray(), *tempHistory.toTypedArray())
            .collect { automaticFunctionExecutingTransform(transformer, tempHistory, it) }
        }
      }
    }
  }

  private fun addTextToHistory(tempHistory: LinkedList<Content>, textPart: TextPart) {
    val lastContent = tempHistory.lastOrNull()
    if (lastContent?.role == "model" && lastContent.parts.any { it is TextPart }) {
      tempHistory.removeLast()
      val editedContent =
        Content(
          "model",
          lastContent.parts.map {
            when (it) {
              is TextPart -> {
                TextPart(it.text + textPart.text)
              }
              else -> {
                it
              }
            }
          }
        )
      tempHistory.add(editedContent)
      return
    }
    tempHistory.add(Content("model", listOf(textPart)))
  }

  private fun attemptLock() {
    if (!lock.tryAcquire()) {
      throw InvalidStateException(
        "This chat instance currently has an ongoing request, please wait for it to complete " +
          "before sending more messages"
      )
    }
  }
}
