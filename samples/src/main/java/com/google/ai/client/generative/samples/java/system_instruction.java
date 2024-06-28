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

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.RequestOptions;

// Set up your API Key
// ====================
//
// To use the Gemini API, you'll need an API key. To learn more, see
// the "Set up your API Key section" in the [Gemini API
// quickstart](https://ai.google.dev/gemini-api/docs/quickstart?lang=android#set-up-api-key).

class SystemInstruction {
  void systemInstruction() {
    // [START system_instruction]
    GenerativeModel model =
        new GenerativeModel(
            // Specify a Gemini model appropriate for your use case
            /* modelName */ "gemini-1.5-flash",
            /* apiKey */ BuildConfig.apiKey,
            /* generationConfig (optional) */ null,
            /* safetySettings (optional) */ null,
            /* requestOptions (optional) */ new RequestOptions(),
            /* tools (optional) */ null,
            /* toolsConfig (optional) */ null,
            /* systemInstruction (optional) */ new Content.Builder()
                .addText("You are a cat. Your name is Neko.")
                .build());
    // [END system_instruction]
  }
}
