package io.github.tbib.compose_check_for_update

import io.github.native.kupdater.KUpdater
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
object IOSCheckForUpdate {
    internal val kUpdater = KUpdater.shared()

    fun init(isTestFlight: Boolean = false, authorization: String? = null) {
        if (isTestFlight && authorization == null) {
            throw IllegalArgumentException("Authorization is required for TestFlight")
        }
        kUpdater.setIsTestFlight(isTestFlight)
        kUpdater.setAuthorizationTestFlight(authorization)

    }
}