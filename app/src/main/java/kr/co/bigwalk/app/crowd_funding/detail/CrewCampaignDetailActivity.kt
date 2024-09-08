package kr.co.bigwalk.app.crowd_funding.detail

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.community.adapter.PagerFragmentStateAdapter
import kr.co.bigwalk.app.crowd_funding.detail.crew.CrewFundingRankingActivity
import kr.co.bigwalk.app.crowd_funding.detail.funding.StepFundingActivity
import kr.co.bigwalk.app.databinding.ActivityCrewCampaignDetailBinding
import kr.co.bigwalk.app.util.EventObserver
import kr.co.bigwalk.app.util.MainRandomViewMaker
import kr.co.bigwalk.app.util.showToast
import java.io.Serializable

class CrewCampaignDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCrewCampaignDetailBinding
    private val crewCampaignDetailViewModel by lazy {
        ViewModelProvider(this, CrewCampaignDetailViewModelFactory(crewCampaignId)).get(CrewCampaignDetailViewModel::class.java)
    }
    private val pagerAdapter by lazy { PagerFragmentStateAdapter(this) }
    private val crewCampaignId: Long by lazy {
        intent.getLongExtra(KEY_CREW_CAMPAIGN_ID, DEF_LONG_VALUE)
    }
    private var progressItem: SendProgressItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crew_campaign_detail)
        binding.lifecycleOwner = this
        FirebaseAnalytics.getInstance(this).logEvent("funding_campaign_view", Bundle())

        overridePendingTransition(R.anim.anim_horizon_enter, R.anim.none)

        this.window?.apply {
            this.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        setToolbar()
        setView()
        bindViewModel()
    }

    private fun setToolbar() {
        with(binding) {
            backButton.setOnClickListener {
                onBackPressed()
            }
            shareBtn.setOnClickListener {
                FirebaseAnalytics.getInstance(this@CrewCampaignDetailActivity).logEvent("funding_campaign_btn_share_click", Bundle())
                crewCampaignDetailViewModel.setCrewCampaignDeepLink()
            }
        }
    }

    private fun setView() {
        with(binding) {
            this.context = this@CrewCampaignDetailActivity
            vm = crewCampaignDetailViewModel
            binding.previewBackground.setImageDrawable(
                ContextCompat.getDrawable(this@CrewCampaignDetailActivity, MainRandomViewMaker.getCrewCampaignPreviewBackground())
            )
            progressLottie.repeatCount = 1

            campaignInfoPager.adapter = pagerAdapter
            campaignInfoPager.isUserInputEnabled = false

            TabLayoutMediator(topTab, campaignInfoPager) { tab, position ->
                tab.text = pagerAdapter.fragments[position].first
            }.attach()

            like.root.setOnClickListener {
                crewCampaignDetailViewModel.changeLikeState()
            }
            comment.root.setOnClickListener {
                FirebaseAnalytics.getInstance(this@CrewCampaignDetailActivity).logEvent("funding_campaign_button_comment_click", Bundle())
                startActivity(StepFundingCommentsActivity.getIntent(this@CrewCampaignDetailActivity, crewCampaignId))
            }
            member.root.setOnClickListener {
                FirebaseAnalytics.getInstance(this@CrewCampaignDetailActivity).logEvent("funding_campaign_button_rank_click", Bundle())
                startActivity(CrewFundingRankingActivity.getIntent(this@CrewCampaignDetailActivity, crewCampaignId))
            }
            fundingBtn.setOnClickListener {
                FirebaseAnalytics.getInstance(this@CrewCampaignDetailActivity).logEvent("funding_campaign_btn_funding_step_click", Bundle())
                startActivity(progressItem?.let { it ->
                    StepFundingActivity.getIntent(this@CrewCampaignDetailActivity, it)
                })
            }
        }
    }

    private fun getTabList(hasChallenge: Boolean): MutableList<Pair<String, Fragment>> {
        val tabList =
            mutableListOf<Pair<String, Fragment>>(Pair(getString(R.string.campaign), CrewCampaignInfoFragment.newInstance(crewCampaignId)))
        if (hasChallenge) {
            tabList.add(Pair(getString(R.string.challenge), CrewCampaignChallengeInfoFragment.newInstance(crewCampaignId)))
        }

        return tabList
    }

    private fun bindViewModel() {
        with(crewCampaignDetailViewModel) {
            crewCampaign.observe(this@CrewCampaignDetailActivity, Observer { campaignInfo ->
                progressItem = SendProgressItem(
                    crewCampaignId = crewCampaignId,
                    isMemberBenefit = campaignInfo.isMemberBenefit,
                    myFundingSteps = campaignInfo.myFundingSteps,
                    targetFundingSteps = campaignInfo.targetFundingSteps
                )
                if (campaignInfo.isMemberBenefit) {
                    showContestDetailDialog(campaignInfo.crewTitle)
                }
            })
            hasActionMission.observe(this@CrewCampaignDetailActivity, Observer { hasChallenge ->
                binding.topTab.getTabAt(0)?.select()
                pagerAdapter.replaceFragments(getTabList(hasChallenge))
            })
            toastMessage.observe(this@CrewCampaignDetailActivity, EventObserver { message ->
                showToast(message)
            })
            shareCrewCampaignDeepLink.observe(this@CrewCampaignDetailActivity, Observer { deepLink ->
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, deepLink)
                }
                startActivity(Intent.createChooser(intent, "크루 캠페인 공유"))
            })
            errorEvent.observe(this@CrewCampaignDetailActivity, EventObserver {
                showErrorDialog(it)
            })
        }
    }

    override fun onStart() {
        super.onStart()
        crewCampaignDetailViewModel.run {
            requestCrewCampaign()
            requestChallengeOfCrewCampaign()
        }
    }

    private fun showContestDetailDialog(crewTitle: String) {
        val dialogFragment = progressItem?.let { FundingCouponDialogFragment.newInstance(crewTitle, it) }
        dialogFragment?.show(supportFragmentManager, dialogFragment.tag)
    }

    private fun showErrorDialog(msg: String) {
        val dialog = AlertDialog.Builder(this)
            .setMessage(msg)
            .setCancelable(false)
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                finish()
            }
            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.none, R.anim.anim_horizon_exit)
    }

    companion object {
        private const val KEY_CREW_CAMPAIGN_ID = "CREW_CAMPAIGN_ID"
        fun getIntent(context: Context, crewCampaignId: Long) =
            Intent(context, CrewCampaignDetailActivity::class.java).apply {
                putExtra(KEY_CREW_CAMPAIGN_ID, crewCampaignId)
            }
    }
}

data class SendProgressItem(
    val crewCampaignId: Long,
    val isMemberBenefit: Boolean,
    val myFundingSteps: Long,
    val targetFundingSteps: Long
): Serializable