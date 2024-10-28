package io.github.tbib.compose_check_for_update

import androidx.compose.runtime.Composable

@Composable
expect fun CheckForUpdateDialog(
    forceUpdate: Boolean = false,
    title: String? = null,
    message: String? = null
)

expect suspend fun isUpdateAvailable(): Boolean