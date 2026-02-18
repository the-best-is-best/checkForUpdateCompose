package io.github.tbib.kmm_check_for_update

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException


@OptIn(ExperimentalForeignApi::class)
actual suspend fun ComposeCheckForUpdate.isUpdateAvailable(): Boolean {
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