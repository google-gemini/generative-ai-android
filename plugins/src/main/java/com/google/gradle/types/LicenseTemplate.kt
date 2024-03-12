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

package com.google.gradle.types

import com.google.gradle.util.appendLines
import com.google.gradle.util.dropIf
import com.google.gradle.util.remove
import java.io.File
import java.time.LocalDate

/**
 * Represents a license header template.
 *
 * License templates can have variables in the form of `${VARIABLE}`.
 *
 * When [matching a file][matches] to see if it has a license header, all variables are ignored.
 *
 * When [applying the license to a file][applyToFile], all variables are removed with the exception
 * for any `${YEAR}` variables- which will be replaced with the current year provided by
 * [LocalDate].
 *
 * @property template string template to use for matching license headers.
 * @see matches
 * @see applyToFile
 */
data class LicenseTemplate(val template: String) {

  /** [Regex] pattern to identify template variables. */
  private val variableRegex = """\$\{.*?}""".toRegex()

  /** Compiled variant of [template] as a [Regex] pattern. */
  private val pattern: Regex = makePattern()

  /**
   * Checks if the content of a file matches the pattern derived from the template.
   *
   * @param other The file whose content is to be matched against the pattern.
   */
  fun matches(other: File) = pattern.containsMatchIn(other.readText())

  /**
   * Applies the license template to a file. Preserves the shebang line if present and appends the
   * processed template.
   *
   * @param file The file to which the template is to be applied.
   */
  fun applyToFile(file: File) {
    val lines = file.readLines()

    val shebang = lines.firstOrNull()?.takeIf { it.startsWith("#!") }
    val contentWithoutShebang = lines.dropIf(shebang)

    file.writeText(
      buildString {
        shebang?.let { appendLine(it) }
        appendLine(prepareTemplate())
        appendLines(contentWithoutShebang)
      }
    )
  }

  /**
   * Prepares the [template] for usage in an actual file.
   *
   * The only feature we currently support is variable substitution, as described in
   * [LicenseTemplate].
   */
  private fun prepareTemplate() =
    template.replace("\${YEAR}", LocalDate.now().year.toString()).remove(variableRegex)

  /**
   * Converts [template] into a [Regex] pattern to be used for matching.
   *
   * It does this by splitting the text around any template variables (`${}`), replacing them with
   * regex to match 0 or more words (`\w*`), and then joining the rest of the text around them as
   * escaped regex.
   *
   * For example, given the input:
   * ```
   * Hello ${}, and good ${TIME}!
   * ```
   *
   * It would create the following pattern:
   * ```
   * \QHello \E\w*\Q, and good \E\w*\Q!\E
   * ```
   *
   * With `\Q` and `\E` wrapping string literals.
   */
  private fun makePattern(): Regex {
    val parts = template.split(variableRegex)
    val pattern = parts.joinToString("\\w*") { Regex.escape(it) }
    return pattern.toRegex()
  }
}
