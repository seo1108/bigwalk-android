package kr.co.bigwalk.app.walk.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.walk.sensor.WalkServiceManager

class PackageReceiver: BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.run {
            when (action) {
                Intent.ACTION_MY_PACKAGE_REPLACED -> {// 앱 업데이트 시 walkservice start
                    DebugLog.d("ACTION_MY_PACKAGE_REPLACED")
                    if (!PreferenceManager.getAccessToken().isNullOrBlank()) {
                        context?.let { context ->
                            WalkServiceManager(context).startWalkService()
                        }
                    }
                }
            }
        }
    }
}