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

import com.google.gradle.util.readTextOrNull
import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Serializable representation of changes made to a file.
 *
 * Typically used for `.api` files in comparing api changes.
 *
 * @property added list of strings representing new lines that were added
 * @property removed list of strings representing old lines that were deleted
 * @property bump the version bump type calculated based on the changes.
 * @see toFile
 * @see fromFile
 */
@Serializable
data class LinesChanged(val added: List<String>, val removed: List<String>) {
  val bump = calculateBumpType()

  /**
   * Saves this instance a given file in JSON format.
   *
   * @param file the file to write the data to
   */
  fun toFile(file: File) = file.writeText(Json.encodeToString(this))

  private fun calculateBumpType(): VersionType {
    if (removed.isNotEmpty()) return VersionType.MAJOR
    if (added.isNotEmpty()) return VersionType.MINOR

    return VersionType.PATCH
  }

  companion object {

    /**
     * Parses a [LinesChanged] object from a `.json` file.
     *
     * If the file doesn't exist, or does not have any content- a [LinesChanged] with empty lists
     * will be returned.
     *
     * @param file a `.json` file to parse from
     */
    fun fromFile(file: File): LinesChanged {
      val content = file.readTextOrNull()

      if (content.isNullOrBlank()) return LinesChanged(emptyList(), emptyList())

      return Json.decodeFromString(content)
    }
  }
}

/**
 * Compares the content of this file with another file and determines the changes as [LinesChanged].
 *
 * @param other the file to compare against
 * @receiver File the file whose changes are to be determined from
 */
fun File.changedFrom(other: File): LinesChanged {
  val myLines = readLines().filter { it.isNotBlank() }
  val otherLines = other.readLines().filter { it.isNotBlank() }

  val added = otherLines - myLines.toSet()
  val removed = myLines - otherLines.toSet()

  return LinesChanged(added, removed)
}
