package com.google.ai.client.generativeai

import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.GenerateContentResponse
import kotlinx.coroutines.flow.Flow

interface Chat {
    val generativeModel: GenerativeModel
    val history: MutableList<Content>

    suspend fun sendMessage(prompt: Content): GenerateContentResponse

    suspend fun sendMessage(prompt: String): GenerateContentResponse

    fun sendMessageStream(prompt: Content): Flow<GenerateContentResponse>

    fun sendMessageStream(prompt: String): Flow<GenerateContentResponse>
}
