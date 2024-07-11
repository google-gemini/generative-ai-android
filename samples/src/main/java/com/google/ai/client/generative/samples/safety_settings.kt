/*
 * Copyright 2024 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ai.client.generative.samples

import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.BlockThreshold
import com.google.ai.client.generativeai.type.HarmCategory
import com.google.ai.client.generativeai.type.SafetySetting

// Set up your API Key
// ====================
//
// To use the Gemini API, you'll need an API key. To learn more, see
// the "Set up your API Key section" in the [Gemini API
// quickstart](https://ai.google.dev/gemini-api/docs/quickstart?lang=android#set-up-api-key).


suspend fun safetySettings() {
  // [START safety_settings]
  val generativeModel =
      GenerativeModel(
          // Specify a Gemini model appropriate for your use case
          modelName = "gemini-1.5-flash",
          apiKey = BuildConfig.apiKey,
          safetySettings = listOf(SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH)))
  // [END safety_settings]

  // Added to silence the compiler warning about unused variable.
  print(generativeModel)
}

suspend fun SafetySettingsMulti() {
  // [START safety_settings_multi]
  val harassmentSafety = SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH)

  val hateSpeechSafety = SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE)

  val generativeModel =
      GenerativeModel(
          // The Gemini 1.5 models are versatile and work with most use cases
          modelName = "gemini-1.5-flash",
          apiKey = BuildConfig.apiKey,
          safetySettings = listOf(harassmentSafety, hateSpeechSafety))
  // [END safety_settings_multi]

  // Added to silence the compiler warning about unused variable.
  print(generativeModel)
}
