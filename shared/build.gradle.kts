plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("app.cash.sqldelight")
}

kotlin {
    androidTarget()
    jvm()

    jvmToolchain(17)

    sourceSets {
        commonMain.dependencies {
            /* LOG*/
            implementation(libs.kermit)

            /* KOTLIN */
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            implementation(libs.koin.compose.viewmodel.nav)

            /* KTOR */
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.cio)

            implementation(libs.kcron.common)

            /* SQLDELIGHT */
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.sqldelight.primitive.adapters)

            /* COMPOSE */
            implementation(libs.compose.ui)
            implementation(libs.compose.foundation)
            implementation(libs.compose.runtime)
            implementation(libs.compose.material3)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.androidx.material.icons)
            implementation(libs.androidx.material.icons.extended)
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
//            implementation(libs.androidx.work.runtime.ktx)
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
    namespace = "ygmd.kmpquiz"
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
        create("KMPQuizDatabase") {
            packageName.set("ygmd.kmpquiz.database")
        }
    }
}