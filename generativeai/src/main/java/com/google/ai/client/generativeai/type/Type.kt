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

package com.google.ai.client.generativeai.type

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import org.json.JSONObject

/**
 * Represents and passes the type information for an automated function call.
 *
 * @property name: the enum name of the type
 * @property parse: the deserialization function
 * @property T: the type of the object that this maps to in code.
 */
class FunctionType<T>(val name: String, val parse: (String?) -> T?) {
  companion object {
    @JvmField
    val STRING = FunctionType<String>("STRING") { it }
    @JvmField
    val INTEGER = FunctionType<Int>("INTEGER") { it?.toIntOrNull() }
    @JvmField
    val LONG = FunctionType<Long>("INTEGER") { it?.toLongOrNull() }
    @JvmField
    val NUMBER = FunctionType<Double>("NUMBER") { it?.toDoubleOrNull() }
    @JvmField
    val BOOLEAN = FunctionType<Boolean>("BOOLEAN") { it?.toBoolean() }
    @JvmField
    val ARRAY =
      FunctionType<List<String>>("ARRAY") { it ->
        it?.let { Json.parseToJsonElement(it).jsonArray.map { element -> element.toString() } }
      }
    @JvmField
    val OBJECT = FunctionType<JSONObject>("OBJECT") { it?.let { JSONObject(it) } }
  }
}
