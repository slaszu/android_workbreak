package pl.slaszu.workbreak.application.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import pl.slaszu.workbreak.application.SetScheduleAlarm
import pl.slaszu.workbreak.domain.model.alarm.Alarm
import pl.slaszu.workbreak.domain.notification.NotificationService
import pl.slaszu.workbreak.domain.repository.SettingRepository
import pl.slaszu.workbreak.domain.repository.WorkWeekRepository
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class NotificationDisplayReceiver() : BroadcastReceiver() {

    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var useCaseSetScheduleAlarm: SetScheduleAlarm

    @Inject
    lateinit var workWeekRepository: WorkWeekRepository

    @Inject
    lateinit var settingRepository: SettingRepository

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("myapp", "NotificationDisplayReceiver")

        if (intent == null) {
            Log.d("myapp", "NotificationDisplayReceiver intent is null")
            return
        }

        val serializedData = intent.getStringExtra("ALARM")
        if (serializedData == null) {
            Log.d("myapp", "NotificationDisplayReceiver serializedData is null")
            return
        }

        val alarm = Json.decodeFromString<Alarm>(serializedData)
        Log.d("myapp", "NotificationDisplayReceiver breakData: $alarm")

        val setting = runBlocking {
            settingRepository.get().first()
        }
        val workWeek = runBlocking {
            workWeekRepository.get().first()
        }

        val nextAlarm = useCaseSetScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeek,
            dateTime = LocalDateTime.now(ZoneId.systemDefault()).plusSeconds(5),
            startWorkAlarmFlag = setting.showWorkStartReminder,
            endWorkAlarmFlag = setting.showWorkEndReminder
        )
        Log.d("myapp", "NotificationDisplayReceiver setNextScheduleAlarm done")
        Log.d("myapp", "NotificationDisplayReceiver nextAlarm: $nextAlarm")

        notificationService.displayNotification(alarm)

        Log.d("myapp", "NotificationDisplayReceiver displayNotification done")
    }
}