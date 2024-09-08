package kr.co.bigwalk.app.lock_screen

import android.content.Intent
import android.os.Build
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.util.DebugLog

object LockScreenManager {

    const val CHANNEL_ID = "BIGWALK_LOCKSCREEN_CHANNEL_ID"

//    fun getLockScreenStatus() : Boolean {
//        return PreferenceManager.getLockScreenSetting()
//    }

//    fun startService() {
//        DebugLog.d("LockScreen Service Start")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val intent = Intent(BigwalkApplication.context, LockScreenService::class.java)
//            BigwalkApplication.context?.startForegroundService(intent)
//        } else {
//            val intent = Intent(BigwalkApplication.context, LockScreenService::class.java)
//            BigwalkApplication.context?.startService(intent)
//        }
//    }

//    fun stopService() {
//        val intent = Intent(BigwalkApplication.context, LockScreenService::class.java)
//        BigwalkApplication.context?.stopService(intent)
//    }
}