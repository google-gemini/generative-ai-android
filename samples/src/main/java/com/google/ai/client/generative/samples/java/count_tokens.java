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

import static com.google.ai.client.generativeai.type.FunctionDeclarationsKt.defineFunction;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.CountTokensResponse;
import com.google.ai.client.generativeai.type.FunctionDeclaration;
import com.google.ai.client.generativeai.type.RequestOptions;
import com.google.ai.client.generativeai.type.Schema;
import com.google.ai.client.generativeai.type.Tool;
import com.google.ai.sample.R;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// Set up your API Key
// ====================
//
// To use the Gemini API, you'll need an API key. To learn more, see
// the "Set up your API Key section" in the [Gemini API
// quickstart](https://ai.google.dev/gemini-api/docs/quickstart?lang=android#set-up-api-key).

class CountTokens {
  void tokensTextOnly() {
    // [START tokens_text_only]
    // Specify a Gemini model appropriate for your use case
    GenerativeModel gm =
        new GenerativeModel(
            /* modelName */ "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key"
            // above)
            /* apiKey */ BuildConfig.apiKey);
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    Content inputContent =
        new Content.Builder().addText("Write a story about a magic backpack.").build();

    // For illustrative purposes only. You should use an executor that fits your needs.
    Executor executor = Executors.newSingleThreadExecutor();

    // For text-only input
    ListenableFuture<CountTokensResponse> countTokensResponse = model.countTokens(inputContent);

    Futures.addCallback(
        countTokensResponse,
        new FutureCallback<CountTokensResponse>() {
          @Override
          public void onSuccess(CountTokensResponse result) {
            int totalTokens = result.getTotalTokens();
            System.out.println("TotalTokens = " + totalTokens);
          }

          @Override
          public void onFailure(Throwable t) {
            t.printStackTrace();
          }
        },
        executor);
    // [END tokens_text_only]
  }

  void tokensChat() {
    // [START tokens_chat]
    // Specify a Gemini model appropriate for your use case
    GenerativeModel gm =
        new GenerativeModel(
            /* modelName */ "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key"
            // above)
            /* apiKey */ BuildConfig.apiKey);
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    // (optional) Create previous chat history for context
    Content.Builder userContentBuilder = new Content.Builder();
    userContentBuilder.setRole("user");
    userContentBuilder.addText("Hello, I have 2 dogs in my house.");
    Content userContent = userContentBuilder.build();

    Content.Builder modelContentBuilder = new Content.Builder();
    modelContentBuilder.setRole("model");
    modelContentBuilder.addText("Great to meet you. What would you like to know?");
    Content modelContent = userContentBuilder.build();

    List<Content> history = Arrays.asList(userContent, modelContent);

    // Initialize the chat
    ChatFutures chat = model.startChat(history);

    Content messageContent =
        new Content.Builder().addText("This is the message I intend to send").build();

    Collections.addAll(history, messageContent);

    // For illustrative purposes only. You should use an executor that fits your needs.
    Executor executor = Executors.newSingleThreadExecutor();

    ListenableFuture<CountTokensResponse> countTokensResponse =
        model.countTokens(history.toArray(new Content[0]));
    Futures.addCallback(
        countTokensResponse,
        new FutureCallback<CountTokensResponse>() {
          @Override
          public void onSuccess(CountTokensResponse result) {
            System.out.println(result);
          }

          @Override
          public void onFailure(Throwable t) {
            t.printStackTrace();
          }
        },
        executor);
    // [END tokens_chat]

  }

  void tokensMultimodalImageInline(Context context) {
    // [START tokens_multimodal_image_inline]
    // Specify a Gemini model appropriate for your use case
    GenerativeModel gm =
        new GenerativeModel(
            /* modelName */ "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key"
            // above)
            /* apiKey */ BuildConfig.apiKey);
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);
    Content text = new Content.Builder().addText("Write a story about a magic backpack.").build();

    // For illustrative purposes only. You should use an executor that fits your needs.
    Executor executor = Executors.newSingleThreadExecutor();

    // For text-and-image input
    Bitmap image1 = BitmapFactory.decodeResource(context.getResources(), R.drawable.image1);
    Bitmap image2 = BitmapFactory.decodeResource(context.getResources(), R.drawable.image2);

    Content multiModalContent =
        new Content.Builder()
            .addImage(image1)
            .addImage(image2)
            .addText("What's different between these pictures?")
            .build();

    ListenableFuture<CountTokensResponse> countTokensResponse =
        model.countTokens(multiModalContent);

    Futures.addCallback(
        countTokensResponse,
        new FutureCallback<CountTokensResponse>() {
          @Override
          public void onSuccess(CountTokensResponse result) {
            int totalTokens = result.getTotalTokens();
            System.out.println("TotalTokens = " + totalTokens);
          }

          @Override
          public void onFailure(Throwable t) {
            t.printStackTrace();
          }
        },
        executor);
    // [END tokens_multimodal_image_inline]

  }

  void tokensSystemInstruction() {
    // [START tokens_system_instruction]
    // Create your system instructions
    Content systemInstruction =
        new Content.Builder().addText("You are a cat. Your name is Neko.").build();

    // Specify a Gemini model appropriate for your use case
    GenerativeModel gm =
        new GenerativeModel(
            /* modelName */ "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key"
            // above)
            /* apiKey */ BuildConfig.apiKey,
            /* generationConfig (optional) */ null,
            /* safetySettings (optional) */ null,
            /* requestOptions (optional) */ new RequestOptions(),
            /* tools (optional) */ null,
            /* toolsConfig (optional) */ null,
            /* systemInstruction (optional) */ systemInstruction);
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    Content inputContent = new Content.Builder().addText("What's your name?.").build();

    // For illustrative purposes only. You should use an executor that fits your needs.
    Executor executor = Executors.newSingleThreadExecutor();

    // For text-only input
    ListenableFuture<CountTokensResponse> countTokensResponse = model.countTokens(inputContent);

    Futures.addCallback(
        countTokensResponse,
        new FutureCallback<CountTokensResponse>() {
          @Override
          public void onSuccess(CountTokensResponse result) {
            int totalTokens = result.getTotalTokens();
            System.out.println("TotalTokens = " + totalTokens);
          }

          @Override
          public void onFailure(Throwable t) {
            t.printStackTrace();
          }
        },
        executor);
    // [END tokens_system_instruction]
  }

  void tokenTools() {
    // [START tokens_tools]
    FunctionDeclaration multiplyDefinition =
        defineFunction(
            /* name  */ "multiply",
            /* description */ "returns a * b.",
            /* parameters */ Arrays.asList(
                Schema.numDouble("a", "First parameter"),
                Schema.numDouble("b", "Second parameter")),
            /* required */ Arrays.asList("a", "b"));

    Tool tool = new Tool(Arrays.asList(multiplyDefinition), null);
    ;

    // Specify a Gemini model appropriate for your use case
    GenerativeModel gm =
        new GenerativeModel(
            /* modelName */ "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key"
            // above)
            /* apiKey */ BuildConfig.apiKey,
            /* generationConfig (optional) */ null,
            /* safetySettings (optional) */ null,
            /* requestOptions (optional) */ new RequestOptions(),
            /* tools (optional) */ Arrays.asList(tool));
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    Content inputContent = new Content.Builder().addText("What's your name?.").build();

    // For illustrative purposes only. You should use an executor that fits your needs.
    Executor executor = Executors.newSingleThreadExecutor();

    // For text-only input
    ListenableFuture<CountTokensResponse> countTokensResponse = model.countTokens(inputContent);

    Futures.addCallback(
        countTokensResponse,
        new FutureCallback<CountTokensResponse>() {
          @Override
          public void onSuccess(CountTokensResponse result) {
            int totalTokens = result.getTotalTokens();
            System.out.println("TotalTokens = " + totalTokens);
          }

          @Override
          public void onFailure(Throwable t) {
            t.printStackTrace();
          }
        },
        executor);
    // [END tokens_tools]
  }
}
