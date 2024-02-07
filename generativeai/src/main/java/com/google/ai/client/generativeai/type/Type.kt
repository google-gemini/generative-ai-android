package com.google.ai.client.generativeai.type

class FunctionType<T>(val name: String, val parse: (String?) -> T?) {
  companion object {
    val STRING = FunctionType<String>("STRING") { it }
    val INT = FunctionType<Int>("INTEGER") { it?.toIntOrNull() }
    val BOOLEAN = FunctionType<Boolean>("BOOLEAN") { it?.toBoolean() }
  }
}
