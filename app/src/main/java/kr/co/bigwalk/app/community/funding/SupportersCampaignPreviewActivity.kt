package kr.co.bigwalk.app.community.funding

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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kr.co.bigwalk.app.DEF_INT_VALUE
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.community.adapter.PagerFragmentStateAdapter
import kr.co.bigwalk.app.community.funding.create.CreateFundingActivity
import kr.co.bigwalk.app.community.funding.preview.PreviewCampaignFragment
import kr.co.bigwalk.app.community.funding.preview.PreviewChallengeFragment
import kr.co.bigwalk.app.data.funding.RequiredToCreateIds
import kr.co.bigwalk.app.databinding.ActivitySupportersCampaignPreviewBinding
import kr.co.bigwalk.app.util.EventObserver
import kr.co.bigwalk.app.util.MainRandomViewMaker
import kr.co.bigwalk.app.util.showToast

class SupportersCampaignPreviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySupportersCampaignPreviewBinding
    private val supportersCampaignPreviewViewModel by lazy {
        ViewModelProvider(this, SupportersCampaignPreviewViewModelFactory(crewCampaignId)).get(SupportersCampaignPreviewViewModel::class.java)
    }
    private val pagerAdapter by lazy { PagerFragmentStateAdapter(this) }
    private val groupId: Long by lazy { intent.getLongExtra(KEY_GROUP_ID, DEF_LONG_VALUE) }
    private val crewCampaignId: Long by lazy { intent.getLongExtra(KEY_CREW_CAMPAIGN_ID, DEF_LONG_VALUE) }
    private val hasActionMission: Boolean by lazy { intent.getBooleanExtra(KEY_HAS_ACTION_MISSION, false) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_supporters_campaign_preview)
        binding.lifecycleOwner = this
        FirebaseAnalytics.getInstance(this).logEvent("contest_exhibit_preview_view", Bundle())

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
            listButton.setOnClickListener {
                onBackPressed()
            }
            editButton.setOnClickListener {
                startActivity(
                    CreateFundingActivity.getIntent(
                        this@SupportersCampaignPreviewActivity,
                        RequiredToCreateIds(groupId, DEF_LONG_VALUE, DEF_INT_VALUE),
                        crewCampaignId
                    )
                )
            }
        }
    }

    private fun setView() {
        with(binding) {
            vm = supportersCampaignPreviewViewModel
            binding.previewBackground.setImageDrawable(
                ContextCompat.getDrawable(this@SupportersCampaignPreviewActivity, MainRandomViewMaker.getCrewCampaignPreviewBackground())
            )

            campaignInfoPager.adapter = pagerAdapter

            TabLayoutMediator(topTab, campaignInfoPager) { tab, position ->
                tab.text = pagerAdapter.fragments[position].first
            }.attach()

            applyBtn.setOnClickListener {
                FirebaseAnalytics.getInstance(this@SupportersCampaignPreviewActivity).logEvent("contest_exhibit_preview_btn_apply_click", Bundle())
                supportersCampaignPreviewViewModel.judgeCrewCampaign(groupId)
            }
        }
    }

    private fun getTabList(hasChallenge: Boolean): MutableList<Pair<String, Fragment>> {
        val tabList =
            mutableListOf<Pair<String, Fragment>>(Pair(getString(R.string.campaign), PreviewCampaignFragment.newInstance(crewCampaignId)))
        if (hasChallenge) {
            tabList.add(Pair(getString(R.string.challenge), PreviewChallengeFragment.newInstance(crewCampaignId, groupId)))
        }

        return tabList
    }

    private fun bindViewModel() {
        with(supportersCampaignPreviewViewModel) {
            hasActionMission.observe(this@SupportersCampaignPreviewActivity, Observer { hasChallenge ->
                binding.topTab.getTabAt(0)?.select()
                pagerAdapter.replaceFragments(getTabList(hasChallenge))
            })
            progressData.observe(this@SupportersCampaignPreviewActivity, Observer {
                binding.progressLottie.run {
                    setAnimation(it.lottieRes)
                    CoroutineScope(Dispatchers.Main).launch {
                        delay(100L)
                        playAnimation()
                    }
                }
            })
            toastMessage.observe(this@SupportersCampaignPreviewActivity, EventObserver { message ->
                showToast(message)
            })
            judgeSuccess.observe(this@SupportersCampaignPreviewActivity, EventObserver {
                showJudgeCrewCampaignDialog()
            })
            errorEvent.observe(this@SupportersCampaignPreviewActivity, EventObserver {
                showErrorDialog(it)
            })
        }
    }

    override fun onStart() {
        super.onStart()
        supportersCampaignPreviewViewModel.run {
            requestCrewCampaign()
            getMyRoleFromGroup(groupId)
            fetchLastModifier()
        }
    }

    private fun showJudgeCrewCampaignDialog() {
        val dialog = AlertDialog.Builder(this)
            .setMessage(getString(R.string.dialog_judge_crew_campaign_msg))
            .setPositiveButton(R.string.confirm) { dialog, _ -> }
            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }
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
        private const val KEY_GROUP_ID = "GROUP_ID"
        private const val KEY_CREW_CAMPAIGN_ID = "CREW_CAMPAIGN_ID"
        private const val KEY_HAS_ACTION_MISSION = "HAS_ACTION_MISSION"
        fun getIntent(context: Context, groupId: Long, crewCampaignId: Long, hasActionMission: Boolean) =
            Intent(context, SupportersCampaignPreviewActivity::class.java).apply {
                putExtra(KEY_GROUP_ID, groupId)
                putExtra(KEY_CREW_CAMPAIGN_ID, crewCampaignId)
                putExtra(KEY_HAS_ACTION_MISSION, hasActionMission)
            }
    }
}