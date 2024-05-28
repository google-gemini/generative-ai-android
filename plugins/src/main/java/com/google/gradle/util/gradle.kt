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

package com.google.gradle.util

import com.android.build.api.dsl.KotlinMultiplatformAndroidExtension
import com.google.gradle.types.ModuleVersion
import java.io.File
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.PluginContainer
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.StopActionException
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.findByType
import org.gradle.kotlin.dsl.getByType
import org.gradle.workers.WorkAction
import org.gradle.workers.WorkParameters
import org.gradle.workers.WorkQueue
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmAndroidCompilation
import org.jetbrains.kotlin.gradle.utils.provider

/** Creates a [RegularFile] [Provider] mapped for the build directory. */
fun Project.buildDir(path: String): Provider<RegularFile> = layout.buildDirectory.file(path)

/** Creates a [RegularFile] [Provider] mapped to the project directory. */
context(Project)
fun ProjectLayout.file(path: String): Provider<RegularFile> = provider {
  projectDirectory.file(path)
}

/**
 * Submits a piece of work to be executed asynchronously.
 *
 * More Kotlin friendly variant of the existing [WorkQueue.submit]
 *
 * Syntax sugar for:
 * ```kotlin
 * submit(T::class.java, paramAction)
 * ```
 */
inline fun <reified T : WorkAction<C>, C : WorkParameters> WorkQueue.submit(
  noinline action: C.() -> Unit
) {
  submit(T::class.java, action)
}

/**
 * Maps a file provider to another file provider as a sub directory.
 *
 * Syntax sugar for:
 * ```
 * fileProvider.map { File("${it.path}/$path") }
 * ```
 */
fun Provider<File>.childFile(path: String): Provider<File> = map { File("${it.path}/$path") }

/** Variant of [childFile] that works with [RegularFile]. */
context(Project)
fun Provider<RegularFile>.childFile(path: String): Provider<RegularFile> =
  project.layout.file(map { it.asFile.childFile(path) })

/**
 * Returns a new [File] under the given sub directory.
 *
 * Syntax sugar for:
 * ```
 * File("$path/$childPath")
 * ```
 */
fun File.childFile(childPath: String) = File("$path/$childPath")

/**
 * Provides a temporary file for use during the task.
 *
 * Creates a file under the [temporaryDir][DefaultTask.getTemporaryDir] of the task, and should be
 * preferred to defining an explicit [File]. This will allow Gradle to make better optimizations on
 * our part, and helps us avoid edge-case scenarios like conflicting file names.
 */
fun DefaultTask.tempFile(path: String): Provider<RegularFile> =
  with(project) { layout.file("$temporaryDir/$path") }

/**
 * Syntax sugar for:
 * ```kotlin
 * plugins.apply(T::class)
 * ```
 */
inline fun <reified T : Plugin<*>> PluginContainer.apply() = apply(T::class)

/**
 * Represents an exception used to skip a Gradle task.
 *
 * Provides a more semantic way to refer to [StopActionException] when skipping tasks; as folks seem
 * to infer from the exception name that it stops *all* execution- when that's not the case.
 */
typealias SkipTask = StopActionException

/**
 * Retrieves the output file of a Gradle task, as a [Provider].
 *
 * Allows for easy access to the primary output file of a task, when tasks output a directory
 * instead of a single file. It filters out directories and returns the first file in the task's
 * outputs.
 */
val TaskProvider<*>.outputFile: Provider<File>
  get() = map { it.outputs.files.allChildren().first { !it.isDirectory } }

/** Variant of [outputFile] that provides the output as a [RegularFile] */
context(Project)
val TaskProvider<*>.regularOutputFile: Provider<RegularFile>
  get() = layout.file(outputFile)

/**
 * Generates a sequence of [File]s under this collection.
 *
 * Allows you to lazily compute against a generator of *non directory* children.
 *
 * In the case that this [FileCollection] is only a single [File] (as in, not a directory), the
 * sequence returned will just contain said [File].
 */
fun FileCollection.allChildren(): Sequence<File> =
  asSequence().flatMap { if (it.isDirectory) it.walk().asSequence() else sequenceOf(it) }

/**
 * Zips a list of providers into a provider of lists.
 *
 * This action is task avoidance friendly- meaning the underlying [Provider] will be a result of
 * mapping each [Provider] in the original list against one another.
 */
fun <T : Any> List<Provider<T>>.asSingleProvider(): Provider<List<T>> {
  val providerOfLists = map { it.map { listOf(it) } }

  return providerOfLists.reduce { finalProvider, currentProvider ->
    finalProvider.zip(currentProvider) { finalList, currentList -> finalList + currentList }
  }
}

/**
 * TODO()
 */
fun Project.getReleaseClasses(): FileCollection {
  extensions.findByType<KotlinAndroidProjectExtension>()?.let {
    return it.target.compilations.getByName("release").output.classesDirs
  }

  extensions.findByType<KotlinMultiplatformExtension>()?.let {
    return it.targets.getByName("android").compilations.getByName("main").output.classesDirs
  }

  throw RuntimeException("Library is missing an Android or KMP plugin")
}

/**
 * Provides a project property as the specified type, or null otherwise.
 *
 * Utilizing a safe cast, an attempt is made to cast the project property (if found) to the receiver
 * type. Should this cast fail, the resulting value will be null.
 *
 * Keep in mind that is provided lazily via a [Provider], so evaluation does not occur until the
 * value is needed.
 *
 * @param property the name of the property to look for
 */
inline fun <reified T> Project.provideProperty(property: String): Provider<T?> = provider {
  findProperty(property) as? T
}

/**
 * Rewrites the lines of a file.
 *
 * The lines of the file are first read and then transformed by the provided `block` function. The
 * transformed lines are then joined together with a newline character and written back to the file.
 *
 * If the `terminateWithNewline` parameter is set to `false`, the file will not be terminated with a
 * newline character.
 *
 * @param terminateWithNewline Whether to terminate the file with a newline character. Defaults to
 *   `true`.
 * @param block A function that takes a string as input and returns a new string. This function is
 *   used to transform the lines of the file before they are rewritten.
 *
 * ```
 * val file = File("my-file.txt")
 *
 * // Rewrite the lines of the file, replacing all spaces with tabs.
 * file.rewriteLines { it.replace(" ", "\t") }
 *
 * // Rewrite the lines of the file, capitalizing the first letter of each word.
 * file.rewriteLines { it.capitalizeWords() }
 * ```
 *
 * @see [readLines]
 * @see [writeText]
 */
fun File.rewriteLines(terminateWithNewline: Boolean = true, block: (String) -> String) {
  val newLines = readLines().map(block)
  writeText(newLines.joinToString("\n").let { if (terminateWithNewline) it + "\n" else it })
}

/**
 * Lazily reads the first line of a file.
 *
 * Uses a buffered reader to avoid loading the whole file in memory, and closes the file after
 * reading the first line.
 */
fun File.readFirstLine(): String = bufferedReader().useLines { it.first() }

/** Fetches the [version][Project.getVersion] of the project as a [ModuleVersion]. */
val Project.moduleVersion: ModuleVersion
  get() =
    ModuleVersion.fromStringOrNull(project.version.toString())
      ?: throw RuntimeException("Invalid project version found.")

/** Maps a [RegularFile] provider to an alternative provider if the original file does not exist. */
fun Provider<RegularFile>.orElseIfNotExists(file: Provider<RegularFile>): Provider<RegularFile> =
  map {
    it.takeIf { it.asFile.exists() } ?: file.get()
  }
