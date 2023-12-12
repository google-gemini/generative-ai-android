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

package com.google.gradle.types

import com.google.gradle.util.readResourceFile
import org.gradle.kotlin.dsl.provideDelegate

/**
 * Object for generating a sequence of random words from a preset list.
 *
 * Useful in avoiding file collisions, while still easily consumable file names.
 *
 * @see generate
 * @see generateString
 */
object RandomWordsGenerator {

  /**
   * Generates a random list of words.
   *
   * @param wordCount how many words to generate
   */
  fun generate(wordCount: Int = 4): List<String> = (1..wordCount).map { words.random() }

  /**
   * Generates a file name safe string representation of a random list of words.
   *
   * The words will be separated by a `"-"`
   *
   * @param wordCount how many words to generate
   */
  fun generateString(wordCount: Int = 4): String = generate(wordCount).joinToString("-")

  private val words by lazy { readResourceFile("/words.txt").filter { it.isNotBlank() } }
}
