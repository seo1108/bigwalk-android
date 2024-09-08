package kr.co.bigwalk.app.campaign.ranking

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.view.View
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.ranking.category.OrganizationRankingFragment
import kr.co.bigwalk.app.campaign.ranking.category.TodayRankingFragment
import kr.co.bigwalk.app.campaign.ranking.category.TotalRankingFragment
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.databinding.ActivityRankingPlusBinding
import kr.co.bigwalk.app.databinding.LayoutTutorialRankingPlusBinding
import kr.co.bigwalk.app.extension.dpToPx
import kr.co.bigwalk.app.util.TopAlignSuperscriptSpan

class RankingPlusActivity : AppCompatActivity(), BaseNavigator {
    private lateinit var binding: ActivityRankingPlusBinding
    lateinit var viewModel: RankingPlusViewModel
        private set
    private lateinit var tutorialAnimation: Animation
    private lateinit var tutorialBinding: LayoutTutorialRankingPlusBinding
    private val maxCategory: Category = if (PreferenceManager.getOrganization() == -1L) Category.TODAY else Category.ORGANIZATION
    private val allRankingFragment: TotalRankingFragment by lazy { TotalRankingFragment.newInstance() }
    private val todayRankingFragment: TodayRankingFragment by lazy { TodayRankingFragment.newInstance() }
    private val groupRankingFragment: OrganizationRankingFragment by lazy { OrganizationRankingFragment.newInstance() }

    private val onClickListener = View.OnClickListener { view ->
        val tabIndex = view.tag.toString().toInt()
        selectTab(tabIndex)
        transitionTab(tabIndex)
    }

    companion object {
        var initTabPosition: Category = Category.TOTAL
        const val SELECT_TAB = "SELECT_TAB"
        const val REQUEST_RANKING_INFO = 123
        const val RANKING_PAGE_SIZE = 30
    }

    enum class Tutorial {
        ORIGINAL_RANKING,
        TOTAL_RANKING,
        TODAY_RANKING,
        ORGANIZATION_RANKING,
        RANKING_PERIOD,
        MY_RANKING_CARD;
    }

    enum class Category(val categoryName: String) {
        TOTAL("all"),
        TODAY("today"),
        ORGANIZATION("group");
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAnalytics.getInstance(this).logEvent("campaign_ranking_view", Bundle())
        binding = DataBindingUtil.setContentView(this, R.layout.activity_ranking_plus)
        val viewModelFactory = RankingPlusViewModelFactory(this)
        viewModel =
            ViewModelProvider(this, viewModelFactory).get(RankingPlusViewModel::class.java)
        binding.viewModel = viewModel
        tutorialAnimation = TranslateAnimation(0f, 0f, 36.dpToPx().toFloat(), 0f).apply {
            duration = 1500
            interpolator = BounceInterpolator()
        }

        updateView()
        setOrganization()

        val tabIndex = intent.getIntExtra(SELECT_TAB, 0)
        selectTab(tabIndex)

        supportFragmentManager.beginTransaction().apply {
            if (maxCategory == Category.ORGANIZATION) {
                add(R.id.ranking_content_container, groupRankingFragment)
            }
            add(R.id.ranking_content_container, todayRankingFragment)
            add(R.id.ranking_content_container, allRankingFragment)
        }.commit()
        transitionTab(initTabPosition.ordinal)

        if (!PreferenceManager.haveSeenRankingTutorial()) {
            viewModel.requestMyRanking()
            viewModel.moveToRankInfo()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_RANKING_INFO && resultCode == RESULT_OK) {
            setTutorialContent()
            PreferenceManager.saveRankingTutorial(true)
        }
    }

    override fun onResume() {
        super.onResume()
        if(initTabPosition != Category.TOTAL){
            selectTab(initTabPosition.ordinal)
            initTabPosition = Category.TOTAL
        }
    }

    override fun onBackPressed() {
        viewModel.finish()
    }

    override fun getContext(): Activity {
        return this
    }

    private fun updateView() {
        val titleText = resources.getString(R.string.ranking_plus)
        val spannable = SpannableString(titleText)
        spannable.setSpan(TopAlignSuperscriptSpan(), titleText.length-1, titleText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.rankingTitle.text = spannable

        binding.totalRanking.setOnClickListener(onClickListener)
        binding.todayRanking.setOnClickListener(onClickListener)
    }

    private fun transitionTab(position: Int) {
        when(position) {
            Category.ORGANIZATION.ordinal -> {
                supportFragmentManager.beginTransaction()
                    .show(groupRankingFragment)
                    .hide(todayRankingFragment)
                    .hide(allRankingFragment)
                    .commit()
            }
            Category.TODAY.ordinal -> {
                supportFragmentManager.beginTransaction()
                    .hide(groupRankingFragment)
                    .show(todayRankingFragment)
                    .hide(allRankingFragment)
                    .commit()
            }
            else -> {
                supportFragmentManager.beginTransaction()
                    .hide(groupRankingFragment)
                    .hide(todayRankingFragment)
                    .show(allRankingFragment)
                    .commit()
            }
        }
    }

    private fun selectTab(tabIndex: Int) {
        unselectAllTab()
        when (tabIndex) {
            Category.TODAY.ordinal -> binding.todayRanking.isSelected = true
            Category.ORGANIZATION.ordinal -> binding.organizationRanking.isSelected = true
            else -> binding.totalRanking.isSelected = true
        }
    }

    private fun unselectAllTab() {
        binding.todayRanking.isSelected = false
        binding.organizationRanking.isSelected = false
        binding.totalRanking.isSelected = false
    }

    private fun setOrganization() {
        if (maxCategory == Category.ORGANIZATION) {
            binding.organizationRanking.visibility = View.VISIBLE
            binding.organizationRanking.setOnClickListener(onClickListener)
        } else {
            binding.organizationRanking.visibility = View.GONE
        }
    }

    private fun setTutorialContent() {
        tutorialBinding = binding.layoutTutorialRankingPlus
        tutorialBinding.viewModel = viewModel
        tutorialBinding.tutorialRootView.visibility = View.VISIBLE
        val dotCount = if (maxCategory == Category.ORGANIZATION) 6 else 5
        tutorialBinding.tutorialIndicator.createDotPanel(dotCount, R.drawable.bg_indicator_tutorial_selector)

        val tutorialRankingCategoryList = if (maxCategory == Category.ORGANIZATION) {
            mutableListOf(tutorialBinding.tutorialTextbox2, tutorialBinding.tutorialTextbox3, tutorialBinding.tutorialTextbox4)
        } else {
            mutableListOf(tutorialBinding.tutorialTextbox3, tutorialBinding.tutorialTextbox3, tutorialBinding.tutorialTextbox3)
        }
        val tutorialCategoryTextList = if (maxCategory == Category.ORGANIZATION) {
            mutableListOf(tutorialBinding.tutorialTextbox2Text, tutorialBinding.tutorialTextbox3Text, tutorialBinding.tutorialTextbox4Text)
        } else {
            mutableListOf(tutorialBinding.tutorialTextbox3Text, tutorialBinding.tutorialTextbox3Text, tutorialBinding.tutorialTextbox3Text)
        }

        var index = Tutorial.ORIGINAL_RANKING.ordinal
        handleTutorialContent(index, tutorialRankingCategoryList, tutorialCategoryTextList)
        tutorialBinding.tutorialRootView.setOnClickListener {
            index++

            if (maxCategory != Category.ORGANIZATION && Tutorial.ORGANIZATION_RANKING.ordinal == index) {
                index++
            }

            if (index <= Tutorial.MY_RANKING_CARD.ordinal) {
                handleTutorialContent(index, tutorialRankingCategoryList, tutorialCategoryTextList)
            } else {
                tutorialBinding.tutorialRootView.visibility = View.GONE
            }
        }
    }

    private fun handleTutorialContent(
        step: Int,
        tutorialRankingCategoryList: List<ConstraintLayout>,
        tutorialCategoryTextList: MutableList<TextView>
    ) {
        var position = step
        with(tutorialBinding) {
            tutorialRankingNext.visibility = View.INVISIBLE
            tutorialTextbox1.visibility = View.INVISIBLE
            tutorialTotalRanking.visibility = View.INVISIBLE
            tutorialTextbox2.visibility = View.INVISIBLE
            tutorialTodayRanking.visibility = View.INVISIBLE
            tutorialTextbox3.visibility = View.INVISIBLE
            tutorialOrganizationRanking.visibility = if (maxCategory == Category.ORGANIZATION) View.INVISIBLE else View.GONE
            tutorialTextbox4.visibility = View.INVISIBLE
            tutorialRankingDate.visibility = View.INVISIBLE
            tutorialTextboxPeriod.visibility = View.INVISIBLE
            tutorialMyRankingSceneCard.visibility = View.INVISIBLE
            tutorialTextboxMyRankingCard.visibility = View.INVISIBLE
            when (position) {
                Tutorial.ORIGINAL_RANKING.ordinal -> {
                    tutorialRankingNext.visibility = View.VISIBLE
                    tutorialTextbox1.visibility = View.VISIBLE
                    tutorialTextbox1.startAnimation(tutorialAnimation)
                }
                Tutorial.TOTAL_RANKING.ordinal -> {
                    tutorialTotalRanking.visibility = View.VISIBLE
                    tutorialTextbox1.clearAnimation()
                    tutorialRankingCategoryList[0].visibility = View.VISIBLE
                    tutorialRankingCategoryList[0].startAnimation(tutorialAnimation)
                    tutorialCategoryTextList[0].text = resources.getString(R.string.ranking_plus_tutorial_text_total_ranking)
                }
                Tutorial.TODAY_RANKING.ordinal -> {
                    tutorialTodayRanking.visibility = View.VISIBLE
                    tutorialRankingCategoryList[1].visibility = View.VISIBLE
                    tutorialRankingCategoryList[0].clearAnimation()
                    tutorialRankingCategoryList[1].startAnimation(tutorialAnimation)
                    tutorialCategoryTextList[1].text = resources.getString(R.string.ranking_plus_tutorial_text_today_ranking)
                }
                Tutorial.ORGANIZATION_RANKING.ordinal -> {
                    tutorialOrganizationRanking.visibility = View.VISIBLE
                    tutorialRankingCategoryList[2].visibility = View.VISIBLE
                    tutorialRankingCategoryList[1].clearAnimation()
                    tutorialRankingCategoryList[2].startAnimation(tutorialAnimation)
                    tutorialCategoryTextList[2].text = resources.getString(R.string.ranking_plus_tutorial_text_organization_ranking)
                }
                Tutorial.RANKING_PERIOD.ordinal -> {
                    tutorialRankingDate.visibility = View.VISIBLE
                    tutorialTextboxPeriod.visibility = View.VISIBLE
                    tutorialRankingCategoryList[2].clearAnimation()
                    tutorialTextboxPeriod.startAnimation(tutorialAnimation)
                    if (maxCategory!=Category.ORGANIZATION) position -= 1
                }
                Tutorial.MY_RANKING_CARD.ordinal -> {
                    tutorialMyRankingSceneCard.visibility = View.VISIBLE
                    tutorialTextboxMyRankingCard.visibility = View.VISIBLE
                    tutorialTextboxPeriod.clearAnimation()
                    tutorialTextboxMyRankingCard.startAnimation(tutorialAnimation)
                    if (maxCategory!=Category.ORGANIZATION) position -= 1
                }
            }
            tutorialBinding.tutorialIndicator.selectDot(position)
        }
    }
}