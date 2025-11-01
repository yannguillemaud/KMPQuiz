import java.util.Properties

// Chargement sécurisé des propriétés locales (qui doivent être dans .gitignore)
val localProperties = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use {
        localProperties.load(it)
    }
}

version = "0.0.1"

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("org.jetbrains.kotlin.plugin.compose")
    id("app.cash.sqldelight")
    id("com.github.breadmoirai.github-release")
    signing
    `maven-publish`
}

kotlin {
    applyDefaultHierarchyTemplate()

    androidTarget {
        publishLibraryVariants("release")
    }
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

            implementation(libs.androidx.datastore)

            implementation(libs.coil.compose)
            implementation(libs.coil.compose.okhttp)

            implementation(libs.paging.compose.common)
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
            implementation(libs.mockk)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.koin.android)
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

        all {
            languageSettings.apply {
                optIn("kotlin.time.ExperimentalTime")
                enableLanguageFeature("WhenGuards")
            }
        }
    }
}

android {
    namespace = "ygmd.kmpquiz"
    compileSdk = 35
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

// === BLOC DE SIGNATURE ET DE PUBLICATION GITHUB ===

// 1. Configurer la signature
// Le plugin KMP a déjà créé les publications (androidRelease, jvm, etc.) dans le conteneur `publishing.publications`.
// Nous demandons simplement au plugin `signing` de toutes les signer.
signing {
    // Condition pour signer seulement si les secrets sont là et une tâche de publication est lancée
    val shouldSign = project.gradle.taskGraph.allTasks.any { it.name.contains("publish", ignoreCase = true) } &&
            (System.getenv("SIGNING_KEY_ID") != null || localProperties.getProperty("signingKeyId") != null)

    // Fournit les secrets au plugin si la signature est requise
    if (shouldSign) {
        val signingKeyId = System.getenv("SIGNING_KEY_ID") ?: localProperties.getProperty("signingKeyId")
        val signingKey = System.getenv("SIGNING_KEY") ?: localProperties.getProperty("signingKey")
        val signingPassword = System.getenv("SIGNING_PASSWORD") ?: localProperties.getProperty("signingPassword")
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    }

    // Signe toutes les publications créées par le plugin Kotlin Multiplatform
    sign(publishing.publications)
}


githubRelease {
    // --- Configuration des propriétés spécifiques au plugin ---
    val githubUser = "yannguillemaud"
    val repoName = "KMPQuiz"

    token(System.getenv("GITHUB_TOKEN") ?: localProperties.getProperty("githubToken"))
    owner = githubUser
    repo = repoName
    tagName = "v${project.version}"
    releaseName = "Release v${project.version}"
    generateReleaseNotes = true

    // --- Configuration des fichiers à uploader (assets) ---
    releaseAssets.from(
        project.rootProject.file("androidApp/build/outputs/apk/release/androidApp-release.apk")
    )
}

val assembleAndroidAppReleaseApk by tasks.registering {
    dependsOn(":androidApp:assembleRelease")
}

tasks.named("githubRelease") {
    dependsOn(assembleAndroidAppReleaseApk)
}