# Google AI SDK for Android


> [!CAUTION]
> **The Google AI SDK for Android is recommended for prototyping only.** If you plan to enable billing, we strongly recommend that you use a backend SDK to access the Google AI Gemini API. You risk
> potentially exposing your API key to malicious actors if you embed your API key directly in your Android app or fetch it remotely at runtime.


The Google AI client SDK for Android enables developers to use Google's state-of-the-art generative AI models (like Gemini) to build AI-powered features and applications. This SDK supports use cases like:
- Generate text from text-only input
- Generate text from text-and-images input (multimodal)
- Build multi-turn conversations (chat)

For example, with just a few lines of code, you can access Gemini's multimodal capabilities to generate text from text-and-image input:

```kotlin
val generativeModel = GenerativeModel(
    modelName = "gemini-1.5-pro-latest",
    apiKey = BuildConfig.apiKey
)

val cookieImage: Bitmap = // ...
val inputContent = content() {
  image(cookieImage)
  text("Does this look store-bought or homemade?")
}

val response = generativeModel.generateContent(inputContent)
print(response.text)
```

> [!NOTE]
> If you want to access Gemini on-device (Gemini Nano), check out the [Google AI Edge SDK for Android](https://ai.google.dev/tutorials/android_aicore), which is enabled via Android AICore.

## Try out the sample Android app

This repository contains a sample app demonstrating how the SDK can access and utilize the Gemini model for various use cases.

To try out the sample app you can directly import the project from Android Studio
via **File > New > Import Sample** and searching for *Generative AI Sample* or follow these steps below:

1.  Check out this repository.\
`git clone https://github.com/google/generative-ai-android`

1.  [Obtain an API key](https://makersuite.google.com/app/apikey) to use with the Google AI SDKs.

1.  Open and build the sample app in the `generativeai-android-sample` folder of this repo.

1.  Paste your API key into the `apiKey` property in the `local.properties` file.

1.  Run the app.

## Installation and usage

Add the dependency `implementation("com.google.ai.client.generativeai:generativeai:<version>"`) to your Android project.

For detailed instructions, you can find a [quickstart](https://ai.google.dev/tutorials/android_quickstart) for the Google AI client SDK for Android in the Google documentation.

This quickstart describes how to add your API key and the SDK's dependency to your app, initialize the model, and then call the API to access the model. It also describes some additional use cases and features, like streaming, counting tokens, and controlling responses.
## Documentation

Find complete documentation for the Google AI SDKs and the Gemini model in the Google documentation:\
https://ai.google.dev/docs
## Contributing

See [Contributing](https://github.com/google/generative-ai-android/blob/main/CONTRIBUTING.md) for more information on contributing to the Google AI client SDK for Android.

## License

The contents of this repository are licensed under the
[Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
