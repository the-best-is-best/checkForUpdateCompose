<h1 align="center">composeCheckForUpdate</h1><br>
<div align="center">
<a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
<a href="https://android-arsenal.com/api?level=21" rel="nofollow">
    <img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat" style="max-width: 100%;">
</a>
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen.svg?logo=android" alt="Badge Android" />
  <img src="https://img.shields.io/badge/iOS-13%2B-blue.svg?logo=apple" alt="iOS 13+ Badge" />

<a href="https://github.com/the-best-is-best/"><img alt="Profile" src="https://img.shields.io/badge/github-%23181717.svg?&style=for-the-badge&logo=github&logoColor=white" height="20"/></a>
</div>

### The ComposeCheckForUpdate package provides a seamless solution for implementing update checking functionality in Jetpack Compose applications on both Android and iOS platforms. This package simplifies the process of checking for app updates, ensuring that users always have access to the latest features and improvements

<hr>

[![Maven Central](https://img.shields.io/maven-central/v/io.github.the-best-is-best/compose-check-for-update)](https://central.sonatype.com/artifact/io.github.the-best-is-best/compose-check-for-update)

KAdmob is available on `mavenCentral()`.

## Install

```kotlin
implementation("io.github.the-best-is-best:compose-check-for-update:1.2.0")
```

### ✅ **Install via SPM

You can install **KUpdater** directly using **Swift Package Manager (SPM)** from:  
🔗 [KUpdater-iOS on GitHub](https://github.com/the-best-is-best/Kupdater)

## How to use it

### First in iosMain

```kotlin
IOSCheckForUpdate.init(false) // optional is default false use it if stag or prod
```

### Second in androidMain

```kotlin
AndroidCheckForUpdate.initialization(this)
```

### in commonMain

#### How use it

```kotlin
    CheckForUpdateDialog(
    forceUpdate = true,
    title = "Update Here", // default is null and default text  `Update Available`
    message = "A new update please update now to continue" 
    /**if (isForceUpdate)
    "A new update is required to continue using this app."
    else
    "A new update is available. Would you like to update now?"
     **/
)
```

#### In version 1.0.1 can check new version in any function not compose and return true or false

```kotlin
  val scope = rememberCoroutineScope()

LaunchedEffect(Unit) {
    scope.launch {
        val isUpdateAvailable = isUpdateAvailable()
        println("new update available $isUpdateAvailable")
    }
}
```
