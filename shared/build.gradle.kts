import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    kotlin("plugin.serialization") version "2.1.20"

    alias(libs.plugins.ksp)
}

kotlin {
    jvm()

    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }

    sourceSets {


        commonMain.dependencies {
            //put your multiplatform dependencies here
            implementation(libs.kermit)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.koin.compose.viewmodel.nav)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation("co.touchlab:kermit:2.0.5")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
            implementation("org.assertj:assertj-core:3.25.3")
            implementation("io.mockk:mockk:1.14.2")
        }

        androidMain.dependencies {
            implementation(libs.androidx.activity.compose)
            implementation(libs.koin.android)
            implementation(libs.ktor.client.okhttp)
            implementation("app.cash.sqldelight:android-driver:2.0.2")
//            ksp(libs.room.compiler)
        }
    }
}

android {
    namespace = "ygmd.kmpquiz"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}