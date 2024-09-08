package kr.co.bigwalk.app.walk.sensor

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.ResultReceiver
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.walk.alarm.AlarmBroadcastReceiver
import kr.co.bigwalk.app.walk.alarm.ScreenEventReceiver
import kr.co.bigwalk.app.walk.alarm.StepManager

class WalkServiceManager(val context: Context, val callback: SyncBGToFGCallback? = null) {

    interface SyncBGToFGCallback {
        fun onSuccess()
    }

    private val handler = Handler()
    private val resultReceiver: ResultReceiver = object : ResultReceiver(handler) {
        override fun onReceiveResult(resultCode: Int, resultData: Bundle) {
            super.onReceiveResult(resultCode, resultData)
            DebugLog.w("onReceiveResult $resultCode")
            if (resultCode == AlarmBroadcastReceiver.REQUEST_ID) {
                DebugLog.w("onReceiveResult $context")
                context?.let { context ->
                    DebugLog.w("onReceiveResult in")
                    val dailyStep = resultData.getInt(WalkService.ARG_DAILY_STEP_TO_FG)
                    val savedDay = resultData.getInt(WalkService.ARG_SAVED_DAY_TO_FG)
                    PreferenceManager.saveDailyStep(dailyStep)
                    PreferenceManager.saveCurrentDay(savedDay)
                    DebugLog.d("received step data - daily: $dailyStep, current day: $savedDay")
                    StepManager.uploadAllWalksAndClear() { callback?.onSuccess() }
                }
            }
        }
    }

    fun startWalkService(option: Boolean = false) {
        // 1 Walk service
        val intent = Intent(context, WalkService::class.java)
        if (option) intent.putExtra(WalkService.ARG_RECEIVER, resultReceiver)
        intent.putExtra(WalkService.ARG_SYNC_DONABLE_STEP, PreferenceManager.getDonableStep())
        WalkService.startService(context, intent)

        if (option) {
            // 2 Register step sensor
            WalkSensor.registerStepSensor()
            // 3 Start alarm manager
            AlarmBroadcastReceiver.startAlarmManager(context)
            ScreenEventReceiver.register(context)
        }
    }

    fun syncStepToForeground(dailyStep: Int, donableStep: Int) {
        val intent = Intent(context, WalkService::class.java)
        intent.putExtra(WalkService.ARG_SYNC_DAILY_STEP, dailyStep)
        intent.putExtra(WalkService.ARG_SYNC_DONABLE_STEP, donableStep)
        WalkService.startService(context, intent)
    }

    fun syncDonableStepToForeground(donableStep: Int) {
        val intent = Intent(context, WalkService::class.java)
        intent.putExtra(WalkService.ARG_SYNC_DONABLE_STEP, donableStep)
        WalkService.startService(context, intent)
    }
}