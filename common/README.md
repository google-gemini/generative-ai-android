# Protos

> [!NOTE]
> Some code and documentation may refer to "Generative AI" as "labs". These two names are used
> interchangeably, and you should just register them as different names for the same service.

Protos are derived from a combination of the [Generative AI proto files](https://github.com/googleapis/googleapis/tree/master/google/ai/generativelanguage/v1beta)
and the [Vertex AI proto files](https://github.com/googleapis/googleapis/tree/master/google/cloud/aiplatform/v1beta1).

The goal is to maintain a sort of overlap between the two protos- representing their "common"
definitions.

## Organization

Within this SDK, the protos are defined under the following three categories.

### [Client](#client-protos)

You can find these types [here](https://github.com/google-gemini/generative-ai-android/blob/main/common/src/main/kotlin/com/google/ai/client/generativeai/common/client/Types.kt).

These are types that can only be sent _to_ the server; meaning the server will never respond
with them. 

You can classify them as "client" only types, or "request" types.

### [Server](#server-protos)

You can find these types [here](https://github.com/google-gemini/generative-ai-android/blob/main/common/src/main/kotlin/com/google/ai/client/generativeai/common/server/Types.kt).

These are types that can only be sent _from_ the server; meaning the client will never create them
on their own.

You can classify them as "server" only types, or "response" types.

### [Shared](#shared-protos)

You can find these types [here](https://github.com/google-gemini/generative-ai-android/blob/main/common/src/main/kotlin/com/google/ai/client/generativeai/common/shared/Types.kt).

These are types that can both be sent _to_ and received _from_ the server; meaning the client can
create them, and the server can also respond with them.

You can classify them as "shared" types, or "common" types.

## Alignment efforts

In aligning with the proto, you should be mindful of the following practices:

### Field presence

Additional Context: [Presence in Proto3 APIs](https://github.com/google-gemini/generative-ai-android/blob/main/common/src/main/kotlin/com/google/ai/client/generativeai/common/shared/Types.kt)

- `optional` types should be nullable.
- non `optional` primitive types (including enums) should default to their [respective default](https://protobuf.dev/programming-guides/proto3/#default).
- `repeated` fields that are not marked with a `google.api.field_behavior` of `REQUIRED` should 
default to an empty list or map.
- message fields that are marked with a `google.api.field_behavior` of `OPTIONAL` should be nullable.
- fields that are marked with a `google.api.field_behavior` of `REQUIRED` should *NOT* have a 
default value, but *ONLY* when it's a [client](#client-protos) or [shared](#shared-protos) type.
- if a field is marked with both `optional` and a `google.api.field_behavior` of `REQUIRED`, then it
should be a nullable field that does _not_ default to null (ie; it needs to be explicitly set).

### Serial names

> [!NOTE]
> The exception to this rule is ENUM fields, which DO use `snake_case` serial names.

While the proto is defined in `snake_case`, it will respect and respond in `camelCase` if you send
the request in `camelCase`. As such, our protos do not have `@SerialName` annotations denoting their
`snake_case` alternative.

So all your fields should be defined in `camelCase` format.
