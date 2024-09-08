package kr.co.bigwalk.app.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.notification.NotiTypeId
import kr.co.bigwalk.app.extension.extractUrl
import kr.co.bigwalk.app.extension.getLauncherClassName
import kr.co.bigwalk.app.feed.FeedDetailActivity
import kr.co.bigwalk.app.feed.isEnableLike
import kr.co.bigwalk.app.feed_comment.FeedCommentActivity
import kr.co.bigwalk.app.feed_notification.FeedNotificationActivity
import kr.co.bigwalk.app.splash.SplashActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.walk.WalkActivity
import java.util.*

class BigwalkFcmService: FirebaseMessagingService() {

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        BigwalkFcmServiceManager.sendRegistrationToServer(p0)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        var title = ""
        var body = ""
        var data: MutableMap<String, String> = mutableMapOf()
        p0.notification?.let {
            title = it.title ?: ""
            body = it.body ?: ""
        }
        p0.data.isNotEmpty().let {
            DebugLog.d("push data: ${p0.data}")
            data = p0.data
            data["title"]?.let {
                title = it
            }
            data["body"]?.let {
                body = it
            }
        }
        sendNotification(title, body, data)
    }

    private fun sendNotification(title: String, body: String, data: MutableMap<String, String>) {
        DebugLog.d("aaaa// title=$title, body=$body, data=$data")
        if (PreferenceManager.getAccessToken().isNullOrBlank()) {
            sendNormalNotification(title, body)
            return
        }
        // 1. Campaign Notification
        val type: Int = data["type"]?.toInt() ?: -1
        if (type == NotiTypeId.CAMPAIGN.id || type == NotiTypeId.STORY.id) {
            val campaignId: Long = data["campaign"]?.toLong() ?: -1
            if (campaignId >= 0)
                sendCampaignNotification(title, body, campaignId)
            return
        }
        // 2. Event Notification
        if (type == NotiTypeId.EVENT.id || type == NotiTypeId.EVENT_WIN.id) {
            val data = data["data"]
            val url = data?.extractUrl()
            if (url != null) {
                sendEventNotification(title, body, url)
                return
            }
        }
        // 3. Mission Notification
        if (type == NotiTypeId.FEED_LIKE.id || type == NotiTypeId.FEED_COMMENT.id) {
            val actionDonationHistoryId = data["feed"]?.toLong() ?: -1L
            val campaignId: Long = data["campaign"]?.toLong() ?: -1
            if (actionDonationHistoryId != -1L) {
                sendMissionNotification(title, body, actionDonationHistoryId, campaignId)
            }
            return
        }

        // 3. Else
        sendNormalNotification(title, body)
    }

    private fun sendCampaignNotification(title: String, body: String, campaignId: Long) {
        val intent = Intent(this, WalkActivity::class.java)
        intent.putExtra("type", NotiTypeId.CAMPAIGN.id)
        intent.putExtra("campaign_id", campaignId)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelData = FCMChannels.CAMPAIGN.channelData
        val notificationBuilder = NotificationCompat.Builder(this, channelData.id)
                .setSmallIcon(R.mipmap.bw_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelData.id,
                    channelData.name,
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Calendar.getInstance().timeInMillis.toInt(), notificationBuilder.build())

        RealtimeNotification.getInstance().enable()

        BigwalkApplication.context?.let {
            val bundle = Bundle()
            bundle.putString("type", "result_post")
            bundle.putLong("campaign_id", campaignId)
            FirebaseAnalytics.getInstance(it).logEvent("notification", bundle)
        }
    }

    private fun sendEventNotification(title: String, body: String, url: String) {
        val intent = Intent(this, WalkActivity::class.java)
        intent.putExtra("type", NotiTypeId.EVENT.id)
        intent.putExtra("data", url)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelData = FCMChannels.EVENT.channelData
        val notificationBuilder = NotificationCompat.Builder(this, channelData.id)
                .setSmallIcon(R.mipmap.bw_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelData.id,
                    channelData.name,
                    NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Calendar.getInstance().timeInMillis.toInt(), notificationBuilder.build())

        RealtimeNotification.getInstance().enable()
    }


    private fun sendMissionNotification(title: String, body: String, actionDonationHistoryId: Long, campaignId: Long) {

        PreferenceManager.increaseNotificationCount()
        val count = PreferenceManager.getNotificationCount()

        val walkIntent = Intent(this, WalkActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        val campaignIntent = Intent(this, CampaignActivity::class.java).apply {
            putExtra(CampaignActivity.SELECT_TAB, 1)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        val feedDetailIntent = Intent(this, FeedDetailActivity::class.java).apply {
            putExtra(FeedDetailActivity.KEY_CAMPAIGN_ID, campaignId)
            putExtra(FeedDetailActivity.KEY_ORGANIZATION_ID, PreferenceManager.getOrganization())
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        val notificationIntent = Intent(this, FeedNotificationActivity::class.java).apply {
            putExtra(FeedNotificationActivity.KEY_CAMPAIGN_ID, campaignId)
            putExtra(FeedNotificationActivity.IS_FROM_NOTIFY, true)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        val commentIntent = Intent(this, FeedCommentActivity::class.java).apply {
            val organizationId = PreferenceManager.getOrganization()
            putExtra(FeedCommentActivity.ACTION_DONATION_HISTORY_ID, actionDonationHistoryId)
            putExtra(FeedCommentActivity.IS_ENABLE_LIKE, isEnableLike(organizationId))
            putExtra(FeedCommentActivity.ORGANIZATION_ID, organizationId)
            putExtra(FeedCommentActivity.IS_FROM_NOTIFY, true)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }

        val task = TaskStackBuilder.create(this).run {
            addParentStack(WalkActivity::class.java)
            addNextIntent(walkIntent)
            addNextIntent(campaignIntent)
            addNextIntent(feedDetailIntent)
            addNextIntent(notificationIntent)
            addNextIntent(commentIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

//        val pendingIntent = PendingIntent.getActivity(this, 0, task, PendingIntent.FLAG_ONE_SHOT)

        val channelData = FCMChannels.EVENT.channelData
        val notificationBuilder = NotificationCompat.Builder(this, channelData.id)
            .setSmallIcon(R.mipmap.bw_launcher)
            .setAutoCancel(true)
            .setContentIntent(task)
            .setContentTitle(title)
            .setContentText(body)
            .setNumber(count)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelData.id,
                channelData.name,
                NotificationManager.IMPORTANCE_DEFAULT)
            channel.enableVibration(true)
            channel.setShowBadge(true)
            notificationManager.createNotificationChannel(channel)
        }else {
            Intent("android.intent.action.BADGE_COUNT_UPDATE")
                .putExtra("badge_count", count)
                .putExtra("badge_count_package_name", packageName)
                .putExtra("badge_count_class_name", getLauncherClassName(this))
                .run { sendBroadcast(this) }
        }
        notificationManager.notify(Calendar.getInstance().timeInMillis.toInt(), notificationBuilder.build())

    }


    private fun sendNormalNotification(title: String, body: String) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelData = FCMChannels.NORMAL.channelData
        val notificationBuilder = NotificationCompat.Builder(this, channelData.id)
                .setSmallIcon(R.mipmap.bw_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(body)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelData.id,
                    channelData.name,
                    NotificationManager.IMPORTANCE_DEFAULT)
            channel.setShowBadge(false)
            channel.vibrationPattern = longArrayOf(0)
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(Calendar.getInstance().timeInMillis.toInt(), notificationBuilder.build())
    }
}