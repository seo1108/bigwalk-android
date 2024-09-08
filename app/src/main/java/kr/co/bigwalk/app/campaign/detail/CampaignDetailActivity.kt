package kr.co.bigwalk.app.campaign.detail

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.content_campaign_detail.*
import kotlinx.android.synthetic.main.item_recycler_campaign_content_video.*
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.adapter.CampaignContentRecyclerAdapter
import kr.co.bigwalk.app.databinding.ActivityCampaignDetailBinding
import kr.co.bigwalk.app.util.gaSendLogEvent
import kr.co.bigwalk.app.util.showToast

class CampaignDetailActivity : AppCompatActivity(), BaseNavigator {

    companion object{
        var firebaseAnalytics : FirebaseAnalytics? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val campaignId = intent.getLongExtra("campaign_id", -1L)
        if (campaignId == -1L) {
            showToast("캠페인 상세 페이지를 불러올 수 없습니다. 다시 시도해주세요.")
            finish()
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val bundle = Bundle()
        bundle.putString("campaign_id", campaignId.toString())
        firebaseAnalytics?.logEvent("campaign_detail $campaignId", bundle)

        val binding : ActivityCampaignDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_campaign_detail)
        binding.viewModel = CampaignDetailViewModel(this, supportFragmentManager, campaignId)
        campaign_content_recycler.adapter = CampaignContentRecyclerAdapter(lifecycle)

        var isSend = false
        binding.scrollContainer.scrollContainer.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (v?.canScrollVertically(1) != true && !isSend) {
                gaSendLogEvent(this@CampaignDetailActivity, "campaign_detail_scroll_to_bottom_event")
                isSend = true
            }
        }
    }

    override fun getContext(): Activity {
        return this
    }

    override fun onDestroy() {
        super.onDestroy()
        youtube_player_view?.release()
    }
}