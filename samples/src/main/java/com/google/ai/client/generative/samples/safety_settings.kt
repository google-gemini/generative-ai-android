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

suspend fun safetySettings() {
  // [START safety-settings]
  val generativeModel = GenerativeModel(
      // The Gemini 1.5 models are versatile and work with most use cases
      modelName = "gemini-1.5-flash",
      apiKey = BuildConfig.apiKey,
      safetySettings = listOf(
          SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH)
      )
  )
  // [END safety-settings]
}

suspend fun SafetySettingsMulti() {
  // [START safety-settings_multi]
  val harassmentSafety = SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH)

  val hateSpeechSafety = SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE)

  val generativeModel = GenerativeModel(
      // The Gemini 1.5 models are versatile and work with most use cases
      modelName = "gemini-1.5-flash",
      apiKey = BuildConfig.apiKey,
      safetySettings = listOf(harassmentSafety, hateSpeechSafety)
  )
  // [END safety-settings_multi]
}
