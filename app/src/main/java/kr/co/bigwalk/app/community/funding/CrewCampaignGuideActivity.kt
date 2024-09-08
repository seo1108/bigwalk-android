package kr.co.bigwalk.app.community.funding

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.databinding.ActivityCrewCampaignGuideBinding

class CrewCampaignGuideActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCrewCampaignGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crew_campaign_guide)
        binding.lifecycleOwner = this

        this.window?.apply {
            this.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        PreferenceManager.saveCrewCampaignGuide(true)

        setView()
    }

    private fun setView() {
        with(binding) {
            exitButton.setOnClickListener {
                finish()
            }
            Glide.with(this@CrewCampaignGuideActivity)
                .asBitmap()
                .load(R.drawable.crew_campaign_guide_v4)
                .placeholder(R.drawable.ranking_information_image_low)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .into(binding.guideImage)
        }
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, CrewCampaignGuideActivity::class.java)
    }
}