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

import com.google.gradle.types.VersionType.*
import com.google.gradle.util.spoiler
import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * A Gradle task to combine multiple api changes into one.
 *
 * The [outputFile] will contain the provided [apiChangesFiles] mapped to spoilers for each.
 *
 * @property apiChangesFiles the api changes to combine
 * @property outputFile where to save combined api changes to
 */
abstract class CombineApiChangesTask : DefaultTask() {
  @get:InputFiles abstract val apiChangesFiles: ListProperty<File>

  @get:OutputFile abstract val outputFile: RegularFileProperty

  @TaskAction
  fun add() {
    val projectNameToChangeFile =
      apiChangesFiles.get().map { it.nameWithoutExtension to it.readText() }

    val texts = projectNameToChangeFile.joinToString("\n\n") { spoiler(it.first, it.second) }

    outputFile.get().asFile.writeText(texts)
  }
}
