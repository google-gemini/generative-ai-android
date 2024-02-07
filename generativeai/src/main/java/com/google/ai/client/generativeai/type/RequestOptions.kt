package com.google.ai.client.generativeai.type

import io.ktor.client.plugins.HttpTimeout
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

/**
 * Configurable options unique to how requests to the backend are performed.
 *
 * @property timeout the maximum amount of time for a request to take, from start to finish.
 * @property apiVersion the api endpoint to call.
 */
class RequestOptions(
  val timeout: Duration = HttpTimeout.INFINITE_TIMEOUT_MS.milliseconds,
  val apiVersion: String = "v1"
)
