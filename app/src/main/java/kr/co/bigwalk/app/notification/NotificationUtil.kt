package kr.co.bigwalk.app.notification

import android.app.NotificationManager
import android.content.Context
import android.os.Build

fun resetBadgeCounterOfPushMessages(context: Context) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        notificationManager.cancelAll()
    }
}