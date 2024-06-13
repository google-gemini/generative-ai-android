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

class CountTokens {
  void tokensTextOnly() {
    // [START tokens_text-only]
    // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
    GenerativeModel gm =
        new GenerativeModel(
            /* modelName */ "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key"
            // above)
            /* apiKey */ BuildConfig.apiKey);
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    Content inputContent =
        new Content.Builder().addText("Write a story about a magic backpack.").build();

    Executor executor; // =  ...

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
    // [END tokens_text-only]
  }

  void tokensChat() {
    // [START tokens_chat]
    // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
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

    List<Content> history = chat.getChat().getHistory();

    Content messageContent =
        new Content.Builder().addText("This is the message I intend to send").build();

    Collections.addAll(history, messageContent);

    ListenableFuture<CountTokensResponse> countTokensResponse =
        model.countTokens(history.toArray(new Content[0]));
    Futures.addCallback(
        response,
        new FutureCallback<CountTokenResponse>() {
          @Override
          public void onSuccess(CountTokenResponse result) {
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

  void tokensMultimodalImageInline() {
    Content text = new Content.Builder().addText("Write a story about a magic backpack.").build();

    Executor executor = // ...

        // For text-only input
        ListenableFuture < CountTokensResponse > countTokensResponse = model.countTokens(text);

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

    // For text-and-image input
    Bitmap image1; // = ...
    Bitmap image2; // = ...

    Content multiModalContent =
        new Content.Builder()
            .addImage(image1)
            .addImage(image2)
            .addText("What's different between these pictures?")
            .build();

    ListenableFuture<CountTokensResponse> countTokensResponse =
        model.countTokens(multiModalContent);

    // For multi-turn conversations (like chat)
    List<Content> history = chat.getChat().getHistory();

    Content messageContent =
        new Content.Builder().addText("This is the message I intend to send").build();

    Collections.addAll(history, messageContent);

    ListenableFuture<CountTokensResponse> countTokensResponse =
        model.countTokens(history.toArray(new Content[0]));
  }
}
