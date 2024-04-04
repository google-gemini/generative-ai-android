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

import com.google.gradle.tasks.CombineApiChangesTask
import com.google.gradle.tasks.CombineReleaseNotesTask
import com.google.gradle.util.asSingleProvider
import com.google.gradle.util.buildDir
import com.google.gradle.util.outputFile
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.register

/**
 * Root plugin for facilitating multi-project tasks.
 *
 * Registers three tasks:
 * - `warnAboutApiChanges` -> creates a single file representing the api changes for all child
 *   projects.
 * - `makeReleaseNotes` -> creates a single file representing the release notes for all child
 *   projects.
 * - `prepareRelease` -> performs all the steps necessary to prepare a release.
 *
 * To learn more about these tasks, you may want to read the documentation under the following
 * plugins and tasks:
 *
 * @see CombineApiChangesTask
 * @see CombineReleaseNotesTask
 * @see ReleasePlugin
 */
abstract class MultiProjectPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    with(project) {
      gradle.projectsEvaluated {
        tasks.register<CombineApiChangesTask>("warnAboutApiChanges") {
          val apiChangesTasks = subprojects.map { it.tasks.named("warnAboutApiChanges") }
          val files = apiChangesTasks.map { it.outputFile }.asSingleProvider()

          // TODO(b/332887819) - Remove when fixed
          dependsOn(apiChangesTasks)

          apiChangesFiles.set(files)
          outputFile.set(buildDir("api_changes.md"))
        }

        val makeReleaseNotesTask =
          tasks.register<CombineReleaseNotesTask>("makeReleaseNotes") {
            group = "publishing"

            val releaseNotesTasks = subprojects.map { it.tasks.named("makeReleaseNotes") }
            val files = releaseNotesTasks.map { it.outputFile }.asSingleProvider()

            // TODO(b/332887819) - Remove when fixed
            dependsOn(releaseNotesTasks)

            releaseNoteFiles.set(files)
            outputFile.set(buildDir("release_notes.md"))
          }

        tasks.register("prepareRelease") {
          group = "publishing"

          val prepareReleaseTasks = subprojects.map { it.tasks.named("prepareRelease") }

          dependsOn(prepareReleaseTasks, makeReleaseNotesTask)
        }
      }
    }
  }
}
