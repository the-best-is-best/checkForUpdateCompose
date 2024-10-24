package io.github.tbib.compose_check_for_update

import android.app.Activity
import java.lang.ref.WeakReference

object AndroidCheckForUpdate {
    private var activity: WeakReference<Activity?> = WeakReference(null)

    internal fun getActivity(): Activity {
        return activity.get()!!
    }

    fun initialization(activity: Activity) {
        this.activity = WeakReference(activity)
    }
}