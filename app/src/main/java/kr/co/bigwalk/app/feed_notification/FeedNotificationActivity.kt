package kr.co.bigwalk.app.feed_notification

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.databinding.ActivityFeedNotificationBinding
import kr.co.bigwalk.app.extension.getLauncherClassName
import kr.co.bigwalk.app.feed.FeedDetailActivity
import kr.co.bigwalk.app.feed_notification.adapter.FeedNotificationAdapter
import kr.co.bigwalk.app.util.showToast

class FeedNotificationActivity: AppCompatActivity(), BaseNavigator {

    companion object {
        const val KEY_CAMPAIGN_ID = "KEY_CAMPAIGN_ID"
        const val IS_FROM_NOTIFY = "IS_FROM_NOTIFY"
    }

    private lateinit var binding: ActivityFeedNotificationBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    private var campaignId = -1L
    private var isFromNotify = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferenceManager.resetNotificationCount()
        Intent("android.intent.action.BADGE_COUNT_UPDATE")
            .putExtra("badge_count", 0)
            .putExtra("badge_count_package_name", packageName)
            .putExtra("badge_count_class_name", getLauncherClassName(this))
            .run { sendBroadcast(this) }

        binding = ActivityFeedNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        campaignId = intent.getLongExtra(KEY_CAMPAIGN_ID, -1L)
        isFromNotify = intent.getBooleanExtra(IS_FROM_NOTIFY, false)
        if (campaignId == -1L) {
            showToast("알림 목록을 불러올 수 없습니다. 다시 시도해 주세요.")
            finish()
        }

        val viewModelFactory = FeedNotificationVIewModelFactory(campaignId, this)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(FeedNotificationViewModel::class.java)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val adapter = FeedNotificationAdapter(viewModel)
        binding.rvFeedNoti.apply {
            setHasFixedSize(true)
            setAdapter(adapter)
        }

        viewModel.feedNotifications.observe(this, Observer {
            adapter.submitList(it)
        })

    }

    override fun getContext(): Activity {
        return this
    }

    override fun onDestroy() {
        if (isFromNotify) {
            val intent = Intent(this, FeedDetailActivity::class.java).apply {
                putExtra(FeedDetailActivity.KEY_CAMPAIGN_ID, campaignId)
                putExtra(FeedDetailActivity.KEY_ORGANIZATION_ID, PreferenceManager.getOrganization())
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
        super.onDestroy()
    }
}