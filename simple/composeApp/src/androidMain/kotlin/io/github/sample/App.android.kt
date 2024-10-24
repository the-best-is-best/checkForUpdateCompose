package io.github.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import io.github.tbib.compose_check_for_update.AndroidCheckForUpdate

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        AndroidCheckForUpdate.initialization(this)
        setContent { App() }
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}
