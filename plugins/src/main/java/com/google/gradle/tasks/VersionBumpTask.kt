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

import com.google.gradle.types.ModuleVersion
import com.google.gradle.util.rewriteLines
import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.provideDelegate

/**
 * Bumps the `version` property of the specified [versionFile].
 *
 * Alternatively can be used to set the version directly, by specifying a [newVersion].
 *
 * @property versionFile A [File] that contains the `version` property. Defaults to the
 *   `gradle.properties` file at the project's root.
 * @property newVersion A [ModuleVersion] of what to set to. Defaults to the project version.
 */
abstract class VersionBumpTask : DefaultTask() {
  @get:[Optional InputFile]
  abstract val versionFile: Property<File>

  @get:[Optional Input]
  abstract val newVersion: Property<ModuleVersion>

  init {
    configure()
  }

  @TaskAction
  fun build() {
    if (newVersion.get().major > 0)
      throw RuntimeException("You're trying to bump the major version. This is a no 1.0+ zone!!")

    versionFile.get().rewriteLines {
      when {
        it.startsWith("version=") -> "version=${newVersion.get()}"
        else -> it
      }
    }
  }

  private fun configure() {
    versionFile.convention(project.file("gradle.properties"))
    newVersion.convention(computeVersionBump())
  }

  private fun computeVersionBump(): ModuleVersion? {
    val version: String? by project

    return version?.let { ModuleVersion.fromStringOrNull(it)?.bump() }
  }
}
