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

import com.google.gradle.tasks.FindChangesTask
import com.google.gradle.tasks.MakeChangeTask
import com.google.gradle.tasks.MakeReleaseNotesTask
import com.google.gradle.tasks.WarnAboutApiChangesTask
import com.google.gradle.types.Changelog
import com.google.gradle.types.RandomWordsGenerator
import com.google.gradle.util.apply
import com.google.gradle.util.buildDir
import com.google.gradle.util.childFile
import com.google.gradle.util.file
import com.google.gradle.util.moduleVersion
import com.google.gradle.util.orElseIfNotExists
import com.google.gradle.util.outputFile
import com.google.gradle.util.provideProperty
import com.google.gradle.util.tempFile
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Delete
import org.gradle.api.tasks.Optional
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.support.listFilesOrdered

/**
 * A Gradle plugin for managing and creating [Changelog] files.
 *
 * By default, a subdirectory under the root `.changes` directory that corresponds to the
 * [project name][Project.getName] will be used to save change files.
 *
 * Will also register the [ApiPlugin] if it's not already present, as it will handle the actual
 * generation of the API files.
 *
 * Registers the following tasks:
 * - `findChanges`
 * - `makeChange`
 * - `warnAboutApiChanges`
 * - `deleteChangeFiles`
 * - `makeReleaseNotes`
 *
 * You can learn more about these tasks by visiting their respective docs below.
 *
 * @see ChangelogPluginExtension
 * @see FindChangesTask
 * @see MakeChangeTask
 * @see WarnAboutApiChangesTask
 * @see MakeReleaseNotesTask
 */
abstract class ChangelogPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    with(project) {
      val extension =
        extensions.create<ChangelogPluginExtension>("changelog").apply { commonConfiguration() }

      val exportedApiFile = provider { file("public.api") }
      val releasedApiFile = exportedApiFile.orElseIfNotExists(apiPlugin.apiFile)
      val newApiFile = tasks.named("buildApi").outputFile

      val findChanges =
        tasks.register<FindChangesTask>("findChanges") {
          old.set(releasedApiFile)
          new.set(newApiFile)
          outputFile.set(tempFile("changes"))
        }

      val fileChanges = findChanges.outputFile

      tasks.register<MakeChangeTask>("makeChange") {
        val changeMessage = provideProperty<String>("changeMessage")
        val changeName = RandomWordsGenerator.generateString()
        val changeOutput = extension.outputDirectory.asFile.childFile("$changeName.json")

        changesFile.set(fileChanges)
        message.set(changeMessage)
        outputFile.set(changeOutput)
      }

      tasks.register<WarnAboutApiChangesTask>("warnAboutApiChanges") {
        changesFile.set(fileChanges)
        outputFile.set(extension.apiChangesDirectory.asFile)
      }

      val changelogFiles =
        extension.outputDirectory.map { it.asFile.listFilesOrdered { it.extension == "json" } }

      val deleteChangeFiles =
        tasks.register<Delete>("deleteChangeFiles") {
          group = "cleanup"

          delete(changelogFiles)
        }

      tasks.register<MakeReleaseNotesTask>("makeReleaseNotes") {
        onlyIf("No changelog files found") { changelogFiles.get().isNotEmpty() }

        changeFiles.set(changelogFiles)
        version.set(project.moduleVersion)
        // TODO() move to extension config with convention (like .changes)
        outputFile.set(extension.releaseNotesDirectory)

        finalizedBy(deleteChangeFiles)
      }
    }
  }

  context(Project)
  private fun ChangelogPluginExtension.commonConfiguration() {
    outputDirectory.convention(rootProject.layout.file(".changes/${project.name}"))
    releaseNotesDirectory.convention(rootProject.buildDir("release_notes/${project.name}.md"))
    apiChangesDirectory.convention(rootProject.buildDir("api_changes/${project.name}.md"))
  }
}

/**
 * Extension properties for the [ChangelogPlugin].
 *
 * @property outputDirectory The directory into which to store the [Changelog] files
 * @property releaseNotesDirectory The directory into which to store the release notes file
 * @property apiChangesDirectory The directory into which to store the api changes warning file
 */
abstract class ChangelogPluginExtension {
  @get:Optional abstract val outputDirectory: RegularFileProperty
  @get:Optional abstract val releaseNotesDirectory: RegularFileProperty
  @get:Optional abstract val apiChangesDirectory: RegularFileProperty
}

/**
 * Helper mapping to the [ApiPluginExtension].
 *
 * Automatically applies the [ApiPlugin] if not already present.
 */
private val Project.apiPlugin: ApiPluginExtension
  get() {
    plugins.apply<ApiPlugin>()
    return extensions.getByType()
  }
