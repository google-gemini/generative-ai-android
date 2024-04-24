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

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * Copies a file (or directory) from one place to another.
 *
 * An alternative to the standard [Copy] task provided by gradle; that allows better interop with
 * providers, caching, directories, and individual files.
 *
 * If the file is a directory, all of its contents will be copied alongside it.
 *
 * ***If there is already a file or directory present at the destination, its contents will be
 * overwritten.***
 *
 * @property source the file or directory to copy from
 * @property dest where to copy the file or directory to
 */
abstract class CopyFileTask : DefaultTask() {
  @get:InputFile abstract val source: RegularFileProperty

  @get:OutputFile abstract val dest: RegularFileProperty

  @TaskAction
  fun create() {
    source.get().asFile.copyRecursively(dest.asFile.get(), overwrite = true)
  }
}
