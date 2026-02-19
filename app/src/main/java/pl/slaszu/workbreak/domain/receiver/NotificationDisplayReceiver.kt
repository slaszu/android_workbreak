package pl.slaszu.workbreak.domain.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.Json
import pl.slaszu.workbreak.application.SetScheduleAlarm
import pl.slaszu.workbreak.domain.notification.NotificationService
import pl.slaszu.workbreak.domain.schedule.AlarmData
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class NotificationDisplayReceiver() : BroadcastReceiver() {

    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var useCaseSetScheduleAlarm: SetScheduleAlarm

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("myapp", "NotificationDisplayReceiver")

        if (intent == null) return

        val serializedData = intent.getStringExtra("BREAK")
        if (serializedData == null) return

        val breakData = Json.decodeFromString<AlarmData>(serializedData)
        Log.d("myapp", "NotificationDisplayReceiver breakData: $breakData")

        useCaseSetScheduleAlarm.setNextScheduleAlarm(
            workWeek = breakData.workWeek,
            dateTime = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(5)
        )
        Log.d("myapp", "NotificationDisplayReceiver setNextBreakScheduleAlarm done")

        notificationService.displayNotification(breakData)

        Log.d("myapp", "NotificationDisplayReceiver displayNotification done")
    }
}