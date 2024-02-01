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
  override fun getParameters() = listOf<FunctionParameter>()
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
class OneParameterFunction(
  name: String,
  description: String,
  val param: FunctionParameter,
  val function: suspend (String) -> String,
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
class TwoParameterFunction(
  name: String,
  description: String,
  val param1: FunctionParameter,
  val param2: FunctionParameter,
  val function: suspend (String, String) -> String,
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
class ThreeParameterFunction(
  name: String,
  description: String,
  val param1: FunctionParameter,
  val param2: FunctionParameter,
  val param3: FunctionParameter,
  val function: suspend (String, String, String) -> String,
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
class FourParameterFunction(
  name: String,
  description: String,
  val param1: FunctionParameter,
  val param2: FunctionParameter,
  val param3: FunctionParameter,
  val param4: FunctionParameter,
  val function: suspend (String, String, String, String) -> String,
) : FunctionDeclaration(name, description) {
  override fun getParameters() = listOf(param1, param2, param3, param4)
}

@BetaGenAiAPI
abstract class FunctionDeclaration(
  val name: String,
  val description: String,
) {
  abstract fun getParameters(): List<FunctionParameter>
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

  fun param(param: FunctionParameter): OneFunctionBuilder {
    return OneFunctionBuilder(name, description, param)
  }
}

@BetaGenAiAPI
class OneFunctionBuilder(
  private val name: String,
  private val description: String,
  private val param1: FunctionParameter
) {
  fun build(function: suspend (String) -> String): FunctionDeclaration {
    return OneParameterFunction(name, description, param1, function)
  }

  fun param(param: FunctionParameter): TwoFunctionBuilder {
    return TwoFunctionBuilder(name, description, param1, param)
  }
}

@BetaGenAiAPI
class TwoFunctionBuilder(
  private val name: String,
  private val description: String,
  private val param1: FunctionParameter,
  private val param2: FunctionParameter,
) {
  fun build(function: suspend (String, String) -> String): FunctionDeclaration {
    return TwoParameterFunction(name, description, param1, param2, function)
  }

  fun param(param: FunctionParameter): ThreeFunctionBuilder {
    return ThreeFunctionBuilder(name, description, param1, param2, param)
  }
}

@BetaGenAiAPI
class ThreeFunctionBuilder(
  private val name: String,
  private val description: String,
  private val param1: FunctionParameter,
  private val param2: FunctionParameter,
  private val param3: FunctionParameter,
) {
  fun build(function: suspend (String, String, String) -> String): FunctionDeclaration {
    return ThreeParameterFunction(name, description, param1, param2, param3, function)
  }

  fun param(param: FunctionParameter): FourFunctionBuilder {
    return FourFunctionBuilder(name, description, param1, param2, param3, param)
  }
}

@BetaGenAiAPI
class FourFunctionBuilder(
  private val name: String,
  private val description: String,
  private val param1: FunctionParameter,
  private val param2: FunctionParameter,
  private val param3: FunctionParameter,
  private val param4: FunctionParameter,
) {
  fun build(function: suspend (String, String, String, String) -> String): FunctionDeclaration {
    return FourParameterFunction(name, description, param1, param2, param3, param4, function)
  }
}
