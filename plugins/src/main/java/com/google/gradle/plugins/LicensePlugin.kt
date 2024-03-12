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

package com.google.gradle.plugins

import com.google.gradle.tasks.ApplyLicenseTask
import com.google.gradle.tasks.ValidateLicenseTask
import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.register

/**
 * A Gradle plugin for managing and applying license headers to source files.
 *
 * By default, all files matching the `src/**/*.kt` file path pattern will be processed.
 *
 * Registers two tasks:
 * - `validateLicense`
 * - `applyLicense`
 *
 * You can learn more about these tasks by visiting their respective docs below.
 *
 * @see LicensePluginExtension
 * @see ValidateLicenseTask
 * @see ApplyLicenseTask
 */
abstract class LicensePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    with(project) {
      val extension =
        extensions.create<LicensePluginExtension>("license").apply { commonConfiguration() }

      val licenseTemplate = extension.template.map { it.readText() }
      val inputFiles =
        extension.include.map {
          fileTree(".") {
              include(it)
              exclude(extension.exclude.get())
            }
            .files
        }

      tasks.register<ValidateLicenseTask>("validateLicense") {
        group = "licensing"
        files.set(inputFiles)
        template.set(licenseTemplate)
      }

      tasks.register<ApplyLicenseTask>("applyLicense") {
        group = "licensing"
        files.set(inputFiles)
        template.set(licenseTemplate)
      }
    }
  }

  private fun LicensePluginExtension.commonConfiguration() {
    include.convention(listOf("src/**/*.kt"))
    exclude.convention(listOf())
  }
}

/**
 * Extension properties for the [LicensePlugin].
 *
 * @property include Files to include in processing. Can be a file path pattern.
 * @property exclude Files to exclude from processing. Can be a file path pattern.
 */
abstract class LicensePluginExtension {
  @get:Optional abstract val include: ListProperty<String>
  @get:Optional abstract val exclude: ListProperty<String>
  @get:Optional abstract val template: Property<File>
}
