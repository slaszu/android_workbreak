package pl.slaszu.notification


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat

class Notification(
    val configuration: Configuration,
    val applicationContext: Context
) {
    private val notificationManager =
        applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    private var launcherRequestPermision: ActivityResultLauncher<String>? = null
    private var launcherActivityForResult: ActivityResultLauncher<Intent>? = null


    fun registerChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                configuration.channelId,
                configuration.channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mChannel.description = configuration.channelDescription
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    fun hasPermission(): Boolean {
        return notificationManager.areNotificationsEnabled()
    }

    fun shouldShowRationale(activity: ComponentActivity): Boolean {
        if (shouldShowRequestPermissionRationale(activity)) {
            return true
        }

        return false
    }

    fun prepareRequest(activity: ComponentActivity, callback: (Boolean) -> Unit) {

        launcherRequestPermision = null
        launcherActivityForResult = null

        if (!shouldShowRationale(activity)) {
            launcherRequestPermision = activity.registerForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                callback = callback
            )
        } else {
            launcherActivityForResult = activity.registerForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                callback = {
                    callback(hasPermission())
                }
            )
        }
    }

    fun launchRequest() {
        if (launcherRequestPermision !== null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                launcherRequestPermision?.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            return
        }

        if (launcherActivityForResult !== null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                launcherActivityForResult?.launch(getIntent())
            }
            return
        }

        throw IllegalStateException("Launcher is not initialized. Run prepareRequest first.")
    }

    private fun shouldShowRequestPermissionRationale(activity: ComponentActivity): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val should = ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            )
            Log.d("myapp", "Should show request permission rationale: $should")
            return should
        } else {
            return false
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getIntent(): Intent {
        return Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, applicationContext.packageName)
        }

    }
}