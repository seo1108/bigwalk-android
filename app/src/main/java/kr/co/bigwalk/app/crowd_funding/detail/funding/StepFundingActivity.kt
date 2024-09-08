package kr.co.bigwalk.app.crowd_funding.detail.funding

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.crowd_funding.adapter.StepFundingMissionAdapter
import kr.co.bigwalk.app.crowd_funding.detail.SendProgressItem
import kr.co.bigwalk.app.crowd_funding.guide.StepFundingGuideActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.databinding.ActivityStepFundingBinding
import kr.co.bigwalk.app.extension.dpToPx
import kr.co.bigwalk.app.util.EventObserver
import kr.co.bigwalk.app.util.OnSingleClickListener
import kr.co.bigwalk.app.util.showToast

class StepFundingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStepFundingBinding
    private val stepFundingViewModel by lazy {
        ViewModelProvider(this, StepFundingViewModelFactory(progressItem)).get(StepFundingViewModel::class.java)
    }
    private val stepFundingMissionAdapter by lazy { StepFundingMissionAdapter() }
    private val progressItem: SendProgressItem by lazy { intent.getSerializableExtra(KEY_PROGRESS_ITEM) as SendProgressItem }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_step_funding)
        binding.lifecycleOwner = this
        FirebaseAnalytics.getInstance(this).logEvent("funding_step_view", Bundle())

        if (PreferenceManager.haveSeenStepFundingGuide().not()) {
            PreferenceManager.saveStepFundingGuide(true)
            startActivity(StepFundingGuideActivity.getIntent(this))
        }

        setToolbar()
        setView()
        bindViewModel()
    }

    private fun setToolbar() {
        with(binding) {
            exitButton.setOnClickListener {
                finish()
            }
            couponBtn.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(view: View) {
                    FirebaseAnalytics.getInstance(this@StepFundingActivity).logEvent("funding_step_button_coupon_click", Bundle())
                    stepFundingViewModel.fundForFundingCoupon()
                }
            })
        }
    }

    private fun setView() {
        with(binding) {
            vm = stepFundingViewModel
            missionPager.setPageTransformer { page, position ->
                page.translationX = 1.dpToPx() * position // 임시
            }

            missionPager.offscreenPageLimit = 3
            missionPager.adapter = stepFundingMissionAdapter
            missionPager.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

            donationSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    stepFundingViewModel.setProgressFundingStep(progress)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                }

            })
            guideContainer.setOnClickListener {
                FirebaseAnalytics.getInstance(this@StepFundingActivity).logEvent("funding_step_button_guide_click", Bundle())
                startActivity(StepFundingGuideActivity.getIntent(this@StepFundingActivity))
            }
            fundingBtn.setOnClickListener {
                FirebaseAnalytics.getInstance(this@StepFundingActivity).logEvent("funding_step_button_funding_click", Bundle())
                stepFundingViewModel.fundingByStep()
            }
        }
    }

    private fun bindViewModel() {
        with(stepFundingViewModel) {
            fundingInfo.observe(this@StepFundingActivity, Observer {
                it.earlyBirdRemainCount?.let { count ->
                    if (count <= 0) binding.missionPager.setCurrentItem(1, true)
                    binding.missionPager.currentItem
                }
            })
            fundingCardImage.observe(this@StepFundingActivity, Observer {
                stepFundingMissionAdapter.submitList(it)
            })
            successEvent.observe(this@StepFundingActivity, EventObserver { msg ->
                showFundingSuccessDialog(msg)
            })
            failureEvent.observe(this@StepFundingActivity, EventObserver {
                showFundingFailureDialog()
            })
            toastMessage.observe(this@StepFundingActivity, EventObserver {
                showToast(it)
            })
        }
    }

    private fun showFundingSuccessDialog(msg: Int) {
        val dialog = AlertDialog.Builder(this)
            .setMessage(getString(msg))
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                finish()
            }
            .setCancelable(false)
            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }
    }

    private fun showFundingFailureDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.dialog_funding_failure_title))
            .setMessage(getString(R.string.dialog_funding_failure_msg))
            .setPositiveButton(R.string.confirm) { _, _ -> }
            .setCancelable(false)
            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }
    }

    companion object {
        private const val KEY_PROGRESS_ITEM = "PROGRESS_ITEM"
        fun getIntent(context: Context, item: SendProgressItem) =
            Intent(context, StepFundingActivity::class.java).apply {
                putExtra(KEY_PROGRESS_ITEM, item)
            }
    }
}