package kr.co.bigwalk.app.walk

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.TranslateAnimation
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.firebase.analytics.FirebaseAnalytics
import com.linecorp.apng.ApngDrawable
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.crowd_funding.StepFundingListActivity
import kr.co.bigwalk.app.crowd_funding.TotalMyFundingActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.databinding.FrameWalk1Binding
import kr.co.bigwalk.app.dialog.SignalImageDialog
import kr.co.bigwalk.app.dialog.WelcomeDialog
import kr.co.bigwalk.app.extension.dpToPx
import kr.co.bigwalk.app.util.*
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.listener.DismissListener
import me.toptas.fancyshowcase.listener.OnCompleteListener
import me.toptas.fancyshowcase.listener.OnViewInflateListener


class WalkFragment1 : Fragment() {

    private var characterDrawable: ApngDrawable? = null
    private lateinit var binding : FrameWalk1Binding
    private lateinit var showcaseQueue: FancyShowCaseQueue
    private lateinit var todayStepsShowcaseView: FancyShowCaseView
    private lateinit var todayKcalShowcaseView: FancyShowCaseView
    private lateinit var donableStepsShowcaseView: FancyShowCaseView
    private lateinit var welcomeDialog: WelcomeDialog
    private lateinit var tutorialWalkTodaysStepConst: ConstraintLayout
    private lateinit var tutorialAnimation: Animation

    private val dismissListener = object: DismissListener {
        override fun onDismiss(id: String?) {
            // nothing to do
        }
        override fun onSkipped(id: String?) {
            /*val intent = Intent(activity, WelcomeActivity::class.java)
            activity?.startActivityForResult(intent, WalkActivity.REQUEST_CODE_WELCOME)*/

            binding.walkTodaysStepConst.viewTreeObserver.removeOnGlobalLayoutListener(walkTodaysStepConstGlobalLayoutListener)
            if (!welcomeDialog.isShowing) {
                welcomeDialog.show()
            }
        }

    }

    private val queueCompleteListener = object: OnCompleteListener {
        override fun onComplete() {
            /*val intent = Intent(activity, WelcomeActivity::class.java)
            activity?.startActivityForResult(intent, WalkActivity.REQUEST_CODE_WELCOME)*/

            binding.walkTodaysStepConst.viewTreeObserver.removeOnGlobalLayoutListener(walkTodaysStepConstGlobalLayoutListener)
            if (!welcomeDialog.isShowing) {
                welcomeDialog.show()
            }
        }

    }

    private val walkTodaysStepConstGlobalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        val params = (tutorialWalkTodaysStepConst.layoutParams as ViewGroup.MarginLayoutParams)
        params.topMargin = binding.walkTodaysKcalConst.y.toInt()
        tutorialWalkTodaysStepConst.layoutParams = params
    }

    companion object {
        fun newInstance(): WalkFragment1 {
            return WalkFragment1()
        }
    }

    override fun onCreateView (
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.frame_walk_1,
            container,
            false
        )
        val walkActivity = activity as WalkActivity
        binding.viewModel = walkActivity.viewModel
        binding.walkRandomText.text = MainRandomViewMaker.getRandomComment()
        WalkActivity.firebaseAnalytics?.logEvent("home_view", Bundle())
        val titleText = resources.getString(R.string.ranking_plus)
        val spannable = SpannableString(titleText)
        spannable.setSpan(TopAlignSuperscriptSpan(), titleText.length-1, titleText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.homeRankingLabel.text = spannable

        welcomeDialog = WelcomeDialog(activity!!)
        welcomeDialog.setOnDismissListener {
            if (PreferenceManager.getWelcomeMissionStatus()!="none") {
                walkActivity.viewModel.showWelcomeEventDialog()
            }
        }

        tutorialAnimation = TranslateAnimation(0f, 0f, 36.dpToPx().toFloat(), 0f).apply {
            duration = 1500
            interpolator = BounceInterpolator()
        }

        showStepFundingView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (!PreferenceManager.haveSeenTutorial()) {
            showTutorialView()
            PreferenceManager.saveTutorial(true)
            binding.viewModel?.requestMissions()
        }
        if (isAdded) playAnimation()
        binding.viewModel?.checkMissionCompleted()
    }

    private fun playAnimation() {
        SingleThreadExecutor().execute {
            val characterAssetName = when {
                PreferenceManager.getCharacter() == "2" -> {
                    "apng_b.png"
                }
                PreferenceManager.getCharacter() == "3" -> {
                    "apng_c.png"
                }
                PreferenceManager.getCharacter() == "4" -> {
                    "apng_d.png"
                }
                PreferenceManager.getCharacter() == "5" -> {
                    "apng_e.png"
                }
                else -> {
                    "apng_a.png"
                }
            }
            if(characterDrawable == null || characterDrawable?.isRecycled == true) {
                characterDrawable = ApngDrawable.decode(activity!!.assets, characterAssetName)
            }
            MainThreadExecutor().execute {
                characterDrawable?.clearAnimationCallbacks()
                binding.walkMovingCharacter.setImageDrawable(null)
                characterDrawable?.loopCount = ApngDrawable.LOOP_FOREVER
                if (isAdded) characterDrawable?.setTargetDensity(resources.displayMetrics)
                binding.walkMovingCharacter.setImageDrawable(characterDrawable)
                characterDrawable?.start()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        characterDrawable?.recycle()
        characterDrawable = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        characterDrawable?.recycle()
        characterDrawable = null
    }

    private fun showTutorialView() {
        todayStepsShowcaseView = FancyShowCaseView.Builder(activity as WalkActivity)
            .focusOn(binding.walkTodaysStepConstFocus)
            .disableFocusAnimation()
            .customView(R.layout.layout_tutorial, object : OnViewInflateListener {
                override fun onViewInflated(view: View) {
                    setTutorialContent(view, todayStepsShowcaseView)
                }
            })
            .fitSystemWindows(true) // 메인화면이 full-screen일 경우, statusbar를 포함하지 않아, focusing이 조금 위쪽으로 올라가는 것을 방지.
            .dismissListener(dismissListener)
            .build()
        todayKcalShowcaseView = FancyShowCaseView.Builder(activity as WalkActivity)
            .focusOn(binding.walkTodaysKcalConstFocus)
            .disableFocusAnimation()
            .customView(R.layout.layout_tutorial, object : OnViewInflateListener {
                override fun onViewInflated(view: View) {
                    setTutorialContent(view, todayKcalShowcaseView)
                }
            })
            .fitSystemWindows(true)
            .dismissListener(dismissListener)
            .build()
        donableStepsShowcaseView = FancyShowCaseView.Builder(activity as WalkActivity)
            .focusOn(binding.walkDonableStepConstFocus)
            .disableFocusAnimation()
            .customView(R.layout.layout_tutorial, object : OnViewInflateListener {
                override fun onViewInflated(view: View) {
                    setTutorialContent(view, donableStepsShowcaseView)
                }
            })
            .fitSystemWindows(true)
            .dismissListener(dismissListener)
            .build()
        showcaseQueue = FancyShowCaseQueue()
            .add(todayStepsShowcaseView)
            .add(todayKcalShowcaseView)
            .add(donableStepsShowcaseView)
        showcaseQueue.completeListener = queueCompleteListener
        showcaseQueue.show()

    }

    private fun setTutorialContent(view: View, fancyShowCaseView: FancyShowCaseView) {
        tutorialWalkTodaysStepConst = view.findViewById<View>(R.id.walk_todays_step_const) as ConstraintLayout
        val tutorialWalkTodaysKcalConst = view.findViewById<View>(R.id.walk_todays_kcal_const) as ConstraintLayout
        val tutorialWalkDonableStepConst = view.findViewById<View>(R.id.walk_donable_step_const) as ConstraintLayout
        val walkTodaysStep = view.findViewById<TextView>(R.id.walk_todays_step)
        val walkTodaysKcal = view.findViewById<TextView>(R.id.walk_todays_kcal)
        val walkDonabeStep = view.findViewById<TextView>(R.id.walk_donabe_step)
        val todayStepConst = view.findViewById<View>(R.id.tutorial_today_step_const) as ConstraintLayout
        val todayKcalConst = view.findViewById<View>(R.id.tutorial_today_kcal_const) as ConstraintLayout
        val donableStepConst = view.findViewById<View>(R.id.tutorial_donable_step_const) as ConstraintLayout

        binding.walkTodaysStepConst.viewTreeObserver.addOnGlobalLayoutListener(walkTodaysStepConstGlobalLayoutListener)
        walkTodaysStep.text = PreferenceManager.getDailyStep().toString()
        walkTodaysKcal.text = PreferenceManager.getKcalorie().toString()
        walkDonabeStep.text = PreferenceManager.getDonableStep().toString()

        todayKcalConst.bringToFront()
        when(fancyShowCaseView) {
            todayStepsShowcaseView -> {
                tutorialWalkTodaysStepConst.visibility = View.VISIBLE
                tutorialWalkTodaysKcalConst.visibility = View.INVISIBLE
                tutorialWalkDonableStepConst.visibility = View.INVISIBLE
                todayStepConst.startAnimation(tutorialAnimation)
                todayStepConst.visibility = View.VISIBLE
                todayKcalConst.visibility = View.GONE
                donableStepConst.visibility = View.GONE
            }
            todayKcalShowcaseView -> {
                tutorialWalkTodaysStepConst.visibility = View.INVISIBLE
                tutorialWalkTodaysKcalConst.visibility = View.VISIBLE
                tutorialWalkDonableStepConst.visibility = View.INVISIBLE
                todayKcalConst.startAnimation(tutorialAnimation)
                todayStepConst.visibility = View.GONE
                todayKcalConst.visibility = View.VISIBLE
                donableStepConst.visibility = View.GONE
            }
            donableStepsShowcaseView -> {
                tutorialWalkTodaysStepConst.visibility = View.INVISIBLE
                tutorialWalkTodaysKcalConst.visibility = View.INVISIBLE
                tutorialWalkDonableStepConst.visibility = View.VISIBLE
                donableStepConst.startAnimation(tutorialAnimation)
                todayStepConst.visibility = View.GONE
                todayKcalConst.visibility = View.GONE
                donableStepConst.visibility = View.VISIBLE
            }
        }
    }

    private fun showStepFundingView() {
        binding.funding.setOnClickListener {
            FirebaseAnalytics.getInstance(requireContext()).logEvent("main_button_crowd_funding_click", Bundle())
            startActivity(StepFundingListActivity.getIntent(requireContext(), it.tag as Long))
        }
        binding.funded.setOnClickListener {
            FirebaseAnalytics.getInstance(requireContext()).logEvent("main_button_my_funding_reward_click", Bundle())
            startActivity(TotalMyFundingActivity.getIntent(requireContext(), it.tag as Long?))
        }
    }

}