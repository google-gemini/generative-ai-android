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

package com.google.ai.client.generative.samples.java;

class SafetySettings {
  void safetySettings() {
    // [START safety-settings]
    SafetySetting harassmentSafety =
        new SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH);

    // The Gemini 1.5 models are versatile and work with most use cases
    GenerativeModel gm =
        new GenerativeModel(
            "gemini-1.5-flash",
            BuildConfig.apiKey,
            null, // generation config is optional
            Collections.singletonList(harassmentSafety));

    GenerativeModelFutures model = GenerativeModelFutures.from(gm);
    // [END safety-settings]
  }

  void SafetySettingsMulti() {
    // [START safety-settings_multi]
    SafetySetting harassmentSafety =
        new SafetySetting(HarmCategory.HARASSMENT, BlockThreshold.ONLY_HIGH);

    SafetySetting hateSpeechSafety =
        new SafetySetting(HarmCategory.HATE_SPEECH, BlockThreshold.MEDIUM_AND_ABOVE);

    // The Gemini 1.5 models are versatile and work with most use cases
    GenerativeModel gm =
        new GenerativeModel(
            "gemini-1.5-flash",
            BuildConfig.apiKey,
            null, // generation config is optional
            Arrays.asList(harassmentSafety, hateSpeechSafety));

    GenerativeModelFutures model = GenerativeModelFutures.from(gm);
    // [END safety-settings_multi]
  }
}
