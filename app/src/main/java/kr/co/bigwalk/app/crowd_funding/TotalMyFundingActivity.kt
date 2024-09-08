package kr.co.bigwalk.app.crowd_funding

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
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.crowd_funding.adapter.TotalMyFundingAdapter
import kr.co.bigwalk.app.crowd_funding.detail.CrewCampaignDetailActivity
import kr.co.bigwalk.app.crowd_funding.myfunding.MyFundingRewardDialogFragment
import kr.co.bigwalk.app.data.crowd_funding.dto.FundingState
import kr.co.bigwalk.app.data.crowd_funding.dto.TotalMyFundingResponse
import kr.co.bigwalk.app.databinding.ActivityTotalMyFundingBinding
import java.io.Serializable

class TotalMyFundingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTotalMyFundingBinding
    private val totalMyFundingViewModel by lazy {
        ViewModelProvider(this, TotalMyFundingViewModelFactory(contestId)).get(TotalMyFundingViewModel::class.java)
    }
    private val totalMyFundingAdapter by lazy {
        TotalMyFundingAdapter {
            if (it.fundingState == FundingState.FUNDING) {
                FirebaseAnalytics.getInstance(this).logEvent("my_funding_button_campaign_click", Bundle())
                startActivity(CrewCampaignDetailActivity.getIntent(this, it.id))
                return@TotalMyFundingAdapter
            }
            showContestDetailDialog(it)
        }
    }
    private val contestId: Long? by lazy {
        val id = intent.getLongExtra(KEY_CONTEST_ID, DEF_LONG_VALUE)
        if (id < 0) null else id
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_total_my_funding)
        binding.lifecycleOwner = this
        FirebaseAnalytics.getInstance(this).logEvent("my_funding_view", Bundle())

        overridePendingTransition(R.anim.anim_horizon_enter, R.anim.none)

        this.window?.apply {
            this.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            vm = totalMyFundingViewModel
            rvMyFundingResult.adapter = totalMyFundingAdapter
            closeButton.setOnClickListener {
                onBackPressed()
            }
        }
    }

    private fun bindViewModel() {
        with(totalMyFundingViewModel) {
            totalFundingList.observe(this@TotalMyFundingActivity, Observer {
                totalMyFundingAdapter.submitList(it)
            })
        }
    }

    private fun showContestDetailDialog(contestResponse: TotalMyFundingResponse) {
        val item =
            FundingRewardResultItem(
                contestResponse.id,
                contestResponse.fundingState,
                contestResponse.getRemainToString(),
                contestResponse.getMyFundingStepsToString()
            )
        val dialogFragment = MyFundingRewardDialogFragment.newInstance(item)
        dialogFragment.show(supportFragmentManager, dialogFragment.tag)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.none, R.anim.anim_horizon_exit)
    }

    companion object {
        private const val KEY_CONTEST_ID = "CONTEST_ID"
        fun getIntent(context: Context, contestId: Long?) =
            Intent(context, TotalMyFundingActivity::class.java).apply {
                putExtra(KEY_CONTEST_ID, contestId)
            }
    }
}

data class FundingRewardResultItem(
    val id: Long,
    val fundingState: FundingState,
    val remainSteps: String,
    val myFundingSteps: String
) : Serializable

//펀딩 적용하기 state를 구분할 수 있는 flag 추가