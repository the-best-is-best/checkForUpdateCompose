package io.github.tbib.kmm_check_for_update

import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


actual suspend fun ComposeCheckForUpdate.isUpdateAvailable(): Boolean {
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