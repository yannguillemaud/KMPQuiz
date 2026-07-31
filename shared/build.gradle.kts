plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.compose.hot.reload)
}

kotlin {
    jvmToolchain(21)

    android {
        compileSdk = 37
        minSdk = 26
        namespace = "ygmd.kmpquiz.shared"
        experimentalProperties["android.experimental.kmp.enableAndroidResources"] = true
        withHostTestBuilder {

        }
    }

    jvm("desktop") {

    }

    sourceSets {
        commonMain {
            dependencies {
                // Logging
                implementation(libs.kermit)

                // Kotlin Core
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.collections.immutable)
                implementation(libs.kotlinx.datetime)

                // Ktor (Networking)
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)

                // SQLDelight (Database)
                implementation(libs.sqldelight.coroutines.extensions)

                // Koin (DI) - core is needed here since core/di, data/di and di/Koin.kt
                // reference Koin types directly from commonMain.
                implementation(libs.koin.core)
                // koin-compose-viewmodel is a genuine Compose Multiplatform artifact
                // (Android/iOS/Desktop/Web) — every commonMain screen already imports
                // org.koin.compose.viewmodel.koinViewModel, so it belongs here, not
                // androidMain-only.
                implementation(libs.koin.compose.viewmodel)

                // Compose (UI)
                implementation(libs.androidx.compose.ui)
                implementation(libs.androidx.compose.ui.tooling)
                implementation(libs.jetbrains.compose.ui.tooling.preview)
                implementation(libs.androidx.compose.foundation)
                implementation(libs.androidx.navigationevent.compose)
                implementation(libs.androidx.lifecycle.runtime.compose)
                implementation(libs.androidx.lifecycle.viewmodel.compose)
                implementation(libs.androidx.navigation3.ui)
                implementation(libs.androidx.lifecycle.viewmodel.navigation3)
                implementation(libs.androidx.compose.material3)
                implementation(libs.androidx.compose.material.icons.extended)
                implementation(libs.material3)

                // DataStore
                implementation(libs.androidx.datastore.preferences.core)

                // Coil (Image Loading)
                implementation(libs.coil.compose)
                implementation(libs.coil.compose.okhttp)
            }
        }

        commonTest {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.kotlinx.coroutines.test)
                implementation(libs.ktor.client.mock)
                implementation(libs.kotlinx.datetime)
                implementation(libs.turbine)
            }
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
            implementation(libs.koin.androidx.workmanager)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.koin.android)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.compose.ui)
            implementation(libs.androidx.compose.ui.graphics)
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.compose.material3)
            implementation(libs.androidx.activity.compose)
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.appcompat)
            implementation(libs.material)

            // Permissions
            implementation(libs.grant.core)
            implementation(libs.grant.compose)
            implementation(libs.grant.core.koin)
        }

        val desktopMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutines.core)
                implementation(libs.kotlinx.coroutines.swing)
                implementation(libs.ktor.client.okhttp)
                implementation(libs.sqldelight.sqlite.driver)
            }
        }
    }
}

sqldelight {
    databases {
        create("KMPQuizDatabase") {
            packageName.set("ygmd.kmpquiz.database")
            dialect("app.cash.sqldelight:sqlite-3-38-dialect:${libs.versions.sqldelight.get()}")
        }
    }
}
