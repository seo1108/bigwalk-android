package kr.co.bigwalk.app.walk.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.walk.sensor.WalkServiceManager

class ScreenEventReceiver {

    companion object {
        fun register(context: Context) {
            val intentFilter = IntentFilter(Intent.ACTION_SCREEN_OFF)
            intentFilter.addAction(Intent.ACTION_SCREEN_ON)
            val receiver = object: BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    val action = intent!!.action

                    when (action) {
                        Intent.ACTION_SCREEN_ON -> {
                            context?.let { WalkServiceManager(it).startWalkService() }
                        }
                        Intent.ACTION_SCREEN_OFF -> { }
                    }
                }
            }

            context.registerReceiver(receiver, intentFilter);
        }
    }
}