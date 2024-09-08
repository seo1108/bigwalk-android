package kr.co.bigwalk.app.community.funding

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.funding.SupportersCampaignSequence
import kr.co.bigwalk.app.databinding.ActivitySupportersCampaignsBinding

class SupportersCampaignsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySupportersCampaignsBinding
    private val supportersCampaignsViewModel: SupportersCampaignsViewModel by lazy {
        ViewModelProvider(this, SupportersCampaignsViewModelFactory(groupId)).get(SupportersCampaignsViewModel::class.java)
    }

    private val groupId: Long by lazy { intent.getLongExtra(KEY_GROUP_ID, -1) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_supporters_campaigns)
        binding.lifecycleOwner = this

        FirebaseAnalytics.getInstance(this).logEvent("crew_campaign_view", Bundle())

        this.window?.apply {
            this.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        if (PreferenceManager.haveSeenCrewCampaignGuide().not()) {
            startActivity(CrewCampaignGuideActivity.getIntent(this))
        }

        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            vm = supportersCampaignsViewModel
            exitButton.setOnClickListener {
                finish()
            }
            guideBtn.setOnClickListener {
                startActivity(CrewCampaignGuideActivity.getIntent(this@SupportersCampaignsActivity))
            }
            group1.setOnClickListener {
                supportersCampaignsViewModel.moveToDetailOrCompetition(SupportersCampaignSequence.FIRST.sequence)
            }
            group2.setOnClickListener {
                supportersCampaignsViewModel.moveToDetailOrCompetition(SupportersCampaignSequence.SECOND.sequence)
            }
            group3.setOnClickListener {
                supportersCampaignsViewModel.moveToDetailOrCompetition(SupportersCampaignSequence.THIRD.sequence)
            }
            group4.setOnClickListener {
                supportersCampaignsViewModel.moveToDetailOrCompetition(SupportersCampaignSequence.FOURTH.sequence)
            }
            group5.setOnClickListener {
                supportersCampaignsViewModel.moveToDetailOrCompetition(SupportersCampaignSequence.FIFTH.sequence)
            }
        }
    }

    private fun bindViewModel() {
        with(supportersCampaignsViewModel) {
            clickCampaignId.observe(this@SupportersCampaignsActivity, Observer { (crewCampaignId, hasActionMission) ->
                startActivity(SupportersCampaignPreviewActivity.getIntent(this@SupportersCampaignsActivity, groupId, crewCampaignId, hasActionMission))
            })
            clickSequence.observe(this@SupportersCampaignsActivity, Observer { sequence ->
                startActivity(ContestActivity.getIntent(this@SupportersCampaignsActivity, groupId, sequence))
            })
        }
    }

    override fun onStart() {
        super.onStart()
        supportersCampaignsViewModel.getMyLabelList()
        supportersCampaignsViewModel.setTooltipVisible()
    }

    companion object {
        private const val KEY_GROUP_ID = "GROUP_ID"
        fun getIntent(context: Context, groupId: Long) =
            Intent(context, SupportersCampaignsActivity::class.java).apply {
                putExtra(KEY_GROUP_ID, groupId)
            }
    }
}