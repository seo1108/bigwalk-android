package kr.co.bigwalk.app.lock_screen

import android.annotation.SuppressLint
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.TransitionDrawable
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.databinding.ActivityLockScreenBinding
import kr.co.bigwalk.app.splash.SplashActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.walk.HomeTransformer
import kr.co.bigwalk.app.walk.sensor.StepCallback
import kr.co.bigwalk.app.walk.sensor.WalkSensor
import kotlin.math.roundToInt

class LockScreenActivity : AppCompatActivity(), LockScreenNavigator, StepCallback {

    lateinit var viewModel: LockScreenViewModel
    private lateinit var binding: ActivityLockScreenBinding
    private lateinit var walkFragment: LockScreenFragment
    private lateinit var rankFragment: LockScreenRankFragment
    private var didTouchEnd: Boolean = false

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //lockscreentsettings
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager.requestDismissKeyguard(this, null)
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_lock_screen)
        viewModel = LockScreenViewModel(this)
        binding.viewModel = this.viewModel

        binding.lockViewPager.setPageTransformer(HomeTransformer())
        walkFragment = LockScreenFragment(viewModel)
        rankFragment = LockScreenRankFragment(viewModel)
        binding.lockViewPager.adapter =
            LockScreenFragmentStateAdapter(mutableListOf(walkFragment, rankFragment), this)

        binding.lockViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> pageChangeToWalk()
                    1 -> pageChangeToRank()
                }
            }
        })

        //잠금해제
        var flagX = binding.unlockBtnCenter.x
        binding.unlockBtnCenter.setOnTouchListener { v, event ->
            val dp = pixelToDp(binding.scrollArea.width)
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN -> {
                    if(flagX == 0.0f) flagX = v.x
                }
                MotionEvent.ACTION_UP -> {
                    binding.unlockBtnCenter.setPadding(0,0,0,0)
                    binding.unlockBtnCenter.setBackgroundResource(R.drawable.rounded_oval_background)
                    v.x = flagX
                }
                MotionEvent.ACTION_MOVE -> {
                    //TODO 이동시에 슬라이딩 애니메이션
//                    val deltaX = v.x - flagX
//                    when {
//                        deltaX<0 ->{
//                            binding.unlockBtnCenter.setBackgroundResource(R.drawable.rounded_oval_background_color)
//                            binding.unlockBtnCenter.setPadding(0,0,-deltaX.toInt(),0)
//                            binding.unlockBtnCenter.right = flagX.toInt()
//                        }
//                        deltaX>0 ->{
//                            binding.unlockBtnCenter.setBackgroundResource(R.drawable.rounded_oval_background_color)
//                            binding.unlockBtnCenter.setPadding(deltaX.toInt(),0,0,0)
//                            binding.unlockBtnCenter.left = flagX.toInt()
//                        }
//                    }
                    when {
                        v.x < binding.unlockBtnL.x -> { // leftbuttonhover
                            if (!didTouchEnd) {
                                val walkIntent = Intent(this, SplashActivity::class.java)
                                walkIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                walkIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                walkIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(walkIntent)
                                binding.unlockBtnCenter.setPadding(0,0,0,0)
                                finish()
                            }
                            didTouchEnd = true
                        }
                        v.x > binding.unlockBtnR.x -> { //rightbuttonhover
                            binding.unlockBtnCenter.setPadding(0,0,0,0)
                            finish()
                        }
                        else -> v.x = (event.rawX - v.width/2) - dp + pixelToDp(binding.unlockBtnCenter.width)
                    }

                }
            }
            true
        }
        WalkSensor.stepCallback = this
    }

    override fun onResume() {
        super.onResume()
        window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            statusBarColor = Color.TRANSPARENT
        }
        binding.viewModel?.refreshMyRank()

        viewModel.notifyStepChanged()
        setProgressPercentage(PreferenceManager.getDonableStepPercentage())
        DebugLog.d("왜안되냐")
    }

    private fun pixelToDp(pixel:Int) : Int{
        val metrics = Resources.getSystem().displayMetrics
        return (pixel / (metrics.densityDpi.toFloat() / 160f)).roundToInt() //pixel to dp
    }

    private fun pageChangeToWalk(){
        binding.walkBackgroundImage.apply {
            animate()
                .alpha(1f).duration = 500L
        }
        binding.walkBottomProgress.apply {
            animate()
                .alpha(1f).duration = 500L
        }
        binding.walkBottomProgressBackground.apply {
            animate()
                .alpha(1f).duration = 500L
        }
        val transitionDrawable = TransitionDrawable(
            arrayOf(
                ContextCompat.getDrawable(this, R.drawable.spring_day)
                , ContextCompat.getDrawable(this, R.drawable.spring_day)
            )
        )

        binding.walkBackgroundImage.setImageDrawable(transitionDrawable)
        transitionDrawable.startTransition(500)
    }

    private fun pageChangeToRank(){
        binding.walkBackgroundImage.apply {
            animate()
                .alpha(1f).duration = 500L
        }
        binding.walkBottomProgress.apply {
            animate()
                .alpha(0f).duration = 500L
        }
        binding.walkBottomProgressBackground.apply {
            animate()
                .alpha(0f).duration = 500L
        }
        val transitionDrawable = TransitionDrawable(
            arrayOf(
                ContextCompat.getDrawable(this, R.drawable.spring_day)
                , ContextCompat.getDrawable(this, R.drawable.spring_day)
            )
        )

        binding.walkBackgroundImage.setImageDrawable(transitionDrawable)
        transitionDrawable.startTransition(500)
    }


    override fun onStepChanged(dailyStep: Int, donableStep: Int) {
        viewModel.notifyStepChanged()
    }

    override fun setProgressPercentage(percentage: Int) {
        if (isFinishing) return
        var adjustPercentage = percentage
        if (percentage < 0) adjustPercentage = 0
        if (percentage > 100) adjustPercentage = 100
        val level = 10000 * adjustPercentage / 100
        binding.walkBottomProgress.background.level = level
    }

    override fun getContext(): Activity {
        return this
    }

    override fun onBackPressed() {

    }

    override fun onStop() {
        super.onStop()
        finish()
    }


}

