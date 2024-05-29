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

package com.google.gradle.plugins

import com.google.gradle.tasks.CopyFileTask
import com.google.gradle.util.apply
import com.google.gradle.util.file
import com.google.gradle.util.getReleaseClasses
import com.google.gradle.util.regularOutputFile
import com.google.gradle.util.tempFile
import kotlinx.validation.KotlinApiBuildTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register

typealias BuildApiTask = KotlinApiBuildTask

/**
 * A Gradle plugin for creating `.api` files; representing the public API of the project.
 *
 * Registers two tasks:
 * - `buildApi` -> creates a `.api` file containing the *current* public API of the project.
 * - `exportApi` -> exports the file generated by `buildApi` to a `public.api` file at the project
 *   directory
 *
 * @see ApiPluginExtension
 * @see ChangelogPluginExtension
 */
abstract class ApiPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    with(project) {
      val extension = extensions.create<ApiPluginExtension>("api").apply { commonConfiguration() }

      val buildApi = registerBuildApiTask()

      tasks.register<CopyFileTask>("exportApi") {
        source.set(buildApi.regularOutputFile)
        dest.set(extension.exportFile)
      }
    }
  }

  private fun Project.registerBuildApiTask() =
    tasks.register<BuildApiTask>("buildApi") {
      val classes = provider { getReleaseClasses() }

      inputClassesDirs = files(classes)
      inputDependencies = files(classes)
      outputApiDir = tempFile("api").get().asFile
    }

  context(Project)
  private fun ApiPluginExtension.commonConfiguration() {
    val latestApiFile = rootProject.layout.file("api/${project.name}/${project.version}.api")

    apiFile.convention(latestApiFile)
    exportFile.convention(project.layout.file("public.api"))
  }
}

/**
 * Extension properties for the [ApiPlugin].
 *
 * @property apiFile The file to reference to for the publicly released api.
 * @property exportFile The file to export the api to when running exportApi.
 */
abstract class ApiPluginExtension {
  @get:Optional abstract val apiFile: RegularFileProperty
  @get:Optional abstract val exportFile: RegularFileProperty
}

/**
 * Helper mapping to the [ApiPluginExtension].
 *
 * Automatically applies the [ApiPlugin] if not already present.
 */
val Project.apiPlugin: ApiPluginExtension
  get() {
    plugins.apply<ApiPlugin>()
    return extensions.getByType()
  }
