package com.google.ai.client.generativeai

import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.CountTokensResponse
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
}
