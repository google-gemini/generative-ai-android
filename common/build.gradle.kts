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

@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("com.android.kotlin.multiplatform.library") // version "8.6.0-alpha02"
    kotlin("multiplatform") version "2.0.0"
    id("maven-publish")
    id("com.ncorti.ktfmt.gradle") version "0.18.0"
    id("changelog-plugin")
    id("release-plugin")
    kotlin("plugin.serialization")
}

ktfmt {
    googleStyle()
}

kotlin {
    val ktorVersion = "2.3.2"
    androidLibrary {
        compileSdk = 34
        minSdk = 21
        namespace = "com.google.ai.client.generativeai.common"
        optimization {
            minify = false
            consumerKeepRules.file("consumer-rules.pro")
            consumerKeepRules.publish = true
        }
        withAndroidTestOnJvm {
            isReturnDefaultValues = true
        }
    }
    jvmToolchain(17)
    compilerOptions {
        apiVersion.set(KotlinVersion.KOTLIN_1_8)
        languageVersion.set(KotlinVersion.KOTLIN_1_8)
    }

    jvm()
    sourceSets {
        commonMain.dependencies {
            implementation("io.ktor:ktor-client-core:$ktorVersion")
            implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.8.22")
            implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
            implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
            implementation("io.ktor:ktor-client-logging:$ktorVersion")

            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
            implementation("org.slf4j:slf4j-nop:2.0.9")
        }
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
            implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
        }
        jvmMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
            implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.22")
        }
        jvmTest.dependencies {
            implementation("junit:junit:4.13.2")
            implementation("io.kotest:kotest-assertions-core-jvm:4.0.7")
            implementation("io.kotest:kotest-assertions-json-jvm:4.0.7")
            implementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")
        }
    }
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.google.ai.client.generativeai"
            artifactId = "common"
            version = project.version.toString()
            pom {
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
            }
        }
    }
    repositories {
        maven {
            url = uri("${projectDir}/m2")
        }
    }
}
