# Developing

The snippets in this directory are organized to simplify their use as documentation.

## Snippets requirements

All snippets must compile.

## Workflow

1. In Android Studio, import the `generativeai-android-sample` project
2. In the left-hand bar, using the "Android" prespective, you'll
   notice that the within the `app` module, there are two packages:
   - `com.google.ai.client.generative.samples` which contains the snippets
   - `com.google.ai.sample` which contains the actual quickstart app
3. Make all necessary changes to the code in the
   `com.google.ai.client.generative.samples` snippets
4. To compile the snippets, compile the `app` module itself.

**IMPORTANT:** Always add both the Kotlin and the Java versions of the
snippets at the same time to maintain parity.

### How does it work

Under the hood, the configuration of the app module, in
`generativeai-android-sample/build.gradle.kts`, has been modified to
include in the main `sourceSet` the code for the samples.
