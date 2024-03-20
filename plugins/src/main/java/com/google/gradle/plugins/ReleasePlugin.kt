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
import com.google.gradle.tasks.MakeReleaseNotesTask
import com.google.gradle.tasks.VersionBumpTask
import com.google.gradle.types.ModuleVersion
import com.google.gradle.util.moduleVersion
import com.google.gradle.util.outputFile
import com.google.gradle.util.readFirstLine
import java.io.File
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.provideDelegate
import org.gradle.kotlin.dsl.register

/**
 * A Gradle plugin for preparing a release.
 *
 * Intended to be ran before running `publishAllPublicationsToMavenRepository`.
 *
 * Registers three tasks:
 * - `updateVersion` -> updates the project version declared in `gradle.properties` file, according
 *   to the release notes.
 * - `createNewApiFile` -> creates a new `.api` file in the `api` directory for the release,
 *   aligning with the current state of the public api; for future auditing.
 * - `prepareRelease` -> does everything needed to prepare a release; creates the release notes and
 *   runs the above tasks.
 *
 * If any of these tasks are ran without changelog files present, the current version declared in
 * the `gradle.properties` file will be used instead.
 *
 * @see ApiPluginExtension
 * @see ChangelogPluginExtension
 */
abstract class ReleasePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    with(project) {
      val buildApi = tasks.named<BuildApiTask>("buildApi")
      val makeReleaseNotes = tasks.named<MakeReleaseNotesTask>("makeReleaseNotes")

      val releaseNotes = makeReleaseNotes.outputFile
      val releasingVersion =
        releaseNotes.map { parseReleaseVersion(it) }.orElse(project.moduleVersion)

      val updateVersion =
        tasks.register<VersionBumpTask>("updateVersion") { newVersion.set(releasingVersion) }

      val createNewApiFile =
        tasks.register<CopyFileTask>("createNewApiFile") {
          val newApiFile = releasingVersion.map { rootProject.file("api/$it.api") }

          source.set(buildApi.outputFile)
          dest.set(newApiFile)
        }

      tasks.register("prepareRelease") {
        group = "publishing"

        dependsOn(makeReleaseNotes, updateVersion, createNewApiFile)
      }
    }
  }

  private fun parseReleaseVersion(releaseNotes: File): ModuleVersion {
    val version = releaseNotes.readFirstLine().substringAfter("#").trim()

    return ModuleVersion.fromStringOrNull(version)
      ?: throw RuntimeException("Invalid release notes version found")
  }
}
