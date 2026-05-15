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
        mavenCentral()
        google()
    }
}

rootProject.name = "KMPQuiz"
include(
    ":androidApp",
    ":shared",
)

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}