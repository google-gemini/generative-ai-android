package com.google.firebase.vertex

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.GenerateContentResponse
import com.google.ai.client.generativeai.type.UsageMetadata

// (this doesn't necessarily need to be a separate file, but I created it for simplicity)

// Suppose that this feature only exists in Vertex, but not in Labs.
// Creating an extension function on the 'common' [GenerateContentResponse] type
// rather than a Vertex-specific type allows us to move it to the common library in
// the future, once Labs adds support for the future.
//
val GenerateContentResponse.usageMetadata: UsageMetadata
    get() = UsageMetadata(12, 32, 44)

// The logic above would also apply if Vertex needs to add a new function (that Labs doesn't support yet)
// to the `FirebaseGenerativeModel` class. That function should be added as an extension function
// to the 'common' `GenerativeModel` instead of `FirebaseGenerativeModel`. Eg:
fun GenerativeModel.vertexNewAwesomeFeature() {
    // This ensures that Labs can add support for it in the future with
    // no breaking change or code duplication.
}
