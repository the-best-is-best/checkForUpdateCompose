@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform.getCurrentOperatingSystem
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    id("maven-publish")
    id("signing")
    alias(libs.plugins.maven.publish)
}




apply(plugin = "maven-publish")
apply(plugin = "signing")


tasks.withType<PublishToMavenRepository> {
    val isMac = getCurrentOperatingSystem().isMacOsX
    onlyIf {
        isMac.also {
            if (!isMac) logger.error(
                """
                    Publishing the library requires macOS to be able to generate iOS artifacts.
                    Run the task on a mac or use the project GitHub workflows for publication and release.
                """
            )
        }
    }
}



extra["groupId"] = "io.github.the-best-is-best"
extra["artifactId"] = "compose-check-for-update"
extra["version"] = "1.2.2"
extra["packageName"] = "ComposeCheckForUpdate"
extra["packageUrl"] = "https://github.com/the-best-is-best/checkForUpdateCompose"
extra["packageDescription"] = "The ComposeCheckForUpdate package provides a seamless solution for implementing update checking functionality in Jetpack Compose applications on both Android and iOS platforms. This package simplifies the process of checking for app updates, ensuring that users always have access to the latest features and improvements."
extra["system"] = "GITHUB"
extra["issueUrl"] = "https://github.com/the-best-is-best/KMMAdmob/issues"
extra["connectionGit"] = "https://github.com/the-best-is-best/KMMAdmob.git"

extra["developerName"] = "Michelle Raouf"
extra["developerNameId"] = "MichelleRaouf"
extra["developerEmail"] = "eng.michelle.raouf@gmail.com"


mavenPublishing {
    coordinates(
        extra["groupId"].toString(),
        extra["artifactId"].toString(),
        extra["version"].toString()
    )

    publishToMavenCentral(true)
    signAllPublications()

    pom {
        name.set(extra["packageName"].toString())
        description.set(extra["packageDescription"].toString())
        url.set(extra["packageUrl"].toString())
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://opensource.org/licenses/Apache-2.0")
            }
        }
        issueManagement {
            system.set(extra["system"].toString())
            url.set(extra["issueUrl"].toString())
        }
        scm {
            connection.set(extra["connectionGit"].toString())
            url.set(extra["packageUrl"].toString())
        }
        developers {
            developer {
                id.set(extra["developerNameId"].toString())
                name.set(extra["developerName"].toString())
                email.set(extra["developerEmail"].toString())
            }
        }
    }

}


signing {
    useGpgCmd()
    sign(publishing.publications)
}


kotlin {
    jvmToolchain(17)


    //jvm()

    //  js {
//        browser()
//        binaries.executable()
//    }
//
//    @OptIn(ExperimentalWasmDsl::class)
//    wasmJs {
//        browser()
//        binaries.executable()
//    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
//        macosX64(),
//        macosArm64(),
//        tvosX64(),
//        tvosArm64(),
//        tvosSimulatorArm64(),
//        watchosArm32(),
//        watchosX64(),
//        watchosArm64(),
//        watchosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "composeCheckForUpdate"
            isStatic = true
        }

        it.compilations.getByName("main") {
            val defFileName = when (target.name) {
                "iosX64" -> "iosX64.def"
                "iosArm64" -> "iosArm64.def"
                "iosSimulatorArm64" -> "iosSimulatorArm64.def"
//                "macosX64" -> "macosX64.def"
//                "macosArm64" -> "macosArm64.def"
//                "tvosX64" -> "tvosX64.def"
//                "tvosArm64" -> "tvosArm64.def"
//                "tvosSimulatorArm64" -> "tvosSimulatorArm64.def"
//                "watchosArm32" -> "watchosArm32.def"
//                "watchosX64" -> "watchosX64.def"
//                "watchosArm64" -> "watchosArm64.def"
//                "watchosSimulatorArm64" -> "watchosSimulatorArm64.def"


                else -> throw IllegalStateException("Unsupported target: ${target.name}")
            }

            val defFile = project.file("native/$defFileName")
            if (defFile.exists()) {
                cinterops.create("KUpdater") {
                    defFile(defFile)
                    packageName = "io.github.native.kupdater"
                }
            } else {
                logger.warn("Def file not found for target ${target.name}: ${defFile.absolutePath}")
            }
        }
    }


    sourceSets {
        all {
            languageSettings.apply {
                progressiveMode = true
                optIn("kotlinx.coroutines.ExperimentalCoroutinesApi")
                if (name.lowercase().contains("ios")) {
                    optIn("kotlinx.cinterop.ExperimentalForeignApi")
                    optIn("kotlinx.cinterop.BetaInteropApi")
                }
            }
        }

        commonMain.dependencies {
            implementation("org.jetbrains.compose.runtime:runtime:1.10.0")
            implementation("org.jetbrains.compose.foundation:foundation:1.10.0")
            implementation("org.jetbrains.compose.material3:material3:1.10.0-alpha05")
        }


        androidMain.dependencies {
            implementation(compose.uiTooling)
            implementation(libs.androidx.activityCompose)

            implementation(libs.app.update.ktx)
        }

//        jvmMain.dependencies {
//            implementation(compose.desktop.currentOs)
//        }
//
//        jsMain.dependencies {
//            implementation(compose.html.core)
//        }

        iosMain.dependencies {
        }

    }

    android {
        namespace = "io.github.tbib.compose_check_for_update"
        compileSdk = 36
        minSdk = 23
    }
}





abstract class GenerateDefFilesTask : DefaultTask() {

    @get:Input
    abstract val packageName: Property<String>

    @get:OutputDirectory
    abstract val interopDir: DirectoryProperty

    @TaskAction
    fun generate() {
        // Ensure the directory exists
        interopDir.get().asFile.mkdirs()

        // Constants
        val kupdaterHeader = "KUpdater-Swift.h"

        // Map targets to their respective paths
        val targetToPath = mapOf(
            "iosX64" to "ios-arm64_x86_64-simulator",
            "iosArm64" to "ios-arm64",
            "iosSimulatorArm64" to "ios-arm64_x86_64-simulator",
//            "macosX64" to "macos-arm64_x86_64",
//            "macosArm64" to "macos-arm64_x86_64",
//            "tvosArm64" to "tvos-arm64",
//            "tvosX64" to "tvos-arm64_x86_64-simulator",
//            "tvosSimulatorArm64" to "tvos-arm64_x86_64-simulator",
//            "watchosSimulatorArm64" to "watchos-arm64_x86_64-simulator",
//            "watchosX64" to "watchos-arm64_arm64_32",
//            "watchosArm32" to "watchos-arm64_arm64_32",
//            "watchosArm64" to "watchos-arm64_arm64_32",
        )

        // Helper function to generate header paths
        fun headerPath(): String {
            return interopDir.dir("libs/$kupdaterHeader")
                .get().asFile.absolutePath
        }

        // Generate headerPaths dynamically
        val headerPaths = targetToPath.mapValues { (target, _) ->
            headerPath()
        }

        // List of targets derived from targetToPath keys
        val iosTargets = targetToPath.keys.toList()

        // Loop through the targets and create the .def files
        iosTargets.forEach { target ->
            val headerPath = headerPaths[target] ?: return@forEach
            val defFile = File(interopDir.get().asFile, "$target.def")

            // Generate the content for the .def file
            val content = """
                language = Objective-C
                package = ${packageName.get()}
                headers = $headerPath
            """.trimIndent()

            // Write content to the .def file
            defFile.writeText(content)
            println("Generated: ${defFile.absolutePath} with headers = $headerPath")
        }
    }
}
// Register the task within the Gradle build
tasks.register<GenerateDefFilesTask>("generateDefFiles") {
    packageName.set("io.github.native.kupdater")
    interopDir.set(project.layout.projectDirectory.dir("native"))
}