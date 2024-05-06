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

package com.google.gradle.tasks

import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * A Gradle task to combine multiple release notes into one.
 *
 * The [outputFile] will contain the provided [releaseNoteFiles] mapped to appropriate markdown
 * headers, following the format of:
 * ```
 * # {LIBRARY_NAME}
 *
 * ## {RELEASING_VERSION_FOR_LIBRARY}
 *
 * - {CHANGES}
 * ```
 *
 * @property releaseNoteFiles the release notes to combine
 * @property outputFile where to save combined release notes to
 */
abstract class CombineReleaseNotesTask : DefaultTask() {
  @get:InputFiles abstract val releaseNoteFiles: ListProperty<File>

  @get:OutputFile abstract val outputFile: RegularFileProperty

  @TaskAction
  fun add() {
    val projectNameToReleaseNotes =
      releaseNoteFiles.get().filter { it.exists() }.map { it.nameWithoutExtension to it.readText() }

    val texts =
      projectNameToReleaseNotes.map {
        """
        | # ${it.first}
        | 
        | #${it.second}
      """
          .trimMargin()
      }

    outputFile.get().asFile.writeText(texts.joinToString("\n\n"))
  }
}
