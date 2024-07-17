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
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Candidate;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.Part;
import com.google.ai.client.generativeai.type.RequestOptions;
import com.google.ai.client.generativeai.type.Tool;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

// Set up your API Key
// ====================
//
// To use the Gemini API, you'll need an API key. To learn more, see
// the "Set up your API Key section" in the [Gemini API
// quickstart](https://ai.google.dev/gemini-api/docs/quickstart?lang=android#set-up-api-key).
class CodeExecution {

    void codeExecutionBasic() {
        // [START code_execution_basic]
        // Specify a Gemini model appropriate for your use case
        GenerativeModel gm =
                new GenerativeModel(
                        /* modelName */ "gemini-1.5-pro",
                        // Access your API key as a Build Configuration variable (see "Set up your API key"
                        // above)
                        /* apiKey */ BuildConfig.apiKey,
                        /* generationConfig */ null,
                        /* safetySettings */ null,
                        /* requestOptions */ new RequestOptions(),
                        /* tools */ Collections.singletonList(Tool.CODE_EXECUTION));
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content inputContent =
                new Content.Builder().addText("What is the sum of the first 50 prime numbers?").build();

        // For illustrative purposes only. You should use an executor that fits your needs.
        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(inputContent);
        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        // Each `part` either contains `text`, `executable_code` or an
                        // `execution_result`
                        Candidate candidate = result.getCandidates().get(0);
                        for (Part part : candidate.getContent().getParts()) {
                            System.out.println(part);
                        }

                        // Alternatively, you can use the `text` accessor joins the parts into a
                        // markdown compatible text representation
                        String resultText = result.getText();
                        System.out.println(resultText);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                },
                executor);
        // [END code_execution_basic]
    }

    void codeExecutionChat() {
        // [START code_execution_chat]
        // Specify a Gemini model appropriate for your use case
        GenerativeModel gm =
                new GenerativeModel(
                        /* modelName */ "gemini-1.5-pro",
                        // Access your API key as a Build Configuration variable (see "Set up your API key"
                        // above)
                        /* apiKey */ BuildConfig.apiKey,
                        /* generationConfig */ null,
                        /* safetySettings */ null,
                        /* requestOptions */ new RequestOptions(),
                        /* tools */ Collections.singletonList(Tool.CODE_EXECUTION));
        GenerativeModelFutures model = GenerativeModelFutures.from(gm);

        Content inputContent =
                new Content.Builder().addText("What is the sum of the first 50 prime numbers?").build();

        ChatFutures chat = model.startChat();

        // For illustrative purposes only. You should use an executor that fits your needs.
        Executor executor = Executors.newSingleThreadExecutor();

        ListenableFuture<GenerateContentResponse> response = chat.sendMessage(inputContent);
        Futures.addCallback(
                response,
                new FutureCallback<GenerateContentResponse>() {
                    @Override
                    public void onSuccess(GenerateContentResponse result) {
                        // Each `part` either contains `text`, `executable_code` or an
                        // `execution_result`
                        Candidate candidate = result.getCandidates().get(0);
                        for (Part part : candidate.getContent().getParts()) {
                            System.out.println(part);
                        }

                        // Alternatively, you can use the `text` accessor which joins the parts into a
                        // markdown compatible text representation
                        String resultText = result.getText();
                        System.out.println(resultText);
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                    }
                },
                executor);
        // [END code_execution_chat]
    }
}
