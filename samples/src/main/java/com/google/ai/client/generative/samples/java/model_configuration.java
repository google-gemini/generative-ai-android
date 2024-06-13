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

class ConfigureModel {
  void configureModel() {
    // [START configure_model]
    GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
    configBuilder.temperature = 0.9f;
    configBuilder.topK = 16;
    configBuilder.topP = 0.1f;
    configBuilder.maxOutputTokens = 200;
    configBuilder.stopSequences = Arrays.asList("red");

    GenerationConfig generationConfig = configBuilder.build();

    // The Gemini 1.5 models are versatile and work with most use cases
    GenerativeModel gm =
        new GenerativeModel("gemini-1.5-flash", BuildConfig.apiKey, generationConfig);

    GenerativeModelFutures model = GenerativeModelFutures.from(gm);
    // [END configure_model]
  }
}
