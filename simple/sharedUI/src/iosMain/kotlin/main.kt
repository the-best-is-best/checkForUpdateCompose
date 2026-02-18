import androidx.compose.ui.window.ComposeUIViewController
import io.github.sample.App
import io.github.tbib.kmm_check_for_update.IOSCheckForUpdate
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController {
    IOSCheckForUpdate.init(false)
    return ComposeUIViewController { App() }
}
