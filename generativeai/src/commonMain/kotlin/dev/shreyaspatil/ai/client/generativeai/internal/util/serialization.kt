/*
 * Copyright 2024 Shreyas Patil
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.shreyaspatil.ai.client.generativeai.internal.util

/*
 * Copyright 2024 Shreyas Patil
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.enums.EnumEntries

interface SerializableEnum<T : Enum<T>> {
    val serialName: String
        get() = "UNKNOWN"
}

/**
 * Serializer for enums that defaults to the first ordinal on unknown types.
 *
 * Convention is that the first enum be named `UNKNOWN`, but any name is valid.
 *
 * When an unknown enum value is found, the enum itself will be logged to stderr with a message
 * about opening an issue on GitHub regarding the new enum value.
 */
internal fun <T> enumSerializer(
    enumValues: EnumEntries<T>,
) where
        T : SerializableEnum<T>,
        T : Enum<T> =
    object : KSerializer<T> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FirstOrdinalSerializer")

        override fun deserialize(decoder: Decoder): T {
            val name = decoder.decodeString()

            return enumValues.firstOrNull { it.serialName == name }
                ?: enumValues.first().also { printWarning(name) }
        }

        private fun printWarning(name: String) {
            dev.shreyaspatil.ai.client.generativeai.platform.Log.e(
                "FirstOrdinalSerializer",
                """
        |Unknown enum value found: $name"
        |This usually means the backend was updated, and the SDK needs to be updated to match it.
        |Check if there's a new version for the SDK, otherwise please open an issue on our
        |GitHub to bring it to our attention:
        |https://github.com/google/google-ai-android
       """
                    .trimMargin(),
            )
        }

        override fun serialize(encoder: Encoder, value: T) {
            encoder.encodeString(value.serialName)
        }
    }
