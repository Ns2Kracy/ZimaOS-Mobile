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
        val commonMain by getting {
            dependencies {
                implementation(project(":generated:openapi"))
            }
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
    "zimaosOpenapi" to "api/zimaos/zimaos/openapi.yaml",
    "zimaosV1Openapi" to "api/zimaos/zimaos/openapi_v1.yaml",

    // ZimaOS-Local-Storage API
    "localstorageOpenapi" to "api/zimaos-local-storage/local_storage/openapi.yaml",
    "localstorageV1Openapi" to "api/zimaos-local-storage/local_storage/openapi_v1.yaml",

    // ZimaOS-App-Management API
    "appmanagementOpenapi" to "api/zimaos-app-management/app_management/openapi.yaml",
    "appmanagementV1Openapi" to "api/zimaos-app-management/app_management/openapi_v1.yaml",

    // ZimaOS-Virt-Management API
    "virtmanagementOpenapi" to "api/zimaos-virt-management/virt_management/openapi.yaml",

    // ZimaOS-Mod-Management API
    "modmanagementOpenapi" to "api/zimaos-mod-management/mod_management/openapi.yaml",

    // ZimaOS-User-Service API
    "userOpenapi" to "api/zimaos-user-service/users/openapi.yaml",
    "userV1Openapi" to "/api/zimaos-user-service/users/openapi_v1.yaml",

    // ZimaOS-AI API
    "aiOpenapi" to "api/zimaos-ai/ai/openapi.yaml",

    // ZimaOS-Search API
    "searchOpenapi" to "api/zimaos-search/openapi.yaml",

    // IceWhale-Drive API
    "driveOpenapi" to "api/icewhale-drive/openapi.yaml",

    // IceWhale-Files API
    "filesOpenapi" to "api/icewhale-files/openapi.yaml",

    // IceWhale-Files-Backup API
    "filesbackupOpenapi" to "api/icewhale-files-backup/openapi.yaml",

    // CasaOS-Installer API
    "installerOpenapi" to "api/casaos-installer/installer/openapi.yaml",

    // CasaOS-Message-Bus API
    "messagebusOpenapi" to "api/casaos-message-bus/message_bus/openapi.yaml",
)

openapiSpecs.forEach {
    tasks.create("openApiGenerate-${it.key}", GenerateTask::class) {
        generatorName.set("kotlin")
        validateSpec.set(false)
        inputSpec.set("$rootDir/${it.value}")
        outputDir.set("$rootDir/generated/openapi")
        apiPackage.set("com.zimaspace.zimaos.openapi")
        modelPackage.set("com.zimaspace.zimaos.model.${it.key}")

        // 添加更多配置选项
        configOptions.set(
            mapOf(
                "library" to "multiplatform",  // 使用 Kotlin Multiplatform
                "dateLibrary" to "kotlinx-datetime",  // 使用 kotlinx-datetime 处理日期
                "collectionType" to "list",  // 使用 List 而不是 Array
                "useCoroutines" to "true",  // 使用协程
                "packageName" to "com.zimaspace.zimaos",  // 基础包名
            )
        )
    }
}

// 清理任务
tasks.register("cleanOpenApiGenerated", Delete::class) {
    delete("$rootDir/generated/openapi")
}

// 在生成之前先清理
tasks.register("openApiGenerateAll") {
    dependsOn("cleanOpenApiGenerated")
    dependsOn(openapiSpecs.keys.map { "openApiGenerate-$it" })
}

// 确保在编译之前生成代码
tasks.withType<KotlinCompile>() {
    dependsOn("openApiGenerateAll")
}

// 添加生成的源码目录
kotlin {
    sourceSets {
        val commonMain by getting {
            kotlin.srcDir("$rootDir/generated/openapi/src/main/kotlin")
        }
    }
}
