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

package com.google.gradle.tasks

import com.google.gradle.types.Changelog
import com.google.gradle.types.LinesChanged
import com.google.gradle.types.RandomWordsGenerator
import com.google.gradle.types.VersionType
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * A Gradle task to create [Changelog] files.
 *
 * By default, the output is a `.json` file with a random sequence of four words as the file name
 * (to avoid collisions). This file will contain the impact of the changes ([VersionType] wise), and
 * optionally the message provided; otherwise absent.
 *
 * @property changesFile a file contained a [LinesChanged]; representing the changes made in this
 *   repo
 * @property message an optional string message to provide to the end user in the compiled
 *   [Changelog] file; to be displayed at release time in the release notes.
 * @property outputFile where to save the serialized [Changelog] to
 * @see [RandomWordsGenerator]
 */
abstract class MakeChangeTask : DefaultTask() {
  @get:InputFile abstract val changesFile: RegularFileProperty

  @get:[Optional Input]
  abstract val message: Property<String>

  @get:OutputFile abstract val outputFile: RegularFileProperty

  @TaskAction
  fun add() {
    val diff = LinesChanged.fromFile(changesFile.asFile.get())
    val changelog = Changelog(diff.bump, listOfNotNull(message.orNull))

    changelog.toFile(outputFile.asFile.get())
  }
}
