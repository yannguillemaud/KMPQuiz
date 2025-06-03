enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "KMPQuiz"
include(
    ":androidApp",
    ":shared",
    ":server"
)

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}