package kr.co.bigwalk.app.campaign.consumption

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.data.campaign.dto.ValueConsumptionCommerceResponse
import kr.co.bigwalk.app.data.campaign.repository.CampaignDataSource
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.databinding.ActivityConsumptionBinding
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class ConsumptionActivity: AppCompatActivity() {

    companion object {
        const val CAMPAIGN_ID = "CAMPAIGN_ID"
    }

    private lateinit var binding: ActivityConsumptionBinding
    private var firebaseAnalytics : FirebaseAnalytics? = null

    private var campaignId: Long = -1
    private var mData: ValueConsumptionCommerceResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityConsumptionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        campaignId = intent.getLongExtra(CAMPAIGN_ID, -1L)
        if (campaignId != -1L) {
            sendLogEvent(campaignId, "value_consumption_view")
            requestValueConsumptionCommerce(campaignId)
        }

        binding.ivConsumptionExit.setOnClickListener {
            finish()
        }

        binding.btnConsumptionCoupon.setOnClickListener {
            mData?.reward?.let {
                val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("label", it)
                clipboard.setPrimaryClip(clip)
                showToast("쿠폰 복사 완료")
                sendLogEvent(campaignId, "value_consumption_reward_copied")
            }
        }

        binding.btnConsumption.setOnClickListener {
            mData?.let {
                sendLogEvent(campaignId, "value_consumption_link")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it.purchaseUrl))
                startActivity(intent)
            }
        }

    }

    private fun sendLogEvent(id: Long, msg: String) {
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id.toString())
        firebaseAnalytics?.logEvent(msg, bundle)
    }

    private fun requestValueConsumptionCommerce(campaignId: Long) {
        CampaignRepository.getAdditionalServiceValueConsumptionCommerce(campaignId, object : CampaignDataSource.AdditionalServiceValueConsumptionCommerceCallback {
            override fun onSuccess(response: ValueConsumptionCommerceResponse) {
                DebugLog.d("가치소비: $response")
                mData = response
                binding.data = response
            }

            override fun onFailed(reason: String) {
                DebugLog.d("$reason")
                showToast("진행하고 있는 챌린지가 없습니다.")
            }
        })
    }
}