package com.google.ai.client.generativeai.type

// Eg. Vertex introduced a new feature that Labs doesn't support yet.
//
// Any new Surface API types will be added to the common module
// even if only one SDK supports it.
// (assume that the other SDK might support it in the future)
class UsageMetadata(
    val promptTokenCount: Int,
    val candidatesTokenCount: Int,
    val totalTokenCount: Int
) { }
