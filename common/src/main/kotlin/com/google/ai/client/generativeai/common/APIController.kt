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

package com.google.ai.client.generativeai.common

import com.google.ai.client.generativeai.common.server.FinishReason
import com.google.ai.client.generativeai.common.util.decodeToFlow
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.preparePost
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

val JSON = Json {
  ignoreUnknownKeys = true
  prettyPrint = false
}

/**
 * Backend class for interfacing with the Gemini API.
 *
 * This class handles making HTTP requests to the API and streaming the responses back.
 *
 * @param httpEngine The HTTP client engine to be used for making requests. Defaults to CIO engine.
 *   Exposed primarily for DI in tests.
 * @property key The API key used for authentication.
 * @property model The model to use for generation.
 */
class APIController
internal constructor(
  private val key: String,
  model: String,
  private val requestOptions: RequestOptions,
  httpEngine: HttpClientEngine
) {

  constructor(
    key: String,
    model: String,
    requestOptions: RequestOptions
  ) : this(key, model, requestOptions, OkHttp.create())

  private val model = fullModelName(model)

  private val client =
    HttpClient(httpEngine) {
      install(HttpTimeout) {
        requestTimeoutMillis = requestOptions.timeout.inWholeMilliseconds
        socketTimeoutMillis = 80_000
      }
      install(ContentNegotiation) { json(JSON) }
    }

  suspend fun generateContent(request: GenerateContentRequest): GenerateContentResponse =
    try {
      client
        .post("${requestOptions.endpoint}/${requestOptions.apiVersion}/$model:generateContent") {
          applyCommonConfiguration(request)
        }
        .also { validateResponse(it) }
        .body<GenerateContentResponse>()
        .validate()
    } catch (e: Throwable) {
      throw GoogleGenerativeAIException.from(e)
    }

  fun generateContentStream(request: GenerateContentRequest): Flow<GenerateContentResponse> =
    client
      .postStream<GenerateContentResponse>(
        "${requestOptions.endpoint}/${requestOptions.apiVersion}/$model:streamGenerateContent?alt=sse"
      ) {
        applyCommonConfiguration(request)
      }
      .map { it.validate() }
      .catch { throw GoogleGenerativeAIException.from(it) }

  suspend fun countTokens(request: CountTokensRequest): CountTokensResponse =
    try {
      client
        .post("${requestOptions.endpoint}/${requestOptions.apiVersion}/$model:countTokens") {
          applyCommonConfiguration(request)
        }
        .also { validateResponse(it) }
        .body()
    } catch (e: Throwable) {
      throw GoogleGenerativeAIException.from(e)
    }

  private fun HttpRequestBuilder.applyCommonConfiguration(request: Request) {
    when (request) {
      is GenerateContentRequest -> setBody<GenerateContentRequest>(request)
      is CountTokensRequest -> setBody<CountTokensRequest>(request)
    }
    contentType(ContentType.Application.Json)
    header("x-goog-api-key", key)
    header("x-goog-api-client", "genai-android/${BuildConfig.VERSION_NAME}")
  }
}

/**
 * Ensures the model name provided has a `models/` prefix
 *
 * Models must be prepended with the `models/` prefix when communicating with the backend.
 */
private fun fullModelName(name: String): String = name.takeIf { it.contains("/") } ?: "models/$name"

/**
 * Makes a POST request to the specified [url] and returns a [Flow] of deserialized response objects
 * of type [R]. The response is expected to be a stream of JSON objects that are parsed in real-time
 * as they are received from the server.
 *
 * This function is intended for internal use within the client that handles streaming responses.
 *
 * Example usage:
 * ```
 * val client: HttpClient = HttpClient(CIO)
 * val request: Request = GenerateContentRequest(...)
 * val url: String = "http://example.com/stream"
 *
 * val responses: GenerateContentResponse = client.postStream(url) {
 *   setBody(request)
 *   contentType(ContentType.Application.Json)
 * }
 * responses.collect {
 *   println("Got a response: $it")
 * }
 * ```
 *
 * @param R The type of the response object.
 * @param url The URL to which the POST request will be made.
 * @param config An optional [HttpRequestBuilder] callback for request configuration.
 * @return A [Flow] of response objects of type [R].
 */
private inline fun <reified R : Response> HttpClient.postStream(
  url: String,
  crossinline config: HttpRequestBuilder.() -> Unit = {}
): Flow<R> = channelFlow {
  launch(CoroutineName("postStream")) {
    preparePost(url) { config() }
      .execute {
        validateResponse(it)

        val channel = it.bodyAsChannel()
        val flow = JSON.decodeToFlow<R>(channel)

        flow.collect { send(it) }
      }
  }
}

private suspend fun validateResponse(response: HttpResponse) {
  if (response.status == HttpStatusCode.OK) return
  val text = response.bodyAsText()
  val message =
    try {
      JSON.decodeFromString<GRpcErrorResponse>(text).error.message
    } catch (e: Throwable) {
      "Unexpected Response:\n$text"
    }
  if (message.contains("API key not valid")) {
    throw InvalidAPIKeyException(message)
  }
  // TODO (b/325117891): Use a better method than string matching.
  if (message == "User location is not supported for the API use.") {
    throw UnsupportedUserLocationException()
  }
  if (message.contains("quota")) {
    throw QuotaExceededException(message)
  }
  throw ServerException(message)
}

private fun GenerateContentResponse.validate() = apply {
  if ((candidates?.isEmpty() != false) && promptFeedback == null) {
    throw SerializationException("Error deserializing response, found no valid fields")
  }
  promptFeedback?.blockReason?.let { throw PromptBlockedException(this) }
  candidates
    ?.mapNotNull { it.finishReason }
    ?.firstOrNull { it != FinishReason.STOP }
    ?.let { throw ResponseStoppedException(this) }
}
