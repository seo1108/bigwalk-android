package kr.co.bigwalk.app.walk.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.walk.sensor.WalkServiceManager

class RebootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            if (!PreferenceManager.getAccessToken().isNullOrBlank()) {
                context?.let { context ->
                    WalkServiceManager(context).startWalkService()
                }
            }
        }
    }
}