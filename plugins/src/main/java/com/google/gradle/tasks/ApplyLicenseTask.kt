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

import com.google.gradle.types.LicenseTemplate
import com.google.gradle.util.submit
import java.io.File
import javax.inject.Inject
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkerExecutor

/**
 * A Gradle task to apply a license header to a set of files.
 *
 * This task is idempotent and will not add the license header if already present, and preserves
 * shebangs.
 *
 * Additionally, the files are processed in parallel through an injected [WorkerExecutor].
 *
 * @property files The files to which the license template will be applied.
 * @property template The [LicenseTemplate] to be applied to the files.
 */
abstract class ApplyLicenseTask @Inject constructor(private val workerExecutor: WorkerExecutor) :
  DefaultTask() {
  @get:InputFiles abstract val files: ListProperty<File>

  @get:Input abstract val template: Property<String>

  @TaskAction
  fun add() {
    val queue = workerExecutor.noIsolation()

    for (currentFile in files.get()) {
      queue.submit<ApplyLicenseWorkAction, ApplyLicenseWorkParameters> {
        file.set(currentFile)
        license.set(template)
      }
    }
  }
}

/**
 * Parameters for the [ApplyLicenseWorkAction].
 *
 * @property file the file to apply the [license] to.
 * @property license a [LicenseTemplate] to use in applying a license header.
 */
interface ApplyLicenseWorkParameters : WorkParameters {
  val file: Property<File>
  val license: Property<String>
}

/**
 * The work action to apply a license template to an individual file.
 *
 * @see ApplyLicenseWorkParameters
 * @see ApplyLicenseTask
 */
abstract class ApplyLicenseWorkAction : WorkAction<ApplyLicenseWorkParameters> {
  override fun execute() {
    val template = LicenseTemplate(parameters.license.get())
    val file = parameters.file.get()

    if (!template.matches(file)) {
      template.applyToFile(file)
      println("Applied license to file: ${file.name}")
    }
  }
}
