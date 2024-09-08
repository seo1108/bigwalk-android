package kr.co.bigwalk.app.lock_screen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.splash.SplashActivity
import kr.co.bigwalk.app.walk.sensor.WalkService


class LockScreenService : Service() {

    private var lockScreenReceiver: LockScreenReceiver? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
//        createNotificationChannel()
//        val notificationIntent = Intent(this, SplashActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
//        val notification = NotificationCompat.Builder(this, LockScreenManager.CHANNEL_ID)
//            .setSmallIcon(R.mipmap.bw_mono)
//            .setContentIntent(pendingIntent)
//            .build()
//        startForeground(1, notification)
    }

    override fun onDestroy() {
        super.onDestroy()
        //unregisterLockReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        //registerLockReceiver()
        return START_STICKY
    }

//    private fun registerLockReceiver() {
//        if (lockScreenReceiver != null) return
//
//        val filter = IntentFilter()
//        filter.addAction(Intent.ACTION_SCREEN_OFF)
//
//        lockScreenReceiver = LockScreenReceiver()
//        registerReceiver(lockScreenReceiver, filter)
//    }
//
//    private fun unregisterLockReceiver() {
//        if (lockScreenReceiver == null) return
//
//        unregisterReceiver(lockScreenReceiver)
//        lockScreenReceiver = null
//    }
//
//    private fun createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val serviceChannel = NotificationChannel(
//                LockScreenManager.CHANNEL_ID, "BIGWALK",
//                NotificationManager.IMPORTANCE_DEFAULT
//            )
//            val manager = getSystemService(NotificationManager::class.java)
//            manager!!.createNotificationChannel(serviceChannel)
//        }
//    }

}