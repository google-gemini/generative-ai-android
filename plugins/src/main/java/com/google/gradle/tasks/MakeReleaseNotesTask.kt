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

import com.google.gradle.types.Changelog
import com.google.gradle.types.ModuleVersion
import com.google.gradle.types.VersionType
import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

/**
 * A Gradle task to make the release notes for a release.
 *
 * By default, saved in the root project's build directory under `release_notes.md`. The release
 * notes are a collection of all the messages provided by [changes][Changelog] before a release, in
 * a markdown list.
 *
 * The markdown file will also have a header representing the version to be released, bumped from
 * the provided [version] according to the highest impact [VersionType] from the list of changes.
 *
 * @property changeFiles the [Changelog] files to use in the release
 * @property version a [ModuleVersion] representing the current version of the project
 * @property outputFile where to save the serialized release notes to
 */
abstract class MakeReleaseNotesTask : DefaultTask() {
  @get:InputFiles abstract val changeFiles: ListProperty<File>

  @get:Input abstract val version: Property<ModuleVersion>

  @get:OutputFile abstract val outputFile: Property<File>

  @TaskAction
  fun add() {
    val changelogs = changeFiles.get().map { Changelog.fromFile(it) }

    val changes = changelogs.flatMap { it.changes }

    val bump = changelogs.minBy { it.type.ordinal }.type

    outputFile
      .get()
      .writeText(
        """
            |# ${version.get().bump(bump)}
            | 
            | - ${changes.joinToString("\n - ")}
        """
          .trimMargin()
      )
  }
}
