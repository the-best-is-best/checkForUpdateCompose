package io.github.tbib.compose_check_for_update

import androidx.compose.runtime.Composable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CheckForUpdateDialog(forceUpdate: Boolean, title: String?, message: String?) {
    val kUpdater = IOSCheckForUpdate.kUpdater
    kUpdater.showUpdateWithForceUpdate(forceUpdate, title, message)
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun isUpdateAvailable(): Boolean {
    return suspendCancellableCoroutine { continuation ->
        val kUpdater = IOSCheckForUpdate.kUpdater
        kUpdater.isUpdateAvailableWithCompletion { b, nsError ->
            if (nsError != null) {
                continuation.resumeWithException(nsError.toThrowable())
            } else {
                continuation.resume(b)
            }
        }

    }
}

fun NSError.toThrowable(): Throwable {
    return Throwable(this.localizedDescription)
}