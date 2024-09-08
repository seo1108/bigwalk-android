package kr.co.bigwalk.app.campaign

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.android.material.tabs.TabLayout
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.SignalBaseActivity
import kr.co.bigwalk.app.databinding.ActivityCampaignBinding
import kr.co.bigwalk.app.feed.FeedFragment
import kr.co.bigwalk.app.feed_home.ChallengeHomeFragment
import kr.co.bigwalk.app.feed_home.FeedHomeFragment
import kr.co.bigwalk.app.story.StoryFragment

class CampaignActivity: SignalBaseActivity(), CampaignNavigator {

    private lateinit var binding: ActivityCampaignBinding
    lateinit var viewModel: CampaignViewModel
    private lateinit var campaignFragment: CampaignFragment
    private lateinit var storyFragment: StoryFragment
    private lateinit var feedFragment: FeedFragment
    private lateinit var feedHomeFragment: FeedHomeFragment
    private lateinit var challengeHomeFragment: ChallengeHomeFragment

    companion object {
        var initTabPosition: Int = 0
        const val CAMPAIGN_POSITION = 0
        const val FEED_POSITION = 1
        const val STORY_POSITION = 2

        var firebaseAnalytics : FirebaseAnalytics? = null

        const val SELECT_TAB = "SELECT_TAB"
    
        fun getIntent(context: Context) =
            Intent(context, CampaignActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_campaign)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        viewModel = CampaignViewModel(this, supportFragmentManager)
        binding.viewModel = viewModel
        campaignFragment = CampaignFragment.newInstance()
        campaignFragment.viewModel = viewModel
        storyFragment = StoryFragment()
        feedFragment = FeedFragment()
        feedHomeFragment = FeedHomeFragment.newInstance()
        challengeHomeFragment = ChallengeHomeFragment.newInstance()



        val tabIndex = intent.getIntExtra(SELECT_TAB, 0)
        binding.campaignTab.getTabAt(tabIndex)?.select()
        transitionTab(tabIndex)

        binding.campaignTab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val position = tab?.position ?: 0
                transitionTab(position)
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //nothing
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //nothing
            }
        })

        val campaignId = intent.getLongExtra("campaign_id", -1L)
        if (campaignId >= 0)
            viewModel.moveToCampaign(campaignId)
    }

    fun transitionTab(position: Int) {
        when(position) {
            CAMPAIGN_POSITION -> {
                //캠페인 프래그먼트
                supportFragmentManager.beginTransaction()
                    .replace(R.id.campaign_list_frame, campaignFragment)
                    .commit()
            }
            FEED_POSITION -> {
                //챌린지홈 프래그먼트
                /*supportFragmentManager.beginTransaction()
                    .replace(R.id.campaign_list_frame, feedHomeFragment)
                    .commit()*/
                supportFragmentManager.beginTransaction()
                    .replace(R.id.campaign_list_frame, challengeHomeFragment)
                    .commit()
            }
            STORY_POSITION -> {
                //포스트 프래그먼트
                supportFragmentManager.beginTransaction()
                    .replace(R.id.campaign_list_frame, storyFragment)
                    .commit()
/*                supportFragmentManager.beginTransaction()
                    .replace(R.id.campaign_list_frame, feedHomeFragment)
                    .commit()*/
            }
            else -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.campaign_list_frame, campaignFragment)
                    .commit()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        //화면에 돌아올때 특정 탭이 선택되어있도록 해둠 (스토리 고를 경우)
        if(initTabPosition != CAMPAIGN_POSITION){
            binding.campaignTab.getTabAt(initTabPosition)?.select()
            initTabPosition = CAMPAIGN_POSITION
        }
    }

    override fun moveToStoryTab() {
        binding.campaignTab.getTabAt(STORY_POSITION)?.select()
        supportFragmentManager.beginTransaction()
            .replace(R.id.campaign_list_frame, storyFragment)
            .commit()
    }

    override fun getContext(): Activity {
        return this
    }

    override fun onBackPressed() {
        finish()
    }
}