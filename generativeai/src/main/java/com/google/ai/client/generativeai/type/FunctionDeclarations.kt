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

import org.json.JSONObject

/**
 * A declared function, including implementation, that a model can be given access to in order to
 * gain info or complete tasks.
 *
 * @property name The name of the function call, this should be clear and descriptive for the model
 * @property description A description of what the function does and its output.
 * @property function the function implementation
 */
class NoParameterFunction(
  name: String,
  description: String,
  val function: suspend () -> JSONObject,
) : FunctionDeclaration(name, description) {
  override fun getParameters() = listOf<Schema<Any>>()

  suspend fun execute() = function()

  override suspend fun execute(part: FunctionCallPart) = function()
}

/**
 * A declared function, including implementation, that a model can be given access to in order to
 * gain info or complete tasks.
 *
 * @property name The name of the function call, this should be clear and descriptive for the model
 * @property description A description of what the function does and its output.
 * @property param A description of the first function parameter
 * @property function the function implementation
 */
class OneParameterFunction<T>(
  name: String,
  description: String,
  val param: Schema<T>,
  val function: suspend (T) -> JSONObject,
) : FunctionDeclaration(name, description) {
  override fun getParameters() = listOf(param)

  override suspend fun execute(part: FunctionCallPart): JSONObject {
    val arg1 = part.getArgOrThrow(param)
    return function(arg1)
  }
}

/**
 * A declared function, including implementation, that a model can be given access to in order to
 * gain info or complete tasks.
 *
 * @property name The name of the function call, this should be clear and descriptive for the model
 * @property description A description of what the function does and its output.
 * @property param1 A description of the first function parameter
 * @property param2 A description of the second function parameter
 * @property function the function implementation
 */
class TwoParameterFunction<T, U>(
  name: String,
  description: String,
  val param1: Schema<T>,
  val param2: Schema<U>,
  val function: suspend (T, U) -> JSONObject,
) : FunctionDeclaration(name, description) {
  override fun getParameters() = listOf(param1, param2)

  override suspend fun execute(part: FunctionCallPart): JSONObject {
    val arg1 = part.getArgOrThrow(param1)
    val arg2 = part.getArgOrThrow(param2)
    return function(arg1, arg2)
  }
}

/**
 * A declared function, including implementation, that a model can be given access to in order to
 * gain info or complete tasks.
 *
 * @property name The name of the function call, this should be clear and descriptive for the model
 * @property description A description of what the function does and its output.
 * @property param1 A description of the first function parameter
 * @property param2 A description of the second function parameter
 * @property param3 A description of the third function parameter
 * @property function the function implementation
 */
class ThreeParameterFunction<T, U, V>(
  name: String,
  description: String,
  val param1: Schema<T>,
  val param2: Schema<U>,
  val param3: Schema<V>,
  val function: suspend (T, U, V) -> JSONObject,
) : FunctionDeclaration(name, description) {
  override fun getParameters() = listOf(param1, param2, param3)

  override suspend fun execute(part: FunctionCallPart): JSONObject {
    val arg1 = part.getArgOrThrow(param1)
    val arg2 = part.getArgOrThrow(param2)
    val arg3 = part.getArgOrThrow(param3)
    return function(arg1, arg2, arg3)
  }
}

/**
 * A declared function, including implementation, that a model can be given access to in order to
 * gain info or complete tasks.
 *
 * @property name The name of the function call, this should be clear and descriptive for the model
 * @property description A description of what the function does and its output.
 * @property param1 A description of the first function parameter
 * @property param2 A description of the second function parameter
 * @property param3 A description of the third function parameter
 * @property param4 A description of the fourth function parameter
 * @property function the function implementation
 */
class FourParameterFunction<T, U, V, W>(
  name: String,
  description: String,
  val param1: Schema<T>,
  val param2: Schema<U>,
  val param3: Schema<V>,
  val param4: Schema<W>,
  val function: suspend (T, U, V, W) -> JSONObject,
) : FunctionDeclaration(name, description) {
  override fun getParameters() = listOf(param1, param2, param3, param4)

  override suspend fun execute(part: FunctionCallPart): JSONObject {
    val arg1 = part.getArgOrThrow(param1)
    val arg2 = part.getArgOrThrow(param2)
    val arg3 = part.getArgOrThrow(param3)
    val arg4 = part.getArgOrThrow(param4)
    return function(arg1, arg2, arg3, arg4)
  }
}

abstract class FunctionDeclaration(val name: String, val description: String) {
  abstract fun getParameters(): List<Schema<out Any?>>

  abstract suspend fun execute(part: FunctionCallPart): JSONObject
}

/**
 * Represents a parameter for a declared function
 *
 * @property name: The name of the parameter
 * @property description: The description of what the parameter should contain or represent
 * @property format: format information for the parameter, this can include bitlength in the case of
 *   int/float or keywords like "enum" for the string type
 * @property enum: contains the enum values for a string enum
 * @property type: contains the type info and parser
 * @property properties: if type is OBJECT, then this contains the description of the fields of the
 *   object by name
 * @property required: if type is OBJECT, then this contains the list of required keys
 * @property items: if the type is ARRAY, then this contains a description of the objects in the
 *   array
 */
class Schema<T>(
  val name: String,
  val description: String,
  val format: String? = null,
  val nullable: Boolean? = null,
  val enum: List<String>? = null,
  val properties: Map<String, Schema<out Any>>? = null,
  val required: List<String>? = null,
  val items: Schema<out Any>? = null,
  val type: FunctionType<T>,
) {
  fun fromString(value: String?) = type.parse(value)

  companion object {
    /** Registers a schema for an integer number */
    fun int(name: String, description: String) =
      Schema<Long>(
        name = name,
        description = description,
        type = FunctionType.INTEGER,
        nullable = false,
      )

    /** Registers a schema for a string */
    fun str(name: String, description: String) =
      Schema<String>(
        name = name,
        description = description,
        type = FunctionType.STRING,
        nullable = false,
      )

    /** Registers a schema for a boolean */
    fun bool(name: String, description: String) =
      Schema<Boolean>(
        name = name,
        description = description,
        type = FunctionType.BOOLEAN,
        nullable = false,
      )

    /** Registers a schema for a floating point number */
    fun num(name: String, description: String) =
      Schema<Double>(
        name = name,
        description = description,
        type = FunctionType.NUMBER,
        nullable = false,
      )

    /**
     * Registers a schema for a complex object. In a function it will be returned as a [JSONObject]
     */
    fun obj(name: String, description: String, vararg contents: Schema<out Any>) =
      Schema<JSONObject>(
        name = name,
        description = description,
        type = FunctionType.OBJECT,
        required = contents.map { it.name },
        properties = contents.associateBy { it.name }.toMap(),
        nullable = false,
      )

    /** Registers a schema for an array */
    fun arr(name: String, description: String, items: Schema<out Any>? = null) =
      Schema<List<String>>(
        name = name,
        description = description,
        type = FunctionType.ARRAY,
        items = items,
        nullable = false,
      )

    /** Registers a schema for an enum */
    fun enum(name: String, description: String, values: List<String>) =
      Schema<String>(
        name = name,
        description = description,
        format = "enum",
        enum = values,
        type = FunctionType.STRING,
        nullable = false,
      )
  }
}

fun defineFunction(name: String, description: String, function: suspend () -> JSONObject) =
  NoParameterFunction(name, description, function)

fun <T> defineFunction(
  name: String,
  description: String,
  arg1: Schema<T>,
  function: suspend (T) -> JSONObject,
) = OneParameterFunction(name, description, arg1, function)

fun <T, U> defineFunction(
  name: String,
  description: String,
  arg1: Schema<T>,
  arg2: Schema<U>,
  function: suspend (T, U) -> JSONObject,
) = TwoParameterFunction(name, description, arg1, arg2, function)

fun <T, U, W> defineFunction(
  name: String,
  description: String,
  arg1: Schema<T>,
  arg2: Schema<U>,
  arg3: Schema<W>,
  function: suspend (T, U, W) -> JSONObject,
) = ThreeParameterFunction(name, description, arg1, arg2, arg3, function)

fun <T, U, W, Z> defineFunction(
  name: String,
  description: String,
  arg1: Schema<T>,
  arg2: Schema<U>,
  arg3: Schema<W>,
  arg4: Schema<Z>,
  function: suspend (T, U, W, Z) -> JSONObject,
) = FourParameterFunction(name, description, arg1, arg2, arg3, arg4, function)

private fun <T> FunctionCallPart.getArgOrThrow(param: Schema<T>): T {
  return param.fromString(args[param.name])
    ?: throw RuntimeException(
      "Missing argument for parameter \"${param.name}\" for function \"$name\""
    )
}
