# Google AI SDK for Android

The Google AI Android SDK is the easiest way for Android developers to build with the Gemini API. The Gemini API gives you access to Gemini [models](https://ai.google.dev/models/gemini) created by [Google DeepMind](https://deepmind.google/technologies/gemini/#introduction). Gemini models are built from the ground up to be multimodal, so you can reason seamlessly across text, images, and code. 

> [!CAUTION]
> **The Google AI SDK for Android is recommended for prototyping only.** If you plan to enable billing, we strongly recommend that you use a backend SDK to access the Google AI Gemini API. You risk
> potentially exposing your API key to malicious actors if you embed your API key directly in your Android app or fetch it remotely at runtime.

> [!NOTE]
> If you want to access Gemini on-device (Gemini Nano), check out the [Google AI Edge SDK for Android](https://ai.google.dev/tutorials/android_aicore), which is enabled via Android AICore.


## Get started with the Gemini API

This repository contains a sample app demonstrating how the SDK can access and utilize the Gemini model for various use cases.

To try out the sample app you can directly import the project from Android Studio
via **File > New > Import Sample** and searching for *Generative AI Sample* or follow these steps below:

1. Go to [Google AI Studio](https://aistudio.google.com/).
2. Login with your Google account.
3. [Create](https://aistudio.google.com/app/apikey) an API key. Note that in Europe the free tier is not available.
4. Check out this repository.\
`git clone https://github.com/google/generative-ai-android`
5. Open and build the sample app in the `generativeai-android-sample` folder of this repo.
6. Paste your API key into the `apiKey` property in the `local.properties` file.
7. Run the app
5. For detailed instructions, try the 
[Android SDK tutorial](https://ai.google.dev/tutorials/android_quickstart) on [ai.google.dev](https://ai.google.dev).

## Usage example

Add the dependency `implementation("com.google.ai.client.generativeai:generativeai:<version>"`) to your Android project.

Initialize the model

```kotlin
val generativeModel = GenerativeModel(
    modelName = "gemini-1.5-pro-latest",
    apiKey = BuildConfig.apiKey
)
```

Run a prompt.

```kotlin
val cookieImage: Bitmap = // ...
val inputContent = content() {
  image(cookieImage)
  text("Does this look store-bought or homemade?")
}

val response = generativeModel.generateContent(inputContent)
print(response.text)
```

For detailed instructions, you can find a [quickstart](https://ai.google.dev/tutorials/android_quickstart) for the Google AI client SDK for Android in the Google documentation.

This quickstart describes how to add your API key and the SDK's dependency to your app, initialize the model, and then call the API to access the model. It also describes some additional use cases and features, like streaming, counting tokens, and controlling responses.

## Documentation

See the [Gemini API Cookbook](https://github.com/google-gemini/gemini-api-cookbook/) or [ai.google.dev](https://ai.google.dev) for complete documentation.

## Contributing

See [Contributing](https://github.com/google/generative-ai-android/blob/main/CONTRIBUTING.md) for more information on contributing to the Google AI client SDK for Android.

## License

The contents of this repository are licensed under the
[Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
