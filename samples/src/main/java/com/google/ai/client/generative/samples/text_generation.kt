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

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

suspend fun textGenTextOnlyPrompt () {
  // [START text_gen_text-only-prompt]
  val generativeModel = GenerativeModel(
      // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
      modelName = "gemini-1.5-flash",
      // Access your API key as a Build Configuration variable (see "Set up your API key" above)
      apiKey = BuildConfig.apiKey
  )

  val prompt = "Write a story about a magic backpack."
  val response = generativeModel.generateContent(prompt)
  print(response.text)
  // [END text_gen_text-only-prompt]
}

suspend fun textGenTextOnlyPromptStreaming () {
  // [START text_gen_text-only-prompt_streaming]
  val generativeModel = GenerativeModel(
      // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
      modelName = "gemini-1.5-flash",
      // Access your API key as a Build Configuration variable (see "Set up your API key" above)
      apiKey = BuildConfig.apiKey
  )

  val prompt = "Write a story about a magic backpack."
  // Use streaming with text-only input
  generativeModel.generateContentStream(prompt).collect { chunk ->
    print(chunk.text)
  }

  // [END text_gen_text-only-prompt_streaming]
}

suspend fun textGenMultimodalOneImagePrompt () {
  // [START text_gen_multimodal-one-image-prompt]
  val generativeModel = GenerativeModel(
      // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
      modelName = "gemini-1.5-flash",
      // Access your API key as a Build Configuration variable (see "Set up your API key" above)
      apiKey = BuildConfig.apiKey
  )



  val inputContent = content {

    text("What's in this picture?")
  }

  val response = generativeModel.generateContent(inputContent)
  print(response.text)
  // [END text_gen_multimodal-one-image-prompt]
}

suspend fun textGenMultimodalOneImagePromptStreaming () {
  // [START text_gen_multimodal-one-image-prompt_streaming]
  val generativeModel = GenerativeModel(
      // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
      modelName = "gemini-1.5-flash",
      // Access your API key as a Build Configuration variable (see "Set up your API key" above)
      apiKey = BuildConfig.apiKey
  )



  val inputContent = content {

    text("What's in this picture?")
  }

  generativeModel.generateContentStream(inputContent).collect { chunk ->
    print(chunk.text)
  }
  // [END text_gen_multimodal-one-image-prompt_streaming]
}

suspend fun textGenMultimodalMultiImagePrompt () {
  // [START text_gen_multimodal-multi-image-prompt]
  val generativeModel = GenerativeModel(
      // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
      modelName = "gemini-1.5-flash",
      // Access your API key as a Build Configuration variable (see "Set up your API key" above)
      apiKey = BuildConfig.apiKey
  )


  val inputContent = content {

    text("What's different between these pictures?")
  }

  val response = generativeModel.generateContent(inputContent)
  print(response.text)

  // [END text_gen_multimodal-multi-image-prompt]
}

suspend fun textGenMultimodalMultiImagePromptStreaming () {
  // [START text_gen_multimodal-multi-image-prompt_streaming]
  val generativeModel = GenerativeModel(
      // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
      modelName = "gemini-1.5-flash",
      // Access your API key as a Build Configuration variable (see "Set up your API key" above)
      apiKey = BuildConfig.apiKey
  )



  val inputContent = content {

    text("What's different between these pictures?")
  }

  generativeModel.generateContentStream(inputContent).collect { chunk ->
    print(chunk.text)
  }
  // [END text_gen_multimodal-multi-image-prompt_streaming]
}

suspend fun textGenMultimodalVideoPrompt () {
  // [START text_gen_multimodal-video-prompt]
  // TODO
  // [END text_gen_multimodal-video-prompt]
}

suspend fun textGenMultimodalVideoPromptStreaming () {
  // [START text_gen_multimodal-video-prompt_streaming]
  // TODO
  // [END text_gen_multimodal-video-prompt_streaming]
}
