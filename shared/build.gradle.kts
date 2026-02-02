plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    jvm()

    androidLibrary {
        compileSdk = 36
        minSdk = 26
        namespace = "ygmd.kmpquiz.shared"
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
    }

    sourceSets {
        commonMain.dependencies {
            // Logging
            implementation(libs.kermit)

            // Kotlin Core
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            // Koin (ViewModel)
            implementation(libs.koin.compose.viewmodel.nav)

            // Ktor (Networking)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // Cron
            implementation(libs.kcron.common)

            // SQLDelight (Database)
            implementation(libs.sqldelight.coroutines.extensions)

            // Compose (UI)
            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.compose.ui.tooling)
            implementation(libs.jetbrains.compose.ui.tooling.preview)
            implementation(libs.androidx.compose.foundation)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.compose.material3)
            implementation(libs.androidx.compose.material.icons.extended)

            // DataStore
            implementation(libs.androidx.datastore)

            // Coil (Image Loading)
            implementation(libs.coil.compose)
            implementation(libs.coil.compose.okhttp)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.kotlinx.datetime)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.workmanager)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.compose.ui.tooling.preview)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.client.cio)
            implementation(libs.kcron.common)
            implementation(libs.sqldelight.jvm.jdbc.driver)
        }

        jvmTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }

        all {
            languageSettings.apply {
                optIn("kotlin.time.ExperimentalTime")
                enableLanguageFeature("WhenGuards")
            }
        }
    }
}

sqldelight {
    databases {
        create("KMPQuizDatabase") {
            packageName.set("ygmd.kmpquiz.database")
        }
    }
}
