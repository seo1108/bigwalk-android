package kr.co.bigwalk.app.walk.sensor

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.os.ResultReceiver
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.walk.WalkUtil.Companion.syncDailyWalk
import kr.co.bigwalk.app.extension.valueToCommaString
import kr.co.bigwalk.app.notification.ChannelData
import kr.co.bigwalk.app.notification.FCMChannels
import kr.co.bigwalk.app.splash.SplashActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.isPermissionGranted
import kr.co.bigwalk.app.walk.alarm.AlarmBroadcastReceiver
import kr.co.bigwalk.app.walk.manage.WalkWorkManage
import java.util.concurrent.TimeUnit


class WalkService : Service(), StepMaxCallback, StepCallback {

    private var isStartedAsForeground = false

    companion object {
        const val ARG_SYNC_DAILY_STEP = "SYNC_DAILY_STEP"
        const val ARG_SYNC_DONABLE_STEP = "SYNC_DONABLE_STEP"
        const val ARG_DAILY_STEP_TO_FG = "DAILY_STEP_TO_FG"
        const val ARG_SAVED_DAY_TO_FG = "SAVED_DAY_TO_FG"
        const val ARG_RECEIVER = "RECEIVER"
        private const val PERMISSION_REQUEST_CODE = 1001

        fun startService(context: Context, intent: Intent) {
            DebugLog.d("WalkService Start!!")
            // Android 12 이상 호환 문제 테스트
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                WalkWorkManage.receiveIntent = intent
                val request =
                    OneTimeWorkRequestBuilder<WalkWorkManage>()
                        .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                        .build()

                WorkManager.getInstance(context).enqueue(request)
            } else {
                ContextCompat.startForegroundService(context, intent)
            }
        }

        fun stopService(context: Context) {
            context.stopService(Intent(context, WalkService::class.java))
        }
    }

    private var isRegisteredStepSensor = false

    override fun onCreate() {
        super.onCreate()
        Log.d("WalkService", "on create")
        createNotificationChannel()
        //sendWalkingNotification(PreferenceManager.getDailyStep(), PreferenceManager.getDonableStep())
        WalkSensor.stepMaxCallback = this
        WalkSensor.stepCallback = this
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("WalkService", "start command. $isRegisteredStepSensor")

        if (checkAndRequestPermissions()) {
            if (!isStartedAsForeground) {
                startForegroundService()
                isStartedAsForeground = true
            }
        }

        if (!isRegisteredStepSensor) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (!isPermissionGranted(this, Manifest.permission.ACTIVITY_RECOGNITION)) {
                    Log.d("WalkService", "register walk sensor")
                    WalkSensor.registerStepSensor()
                    isRegisteredStepSensor = true
                }
            } else {
                Log.d("WalkService", "register walk sensor")
                WalkSensor.registerStepSensor()
                isRegisteredStepSensor = true
            }
        }

        // Sync BG & FG Start
        val currentDay = syncDailyWalk()
        intent?.let {
            val result = syncFGToBG(intent, PreferenceManager.getDonableStep())
            syncBGToFG(it, currentDay, result.first)
        }
        // Sync BG & FG End

        sendWalkingNotification(PreferenceManager.getDailyStep(), PreferenceManager.getDonableStep())

        return START_STICKY
    }

    private fun checkAndRequestPermissions(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_HEALTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this as Activity, arrayOf(Manifest.permission.FOREGROUND_SERVICE_HEALTH), PERMISSION_REQUEST_CODE)
                return false
            }
        }
        return true
    }

    private fun startForegroundService() {
        val dailyStep = PreferenceManager.getDailyStep()
        val donableStep = PreferenceManager.getDonableStep()
        sendWalkingNotification(dailyStep, donableStep)
    }

    private fun syncFGToBG(intent: Intent, bgDonableStep: Int): Pair<Int, Int> {
        val fgDonableStep = intent.getIntExtra(ARG_SYNC_DONABLE_STEP, bgDonableStep)
        val fgDailyStepToSync = intent.getIntExtra(ARG_SYNC_DAILY_STEP, -1)
        if (fgDailyStepToSync >= 0)
            PreferenceManager.saveDailyStep(fgDailyStepToSync)
        PreferenceManager.saveDonableStep(fgDonableStep)
        Log.d("WalkService", "sync service to activity(syncFGToBG) - daily: $fgDailyStepToSync")
        return PreferenceManager.getDailyStep() to PreferenceManager.getDonableStep()
    }

    private fun syncBGToFG(intent: Intent, currentDay: Int, dailyStep: Int) {
        val bundle = Bundle()
        val receiver = intent.getParcelableExtra<ResultReceiver>(ARG_RECEIVER)
        receiver?.let {
            bundle.putInt(ARG_DAILY_STEP_TO_FG, dailyStep)
            bundle.putInt(ARG_SAVED_DAY_TO_FG, currentDay)

            Log.d("WalkService", "sync service to activity - daily: $dailyStep")
            receiver.send(AlarmBroadcastReceiver.REQUEST_ID, bundle)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val channelData: ChannelData = FCMChannels.WALK.channelData
                val serviceChannel = NotificationChannel(
                    channelData.id, channelData.name,
                    NotificationManager.IMPORTANCE_LOW
                )
                val manager = getSystemService(NotificationManager::class.java)
                serviceChannel.vibrationPattern = longArrayOf(0)
                serviceChannel.enableVibration(true);
                serviceChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                serviceChannel.setShowBadge(false)
                manager!!.createNotificationChannel(serviceChannel)
                serviceChannel.importance = NotificationManager.IMPORTANCE_UNSPECIFIED
                manager!!.createNotificationChannel(serviceChannel)
            } catch (e: Exception) {
                // java.lang.RuntimeException: Unable to create service kr.co.bigwalk.app.walk.sensor.WalkService: java.lang.IllegalArgumentException: Invalid importance level 용 에러 처리 (테스트 필요)
                val channelData: ChannelData = FCMChannels.WALK.channelData
                val serviceChannel = NotificationChannel(
                    channelData.id, channelData.name,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                val manager = getSystemService(NotificationManager::class.java)
                serviceChannel.vibrationPattern = longArrayOf(0)
                serviceChannel.enableVibration(true);
                serviceChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
                serviceChannel.setShowBadge(false)
                manager!!.createNotificationChannel(serviceChannel)
                serviceChannel.importance = NotificationManager.IMPORTANCE_DEFAULT
                manager!!.createNotificationChannel(serviceChannel)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val sensorManager = getSystemService(Context.SENSOR_SERVICE)
        if (sensorManager is SensorManager) {
            sensorManager.unregisterListener(WalkSensor.registeredListener)
        }
    }

    override fun onStepMaxed() {
//        showToast("WalkService - onStepMaxed()")
        sendWalkMaxedNotification()
    }

    private fun sendWalkMaxedNotification() {
        val intent = Intent(this, LauncherActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelData = FCMChannels.WALK_MAXED.channelData
        val notificationBuilder = NotificationCompat.Builder(this, channelData.id)
            .setSmallIcon(R.mipmap.bw_launcher)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        notificationBuilder.setContentTitle("걸음수 알림")
        notificationBuilder.setContentText("기부 가능한 걸음수가 가득 찾어요!!")

        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelData.id,
                    channelData.name,
                NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(2 /* ID of notification */, notificationBuilder.build())
    }

    private fun sendWalkingNotification(dailyStep: Int, donableStep: Int) {
        try {
            val notificationIntent = Intent(this, SplashActivity::class.java)
            val channelData = FCMChannels.WALK.channelData
            val pendingIntent = PendingIntent.getActivity(
                this,
                0,
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notification = NotificationCompat.Builder(this, channelData.id)
                .setContentTitle("기부 가능한 걸음 ${donableStep.valueToCommaString()}")
                .setContentText("오늘의 걸음 ${dailyStep.valueToCommaString()}")
                .setSmallIcon(R.mipmap.bw_mono)
                .setContentIntent(pendingIntent)
                .setShowWhen(false)
                .build()
            startForeground(1, notification)
        } catch (e: Exception) {}
    }

    override fun onStepChanged(dailyStep: Int, donableStep: Int) {
        sendWalkingNotification(dailyStep, donableStep)
    }

}