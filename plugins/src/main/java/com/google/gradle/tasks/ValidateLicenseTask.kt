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

import com.google.gradle.types.LicenseTemplate
import com.google.gradle.util.submit
import java.io.File
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.StopExecutionException
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor

/**
 * A Gradle task to validate that a license header in present in a set of files.
 *
 * The files are processed in parallel through an injected [WorkerExecutor].
 *
 * When a file is found to be missing the license header, it will throw a [StopExecutionException]
 * with the file path. Since the files are evaluated in parallel- one file missing the license
 * header will not cause the other files to not be processed.
 *
 * @property files The files to check for a license header.
 * @property template A [LicenseTemplate] to use for checking if a license header is present.
 */
abstract class ValidateLicenseTask @Inject constructor(private val workerExecutor: WorkerExecutor) :
  DefaultTask() {
  @get:InputFiles abstract val files: ListProperty<File>

  @get:Input abstract val template: Property<String>

  @TaskAction
  fun add() {
    val queue = workerExecutor.noIsolation()

    for (currentFile in files.get()) {
      queue.submit<ValidateLicenseWorkAction, ValidateLicenseWorkParameters> {
        file.set(currentFile)
        license.set(template)
      }
    }
  }
}

/**
 * Parameters for the [ValidateLicenseWorkAction].
 *
 * @property file the file to check for a license header in.
 * @property license a [LicenseTemplate] to use for checking if a license header is present.
 */
interface ValidateLicenseWorkParameters : WorkParameters {
  val file: Property<File>
  val license: Property<String>
}

/**
 * The work action to verify a license header is present in an individual file.
 *
 * @throws StopExecutionException if a file is missing a license header
 * @see ValidateLicenseWorkParameters
 * @see ValidateLicenseTask
 */
abstract class ValidateLicenseWorkAction : WorkAction<ValidateLicenseWorkParameters> {
  override fun execute() {
    val template = LicenseTemplate(parameters.license.get())
    val file = parameters.file.get()

    if (!template.matches(file)) {
      throw StopExecutionException("File is missing a license header: ${file.canonicalPath}")
    }
  }
}
