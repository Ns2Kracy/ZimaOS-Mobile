import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.openapitools.generator.gradle.plugin.tasks.GenerateTask

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


val openapiSpecs = mapOf(
    // ZimaOS-Main API
    "zimaos-openapi" to "api/zimaos/zimaos/openapi.yaml",
    "zimaosV1-openapi" to "api/zimaos/zimaos/openapi_v1.yaml",

    // ZimaOS-Local-Storage API
    "localstorage-openapi" to "api/zimaos-local-storage/local_storage/openapi.yaml",
    "localstorageV1-openapi" to "api/zimaos-local-storage/local_storage/openapi_v1.yaml",

    // ZimaOS-App-Management API
    "appmanagement-openapi" to "api/zimaos-app-management/app_management/openapi.yaml",
    "appmanagementV1-openapi" to "api/zimaos-app-management/app_management/openapi_v1.yaml",

    // ZimaOS-Virt-Management API
    "virtmanagement-openapi" to "api/zimaos-virt-management/virt_management/openapi.yaml",

    // ZimaOS-Mod-Management API
    "modmanagement-openapi" to "api/zimaos-mod-management/mod_management/openapi.yaml",

    // ZimaOS-User-Service API
    "user-openapi" to "api/zimaos-user-service/users/openapi.yaml",
    "userV1-openapi" to "/api/zimaos-user-service/users/openapi_v1.yaml",

    // ZimaOS-AI API
    "ai-openapi" to "api/zimaos-ai/ai/openapi.yaml",

    // ZimaOS-Search API
    "search-openapi" to "api/zimaos-search/openapi.yaml",

    // IceWhale-Drive API
    "drive-openapi" to "api/icewhale-drive/openapi.yaml",

    // IceWhale-Files API
    "files-openapi" to "api/icewhale-files/openapi.yaml",

    // IceWhale-Files-Backup API
    "files-backup-openapi" to "api/icewhale-files-backup/openapi.yaml",

    // CasaOS-Installer API
    "casaos-installer-openapi" to "api/casaos-installer/installer/openapi.yaml",

    // CasaOS-Message-Bus API
    "casaos-message-bus-openapi" to "api/casaos-message-bus/message_bus/openapi.yaml",
)

openapiSpecs.forEach {
    tasks.create("openApiGenerate-${it.key}", GenerateTask::class) {
        generatorName.set("kotlin")
        validateSpec.set(false)
        inputSpec.set("$rootDir/${it.value}")
        outputDir.set("$rootDir/generated/openapi")
        apiPackage.set("com.zimaspace.zimaos.openapi")
        modelPackage.set("com.zimaspace.zimaos.model.${it.key}")

        configOptions.set(mapOf(
            "library" to "multiplatform",
            "serializationLibrary" to "kotlinx_serialization",
            "dateLibrary" to "kotlinx-datetime",
        ))
    }
}
tasks.register("openApiGenerateAll") { dependsOn(openapiSpecs.keys.map { "openApiGenerate-$it" }) }

tasks.withType<KotlinCompile>(){
    dependsOn("openApiGenerateAll")
}
