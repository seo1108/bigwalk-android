package kr.co.bigwalk.app.data

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kr.co.bigwalk.app.BigwalkApplication

object BroadcastManager {
    private val broadcast = BigwalkApplication.context?.let { LocalBroadcastManager.getInstance(it) }

    enum class Key {
        WELCOME_MISSION1,
        WELCOME_MISSION2
    }

    fun sendWelcomeMission1Broadcast() {
        broadcast?.sendBroadcast(Intent(Key.WELCOME_MISSION1.name))
    }

    fun sendWelcomeMission2Broadcast() {
        broadcast?.sendBroadcast(Intent(Key.WELCOME_MISSION2.name))
    }
}