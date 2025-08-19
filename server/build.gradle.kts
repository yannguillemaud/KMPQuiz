plugins {
    application
    kotlin("jvm")
    alias(libs.plugins.kotlin.serialization)
}

group = "ygmd.kmpquiz"
version = "1.0.0"
application {
    mainClass.set("ygmd.kmpquiz.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.status.page)

    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.postgresql)
    implementation(libs.hikaricp)

    /* KOIN */
    implementation(libs.koin.core)
    implementation(libs.koin.ktor)
    implementation(libs.koin.logger.slf4j)

    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

