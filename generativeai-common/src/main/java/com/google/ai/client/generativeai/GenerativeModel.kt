package com.google.ai.client.generativeai

import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.CountTokensResponse
import com.google.ai.client.generativeai.type.FunctionCallResponse
import com.google.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.flow.Flow

interface GenerativeModel {
    val modelName: String

    suspend fun generateContent(vararg prompt: Content): GenerateContentResponse

    fun generateContentStream(vararg prompt: Content): Flow<GenerateContentResponse>

    suspend fun generateContent(prompt: String): GenerateContentResponse

    fun startChat(history: List<Content> = emptyList()): Chat

    suspend fun countTokens(prompt: String): CountTokensResponse

    suspend fun countTokens(vararg prompt: Content): CountTokensResponse

    // Suppose that both SDKs are shipping a new 'function calling' feature around the same timeline.
    // That new feature's public surface should be added to the interface so it can be used by both SDKs
    suspend fun executeFunction(function: () -> Unit): FunctionCallResponse
}
