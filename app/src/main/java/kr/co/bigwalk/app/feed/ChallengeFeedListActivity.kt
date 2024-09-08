package kr.co.bigwalk.app.feed

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.databinding.ActivityChallengeFeedListBinding
import kr.co.bigwalk.app.feed.adapter.FeedChallengeRecyclerAdapter
import kr.co.bigwalk.app.feed.category.FeedByCategoryFragment
import kr.co.bigwalk.app.feed.category.FeedByCategoryViewModel
import kr.co.bigwalk.app.feed.category.FeedByCategoryViewModelFactory
import kr.co.bigwalk.app.util.DebugLog

class ChallengeFeedListActivity: AppCompatActivity(), BaseNavigator {

    companion object {
        const val CAMPAIGN_ID = "CAMPAIGN_ID"
        const val CATEGORY_TYPE = "CATEGORY_TYPE"
        const val CATEGORY_ID = "CATEGORY_ID"
        const val CATEGORY_NAME = "CATEGORY_NAME"
        const val IS_ENABLE_LIKE = "IS_ENABLE_LIKE"
        const val ORGANIZATION_ID = "ORGANIZATION_ID"
        const val USER_ID = "USER_ID"
        const val IS_MY_PAGE = "IS_MY_PAGE"


        lateinit var firebaseAnalytics: FirebaseAnalytics
        fun getIntent(context: Context, campaignId: Long, organizationId: Long) =
            Intent(context, ChallengeFeedListActivity::class.java).apply {
                putExtra(CAMPAIGN_ID, campaignId)
                putExtra(ORGANIZATION_ID, organizationId)
            }
    }

    private lateinit var binding: ActivityChallengeFeedListBinding
    private lateinit var viewModel: FeedByCategoryViewModel
    private lateinit var factory: FeedByCategoryViewModelFactory
    private lateinit var adapter: FeedChallengeRecyclerAdapter

    private var isEnableLike = false
    private var organizationId = -1L
    private var isMyPage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isEnableLike = intent.getBooleanExtra(IS_ENABLE_LIKE, false)
        organizationId = intent.getLongExtra(ORGANIZATION_ID, -1)
        isMyPage = intent.getBooleanExtra(IS_MY_PAGE, false)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_challenge_feed_list)

        factory = FeedByCategoryViewModelFactory(this,
            intent.getLongExtra(FeedByCategoryFragment.CAMPAIGN_ID, -1),
            FeedViewModel.getFeedCategoryType(intent.getIntExtra(CATEGORY_TYPE, 0)),
            intent.getLongExtra(CATEGORY_ID, -1),
            intent.getLongExtra(USER_ID, -1))

        viewModel = ViewModelProvider(this, factory).get(FeedByCategoryViewModel::class.java)
        ChallengeFeedListActivity.firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        val campaignId = intent.getLongExtra(FeedDetailActivity.KEY_CAMPAIGN_ID, -1)
        val organizationId = intent.getLongExtra(FeedDetailActivity.KEY_ORGANIZATION_ID, -1)

        DebugLog.d("challengeViewModel ${PreferenceManager.getSelectedChallengeId()}")
        DebugLog.d("challengeViewModel ${PreferenceManager.getSelectedImageId()}")
        DebugLog.d("challengeViewModel ${PreferenceManager.getSelectedPage()}")
        DebugLog.d("challengeViewModel ${PreferenceManager.getSelectedSize()}")
        /*viewModel!!.requestHitFeedList(PreferenceManager.getSelectedChallengeId(), PreferenceManager.getSelectedImageId(), 0, PreferenceManager.getSelectedPage(), PreferenceManager.getSelectedSize())

        adapter = FeedChallengeRecyclerAdapter(viewModel, isEnableLike, organizationId, isMyPage)
        binding.feedRecycler.adapter = adapter
        binding.feedRecycler.setHasFixedSize(true)
        binding.feedRecycler.itemAnimator = null
*/
    }

    override fun getContext(): Activity {
        return this
    }
}