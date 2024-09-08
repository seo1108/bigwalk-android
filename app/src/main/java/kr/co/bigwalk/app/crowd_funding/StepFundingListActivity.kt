package kr.co.bigwalk.app.crowd_funding

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.HorizontalScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.crowd_funding.adapter.MyStepFundingListAdapter
import kr.co.bigwalk.app.crowd_funding.adapter.StepFundingListAdapter
import kr.co.bigwalk.app.crowd_funding.detail.CrewCampaignDetailActivity
import kr.co.bigwalk.app.crowd_funding.guide.ContestApplyGuideActivity
import kr.co.bigwalk.app.crowd_funding.guide.CrowdFundingGuideActivity
import kr.co.bigwalk.app.crowd_funding.guide.GuideActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.databinding.ActivityStepFundingListBinding
import kr.co.bigwalk.app.extension.*

class StepFundingListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStepFundingListBinding
    private val stepFundingListViewModel by lazy {
        ViewModelProvider(this, StepFundingListViewModelFactory(contestId)).get(StepFundingListViewModel::class.java)
    }
    private val contestId: Long by lazy { intent.getLongExtra(KEY_CONTEST_ID, DEF_LONG_VALUE) }
    private val stepFundingListAdapter by lazy {
        StepFundingListAdapter {
            FirebaseAnalytics.getInstance(this@StepFundingListActivity).logEvent("cf_list_button_campaign_click", Bundle())
            startActivity(CrewCampaignDetailActivity.getIntent(this, it.id))
        }
    }
    private val myStepFundingListAdapter by lazy {
        MyStepFundingListAdapter {
            FirebaseAnalytics.getInstance(this@StepFundingListActivity).logEvent("cf_my_funding_button_campaign_click", Bundle())
            startActivity(CrewCampaignDetailActivity.getIntent(this, it.id))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_step_funding_list)
        binding.lifecycleOwner = this

        overridePendingTransition(R.anim.anim_horizon_enter, R.anim.none)

        this.window?.apply {
            this.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        if (PreferenceManager.haveSeenCrowdFundingGuide().not()) {
            PreferenceManager.saveCrowdFundingGuide(true)
            startActivity(CrowdFundingGuideActivity.getIntent(this))
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
            infoButton.setOnClickListener {
                FirebaseAnalytics.getInstance(this@StepFundingListActivity).logEvent("cf_button_guide_click", Bundle())
                startActivity(CrowdFundingGuideActivity.getIntent(this@StepFundingListActivity))
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setView() {
        with(binding) {
            vm = stepFundingListViewModel
            backgroundImage.layoutParams.width = getDeviceWidth() * 3
            startContainer.layoutParams.width = getDeviceWidth()
            centerContainer.layoutParams.width = getDeviceWidth()
            endContainer.layoutParams.width = getDeviceWidth()
            stepFundingList.adapter = stepFundingListAdapter
            myStepFundingList.adapter = myStepFundingListAdapter

            backgroundImage.setImageByTime(R.drawable.aos_bg_crowd_funding_day_bg, R.drawable.aos_bg_crowd_funding_night_bg)
            frameImage.setImageByTime(R.drawable.aos_zfold_3_illust_funding_frame, R.drawable.aos_zfold_3_illust_funding_frame_night)
            leftBanner1.setImageByTime(R.drawable.aos_illust_funding_l_banner_1, R.drawable.aos_illust_funding_l_banner_1_night)
            leftBanner2.setImageByTime(R.drawable.aos_illust_funding_l_banner_2, R.drawable.aos_illust_funding_l_banner_2_night)
            rightBanner1.setImageByTime(R.drawable.aos_illust_funding_r_banner_1, R.drawable.aos_illust_funding_r_banner_1_night)
            rightBanner2.setImageByTime(R.drawable.aos_illust_funding_r_banner_2, R.drawable.aos_illust_funding_r_banner_2_night)
            goToListBtn.setImageByTime(R.drawable.aos_icon_funding_mylist, R.drawable.aos_icon_funding_mylist_night)
            rightSign.setAnimationByTime(R.raw.aosios_day_sign3, R.raw.aosios_night_sign2)
            stepFundingInfo.setBackgroundColorByTime(R.color.white, R.color.main_black)
            stepFundingBoard.setBackgroundColorByTime(R.color.white, R.color.theme_2d2d2d)
            myStepFundingBoard.setBackgroundColorByTime(R.color.white, R.color.theme_2d2d2d)
            image1.setAnimationByNight(R.raw.aosios_light_left)
            image2.setAnimationByNight(R.raw.aosios_light_center)
            image3.setAnimationByNight(R.raw.aosios_light_right)


            scrollContainer.run {
                setOnTouchListener { _, _ -> true }
                hiddenScrollButton(this)
            }

            ArrayAdapter.createFromResource(
                this@StepFundingListActivity,
                R.array.funding_sort_array,
                R.layout.item_spinner_funding
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                sortSpinner.adapter = adapter
            }
            sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    FirebaseAnalytics.getInstance(this@StepFundingListActivity).logEvent("cf_button_sorting_click", Bundle())
                    stepFundingListAdapter.submitList(null)
                    when (position) {
                        0 -> {
                            stepFundingListViewModel.listByNewest.observe(this@StepFundingListActivity, Observer { list ->
                                stepFundingListAdapter.submitList(list)
                            })
                            stepFundingListViewModel.setSortTypeToPosition(0)
                        }
                        1 -> {
                            stepFundingListViewModel.listByHottest.observe(this@StepFundingListActivity, Observer { list ->
                                stepFundingListAdapter.submitList(list)
                            })
                            stepFundingListViewModel.setSortTypeToPosition(1)
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
            guideContainer.setOnClickListener {
                FirebaseAnalytics.getInstance(this@StepFundingListActivity).logEvent("cf_button_how_to_apply_click", Bundle())
                startActivity(ContestApplyGuideActivity.getIntent(this@StepFundingListActivity))
            }
            contestDetailBtn.setOnClickListener {
                FirebaseAnalytics.getInstance(this@StepFundingListActivity).logEvent("cf_ce_btn_view_content_click", Bundle())
                startActivity(GuideActivity.getIntent(this@StepFundingListActivity, it.tag.toString(), contestDetailBtn.text.toString()))
            }
            goToListBtn.setOnClickListener {
                FirebaseAnalytics.getInstance(this@StepFundingListActivity).logEvent("cf_my_funding_button_list_click", Bundle())
                startActivity(TotalMyFundingActivity.getIntent(this@StepFundingListActivity, null))
            }

            leftButton.setOnClickListener {
                stepFundingListViewModel.sideClick(StepFundingListViewModel.KEY_LEFT_CLICK)
            }

            rightButton.setOnClickListener {
                stepFundingListViewModel.sideClick(StepFundingListViewModel.KEY_RIGHT_CLICK)
            }
        }
    }

    private fun hiddenScrollButton(horizontalScrollView: HorizontalScrollView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            horizontalScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
                binding.leftButton.isInvisible = scrollX <= 0
                binding.rightButton.isInvisible = scrollX >= binding.endContainer.left
                when (scrollX) {
                    0 -> {
                        FirebaseAnalytics.getInstance(this).logEvent("cf_contest_exhibit_view", Bundle())
                        binding.image1.resumeAnimation()
                        binding.image2.pauseAnimation()
                        binding.image3.pauseAnimation()
                        binding.backgroundLottie.resumeAnimation()
                        binding.leftSign1.resumeAnimation()
                        binding.leftSign2.resumeAnimation()
                        binding.leftSignNight.resumeAnimation()
                        binding.rightSign.pauseAnimation()
                        binding.rightSignNight.pauseAnimation()
                        binding.fireworkLeft.resumeAnimation()
                        binding.fireworkRight1.pauseAnimation()
                        binding.fireworkRight2.pauseAnimation()

                    }
                    binding.endContainer.left -> {
                        FirebaseAnalytics.getInstance(this).logEvent("cf_my_funding_view", Bundle())
                        binding.image1.pauseAnimation()
                        binding.image2.pauseAnimation()
                        binding.image3.resumeAnimation()
                        binding.backgroundLottie.pauseAnimation()
                        binding.leftSign1.pauseAnimation()
                        binding.leftSign2.pauseAnimation()
                        binding.leftSignNight.pauseAnimation()
                        binding.rightSign.resumeAnimation()
                        binding.rightSignNight.resumeAnimation()
                        binding.fireworkLeft.pauseAnimation()
                        binding.fireworkRight1.resumeAnimation()
                        binding.fireworkRight2.resumeAnimation()
                    }
                    binding.centerContainer.left -> {
                        FirebaseAnalytics.getInstance(this).logEvent("cf_list_view", Bundle())
                        binding.image1.pauseAnimation()
                        binding.image2.resumeAnimation()
                        binding.image3.pauseAnimation()
                        binding.backgroundLottie.pauseAnimation()
                        binding.leftSign1.pauseAnimation()
                        binding.leftSign2.pauseAnimation()
                        binding.leftSignNight.pauseAnimation()
                        binding.rightSign.pauseAnimation()
                        binding.rightSignNight.pauseAnimation()
                        binding.fireworkLeft.pauseAnimation()
                        binding.fireworkRight1.pauseAnimation()
                        binding.fireworkRight2.pauseAnimation()
                    }
                }
            }
        }
    }

    private fun bindViewModel() {
        with(stepFundingListViewModel) {
            myFundingList.observe(this@StepFundingListActivity, Observer {
                myStepFundingListAdapter.submitList(it)
            })
            leftClick.observe(this@StepFundingListActivity, Observer {
                binding.scrollContainer.smoothScrollBy(getDeviceWidth() * -1, 0)
            })
            rightClick.observe(this@StepFundingListActivity, Observer {
                binding.scrollContainer.smoothScrollBy(getDeviceWidth(), 0)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        stepFundingListViewModel.refreshData()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.none, R.anim.anim_horizon_exit)
    }

    companion object {
        private const val KEY_CONTEST_ID = "CONTEST_ID"
        fun getIntent(context: Context, contestId: Long) =
            Intent(context, StepFundingListActivity::class.java).apply {
                putExtra(KEY_CONTEST_ID, contestId)
            }
    }
}