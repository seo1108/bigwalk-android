package kr.co.bigwalk.app.feed_mypage

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.blame.BlameActivity
import kr.co.bigwalk.app.blame.BlameType
import kr.co.bigwalk.app.campaign.donation.DonationData
import kr.co.bigwalk.app.campaign.donation.DonationStepMissionViewModel
import kr.co.bigwalk.app.data.feed.dto.FeedCategory
import kr.co.bigwalk.app.databinding.ActivityFeedMypageBinding
import kr.co.bigwalk.app.feed.*
import kr.co.bigwalk.app.feed.category.FeedByCategoryFragment

class FeedMyPageActivity : FeedDonationBaseActivity(), BaseNavigator {

    lateinit var binding: ActivityFeedMypageBinding
    lateinit var firebaseAnalytics: FirebaseAnalytics

    companion object {
        const val EXTRA_ORGANIZATION_ID = "EXTRA_ORGANIZATION_ID"
        const val EXTRA_CAMPAIGN_ID = "EXTRA_CAMPAIGN_ID"
        const val EXTRA_USER_ID = "EXTRA_USER_ID"
        const val EXTRA_USER_NAME = "EXTRA_USER_NAME"
        const val EXTRA_IS_MIND = "EXTRA_IS_MIND"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_feed_mypage)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val organizationId = intent.getLongExtra(EXTRA_ORGANIZATION_ID, -1L)
        val campaignId = intent.getLongExtra(EXTRA_CAMPAIGN_ID, -1L)
        val userId = intent.getLongExtra(EXTRA_USER_ID, -1L)
        val userName = intent.getStringExtra(EXTRA_USER_NAME)
        val isMine = intent.getBooleanExtra(EXTRA_IS_MIND, false)

        val isEnableLike = isEnableLike(organizationId)
        val category = FeedCategory(
            if (isMine) FeedViewModel.FeedCategoryType.ME else FeedViewModel.FeedCategoryType.USER,
            -1,
            "카테고리 이름"
        )

        val feedFragment = FeedByCategoryFragment.newInstance(this, campaignId, category, isEnableLike, organizationId, "my", userId, isMine)
        supportFragmentManager.beginTransaction()
            .replace(binding.feedMypageContainer.id, feedFragment)
            .commit()

        binding.tvFeedMypageTitle.text = userName
        binding.ivFeedMypagePlay.isVisible = isMine
        binding.ivFeedMypageBlock.isVisible = isMine
        binding.btnUserBlocking.isGone = isMine
        binding.ivFeedMypagePlay.setOnClickListener {
            val dialog = FeedPhotoSelectFragment.newInstance(campaignId)
            dialog.show(supportFragmentManager, "FeedPhotoSelectFragment")
        }
        binding.ivFeedMypageBlock.setOnClickListener {
            startActivity(UserBlockListActivity.getIntent(this))
            FirebaseAnalytics.getInstance(this).logEvent("feed_btn_blocklist_click", Bundle())
        }
        binding.btnUserBlocking.setOnClickListener {
            startActivity(BlameActivity.getIntent(this, -1L, userId, BlameType.USER))
        }
        binding.ivFeedMypageBack.setOnClickListener {
            finish()
        }

        viewModel = DonationStepMissionViewModel(this, DonationData(campaignId))

        FeedManager.blockUserId.observe(this, Observer {
            if (userId == it) {
                startActivity(
                    FeedDetailActivity.getIntent(this@FeedMyPageActivity, campaignId, organizationId, "my").addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                )
            }
        })
    }

    override fun getContext(): Activity {
        return this
    }
}