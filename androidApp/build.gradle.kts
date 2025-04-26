import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val localProperties = Properties()
val localPropertiesFile = project.rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use {
        localProperties.load(it)
    }
}


plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.compose.multiplatform)
}

android {
    namespace = "ygmd.kmpquiz.android"
    compileSdk = 35
    defaultConfig {
        applicationId = "ygmd.kmpquiz"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    signingConfigs {
        create("release") {
            // Lire les informations depuis local.properties
            val keystoreFilePath = localProperties.getProperty("KEYSTORE_FILE")
            val keystorePasswordValue = localProperties.getProperty("KEYSTORE_PASSWORD")
            val keyAliasValue = localProperties.getProperty("KEY_ALIAS")
            val keyPasswordValue = localProperties.getProperty("KEY_PASSWORD")

            // Vérifier que le fichier keystore existe
            if (keystoreFilePath != null && File(keystoreFilePath).exists()) {
                // Assigner les valeurs aux propriétés de la configuration de signature
                this.storeFile = File(keystoreFilePath)
                this.storePassword = keystorePasswordValue
                this.keyAlias = keyAliasValue
                this.keyPassword = keyPasswordValue
            } else {
                // Si le keystore n'est pas trouvé, le build de release échouera plus tard,
                // mais ce message aide au débogage.
                println("ATTENTION : Fichier keystore non trouvé pour la signature 'release'. Le chemin est-il correct dans local.properties ?")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.koin.android)
    implementation(libs.koin.compose.viewmodel)
    implementation(libs.koin.compose.viewmodel.nav)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.material3)
}