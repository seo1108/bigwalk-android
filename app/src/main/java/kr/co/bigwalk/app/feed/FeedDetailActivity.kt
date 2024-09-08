package kr.co.bigwalk.app.feed

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.donation.DonationData
import kr.co.bigwalk.app.campaign.donation.DonationStepMissionViewModel
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.databinding.ActivityFeedDetailBinding
import kr.co.bigwalk.app.feed_mypage.FeedMyPageActivity
import kr.co.bigwalk.app.feed_notification.FeedNotificationActivity


class FeedDetailActivity: FeedDonationBaseActivity(), BaseNavigator {

    companion object {
        val KEY_CAMPAIGN_ID = "KEY_CAMPAIGN_ID"
        val KEY_ORGANIZATION_ID = "KEY_ORGANIZATION_ID"
        val KEY_SORT_TYPE = "KEY_SORT_TYPE"

        lateinit var firebaseAnalytics: FirebaseAnalytics
        fun getIntent(context: Context, campaignId: Long, organizationId: Long, sortType: String) =
            Intent(context, FeedDetailActivity::class.java).apply {
                putExtra(KEY_CAMPAIGN_ID, campaignId)
                putExtra(KEY_ORGANIZATION_ID, organizationId)
                putExtra(KEY_SORT_TYPE, sortType)
            }

        var SCROLL_TO_POSITION_LOADED : Boolean = false
    }

    lateinit var binding: ActivityFeedDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_feed_detail)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val campaignId = intent.getLongExtra(KEY_CAMPAIGN_ID, -1)
        val organizationId = intent.getLongExtra(KEY_ORGANIZATION_ID, -1)
        var sortType = intent.getStringExtra(KEY_SORT_TYPE)
        if (null == sortType) sortType = "hot"
        val feedFragment = FeedFragment.newInstance(campaignId, organizationId, sortType!!)

        supportFragmentManager.beginTransaction()
            .replace(binding.feedDetailContainer.id, feedFragment)
            .commit()

        binding.ivFeedDetailBack.setOnClickListener {
            finish()
        }
        binding.feedDetailNoticeFrame.isVisible = isEnableLike(organizationId)

        binding.feedDetailNoticeFrame.setOnClickListener {
            val intent = Intent(this, FeedNotificationActivity::class.java).apply {
                putExtra(FeedNotificationActivity.KEY_CAMPAIGN_ID, campaignId)
            }
            startActivity(intent)
        }
        binding.ivFeedDetailMypage.isVisible = isEnableLike(organizationId)
        binding.ivFeedDetailMypage.setOnClickListener {
            val name = PreferenceManager.getName()
            Intent(this, FeedMyPageActivity::class.java).also {
                it.putExtra(FeedMyPageActivity.EXTRA_ORGANIZATION_ID, organizationId)
                it.putExtra(FeedMyPageActivity.EXTRA_CAMPAIGN_ID, campaignId)
                it.putExtra(FeedMyPageActivity.EXTRA_USER_NAME, name)
                it.putExtra(FeedMyPageActivity.EXTRA_IS_MIND, true)
                startActivity(it)
            }
        }

        binding.ivFeedDetailPlay.isVisible = isEnableLike(organizationId)
        binding.ivFeedDetailPlay.setOnClickListener {
            val dialog = FeedPhotoSelectFragment.newInstance(campaignId)
            dialog.show(supportFragmentManager, "FeedPhotoSelectFragment")
        }
        viewModel = DonationStepMissionViewModel(this, DonationData(campaignId))


        PreferenceManager.saveFeedInfo(-1, 0, false, 0, "", false)
        PreferenceManager.saveFeedUpload(false, -1L)
        SCROLL_TO_POSITION_LOADED = false
    }

    override fun getContext(): Activity {
        return this
    }

    override fun onResume() {
        super.onResume()
        binding.feedDetailNoticeCircle.isVisible = PreferenceManager.getNotificationCount() > 0
    }

}