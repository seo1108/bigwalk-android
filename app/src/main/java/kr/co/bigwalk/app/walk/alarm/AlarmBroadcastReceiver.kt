package kr.co.bigwalk.app.walk.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kr.co.bigwalk.app.event.alarm.EventManager
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.walk.sensor.WalkServiceManager
import java.text.SimpleDateFormat
import java.util.*

class AlarmBroadcastReceiver: BroadcastReceiver() {

    companion object {
        const val REQUEST_ID = 11
        const val REQUEST_ID_MISSION = 12

        fun startAlarmManager(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val intent = Intent(context, AlarmBroadcastReceiver::class.java)
            intent.action = "DAILY"

            val pendingIntent = PendingIntent.getBroadcast(
                    context, REQUEST_ID, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
            }

            alarmManager?.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            )
        }

        fun startMissionAlarmManager(context: Context, expiredDate: String) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val intent = Intent(context, AlarmBroadcastReceiver::class.java)
            intent.action = "MISSIONS"

            val pendingIntent = PendingIntent.getBroadcast(
                context, REQUEST_ID_MISSION, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
            val calendar: Calendar = Calendar.getInstance(Locale.KOREA).apply {
                time = dateFormat.parse(expiredDate)
                set(Calendar.HOUR_OF_DAY, get(Calendar.HOUR_OF_DAY) - 5)
                set(Calendar.MINUTE, get(Calendar.MINUTE) - 59)
                set(Calendar.SECOND, get(Calendar.SECOND) - 59)
            }

            try {
                alarmManager?.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } catch (e: Exception) {}
        }

        fun cancelMissionAlarmManager(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val intent = Intent(context, AlarmBroadcastReceiver::class.java)
            intent.action = "MISSIONS"
            val pendingIntent = PendingIntent.getBroadcast(context, REQUEST_ID_MISSION, intent, PendingIntent.FLAG_IMMUTABLE)
            alarmManager?.cancel(pendingIntent)
        }
    }


    override fun onReceive(context: Context?, intent: Intent?) {
        DebugLog.d("AlarmBroadcastReceiver onReceive ${intent?.action}")
        if (intent?.action == "MISSIONS") {
            EventManager.sendWelcomeMissionNotYetNotification()
        } else {
            context?.let { WalkServiceManager(it).startWalkService() }
        }
    }

}