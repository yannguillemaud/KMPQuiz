plugins {
    application
    kotlin("jvm")
}

group = "ygmd.kmpquiz"
version = "1.0.0"
application {
    mainClass.set("ygmd.kmpquiz.ApplicationKt")
    
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback)

    implementation(libs.postgresql)

    // Exposed
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)

    // HikariCP pour la gestion des connexions
    implementation(libs.hikaricp)
}
repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(21)
}