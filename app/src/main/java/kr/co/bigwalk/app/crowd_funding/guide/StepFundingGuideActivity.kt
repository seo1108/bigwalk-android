package kr.co.bigwalk.app.crowd_funding.guide

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityStepFundingGuideBinding

class StepFundingGuideActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStepFundingGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_step_funding_guide)
        binding.lifecycleOwner = this
        FirebaseAnalytics.getInstance(this).logEvent("funding_step_guide_view", Bundle())

        this.window?.apply {
            this.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        setView()
    }

    private fun setView() {
        with(binding) {
            exitButton.setOnClickListener {
                finish()
            }
            Glide.with(this@StepFundingGuideActivity)
                .asBitmap()
                .load(R.drawable.step_funding_guide)
                .placeholder(R.drawable.ranking_information_image_low)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(binding.guideImage)
        }
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, StepFundingGuideActivity::class.java)
    }
}