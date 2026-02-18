package io.github.tbib.compose_check_for_update

import androidx.compose.runtime.Composable
import io.github.tbib.kmm_check_for_update.IOSCheckForUpdate
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun CheckForUpdateDialog(forceUpdate: Boolean, title: String?, message: String?) {
    val kUpdater = IOSCheckForUpdate.kUpdater
    kUpdater.showUpdateWithForceUpdate(forceUpdate, title, message)
}
