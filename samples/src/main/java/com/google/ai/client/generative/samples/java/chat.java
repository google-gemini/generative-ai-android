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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.sample.R;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

class Chat {
  void chat() {
    // [START chat]
    // The Gemini 1.5 models are versatile and work with multi-turn conversations (like chat)
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

    // Create a new user message
    Content.Builder userMessageBuilder = new Content.Builder();
    userMessageBuilder.setRole("user");
    userMessageBuilder.addText("How many paws are in my house?");
    Content userMessage = userMessageBuilder.build();

    // TODO COMMENT
    Executor executor = Executors.newSingleThreadExecutor();

    // Send the message
    ListenableFuture<GenerateContentResponse> response = chat.sendMessage(userMessage);

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
    // [END chat]
  }

  void chatStreaming() {
    // [START chat_streaming]
    // The Gemini 1.5 models are versatile and work with multi-turn conversations (like chat)
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

    // Create a new user message
    Content.Builder userMessageBuilder = new Content.Builder();
    userMessageBuilder.setRole("user");
    userMessageBuilder.addText("How many paws are in my house?");
    Content userMessage = userMessageBuilder.build();

    // Use streaming with text-only input
    Publisher<GenerateContentResponse> streamingResponse = model.generateContentStream(userMessage);

    StringBuilder outputContent = new StringBuilder();

    streamingResponse.subscribe(
        new Subscriber<GenerateContentResponse>() {
          @Override
          public void onNext(GenerateContentResponse generateContentResponse) {
            String chunk = generateContentResponse.getText();
            outputContent.append(chunk);
          }

          @Override
          public void onComplete() {
            System.out.println(outputContent);
          }

          @Override
          public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
          }

          // ... other methods omitted for brevity
          // [START_EXCLUDE]
          @Override
          public void onError(Throwable t) {}
          // [END_EXCLUDE]
        });

    // [END chat_streaming]
  }

  void chatStreamingWithImages(Context context) {
    // [START chat_with-images_streaming]
    // The Gemini 1.5 models are versatile and work with multi-turn conversations (like chat)
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

    // Create a new user message
    Bitmap image = BitmapFactory.decodeResource(context.getResources(), R.drawable.image);

    Content.Builder userMessageBuilder = new Content.Builder();
    userMessageBuilder.setRole("user");
    userMessageBuilder.addImage(image);
    userMessageBuilder.addText("This is a picture of them, what breed are they?");
    Content userMessage = userMessageBuilder.build();

    // Use streaming with text-only input
    Publisher<GenerateContentResponse> streamingResponse = model.generateContentStream(userMessage);

    StringBuilder outputContent = new StringBuilder();

    streamingResponse.subscribe(
        new Subscriber<GenerateContentResponse>() {
          @Override
          public void onNext(GenerateContentResponse generateContentResponse) {
            String chunk = generateContentResponse.getText();
            outputContent.append(chunk);
          }

          @Override
          public void onComplete() {
            System.out.println(outputContent);
          }

          @Override
          public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
          }

          // ... other methods omitted for brevity
          // [START_EXCLUDE]
          @Override
          public void onError(Throwable t) {}
          // [END_EXCLUDE]
        });
    // [END chat_with-images_streaming]
  }
}
