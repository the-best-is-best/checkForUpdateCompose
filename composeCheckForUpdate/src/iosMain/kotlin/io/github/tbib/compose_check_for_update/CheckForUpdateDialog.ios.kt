package io.github.tbib.compose_check_for_update

import androidx.compose.runtime.Composable


@Composable
actual fun CheckForUpdateDialog(forceUpdate: Boolean, title: String?, message: String?) {
    val kUpdater = IOSCheckForUpdate.kUpdater

    kUpdater.showUpdateWithForceUpdate(forceUpdate, title, message)
}
