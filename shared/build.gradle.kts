plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget()
    jvm()

    sourceSets {
        commonMain.dependencies {
            //put your multiplatform dependencies here
            implementation(libs.kermit)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)

            implementation(libs.koin.compose.viewmodel.nav)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            implementation(libs.kermit)
            implementation(libs.kcron.common)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.mockk)
            implementation(libs.ktor.client.mock)
            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            // Koin pour Android
            implementation(libs.koin.android)
            // WorkManager
            implementation(libs.androidx.work.runtime.ktx)
            implementation(libs.koin.androidx.workmanager)
        }

        jvmMain {
            dependencies {
                implementation(libs.ktor.client.cio)
                implementation(libs.kcron.common)
            }
        }

        compilerOptions {
            optIn.add("kotlin.time.ExperimentalTime")
        }
    }
}

android {
    namespace = "ygmd.kmpquiz.shared"
    compileSdk = 36

    defaultConfig {
        minSdk = 26 
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}