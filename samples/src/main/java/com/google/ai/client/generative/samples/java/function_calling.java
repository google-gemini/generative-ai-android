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

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.ChatFutures;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.FunctionCallPart;
import com.google.ai.client.generativeai.type.FunctionDeclaration;
import com.google.ai.client.generativeai.type.FunctionResponsePart;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.ai.client.generativeai.type.RequestOptions;
import com.google.ai.client.generativeai.type.Schema;
import com.google.ai.client.generativeai.type.Tool;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.Arrays;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.json.JSONException;
import org.json.JSONObject;

// Set up your API Key
// ====================
//
// To use the Gemini API, you'll need an API key. To learn more, see
// the "Set up your API Key section" in the [Gemini API
// quickstart](https://ai.google.dev/gemini-api/docs/quickstart?lang=android#set-up-api-key).

class FunctionCalling {

  double multiply(double a, double b) {
    return a * b;
  }

  void functionCalling() {
    // [START function_calling]
    FunctionDeclaration multiplyDefinition =
        defineFunction(
            /* name  */ "multiply",
            /* description */ "returns a * b.",
            /* parameters */ Arrays.asList(
                Schema.numDouble("a", "First parameter"),
                Schema.numDouble("b", "Second parameter")),
            /* required */ Arrays.asList("a", "b"));

    Tool tool = new Tool(Arrays.asList(multiplyDefinition), null);

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
            /* functionDeclarations (optional) */ Arrays.asList(tool));
    GenerativeModelFutures model = GenerativeModelFutures.from(gm);

    // Create prompt
    Content.Builder userContentBuilder = new Content.Builder();
    userContentBuilder.setRole("user");
    userContentBuilder.addText(
        "I have 57 cats, each owns 44 mittens, how many mittens is that in total?");
    Content userMessage = userContentBuilder.build();

    // For illustrative purposes only. You should use an executor that fits your needs.
    Executor executor = Executors.newSingleThreadExecutor();

    // Initialize the chat
    ChatFutures chat = model.startChat();

    // Send the message
    ListenableFuture<GenerateContentResponse> response = chat.sendMessage(userMessage);

    Futures.addCallback(
        response,
        new FutureCallback<GenerateContentResponse>() {
          @Override
          public void onSuccess(GenerateContentResponse result) {
            if (!result.getFunctionCalls().isEmpty()) {
              handleFunctionCall(result);
            }
            if (!result.getText().isEmpty()) {
              System.out.println(result.getText());
            }
          }

          @Override
          public void onFailure(Throwable t) {
            t.printStackTrace();
          }

          private void handleFunctionCall(GenerateContentResponse result) {
            FunctionCallPart multiplyFunctionCallPart =
                result.getFunctionCalls().stream()
                    .filter(fun -> fun.getName().equals("multiply"))
                    .findFirst()
                    .get();
            double a = Double.parseDouble(multiplyFunctionCallPart.getArgs().get("a"));
            double b = Double.parseDouble(multiplyFunctionCallPart.getArgs().get("b"));

            try {
              // `multiply(a, b)` is a regular java function defined in another class
              FunctionResponsePart functionResponsePart =
                  new FunctionResponsePart(
                      "multiply", new JSONObject().put("result", multiply(a, b)));

              // Create prompt
              Content.Builder functionCallResponse = new Content.Builder();
              userContentBuilder.setRole("user");
              userContentBuilder.addPart(functionResponsePart);
              Content userMessage = userContentBuilder.build();

              chat.sendMessage(userMessage);
            } catch (JSONException e) {
              throw new RuntimeException(e);
            }
          }
        },
        executor);

    // [END function_calling]
  }
}
