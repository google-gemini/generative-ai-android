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

// Set up your API Key
// ====================
//
// To use the Gemini API, you'll need an API key. To learn more, see
// the "Set up your API Key section" in the [Gemini API
// quickstart](https://ai.google.dev/gemini-api/docs/quickstart?lang=android#set-up-api-key).

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.FunctionType;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.GenerationConfig;
import com.google.ai.client.generativeai.type.Schema;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class ControlledGeneration {
  void jsonControlledGeneration() {
    // [START json_controlled_generation]
    Schema<List<String>> schema =
        new Schema(
            /* name */ "recipes",
            /* description */ "List of recipes",
            /* format */ null,
            /* nullable */ false,
            /* list */ null,
            /* properties */ null,
            /* required */ null,
            /* items */ new Schema(
                /* name */ "recipe",
                /* description */ "A recipe",
                /* format */ null,
                /* nullable */ false,
                /* list */ null,
                /* properties */ Map.of(
                    "recipeName",
                    new Schema(
                        /* name */ "recipeName",
                        /* description */ "Name of the recipe",
                        /* format */ null,
                        /* nullable */ false,
                        /* list */ null,
                        /* properties */ null,
                        /* required */ null,
                        /* items */ null,
                        /* type */ FunctionType.STRING)),
                /* required */ null,
                /* items */ null,
                /* type */ FunctionType.OBJECT),
            /* type */ FunctionType.ARRAY);

    GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
    configBuilder.responseMimeType = "application/json";
    configBuilder.responseSchema = schema;

    GenerationConfig generationConfig = configBuilder.build();

    // Specify a Gemini model appropriate for your use case
    GenerativeModel gm =
        new GenerativeModel(
            /* modelName */ "gemini-1.5-pro",
            // Access your API key as a Build Configuration variable (see "Set up your API key"
            // above)
            /* apiKey */ BuildConfig.apiKey,
            /* generationConfig */ generationConfig);
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    Content content = new Content.Builder().addText("List a few popular cookie recipes.").build();

    // For illustrative purposes only. You should use an executor that fits your needs.
    Executor executor = Executors.newSingleThreadExecutor();

    ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
    Futures.addCallback(
        response,
        new FutureCallback<GenerateContentResponse>() {
          @Override
          public void onSuccess(GenerateContentResponse result) {
            String resultText = result.getText();
            System.out.println(resultText);
          }

          @Override
          public void onFailure(Throwable t) {
            t.printStackTrace();
          }
        },
        executor);
    // [END json_controlled_generation]
  }

  void json_no_schema() {
    // [START json_no_schema]
    GenerationConfig.Builder configBuilder = new GenerationConfig.Builder();
    configBuilder.responseMimeType = "application/json";

    GenerationConfig generationConfig = configBuilder.build();

    // Specify a Gemini model appropriate for your use case
    GenerativeModel gm =
        new GenerativeModel(
            /* modelName */ "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key"
            // above)
            /* apiKey */ BuildConfig.apiKey,
            /* generationConfig */ generationConfig);
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    Content content =
        new Content.Builder()
            .addText(
                "List a few popular cookie recipes using this JSON schema:\n"
                    + "Recipe = {'recipeName': string}\n"
                    + "Return: Array<Recipe>")
            .build();

    // For illustrative purposes only. You should use an executor that fits your needs.
    Executor executor = Executors.newSingleThreadExecutor();

    ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
    Futures.addCallback(
        response,
        new FutureCallback<GenerateContentResponse>() {
          @Override
          public void onSuccess(GenerateContentResponse result) {
            String resultText = result.getText();
            System.out.println(resultText);
          }

          @Override
          public void onFailure(Throwable t) {
            t.printStackTrace();
          }
        },
        executor);
    // [END json_no_schema]
  }
}
