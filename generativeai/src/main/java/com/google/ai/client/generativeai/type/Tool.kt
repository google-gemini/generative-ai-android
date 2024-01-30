package com.google.ai.client.generativeai.type

/**
 * Contains a set of function declarations that the model has access to. These can be used to gather
 * information, or complete tasks
 *
 * @param functionDeclarations The set of functions that this tool allows the model access to
 */
class Tool(
  val functionDeclarations: List<FunctionDeclaration>,
)
