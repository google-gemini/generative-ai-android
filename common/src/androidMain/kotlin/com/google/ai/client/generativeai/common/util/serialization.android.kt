/*
 * Copyright 2024 Google LLC
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

package com.google.ai.client.generativeai.common.util

import com.google.ai.client.generativeai.common.SerializationException
import kotlinx.serialization.SerialName
import java.lang.reflect.Field
import kotlin.reflect.KClass

/**
 * A variant of [getAnnotation][Field.getAnnotation] that provides implicit Kotlin support.
 *
 * Syntax sugar for:
 * ```
 * getAnnotation(T::class.java)
 * ```
 */
internal inline fun <reified T : Annotation> Field.getAnnotation() = getAnnotation(T::class.java)


/**
 * Provides the name to be used in serialization for this enum value.
 *
 * By default an enum is serialized to its [name][Enum.name], and can be overwritten by providing a
 * [SerialName] annotation.
 */
internal actual fun <T : Enum<T>> T.serialName(): String = declaringJavaClass.getField(name).getAnnotation<SerialName>()?.value ?: name

/**
 * Variant of [kotlin.enumValues] that provides support for [KClass] instances of enums.
 *
 * @throws SerializationException if the class is not a valid enum. Beyond runtime emily magic, this
 *   shouldn't really be possible.
 */
internal actual fun <T : Enum<T>> KClass<T>.enumValues(): Array<T> = java.enumConstants ?: throw SerializationException("$simpleName is not a valid enum type.")
