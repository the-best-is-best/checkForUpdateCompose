package io.github.tbib.compose_check_for_update

import android.app.Activity
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Composable
actual fun CheckForUpdateDialog(forceUpdate: Boolean, title: String?, message: String?) {
    var showUpdateDialog by remember { mutableStateOf(false) }

    val appUpdateManager: AppUpdateManager =
        AppUpdateManagerFactory.create(AndroidCheckForUpdate.getActivity())
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    // Check if there's an update available
    LaunchedEffect(Unit) {
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            println("Check for update info ${appUpdateInfo}")
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                showUpdateDialog = true
            }
        }
        appUpdateInfoTask.addOnFailureListener { error ->
            println(" Check for update error ${error}")


        }
    }

    // Show the update dialog if required
    if (showUpdateDialog) {
        UpdateDialog(
            isForceUpdate = forceUpdate,
            title = title,
            message = message,
            onUpdate = {
                startUpdateFlow(AndroidCheckForUpdate.getActivity(), appUpdateManager, forceUpdate)
            },
            onCancel = {
                    showUpdateDialog = false // Dismiss the dialog if it's not a forced update
            }
        )
    }
}

@Composable
private fun UpdateDialog(
    isForceUpdate: Boolean,
    onUpdate: () -> Unit,
    onCancel: () -> Unit,
    title: String? = null,
    message: String? = null
) {
    val titleDialog = title ?: "Update Available"
    val messageDialog = message ?: if (isForceUpdate)
        "A new update is required to continue using this app."
    else
        "A new update is available. Would you like to update now?"

    AlertDialog(
        onDismissRequest = {
            if (!isForceUpdate) onCancel()
        },
        title = { Text(text = titleDialog) },
        text = {
            Text(
                text = messageDialog
            )
        },
        confirmButton = {
            Button(onClick = onUpdate) {
                Text(text = "Update Now")
            }
        },
        dismissButton = {
            if (!isForceUpdate) {
                TextButton(onClick = onCancel) {
                    Text(text = "Later")
                }
            }
        }
    )
}

// This function handles starting the update process
private fun startUpdateFlow(
    activity: Activity,
    appUpdateManager: AppUpdateManager,
    forceUpdate: Boolean
) {
    val updateType = if (forceUpdate) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE
    val appUpdateOptions = AppUpdateOptions.newBuilder(updateType)
        .setAllowAssetPackDeletion(true)  // Optional: for asset pack management
        .build()

    // Check if the update is available
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo
    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
            appUpdateManager.startUpdateFlow(
                appUpdateInfo,
                activity,
                appUpdateOptions
            ).addOnFailureListener {
                Toast.makeText(activity, "Error updating the app", Toast.LENGTH_LONG).show()
            }.addOnSuccessListener {
                Toast.makeText(activity, "App updated. Please restart the app.", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}

actual suspend fun isUpdateAvailable(): Boolean {
    val appUpdateManager: AppUpdateManager =
        AppUpdateManagerFactory.create(AndroidCheckForUpdate.getActivity())
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    return suspendCancellableCoroutine { continuation ->
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val isAvailable =
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            continuation.resume(isAvailable)
        }.addOnFailureListener {
            continuation.resume(false) // Return false if there's an error
        }
    }
}