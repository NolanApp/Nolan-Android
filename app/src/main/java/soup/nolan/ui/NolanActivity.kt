package soup.nolan.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.Consumer
import androidx.window.DeviceState
import androidx.window.WindowManager
import soup.nolan.R
import soup.nolan.ui.system.SystemViewModel
import java.util.concurrent.Executor

class NolanActivity : AppCompatActivity(R.layout.nolan_activity) {

    private var windowManager: WindowManager? = null
    private val handler = Handler(Looper.getMainLooper())
    private val mainThreadExecutor = Executor { r: Runnable -> handler.post(r) }
    private val deviceStateChangeCallback = Consumer<DeviceState> { newDeviceState ->
        systemViewModel.onDeviceStateChanged(newDeviceState)
    }

    private val systemViewModel: SystemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Nolan_Main)
        super.onCreate(savedInstanceState)

        windowManager = WindowManager(this, null).apply {
            registerDeviceStateChangeCallback(
                mainThreadExecutor,
                deviceStateChangeCallback
            )
            systemViewModel.onDeviceStateChanged(deviceState)
        }
    }

    override fun onDestroy() {
        windowManager?.unregisterDeviceStateChangeCallback(deviceStateChangeCallback)
        super.onDestroy()
    }
}
