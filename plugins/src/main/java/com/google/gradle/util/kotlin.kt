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

package com.google.gradle.util

import java.io.File

/* Replaces all matching substrings with an empty string (nothing) */
fun String.remove(regex: Regex) = replace(regex, "")

/**
 * Conditionally drops the first element of the list if the provided value is null.
 *
 * @param T The type of elements in the list.
 * @param V The type of the value to be checked.
 * @param value The value to check against.
 */
fun <T, V> List<T>.dropIf(value: V?) = takeUnless { value != null } ?: drop(1)

/**
 * Appends a list of strings to a [StringBuilder], each on a new line.
 *
 * @param lines A list of strings to be appended.
 */
fun StringBuilder.appendLines(lines: List<String>) = appendLine(lines.joinToString("\n"))

/** Reads the text from a file in the resource directory. */
fun readResourceFile(path: String): List<String> {
  return object {}.javaClass.getResourceAsStream(path)?.bufferedReader()?.use { it.readLines() }
    ?: throw RuntimeException("Couldn't find resource file: $path")
}

/** Reads the text from a file if it exists, otherwise returns null. */
fun File.readTextOrNull(): String? = takeIf { exists() }?.readText()
