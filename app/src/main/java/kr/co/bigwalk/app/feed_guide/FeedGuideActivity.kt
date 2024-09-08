package kr.co.bigwalk.app.feed_guide

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.databinding.ActivityFeedGuideBinding

class FeedGuideActivity : AppCompatActivity() {
    lateinit var binding: ActivityFeedGuideBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAnalytics.getInstance(this).logEvent("feed_challenge_guide_view", Bundle())
        binding = ActivityFeedGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val campaignId = intent.getLongExtra(CAMPAIGN_ID, -1L)
        val viewModel = FeedGuideViewModel(this, campaignId)
        binding.viewModel = viewModel
    }

    companion object {
        private const val CAMPAIGN_ID = "CAMPAIGN_ID"
        fun getIntent(context: Context, campaignId: Long) =
            Intent(context, FeedGuideActivity::class.java).apply {
                putExtra(CAMPAIGN_ID, campaignId)
            }
    }
}