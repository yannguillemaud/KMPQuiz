plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.sqldelight)
    id("dev.mokkery") version "2.9.0"
}

kotlin {
    androidTarget()
    jvm()

    jvmToolchain(17)

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
            implementation(libs.ktor.client.cio)

            implementation(libs.kermit)
            implementation(libs.kcron.common)

            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.sqldelight.primitive.adapters)

            implementation("org.jetbrains.compose.components:components-resources:1.10.0-alpha01")
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.kotlinx.datetime)
            implementation(libs.sqldelight.sqlite.driver)
            implementation(libs.kotest.framework.engine)
            implementation(libs.kotest.assertions.core)
            implementation(libs.turbine)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            // Koin pour Android
            implementation(libs.koin.android)
            // WorkManager
            implementation(libs.androidx.work.runtime.ktx)
            implementation(libs.koin.androidx.workmanager)
            implementation(libs.sqldelight.android.driver)
        }

        jvmMain {
            dependencies {
                implementation(libs.ktor.client.cio)
                implementation(libs.kcron.common)
                implementation(libs.sqldelight.sqlite.driver)
            }
        }

        jvmTest {
            dependencies {
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.sqldelight.sqlite.driver)
                implementation(libs.kotest.framework.engine)
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

sqldelight {
    databases {
        create("KMPQuizDatabase"){
            packageName.set("ygmd.kmpquiz.database")
        }
    }
}