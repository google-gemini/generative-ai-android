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
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class TextGeneration {
  void TextGenTextOnlyPrompt() {
    // [START text_gen_text-only-prompt]
    // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
    GenerativeModel gm =
        new GenerativeModel(
            /* modelName */ "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key"
            // above)
            /* apiKey */ BuildConfig.apiKey);
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    Content content =
        new Content.Builder().addText("Write a story about a magic backpack.").build();

    // TODO COMMENT
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
    // [END text_gen_text-only-prompt]
  }

  void TextGenTextOnlyPromptStreaming() {
    // [START text_gen_text-only-prompt_streaming]
    // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
    GenerativeModel gm =
        new GenerativeModel(
            /* modelName */ "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key"
            // above)
            /* apiKey */ BuildConfig.apiKey);
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    Content content =
        new Content.Builder().addText("Write a story about a magic backpack.").build();

    Publisher<GenerateContentResponse> streamingResponse = model.generateContentStream(content);

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
          public void onError(Throwable t) {
            t.printStackTrace();
          }

          @Override
          public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
          }
        });
    // [END text_gen_text-only-prompt_streaming]
  }

  void TextGenMultimodalOneImagePrompt() {
    // [START text_gen_multimodal-one-image-prompt]
    // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
    GenerativeModel gm =
        new GenerativeModel(
            /* modelName */ "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key"
            // above)
            /* apiKey */ BuildConfig.apiKey);
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    Bitmap image; // = ...

    Content content =
        new Content.Builder()
            .addText("What's different between these pictures?")
            .addImage(image)
            .build();

    Executor executor; // = ...

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
    // [END text_gen_multimodal-one-image-prompt]
  }

  void TextGenMultimodalOneImagePromptStreaming() {
    // [START text_gen_multimodal-one-image-prompt_streaming]
    // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
    GenerativeModel gm =
        new GenerativeModel(
            /* modelName */ "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key"
            // above)
            /* apiKey */ BuildConfig.apiKey);
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    Bitmap image1; // = ...
    Bitmap image2; // = ...

    Content content =
        new Content.Builder()
            .addText("What's different between these pictures?")
            .addImage(image1)
            .addImage(image2)
            .build();

    Executor executor; // = ...

    Publisher<GenerateContentResponse> streamingResponse = model.generateContentStream(content);

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
          public void onError(Throwable t) {
            t.printStackTrace();
          }

          @Override
          public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
          }
        });
    // [END text_gen_multimodal-one-image-prompt_streaming]
  }

  void TextGenMultimodalMultiImagePrompt() {
    // [START text_gen_multimodal-multi-image-prompt]
    // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
    GenerativeModel gm =
        new GenerativeModel(
            /* modelName */ "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key"
            // above)
            /* apiKey */ BuildConfig.apiKey);
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    Bitmap image1; // = ...
    Bitmap image2; // = ...

    Content content =
        new Content.Builder()
            .addText("What's different between these pictures?")
            .addImage(image1)
            .addImage(image2)
            .build();

    Executor executor; // = ...

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
    // [END text_gen_multimodal-multi-image-prompt]
  }

  void TextGenMultimodalMultiImagePromptStreaming() {
    // [START text_gen_multimodal-multi-image-prompt_streaming]
    // The Gemini 1.5 models are versatile and work with both text-only and multimodal prompts
    GenerativeModel gm =
        new GenerativeModel(
            /* modelName */ "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key"
            // above)
            /* apiKey */ BuildConfig.apiKey);
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    Bitmap image1; // = ...
    Bitmap image2; // = ...

    Content content =
        new Content.Builder()
            .addText("What's different between these pictures?")
            .addImage(image1)
            .addImage(image2)
            .build();

    Executor executor; // = ...

    Publisher<GenerateContentResponse> streamingResponse = model.generateContentStream(content);

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
          public void onError(Throwable t) {
            t.printStackTrace();
          }

          @Override
          public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
          }
        });
    // [END text_gen_multimodal-multi-image-prompt_streaming]
  }

  void TextGenMultimodalVideoPrompt() {
    // [START text_gen_multimodal-video-prompt]
    // TODO
    // [END text_gen_multimodal-video-prompt]
  }

  void TextGenMultimodalVideoPromptStreaming() {
    // [START text_gen_multimodal-video-prompt_streaming]
    // TODO
    // [END text_gen_multimodal-video-prompt_streaming]
  }
}
