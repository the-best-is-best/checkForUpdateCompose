<h1 align="center">Compose Check For Update</h1><br>
<div align="center">
<a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
<a href="https://android-arsenal.com/api?level=21" rel="nofollow">
    <img alt="API" src="https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat" style="max-width: 100%;">
</a>
  <img src="https://img.shields.io/badge/Platform-Android-brightgreen.svg?logo=android" alt="Badge Android" />
  <img src="https://img.shields.io/badge/iOS-13%2B-blue.svg?logo=apple" alt="iOS 13+ Badge" />

<a href="https://github.com/the-best-is-best/"><img alt="Profile" src="https://img.shields.io/badge/github-%23181717.svg?&style=for-the-badge&logo=github&logoColor=white" height="20"/></a>

</div>

The Compose Check For Update package provides a seamless solution for implementing update checking functionality in Jetpack Compose applications on both Android and iOS platforms. This package simplifies the process of checking for app updates, ensuring that users always have access to the latest features and improvements.

<hr>

[![Maven Central](https://img.shields.io/maven-central/v/io.github.the-best-is-best/compose-check-for-update)](https://central.sonatype.com/artifact/io.github.the-best-is-best/compose-check-for-update)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.the-best-is-best/kmm-check-for-update)](https://central.sonatype.com/artifact/io.github.the-best-is-best/kmm-check-for-update)

Available on `mavenCentral()`.

## Install

### Option 1: Compose UI (recommended for Compose apps)

Includes `CheckForUpdateDialog` composable and the core update logic.

```kotlin
implementation("io.github.the-best-is-best:compose-check-for-update:2.0.0")
```

### Option 2: Core library only

Use this if you only need the update check logic without the Compose dialog UI.

```kotlin
implementation("io.github.the-best-is-best:kmm-check-for-update:2.0.0")
```

### iOS: KUpdater SPM

You need to install **KUpdater** via **Swift Package Manager (SPM)**:

🔗 [KUpdater-iOS on GitHub](https://github.com/the-best-is-best/Kupdater)

## How to use it

### First in iosMain

```kotlin
IOSCheckForUpdate.init(
    isTestFlight = false,  // optional, default false (use true for TestFlight builds)
    authorization = null,  // required when isTestFlight is true
    countryCode = null,
    appStoreId = null      // required when isTestFlight is true
)
```

### Second in androidMain

```kotlin
AndroidCheckForUpdate.initialization(this)
// for compose
ComposeCheckForUpdate.initialization(this)

```

### In commonMain

#### Check for updates

```kotlin
LaunchedEffect(Unit) {
    val isUpdateAvailable = ComposeCheckForUpdate.isUpdateAvailable()
    if (isUpdateAvailable) {
        // Show update dialog or navigate to store
    }
}
```

#### With CheckForUpdateDialog (compose-check-for-update only)

```kotlin
if (isUpdateAvailable) {
    CheckForUpdateDialog(
        forceUpdate = true,
        title = "Update Here",  // optional, default is "Update Available"
        message = "A new update please update now to continue"
        // forceUpdate = true → "A new update is required to continue using this app."
        // forceUpdate = false → "A new update is available. Would you like to update now?"
    )
}
```
