package io.github.tbib.compose_check_for_update

import cocoapods.KUpdater.KUpdater

object IOSCheckForUpdate {
    internal val kUpdater = KUpdater.shared()
    fun init(isTestFlight: Boolean = false) {
        kUpdater.setIsTestFlight(isTestFlight)

    }
}