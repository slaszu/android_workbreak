package pl.slaszu.schedule

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.net.toUri

class Schedule(
    private val applicationContext: Context
) {
    private val alarmManager =
        applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private var launcher: ActivityResultLauncher<Intent>? = null

    fun hasPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return alarmManager.canScheduleExactAlarms()
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun getIntent(): Intent {
        return Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = "package:${applicationContext.packageName}".toUri()
            //    flags = FLAG_ACTIVITY_NEW_TASK
        }
    }

    fun prepareRequest(activity: ComponentActivity, callback: (Boolean) -> Unit) {
        launcher =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                callback(this.hasPermission())
            }
    }

    fun launch() {
        if (launcher !== null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                launcher?.launch(this.getIntent())
            }
            return
        }

        throw IllegalStateException("Launcher is not initialized. Run prepareRequest first.")
    }
}