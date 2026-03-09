package pl.slaszu.workbreak.application.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.toKotlinLocalDateTime
import pl.slaszu.workbreak.application.MuteForToday
import pl.slaszu.workbreak.application.SetScheduleAlarm
import pl.slaszu.workbreak.domain.notification.NotificationService
import pl.slaszu.workbreak.domain.repository.SettingRepository
import pl.slaszu.workbreak.domain.repository.WorkWeekRepository
import pl.slaszu.workbreak.domain.utils.getDayName
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

@AndroidEntryPoint
class MuteForTodayReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var useCaseMuteForToday: MuteForToday

    @Inject
    lateinit var workWeekRepository: WorkWeekRepository

    @Inject
    lateinit var settingRepository: SettingRepository

    @Inject
    lateinit var useCaseSetScheduleAlarm: SetScheduleAlarm

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.d("myapp", "MuteForTodayReceiver")

        if (intent == null) {
            Log.d("myapp", "MuteForTodayReceiver intent is null")
            return
        }

        val workWeek = runBlocking {
            workWeekRepository.get().first()
        }
        val setting = runBlocking {
            settingRepository.get().first()
        }

        val muteUntil = useCaseMuteForToday.getMuteUntilNextFreeTime(
            workWeek,
            LocalDateTime.now(ZoneId.systemDefault())
        )

        runBlocking {
            settingRepository.persist(
                setting.copy(
                    muteUntil = muteUntil?.toKotlinLocalDateTime()
                )
            )
        }

        Log.d("myapp", "MuteForTodayReceiver muteUntil: $muteUntil")

        val alarm = useCaseSetScheduleAlarm.setNextScheduleAlarm(
            workWeek = workWeek,
            nowTime = LocalDateTime.now(ZoneId.systemDefault()),
            startWorkAlarmFlag = setting.showWorkStartReminder,
            endWorkAlarmFlag = setting.showWorkEndReminder,
            muteUntil = muteUntil
        )

        if (alarm == null) {
            Log.d("myapp", "MuteForTodayReceiver alarm is null")
            return
        }

        val dayName = alarm.alarmDateTime.getDayName()

        notificationService.displayNotification(
            title = "Notifications muted",
            message = "Your reminders will resume on $dayName"
        )
    }
}