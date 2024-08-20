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

package com.google.ai.client.generativeai.java

import androidx.concurrent.futures.SuspendToFutureAdapter
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.java.ChatFutures.Companion.from
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.CountTokensResponse
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.reactive.asPublisher
import org.reactivestreams.Publisher

/**
 * Helper method for interacting with a [GenerativeModel] from Java.
 *
 * @see from
 */
abstract class GenerativeModelFutures internal constructor() {

  /**
   * Generates a response from the backend with the provided [Content]s.
   *
   * @param prompt A group of [Content]s to send to the model.
   */
  abstract fun generateContent(
    prompt: Content,
    vararg prompts: Content
  ): ListenableFuture<GenerateContentResponse>

  /**
   * Generates a streaming response from the backend with the provided [Content]s.
   *
   * @param prompt A group of [Content]s to send to the model.
   */
  abstract fun generateContentStream(
    prompt: Content,
    vararg prompts: Content
  ): Publisher<GenerateContentResponse>

  /**
   * Counts the number of tokens used in a prompt.
   *
   * @param prompt A group of [Content]s to count tokens of.
   */
  abstract fun countTokens(
    prompt: Content,
    vararg prompts: Content
  ): ListenableFuture<CountTokensResponse>

  /** Creates a chat instance which internally tracks the ongoing conversation with the model */
  abstract fun startChat(): ChatFutures

  /**
   * Creates a chat instance which internally tracks the ongoing conversation with the model
   *
   * @param history an existing history of context to use as a starting point
   */
  abstract fun startChat(history: List<Content>): ChatFutures

  /** Returns the [GenerativeModel] instance that was used to create this object */
  abstract fun getGenerativeModel(): GenerativeModel

  private class FuturesImpl(private val model: GenerativeModel) : GenerativeModelFutures() {
    override fun generateContent(
      prompt: Content,
      vararg prompts: Content
    ): ListenableFuture<GenerateContentResponse> =
      SuspendToFutureAdapter.launchFuture { model.generateContent(prompt, *prompts) }

    override fun generateContentStream(
      prompt: Content,
      vararg prompts: Content
    ): Publisher<GenerateContentResponse> =
      model.generateContentStream(prompt, *prompts).asPublisher()

    override fun countTokens(
      prompt: Content,
      vararg prompts: Content
    ): ListenableFuture<CountTokensResponse> =
      SuspendToFutureAdapter.launchFuture { model.countTokens(prompt, *prompts) }

    override fun startChat(): ChatFutures = startChat(emptyList())

    override fun startChat(history: List<Content>): ChatFutures = from(model.startChat(history))

    override fun getGenerativeModel(): GenerativeModel = model
  }

  companion object {

    /** @return a [GenerativeModelFutures] created around the provided [GenerativeModel] */
    @JvmStatic fun from(model: GenerativeModel): GenerativeModelFutures = FuturesImpl(model)
  }
}
