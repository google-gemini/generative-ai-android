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

plugins {
    `java-library`
    `java-gradle-plugin`
    `kotlin-dsl`
    kotlin("jvm") version "1.8.22"
    id("com.ncorti.ktfmt.gradle") version "0.16.0"
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.13.2"
    kotlin("plugin.serialization") version "1.8.22"
}

ktfmt {
    googleStyle()
}

kotlin {
    jvmToolchain(17)
    compilerOptions {
        freeCompilerArgs.add("-Xcontext-receivers")
    }
}

gradlePlugin {
    plugins {
        register("license-plugin") {
            id = "license-plugin"
            implementationClass = "com.google.gradle.plugins.LicensePlugin"
        }
        register("changelog-plugin") {
            id = "changelog-plugin"
            implementationClass = "com.google.gradle.plugins.ChangelogPlugin"
        }
        register("release-plugin") {
            id = "release-plugin"
            implementationClass = "com.google.gradle.plugins.ReleasePlugin"
        }
        register("multi-project-plugin") {
            id = "multi-project-plugin"
            implementationClass = "com.google.gradle.plugins.MultiProjectPlugin"
        }
    }
}

dependencies {
    implementation("com.android.tools.build:gradle:8.1.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.22")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    implementation("org.jetbrains.kotlinx.binary-compatibility-validator:org.jetbrains.kotlinx.binary-compatibility-validator.gradle.plugin:0.13.2")
}
