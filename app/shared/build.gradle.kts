import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)

    id("org.openapi.generator") version "7.10.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
    id("com.rickclephas.kmp.nativecoroutines") version "1.0.0-ALPHA-38"
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            // coroutines
            implementation(libs.kotlinx.coroutines.core)

            // ktor
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // koin
            implementation(libs.koin.core)

            // serialization
            implementation(libs.kotlinx.serialization.json)
        }
        androidMain.dependencies {
            // ktor client
            implementation(libs.ktor.client.android)
        }
        iosMain.dependencies {
            // ktor client
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.zimaspace.zimaos.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}
