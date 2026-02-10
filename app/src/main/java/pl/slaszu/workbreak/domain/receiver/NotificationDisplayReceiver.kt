package pl.slaszu.workbreak.domain.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.serialization.json.Json
import pl.slaszu.workbreak.domain.schedule.BreakScheduleAlarm

class NotificationDisplayReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("myapp", "NotificationDisplayReceiver")

        if (intent == null) return

        val serializedData = intent.getStringExtra("BREAK")
        if (serializedData == null) return

        val breakData = Json.decodeFromString<BreakScheduleAlarm>(serializedData)

        Log.d("myapp", "NotificationDisplayReceiver breakData: $breakData")
    }
}