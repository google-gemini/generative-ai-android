package com.google.firebase.vertex.internal

import com.google.ai.client.generativeai.type.Candidate
import com.google.ai.client.generativeai.type.PromptFeedback

internal sealed interface Response

internal data class GenerateContentResponse(
  val candidates: List<Candidate>? = null,
  val promptFeedback: PromptFeedback? = null,
) : Response

internal data class CountTokensResponse(val totalTokens: Int) : Response
