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
extra["version"] = "2.0.0"
extra["packageName"] = "ComposeCheckForUpdate"
extra["packageUrl"] = "https://github.com/the-best-is-best/checkForUpdateCompose"
extra["packageDescription"] = "The ComposeCheckForUpdate package provides a seamless solution for implementing update checking functionality in Jetpack Compose applications on both Android and iOS platforms. This package simplifies the process of checking for app updates, ensuring that users always have access to the latest features and improvements."
extra["system"] = "GITHUB"
extra["issueUrl"] = "https://github.com/the-best-is-best/checkForUpdateCompose/issues"
extra["connectionGit"] = "https://github.com/the-best-is-best/checkForUpdateCompose.git"

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
            implementation(libs.runtime)
            implementation(libs.foundation)
            implementation(libs.material3)
            implementation(libs.kotlinx.coroutines.core)

            api(project(":kmm_check_for_update"))
        }


        androidMain.dependencies {
            implementation(libs.ui.tooling)
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



