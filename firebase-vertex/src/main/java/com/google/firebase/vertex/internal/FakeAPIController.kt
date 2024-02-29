package com.google.firebase.vertex.internal

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Created for demonstration purposes only.
 * This class would handle network requests to the backend.
 */
internal class FakeAPIController {

    suspend fun generateContent(request: GenerateContentRequest): GenerateContentResponse {
        return GenerateContentResponse()
    }

    fun generateContentStream(request: GenerateContentRequest): Flow<GenerateContentResponse> {
        return flow { GenerateContentResponse() }
    }

    suspend fun countTokens(request: CountTokensRequest): CountTokensResponse {
        return CountTokensResponse(10)
    }
}
