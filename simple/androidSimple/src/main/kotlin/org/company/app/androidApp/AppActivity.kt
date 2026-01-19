package org.company.app.androidApp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import io.github.sample.App
import io.github.tbib.compose_check_for_update.AndroidCheckForUpdate

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AndroidCheckForUpdate.initialization(this)
        setContent {
            App()
        }
    }
}

