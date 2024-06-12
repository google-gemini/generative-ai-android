// Copyright 2024 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.ai.client.generative.samples

suspend fun chat() {
  // [START chat]
  val generativeModel = GenerativeModel(
      // The Gemini 1.5 models are versatile and work with multi-turn conversations (like chat)
      modelName = "gemini-1.5-flash",
      // Access your API key as a Build Configuration variable (see "Set up your API key" above)
      apiKey = BuildConfig.apiKey
  )

  val chat = generativeModel.startChat(
      history = listOf(
          content(role = "user") { text("Hello, I have 2 dogs in my house.") },
          content(role = "model") { text("Great to meet you. What would you like to know?") }
      )
  )

  val response = chat.sendMessage("How many paws are in my house?")
  print(response.text)
  // [END chat]
}

suspend fun chatStreaming() {
  // [START chat_streaming]
  // Use streaming with multi-turn conversations (like chat)
  val generativeModel = GenerativeModel(
      // The Gemini 1.5 models are versatile and work with multi-turn conversations (like chat)
      modelName = "gemini-1.5-flash",
      // Access your API key as a Build Configuration variable (see "Set up your API key" above)
      apiKey = BuildConfig.apiKey
  )

  val chat = generativeModel.startChat(
      history = listOf(
          content(role = "user") { text("Hello, I have 2 dogs in my house.") },
          content(role = "model") { text("Great to meet you. What would you like to know?") }
      )
  )

  val chat = generativeModel.startChat()
  chat.sendMessageStream("How many paws are in my house?").collect { chunk ->
    print(chunk.text)
  }
  // [END chat_streaming]
}

suspend fun chatStreamingWithImages() {
  // [START chat_with-images_streaming]
  // Use streaming with multi-turn conversations (like chat)
  val generativeModel = GenerativeModel(
      // The Gemini 1.5 models are versatile and work with multi-turn conversations (like chat)
      modelName = "gemini-1.5-flash",
      // Access your API key as a Build Configuration variable (see "Set up your API key" above)
      apiKey = BuildConfig.apiKey
  )

  val chat = generativeModel.startChat(
      history = listOf(
          content(role = "user") { text("Hello, I have 2 dogs in my house.") },
          content(role = "model") { text("Great to meet you. What would you like to know?") }
      )
  )

  val chat = generativeModel.startChat()
  val image: Bitmap // = ...

  val inputContent = content {
    image(image)
    text("This is a picture of them, what breed are they?")
  }

  chat.sendMessageStream(inputContent).collect { chunk ->
    print(chunk.text)
  }
  // [END chat_with-images_streaming]
}
