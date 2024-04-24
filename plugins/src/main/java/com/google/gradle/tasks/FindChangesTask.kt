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

import com.google.gradle.types.LinesChanged
import com.google.gradle.types.changedFrom
import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * A Gradle task to find the changes in the public api between the [old] and [new] api files.
 *
 * The changes are represented as [LinesChanged]; with a list of added and removed lines between the
 * files. These are saved in `.json` format in the [outputFile].
 *
 * @property old the previously released api file (typically `released.api)
 * @property new the current API pending release (generated from the state of the repo as is)
 * @property outputFile where to save the diff to
 */
abstract class FindChangesTask : DefaultTask() {
  @get:InputFile abstract val old: RegularFileProperty

  @get:InputFile abstract val new: RegularFileProperty

  @get:OutputFile abstract val outputFile: RegularFileProperty

  @TaskAction
  fun add() {
    val diff = old.asFile.get().changedFrom(new.asFile.get())

    diff.toFile(outputFile.asFile.get())
  }
}
