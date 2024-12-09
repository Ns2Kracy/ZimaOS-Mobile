import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)

    id("org.openapi.generator") version "7.10.0"
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.0"
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
        all {
            languageSettings.optIn("kotlin.experimental.ExperimentalObjCName")
        }
        commonMain.dependencies {
            // Asynchronous
            implementation(libs.kotlinx.coroutines.core)

            // Http
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // DI
            implementation(libs.koin.core)

            // Serializer
            implementation(libs.kotlinx.serialization.json)

            // File
            implementation(libs.okio)

            // Date, Time
            implementation(libs.kotlinx.datetime)
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

openApiGenerate {
    generatorName.set("kotlin")

    // input spec
    inputSpec.set("$rootDir/api/zimaos/zimaos/openapi.yaml")
    inputSpec.set("$rootDir/api/zimaos/zimaos/openapi_v1.yaml")
    inputSpec.set("$rootDir/api/zimaos-local-storage/local_storage/openapi.yaml")
    inputSpec.set("$rootDir/api/zimaos-local-storage/local_storage/openapi_v1.yaml")
    inputSpec.set("$rootDir/api/zimaos-app-management/app_management/openapi.yaml")
    inputSpec.set("$rootDir/api/zimaos-app-management/app_management/openapi_v1.yaml")
    inputSpec.set("$rootDir/api/zimaos-virt-management/virt_management/openapi.yaml")
    inputSpec.set("$rootDir/api/zimaos-virt-management/virt_management/openapi_v1.yaml")
    inputSpec.set("$rootDir/api/zimaos-mod-management/mod_management/openapi.yaml")
    inputSpec.set("$rootDir/api/zimaos-mod-management/mod_management/openapi_v1.yaml")
    inputSpec.set("$rootDir/api/zimaos-user-service/user/openapi.yaml")
    inputSpec.set("$rootDir/api/zimaos-user-service/user/openapi_v1.yaml")
    inputSpec.set("$rootDir/api/zimaos-ai/ai/openapi.yaml")
    inputSpec.set("$rootDir/api/zimaos-search/openapi.yaml")
    inputSpec.set("$rootDir/api/icewhale-drive/openapi.yaml")
    inputSpec.set("$rootDir/api/icewhale-files/openapi.yaml")
    inputSpec.set("$rootDir/api/icewhale-files/backup/openapi.yaml")
    inputSpec.set("$rootDir/api/casaos-installer/installer/openapi.yaml")
    inputSpec.set("$rootDir/api/casaos-message-bus/message_bus/openapi.yaml")

    outputDir.set("$rootDir/app/openapi")
    apiPackage.set("com.zimaspace.zimaos.openapi")
    modelPackage.set("com.zimaspace.zimaos.openapi.model")
    configOptions.set(mapOf(
        "library" to "multiplatform",
        "serializationLibrary" to "kotlinx_serialization",
        "dateLibrary" to "kotlinx-datetime",
    ))
}