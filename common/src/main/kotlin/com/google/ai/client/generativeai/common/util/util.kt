package com.google.ai.client.generativeai.common.util

/**
 * Ensures the model name provided has a `models/` prefix
 *
 * Models must be prepended with the `models/` prefix when communicating with the backend.
 */
fun fullModelName(name: String): String = name.takeIf { it.contains("/") } ?: "models/$name"
