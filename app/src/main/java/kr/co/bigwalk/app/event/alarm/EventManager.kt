package kr.co.bigwalk.app.event.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.mission.WelcomeMissionStatus
import kr.co.bigwalk.app.event.EventActivity
import kr.co.bigwalk.app.notification.FCMChannels
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.intValueToCommaString
import kr.co.bigwalk.app.walk.WalkActivity


//이벤트에 대한 로직을 담당하는 객체
object EventManager {
    private const val NOTI_ID_MISSION_COMPLETE = 513
    private const val NOTI_ID_MISSION_NOT_YET = 514
    private const val NOTI_ID_WELCOME_MISSION_SUMMARY = 5000
    private const val NOTI_GROUP_ID_WELCOME_MISSION = "WELCOME_MISSION"

    private fun checkWelcomeMission1(): Boolean {
        return PreferenceManager.getWelcomeMission1Max()>0 && PreferenceManager.getWelcomeMission1()!=-1 && !PreferenceManager.getWelcomeMission1Completed()
    }

    fun calculateStep(currentStep: Int) {//current step: 추가된 걸음 수
        if(currentStep < 0) { //device reboot
            return
        }
        if (!checkWelcomeMission1()) {
            return
        }

        val prevMissionStep = PreferenceManager.getWelcomeMission1()
        var missionStep = PreferenceManager.getWelcomeMission1() + currentStep
        if (missionStep>PreferenceManager.getWelcomeMission1Max()) {
            missionStep = PreferenceManager.getWelcomeMission1Max()
        }
        PreferenceManager.saveWelcomeMission1(missionStep)

        DebugLog.d("prevMissionStep: $prevMissionStep / missionStep: $missionStep / " +
                "PreferenceManager.getWelcomeMission1(): ${PreferenceManager.getWelcomeMission1()}")

        // 걸음 미션 성공 여부 체크
        if (PreferenceManager.getWelcomeMission1Max()>0 &&
            missionStep==PreferenceManager.getWelcomeMission1Max() &&
            prevMissionStep < PreferenceManager.getWelcomeMission1Max() &&
            !PreferenceManager.getWelcomeMission1Completed()) {

            PreferenceManager.saveWelcomeMission1Completed(true)
            sendWelcomeMission1Notification()

            if (haveClearedAllMissions()) {
                PreferenceManager.saveWelcomeMissionStatus(WelcomeMissionStatus.COMPLETED.type)
                sendWelcomeMissionCompletedNotification()
            }
        }
    }

    fun calculateDonationCount() {
        if (PreferenceManager.getWelcomeMission2Max()==0) {
            return
        }
        val prevDonationCount = PreferenceManager.getWelcomeMission2()
        PreferenceManager.saveWelcomeMission2(PreferenceManager.getWelcomeMission2() + 1)

        // 걸음 기부 미션 성공 여부 체크
        if (PreferenceManager.getWelcomeMission2Max()>0 &&
            PreferenceManager.getWelcomeMission2()==PreferenceManager.getWelcomeMission2Max() &&
            prevDonationCount < PreferenceManager.getWelcomeMission2Max() &&
            !PreferenceManager.getWelcomeMission2Completed()) {

            PreferenceManager.saveWelcomeMission2Completed(true)
            sendWelcomeMission2Notification()

            if (haveClearedAllMissions()) {
                PreferenceManager.saveWelcomeMissionStatus(WelcomeMissionStatus.COMPLETED.type)
                sendWelcomeMissionCompletedNotification()
            }
        }
    }

    private fun haveClearedAllMissions(): Boolean {
        return PreferenceManager.getWelcomeMission1Completed() && PreferenceManager.getWelcomeMission2Completed()
    }

    private fun sendWelcomeMission1Notification() {
        DebugLog.d("Mission1")
        val format = BigwalkApplication.context?.getString(R.string.welcome_mission1_noti_msg_text)!!
        sendNotification(
            BigwalkApplication.context?.getString(R.string.welcome_mission1_noti_msg_title)!!,
            String.format(format, intValueToCommaString(PreferenceManager.getWelcomeMission1Max())),
            NOTI_ID_MISSION_COMPLETE
        )
    }

    private fun sendWelcomeMission2Notification() {
        val format = BigwalkApplication.context?.getString(R.string.welcome_mission2_noti_msg_text)!!
        sendNotification(
            BigwalkApplication.context?.getString(R.string.welcome_mission2_noti_msg_title)!!,
            String.format(format, PreferenceManager.getWelcomeMission2Max()),
            NOTI_ID_MISSION_COMPLETE
        )
    }

    private fun sendWelcomeMissionCompletedNotification() {
        DebugLog.d("All Missions Completed")
        sendNotification(
            BigwalkApplication.context?.getString(R.string.welcome_mission_completed_noti_msg_title)!!,
            BigwalkApplication.context?.getString(R.string.welcome_mission_completed_noti_msg_text)!!,
            NOTI_ID_MISSION_COMPLETE
        )
    }

    fun sendWelcomeMissionNotYetNotification() {
        val format = BigwalkApplication.context?.getString(R.string.welcome_mission_not_yet_noti_msg_text)!!
        val count = if (!PreferenceManager.getWelcomeMission1Completed() && !PreferenceManager.getWelcomeMission2Completed()) 2 else 1
        val almostText = BigwalkApplication.context?.getString(R.string.welcome_mission_almost_noti_msg_text)!!
        sendNotification(
            BigwalkApplication.context?.getString(R.string.welcome_mission_not_yet_noti_msg_title)!!,
            if (haveClearedAllMissions()) almostText else String.format(format, count),
            NOTI_ID_MISSION_NOT_YET
        )
    }

    private fun sendNotification(title: String, msg: String, notiId: Int) {
        DebugLog.d("title: $title")
        val walkIntent = Intent(BigwalkApplication.context, WalkActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        val eventIntent = Intent(BigwalkApplication.context, EventActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
        val task = TaskStackBuilder.create(BigwalkApplication.context).run {
            addParentStack(WalkActivity::class.java)
            addNextIntent(walkIntent)
            addNextIntent(eventIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val channelData = FCMChannels.MISSION.channelData
        val notificationBuilder = NotificationCompat.Builder(BigwalkApplication.context!!, channelData.id)
            .setSmallIcon(R.mipmap.bw_launcher)
            .setAutoCancel(true)
            .setContentIntent(task)
            .setContentTitle(title)
            .setContentText(msg)
            .setGroup(NOTI_GROUP_ID_WELCOME_MISSION)

        val notificationManager = BigwalkApplication.context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelData.id,
                channelData.name,
                NotificationManager.IMPORTANCE_DEFAULT)
            channel.vibrationPattern = longArrayOf(0)
            channel.enableVibration(true)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(notiId, notificationBuilder.build())
        notificationManager.notify(NOTI_ID_WELCOME_MISSION_SUMMARY, getSummaryNoti(BigwalkApplication.context!!, channelData.id).build())
    }

    private fun getSummaryNoti(context: Context, channelId: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.bw_launcher)
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setGroup(NOTI_GROUP_ID_WELCOME_MISSION)
            .setGroupSummary(true)
    }
}