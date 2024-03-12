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

import java.io.File
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * A sequence of changes, to be displayed to the end user after a release.
 *
 * @property type the version bump implications this change has the project, represented as a
 *   [VersionType]
 * @property changes a list of changes made; to be displayed in the release notes
 * @see toFile
 * @see fromFile
 */
@Serializable
data class Changelog(val type: VersionType, val changes: List<String>) {

  /**
   * Saves this instance a given file in JSON format.
   *
   * @param file the file to write the data to
   */
  fun toFile(file: File) = file.writeText(Json.encodeToString(this) + '\n')

  companion object {

    /**
     * Parses a [Changelog] object from a `.json` file.
     *
     * @param file a `.json` file to parse from
     */
    fun fromFile(file: File): Changelog = Json.decodeFromString(file.readText())
  }
}
