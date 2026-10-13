package io.github.tbib.compose_check_for_update

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability

@Composable
actual fun CheckForUpdateDialog(forceUpdate: Boolean, title: String?, message: String?) {
    var showUpdateDialog by remember { mutableStateOf(false) }
    var installStatus by remember { mutableStateOf(InstallStatus.UNKNOWN) }

    val appUpdateManager: AppUpdateManager =
        AppUpdateManagerFactory.create(ComposeCheckForUpdate.getActivity())
    val appUpdateInfoTask = appUpdateManager.appUpdateInfo

    DisposableEffect(Unit) {
        val listener = InstallStateUpdatedListener { state ->
            if (state.installStatus() == InstallStatus.DOWNLOADED) {
                installStatus = InstallStatus.DOWNLOADED
            }
        }
        appUpdateManager.registerListener(listener)
        onDispose {
            appUpdateManager.unregisterListener(listener)
        }
    }

    // Check if there's an update available or already downloaded
    LaunchedEffect(Unit) {
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                installStatus = InstallStatus.DOWNLOADED
            } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                showUpdateDialog = true
            }
        }
        appUpdateInfoTask.addOnFailureListener { error ->
        }
    }

    // Show the "Ready to install" dialog
    if (installStatus == InstallStatus.DOWNLOADED) {
        UpdateDialog(
            isDownloaded = true,
            isForceUpdate = forceUpdate,
            title = "Update Ready",
            message = "An update has been downloaded. Restart the app to install.",
            onUpdate = {
                appUpdateManager.completeUpdate()
            },
            onCancel = {
                // Should we allow canceling the restart for force update?
                // For now, follow forceUpdate logic.
                if (!forceUpdate) installStatus = InstallStatus.UNKNOWN
            }
        )
    } else if (showUpdateDialog) {
        // Show the update availability dialog
        UpdateDialog(
            isDownloaded = false,
            isForceUpdate = forceUpdate,
            title = title,
            message = message,
            onUpdate = {
                startUpdateFlow(ComposeCheckForUpdate.getActivity(), appUpdateManager, forceUpdate)
            },
            onCancel = {
                showUpdateDialog = false // Dismiss the dialog if it's not a forced update
            }
        )
    }
}

@Composable
private fun UpdateDialog(
    isDownloaded: Boolean = false,
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

    val confirmButtonText = if (isDownloaded) "Restart Now" else "Update Now"

    Dialog(onDismissRequest = {
        if (!isForceUpdate) onCancel()
    }) {
        Column(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(20.dp))
                .padding(vertical = 16.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = titleDialog,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            )
            Text(
                text = messageDialog,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = TextStyle(
                    color = Color(0xFF808080),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal
                )
            )

            HorizontalDivider(color = Color(0xffE5E7EB), thickness = 1.dp)

            Row(
                modifier = Modifier
                    .height(43.dp)
                    .fillMaxWidth()
            ) {
                if (!isForceUpdate) {
                    TextButton(
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        onClick = { onCancel() },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                    ) {
                        Text(
                            text = "Later",
                            style = TextStyle(
                                color = Color(0xffBBBBBB),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }

                    VerticalDivider(color = Color(0xffE5E7EB), thickness = 1.dp)
                }

                TextButton(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    onClick = { onUpdate() },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                ) {
                    Text(
                        text = confirmButtonText,
                        style = TextStyle(
                            color = Color(0xFF0C91C0),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

// This function handles starting the update process
private fun startUpdateFlow(
    activity: Activity,
    appUpdateManager: AppUpdateManager,
    forceUpdate: Boolean
) {
    val updateType = if (forceUpdate) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE
    val appUpdateOptions = AppUpdateOptions.newBuilder(updateType)
        .setAllowAssetPackDeletion(true)
        .build()

    val appUpdateInfoTask = appUpdateManager.appUpdateInfo
    appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            && appUpdateInfo.isUpdateTypeAllowed(updateType)
        ) {
            appUpdateManager.startUpdateFlow(
                appUpdateInfo,
                activity,
                appUpdateOptions
            ).addOnFailureListener {
                Toast.makeText(activity, "Error starting update flow", Toast.LENGTH_LONG).show()
            }.addOnSuccessListener { resultCode ->
                if (resultCode == Activity.RESULT_CANCELED) {
                    Toast.makeText(activity, "Update canceled", Toast.LENGTH_LONG).show()
                } else if (resultCode != Activity.RESULT_OK) {
                    Toast.makeText(activity, "Update flow failed", Toast.LENGTH_LONG).show()
                }
                // For RESULT_OK, for FLEXIBLE updates, we wait for the InstallStateUpdatedListener.
                // For IMMEDIATE updates, the app usually restarts or resumes.
            }
        }
    }
}
