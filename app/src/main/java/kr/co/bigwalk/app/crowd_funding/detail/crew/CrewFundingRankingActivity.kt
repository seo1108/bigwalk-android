package kr.co.bigwalk.app.crowd_funding.detail.crew

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.crowd_funding.adapter.CrewCampaignFundingRankingAdapter
import kr.co.bigwalk.app.databinding.ActivityCrewFundingRankingBinding
import kr.co.bigwalk.app.util.EventObserver

class CrewFundingRankingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCrewFundingRankingBinding
    private val crewFundingRankingViewModel by lazy {
        ViewModelProvider(this, CrewFundingRankingViewModelFactory(crewCampaignId)).get(CrewFundingRankingViewModel::class.java)
    }
    private val crewCampaignFundingRankingAdapter by lazy { CrewCampaignFundingRankingAdapter() }
    private val crewCampaignId: Long by lazy { intent.getLongExtra(KEY_CREW_CAMPAIGN_ID, DEF_LONG_VALUE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crew_funding_ranking)
        binding.lifecycleOwner = this

        FirebaseAnalytics.getInstance(this).logEvent("funding_ranking_view", Bundle())

        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            vm = crewFundingRankingViewModel
            memberListContainer.adapter = crewCampaignFundingRankingAdapter
            exitButton.setOnClickListener {
                finish()
            }
            joinButton.setOnClickListener {
                FirebaseAnalytics.getInstance(this@CrewFundingRankingActivity).logEvent("crew_join_request_button_apply_button", Bundle())
                crewFundingRankingViewModel.joinCrewByRecruitSetting()
            }
        }
    }

    private fun bindViewModel() {
        with(crewFundingRankingViewModel) {
            fundingRankList.observe(this@CrewFundingRankingActivity, Observer {
                crewCampaignFundingRankingAdapter.submitList(it)
            })
            applySuccess.observe(this@CrewFundingRankingActivity, EventObserver {
                showJoinCrewCampaignDialog(it)
            })
            applyFailureEvent.observe(this@CrewFundingRankingActivity, EventObserver {
                showErrorDialog(it)
            })
            crewJoinEvent.observe(this@CrewFundingRankingActivity, EventObserver {
                showJoinCrewCampaignGuideDialog(it.first, it.second, it.third)
            })
        }
    }

    private fun showJoinCrewCampaignGuideDialog(msg: Int, confirmMsg: Int, question: String) {
        val dialog = AlertDialog.Builder(this)
            .setMessage(getString(msg))
            .setPositiveButton(R.string.confirm) { _, _ ->
                if (question.isNotEmpty()) {
                    startActivity(CrewJoinFromActivity.getIntent(this@CrewFundingRankingActivity, question, crewCampaignId))
                } else {
                    crewFundingRankingViewModel.applyForCrewCampaign(confirmMsg)
                }
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }
    }

    private fun showJoinCrewCampaignDialog(message: Int) {
        val dialog = AlertDialog.Builder(this)
            .setMessage(getString(message))
            .setPositiveButton(R.string.confirm) { _, _ ->
                crewCampaignFundingRankingAdapter.currentList?.dataSource?.invalidate()
                crewFundingRankingViewModel.fetchCrewCampaignMyRanking()
            }
            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }
    }

    private fun showErrorDialog(message: String) {
        val dialog = AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(R.string.confirm) { _, _ -> }
            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }
    }


    companion object {
        private const val KEY_CREW_CAMPAIGN_ID = "CREW_CAMPAIGN_ID"
        fun getIntent(context: Context, crewCampaignId: Long) =
            Intent(context, CrewFundingRankingActivity::class.java).apply {
                putExtra(KEY_CREW_CAMPAIGN_ID, crewCampaignId)
            }
    }
}