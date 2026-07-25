plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":shared"))
    implementation(compose.desktop.currentOs)
}

// jpackage/MSI requires a strictly numeric "major.minor.build" version.
val rawVersion: String = System.getenv("VERSION_NAME")
    ?: providers.exec { commandLine("git", "describe", "--tags", "--always", "--dirty") }
        .standardOutput.asText.get().trim()

val msiPackageVersion: String = run {
    val numeric = rawVersion.removePrefix("v").takeWhile { it.isDigit() || it == '.' }
    val parts = numeric.split('.').filter { it.isNotEmpty() }.mapNotNull { it.toIntOrNull() }
    if (parts.isEmpty()) "1.0.0"
    else (parts + listOf(0, 0)).take(3).joinToString(".")
}

compose.desktop {
    application {
        mainClass = "ygmd.kmpquiz.desktop.MainKt"

        nativeDistributions {
            targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Msi)
            packageName = "KMPQuiz"
            packageVersion = msiPackageVersion

            // jpackage's jdeps-based module detection doesn't see java.sql: SQLDelight's
            // JVM driver (sqlite-jdbc) reaches it via ServiceLoader/reflection, so without
            // this it's stripped from the bundled runtime image ("java/sql/DriverManager"
            // NoClassDefFoundError at startup).
            modules("java.sql")
        }
    }
}
