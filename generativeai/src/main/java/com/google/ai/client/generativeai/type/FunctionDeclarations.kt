/*
 * Copyright 2023 Google LLC
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
@GenerativeBeta
class NoParameterFunction(
  name: String,
  description: String,
  val function: suspend () -> JSONObject,
) : FunctionDeclaration(name, description) {
  override fun getParameters() = listOf<ParameterDeclaration<Any>>()

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
@GenerativeBeta
class OneParameterFunction<T>(
  name: String,
  description: String,
  val param: ParameterDeclaration<T>,
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
@GenerativeBeta
class TwoParameterFunction<T, U>(
  name: String,
  description: String,
  val param1: ParameterDeclaration<T>,
  val param2: ParameterDeclaration<U>,
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
@GenerativeBeta
class ThreeParameterFunction<T, U, V>(
  name: String,
  description: String,
  val param1: ParameterDeclaration<T>,
  val param2: ParameterDeclaration<U>,
  val param3: ParameterDeclaration<V>,
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
@GenerativeBeta
class FourParameterFunction<T, U, V, W>(
  name: String,
  description: String,
  val param1: ParameterDeclaration<T>,
  val param2: ParameterDeclaration<U>,
  val param3: ParameterDeclaration<V>,
  val param4: ParameterDeclaration<W>,
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

@GenerativeBeta
abstract class FunctionDeclaration(val name: String, val description: String) {
  abstract fun getParameters(): List<ParameterDeclaration<out Any?>>

  abstract suspend fun execute(part: FunctionCallPart): JSONObject
}

class ParameterDeclaration<T>(
  val name: String,
  val description: String,
  val format: String? = null,
  val enum: List<String>? = null,
  val type: FunctionType<T>,
) {
  fun fromString(value: String?) = type.parse(value)

  companion object {
    fun int(name: String, description: String) =
      ParameterDeclaration<Long>(name, description, null, null, FunctionType.INTEGER)

    fun str(name: String, description: String) =
      ParameterDeclaration<String>(name, description, null, null, FunctionType.STRING)

    fun bool(name: String, description: String) =
      ParameterDeclaration<Boolean>(name, description, null, null, FunctionType.BOOLEAN)

    fun num(name: String, description: String) =
      ParameterDeclaration<Double>(name, description, null, null, FunctionType.NUMBER)

    fun obj(name: String, description: String) =
      ParameterDeclaration<JSONObject>(name, description, null, null, FunctionType.OBJECT)

    fun arr(name: String, description: String) =
      ParameterDeclaration<List<String>>(name, description, null, null, FunctionType.ARRAY)

    fun enum(name: String, description: String, values: List<String>) =
      ParameterDeclaration<String>(name, description, "enum", values, FunctionType.STRING)
  }
}

@GenerativeBeta
fun defineFunction(name: String, description: String, function: suspend () -> JSONObject) =
  NoParameterFunction(name, description, function)

@GenerativeBeta
fun <T> defineFunction(
  name: String,
  description: String,
  arg1: ParameterDeclaration<T>,
  function: suspend (T) -> JSONObject,
) = OneParameterFunction(name, description, arg1, function)

@GenerativeBeta
fun <T, U> defineFunction(
  name: String,
  description: String,
  arg1: ParameterDeclaration<T>,
  arg2: ParameterDeclaration<U>,
  function: suspend (T, U) -> JSONObject,
) = TwoParameterFunction(name, description, arg1, arg2, function)

@GenerativeBeta
fun <T, U, W> defineFunction(
  name: String,
  description: String,
  arg1: ParameterDeclaration<T>,
  arg2: ParameterDeclaration<U>,
  arg3: ParameterDeclaration<W>,
  function: suspend (T, U, W) -> JSONObject,
) = ThreeParameterFunction(name, description, arg1, arg2, arg3, function)

@GenerativeBeta
fun <T, U, W, Z> defineFunction(
  name: String,
  description: String,
  arg1: ParameterDeclaration<T>,
  arg2: ParameterDeclaration<U>,
  arg3: ParameterDeclaration<W>,
  arg4: ParameterDeclaration<Z>,
  function: suspend (T, U, W, Z) -> JSONObject,
) = FourParameterFunction(name, description, arg1, arg2, arg3, arg4, function)

private fun <T> FunctionCallPart.getArgOrThrow(param: ParameterDeclaration<T>): T {
  return param.fromString(args[param.name])
    ?: throw RuntimeException(
      "Missing argument for parameter \"${param.name}\" for function \"$name\""
    )
}
