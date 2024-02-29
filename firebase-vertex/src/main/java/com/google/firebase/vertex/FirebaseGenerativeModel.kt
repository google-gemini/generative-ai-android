package com.google.firebase.vertex

import com.google.ai.client.generativeai.Chat
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.CountTokensResponse
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.firebase.vertex.internal.FakeAPIController
import kotlinx.coroutines.flow.Flow

class FirebaseGenerativeModel
internal constructor(
    override val modelName: String,
    location: String
) : GenerativeModel {

    private val apiController = FakeAPIController()

    override suspend fun generateContent(vararg prompt: Content): GenerateContentResponse {
        TODO("Not yet implemented")
    }

    override fun generateContentStream(vararg prompt: Content): Flow<GenerateContentResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun generateContent(prompt: String): GenerateContentResponse {
        TODO("Not yet implemented")
    }

    fun generateContentStream(prompt: String): Flow<GenerateContentResponse> {
        TODO("Not yet implemented")
    }

    override fun startChat(history: List<Content>): Chat {
        TODO("Not yet implemented")
    }

    override suspend fun countTokens(prompt: String): CountTokensResponse {
        TODO("Not yet implemented")
    }

    override suspend fun countTokens(vararg prompt: Content): CountTokensResponse {
        TODO("Not yet implemented")
    }
}
