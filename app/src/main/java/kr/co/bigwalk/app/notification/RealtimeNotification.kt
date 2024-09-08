package kr.co.bigwalk.app.notification

import androidx.databinding.ObservableField
import kr.co.bigwalk.app.data.PreferenceManager

class RealtimeNotification private constructor() {
    companion object {
        @Volatile private var instance: RealtimeNotification? = null

        @JvmStatic fun getInstance(): RealtimeNotification =
            instance ?: synchronized(this) {
                instance ?: RealtimeNotification().also {
                    instance = it
                }
            }
    }
    var notification: ObservableField<Boolean> = ObservableField(PreferenceManager.isNotification())
    fun sync() {
        notification.set(PreferenceManager.isNotification())
    }
    fun enable() {
        PreferenceManager.enableNotification()
        sync()
    }
    fun disable() {
        PreferenceManager.disableNotification()
        sync()
    }
}