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

repositories {
    mavenCentral()
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

    testImplementation("org.junit.jupiter:junit-jupiter:5.7.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
repositories {
    mavenCentral()
}
kotlin {
    jvmToolchain(21)
}

