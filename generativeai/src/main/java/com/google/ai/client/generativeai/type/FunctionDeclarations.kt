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

/**
 * A declared function, including implementation, that a model can be given access to in order to
 * gain info or complete tasks.
 *
 * @property name The name of the function call, this should be clear and descriptive for the model
 * @property description A description of what the function does and its output.
 * @property function the function implementation
 */
@BetaGenAiAPI
class NoParameterFunction(
  name: String,
  description: String,
  val function: suspend () -> String,
) : FunctionDeclaration(name, description) {
  override fun getParameters() = listOf<FunctionParameter<Any>>()
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
@BetaGenAiAPI
class OneParameterFunction<T>(
  name: String,
  description: String,
  val param: FunctionParameter<T>,
  val function: suspend (T) -> String,
) : FunctionDeclaration(name, description) {
  override fun getParameters() = listOf(param)
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
@BetaGenAiAPI
class TwoParameterFunction<T, U>(
  name: String,
  description: String,
  val param1: FunctionParameter<T>,
  val param2: FunctionParameter<U>,
  val function: suspend (T, U) -> String,
) : FunctionDeclaration(name, description) {
  override fun getParameters() = listOf(param1, param2)
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
@BetaGenAiAPI
class ThreeParameterFunction<T, U, V>(
  name: String,
  description: String,
  val param1: FunctionParameter<T>,
  val param2: FunctionParameter<U>,
  val param3: FunctionParameter<V>,
  val function: suspend (T, U, V) -> String,
) : FunctionDeclaration(name, description) {
  override fun getParameters() = listOf(param1, param2, param3)
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
@BetaGenAiAPI
class FourParameterFunction<T, U, V, W>(
  name: String,
  description: String,
  val param1: FunctionParameter<T>,
  val param2: FunctionParameter<U>,
  val param3: FunctionParameter<V>,
  val param4: FunctionParameter<W>,
  val function: suspend (T, U, V, W) -> String,
) : FunctionDeclaration(name, description) {
  override fun getParameters() = listOf(param1, param2, param3, param4)
}

@BetaGenAiAPI
abstract class FunctionDeclaration(
  val name: String,
  val description: String,
) {
  abstract fun getParameters(): List<FunctionParameter<out Any?>>
}

/**
 * A builder to help build [FunctionDeclaration] objects
 *
 * @property name The name of the function call, this should be clear and descriptive for the model
 * @property description A description of what the function does and its output.
 */
@BetaGenAiAPI
class FunctionBuilder(private val name: String, private val description: String) {

  fun build(function: suspend () -> String): FunctionDeclaration {
    return NoParameterFunction(name, description, function)
  }

  fun <T> param(param: FunctionParameter<T>): OneFunctionBuilder<T> {
    return OneFunctionBuilder<T>(name, description, param)
  }

  fun <T> param(
    paramName: String,
    paramDescription: String,
    type: FunctionType<T>
  ): OneFunctionBuilder<T> {
    return OneFunctionBuilder<T>(
      name,
      description,
      FunctionParameter(paramName, paramDescription, type)
    )
  }

  fun stringParam(paramName: String, paramDescription: String): OneFunctionBuilder<String> {
    return OneFunctionBuilder(
      name,
      description,
      FunctionParameter(paramName, paramDescription, FunctionType.STRING)
    )
  }

  fun intParam(paramName: String, paramDescription: String): OneFunctionBuilder<Int> {
    return OneFunctionBuilder(
      name,
      description,
      FunctionParameter(paramName, paramDescription, FunctionType.INT)
    )
  }

  fun boolParam(paramName: String, paramDescription: String): OneFunctionBuilder<Boolean> {
    return OneFunctionBuilder(
      name,
      description,
      FunctionParameter(paramName, paramDescription, FunctionType.BOOLEAN)
    )
  }
}

@BetaGenAiAPI
class OneFunctionBuilder<T>(
  private val name: String,
  private val description: String,
  private val param1: FunctionParameter<T>
) {
  fun build(function: suspend (T) -> String): FunctionDeclaration {
    return OneParameterFunction(name, description, param1, function)
  }

  fun <U> param(param: FunctionParameter<U>): TwoFunctionBuilder<T, U> {
    return TwoFunctionBuilder(name, description, param1, param)
  }

  fun <U> param(
    paramName: String,
    paramDescription: String,
    type: FunctionType<U>
  ): TwoFunctionBuilder<T, U> {
    return TwoFunctionBuilder(
      name,
      description,
      param1,
      FunctionParameter(paramName, paramDescription, type)
    )
  }

  fun stringParam(paramName: String, paramDescription: String): TwoFunctionBuilder<T, String> {
    return TwoFunctionBuilder(
      name,
      description,
      param1,
      FunctionParameter(paramName, paramDescription, FunctionType.STRING)
    )
  }

  fun intParam(paramName: String, paramDescription: String): TwoFunctionBuilder<T, Int> {
    return TwoFunctionBuilder(
      name,
      description,
      param1,
      FunctionParameter(paramName, paramDescription, FunctionType.INT)
    )
  }

  fun boolParam(paramName: String, paramDescription: String): TwoFunctionBuilder<T, Boolean> {
    return TwoFunctionBuilder(
      name,
      description,
      param1,
      FunctionParameter(paramName, paramDescription, FunctionType.BOOLEAN)
    )
  }
}

@BetaGenAiAPI
class TwoFunctionBuilder<T, U>(
  private val name: String,
  private val description: String,
  private val param1: FunctionParameter<T>,
  private val param2: FunctionParameter<U>,
) {
  fun build(function: suspend (T, U) -> String): FunctionDeclaration {
    return TwoParameterFunction(name, description, param1, param2, function)
  }

  fun <V> param(param: FunctionParameter<V>): ThreeFunctionBuilder<T, U, V> {
    return ThreeFunctionBuilder(name, description, param1, param2, param)
  }

  fun <V> param(
    paramName: String,
    paramDescription: String,
    type: FunctionType<V>
  ): ThreeFunctionBuilder<T, U, V> {
    return ThreeFunctionBuilder(
      name,
      description,
      param1,
      param2,
      FunctionParameter(paramName, paramDescription, type)
    )
  }

  fun stringParam(paramName: String, paramDescription: String): ThreeFunctionBuilder<T, U, String> {
    return ThreeFunctionBuilder(
      name,
      description,
      param1,
      param2,
      FunctionParameter(paramName, paramDescription, FunctionType.STRING)
    )
  }

  fun intParam(paramName: String, paramDescription: String): ThreeFunctionBuilder<T, U, Int> {
    return ThreeFunctionBuilder(
      name,
      description,
      param1,
      param2,
      FunctionParameter(paramName, paramDescription, FunctionType.INT)
    )
  }

  fun boolParam(paramName: String, paramDescription: String): ThreeFunctionBuilder<T, U, Boolean> {
    return ThreeFunctionBuilder(
      name,
      description,
      param1,
      param2,
      FunctionParameter(paramName, paramDescription, FunctionType.BOOLEAN)
    )
  }
}

@BetaGenAiAPI
class ThreeFunctionBuilder<T, U, V>(
  private val name: String,
  private val description: String,
  private val param1: FunctionParameter<T>,
  private val param2: FunctionParameter<U>,
  private val param3: FunctionParameter<V>,
) {
  fun build(function: suspend (T, U, V) -> String): FunctionDeclaration {
    return ThreeParameterFunction(name, description, param1, param2, param3, function)
  }

  fun <W> param(param: FunctionParameter<W>): FourFunctionBuilder<T, U, V, W> {
    return FourFunctionBuilder(name, description, param1, param2, param3, param)
  }

  fun <W> param(
    paramName: String,
    paramDescription: String,
    type: FunctionType<W>
  ): FourFunctionBuilder<T, U, V, W> {
    return FourFunctionBuilder(
      name,
      description,
      param1,
      param2,
      param3,
      FunctionParameter(paramName, paramDescription, type)
    )
  }

  fun stringParam(
    paramName: String,
    paramDescription: String
  ): FourFunctionBuilder<T, U, V, String> {
    return FourFunctionBuilder(
      name,
      description,
      param1,
      param2,
      param3,
      FunctionParameter(paramName, paramDescription, FunctionType.STRING)
    )
  }

  fun intParam(paramName: String, paramDescription: String): FourFunctionBuilder<T, U, V, Int> {
    return FourFunctionBuilder(
      name,
      description,
      param1,
      param2,
      param3,
      FunctionParameter(paramName, paramDescription, FunctionType.INT)
    )
  }

  fun boolParam(
    paramName: String,
    paramDescription: String
  ): FourFunctionBuilder<T, U, V, Boolean> {
    return FourFunctionBuilder(
      name,
      description,
      param1,
      param2,
      param3,
      FunctionParameter(paramName, paramDescription, FunctionType.BOOLEAN)
    )
  }
}

@BetaGenAiAPI
class FourFunctionBuilder<T, U, V, W>(
  private val name: String,
  private val description: String,
  private val param1: FunctionParameter<T>,
  private val param2: FunctionParameter<U>,
  private val param3: FunctionParameter<V>,
  private val param4: FunctionParameter<W>,
) {
  fun build(function: suspend (T, U, V, W) -> String): FunctionDeclaration {
    return FourParameterFunction(name, description, param1, param2, param3, param4, function)
  }
}
