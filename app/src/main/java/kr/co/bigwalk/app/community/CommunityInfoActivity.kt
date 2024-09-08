package kr.co.bigwalk.app.community

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.method.LinkMovementMethod
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.SignalBaseActivity
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.community.funding.SupportersCampaignsActivity
import kr.co.bigwalk.app.community.info.ModifyCrewIntroActivity
import kr.co.bigwalk.app.community.share.GroupShareActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.community.GroupDetailResponse
import kr.co.bigwalk.app.databinding.ActivityCommunityInfoBinding

class CommunityInfoActivity : SignalBaseActivity() {
    private lateinit var binding: ActivityCommunityInfoBinding
    private val communityInfoViewModel: CommunityInfoViewModel by lazy {
        ViewModelProvider(this, CommunityInfoViewModelFactory(groupId)).get(CommunityInfoViewModel::class.java)
    }
    private val communityActivityIntent: Intent by lazy {
        CommunityActivity.getIntent(this)
    }

    private var groupId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_community_info)
        binding.lifecycleOwner = this

        overridePendingTransition(R.anim.anim_horizon_enter, R.anim.none)

        groupId = intent.getLongExtra(PARM_GROUP_ID, -1L)

        setToolbar()
        setView()
        bindViewModel()
    }

    override fun onResume() {
        super.onResume()
        communityInfoViewModel.getGroupDetail()
    }

    private fun setToolbar() {
        with(binding) {
            back.setOnClickListener {
                onBackPressed()
            }
            toolbarTitleContainer.setOnClickListener {
                communityInfoViewModel.setIntroduceVisible()
            }
            infoEditButton.setOnClickListener {
                startActivity(ModifyCrewIntroActivity.getIntent(this@CommunityInfoActivity, groupId))
            }
            donationButton.setOnClickListener {
                startActivity(CampaignActivity.getIntent(this@CommunityInfoActivity))
            }
        }
    }

    private fun setView() {
        with(binding) {
            viewModel = communityInfoViewModel
            val tabList = resources.getStringArray(R.array.group_info_tab)

            communityInfoPager.adapter = object : FragmentStateAdapter(
                supportFragmentManager,
                lifecycle
            ) {
                override fun getItemCount(): Int =
                    tabList.size

                override fun createFragment(position: Int): Fragment =
                    when (position) {
                        0 -> GroupGoalFragment.newInstance()
                        1 -> GroupReportFragment.newInstance()
                        else -> error("Invalid position")
                    }
            }

            TabLayoutMediator(topTab, communityInfoPager) { tab, position ->
                tab.text = tabList[position]
            }.attach()

            topTab.run {
                addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        val position = tab?.position
                        if (position != null) {
                            tabPosition = position
                        }
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {
                    }

                    override fun onTabReselected(tab: TabLayout.Tab?) {
                    }

                })
            }

            introduce.setOnClickListener {
                communityInfoViewModel.setIntroduceVisible()
            }
            introduceText.movementMethod = LinkMovementMethod.getInstance()
            memberCount.setOnClickListener {
                startActivity(communityActivityIntent)
            }
            crewCampaignBtn.setOnClickListener {
                startActivity(SupportersCampaignsActivity.getIntent(this@CommunityInfoActivity, groupId))
            }
            communityMakingInfluenceBtn.setOnClickListener {
                startActivity(GroupShareActivity.getIntent(this@CommunityInfoActivity, groupId))
            }
            goalSetBtn.setOnClickListener {
                communityInfoViewModel.postGroupGoal()
                FirebaseAnalytics.getInstance(this@CommunityInfoActivity).logEvent("group_button_set_goal_click", null)
            }
        }
    }

    private fun bindViewModel() {
        with(communityInfoViewModel) {
            communityInfo.observe(this@CommunityInfoActivity, Observer<GroupDetailResponse> { groupDetail ->
                communityActivityIntent.putExtra(PARM_GROUP_ID, groupId)
                communityActivityIntent.putExtra(PARM_GROUP_NAME, groupDetail.name)
                communityActivityIntent.putExtra(PARM_GROUP_IMAGE_PATH, groupDetail.mainImagePath)
                communityActivityIntent.putExtra(PARM_GROUP_IMAGE_PATH, groupDetail.mainImagePath)
                communityActivityIntent.putExtra(PARM_JOIN_REQUEST_COUNT, groupDetail.joinRequestCounts)
            })
            invalidMember.observe(this@CommunityInfoActivity, Observer<String> {
                this@CommunityInfoActivity.groupId = -1
                PreferenceManager.saveGroupId(-1)
                onBackPressed()
            })
            goalSetRotate.observe(this@CommunityInfoActivity, Observer {
                binding.goalSetBtn.animate().rotation(it).setDuration(200).start()
            })
        }
    }

    override fun onStart() {
        super.onStart()
        communityInfoViewModel.setTooltipVisible()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.none, R.anim.anim_horizon_exit)
    }

    companion object {
        fun getIntent(context: Context, groupId: Long) =
            Intent(context, CommunityInfoActivity::class.java).apply {
                putExtra(PARM_GROUP_ID, groupId)
            }

        const val PARM_GROUP_ID = "group_id"
        const val PARM_GROUP_NAME = "group_name"
        const val PARM_GROUP_IMAGE_PATH = "group_image_path"
        const val PARM_INVITED_GROUP_ID = "invited_group_id"
        const val PARM_INVITED_GROUP_KEY = "group_join_key"
        const val PARM_JOIN_REQUEST_COUNT = "join_request_count"
    }
}