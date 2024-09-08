package kr.co.bigwalk.app.campaign.donation

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.campaign.dto.ValueConsumptionCommerceResponse
import kr.co.bigwalk.app.extension.setBoldOfParticularPart
import kr.co.bigwalk.app.extension.setColorOfParticularPart
import kr.co.bigwalk.app.util.showToast


class DonationValueConsumptionCommerceViewModel(
    private val activity: Activity,
    private val donationData: DonationData,
    val commerce: ValueConsumptionCommerceResponse
) {

    lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>

    fun dismiss() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun getTitle(): SpannableString {
        var title1 = "${PreferenceManager.getName()} 님의\n기후 행동으로"
        val title2 = "탄소 ${commerce.title} 감소시켰어요 :)"
        title1 = "$title1\n$title2"
        return title1.setColorOfParticularPart(
            title2, ContextCompat.getColor(
                activity,
                R.color.shamrock_green
            )
        )
    }

    fun getDescription(): SpannableString {
        var content = commerce.content
        val importantContent = commerce.importantContent
        content = "$content\n$importantContent"
        return content.setBoldOfParticularPart(importantContent)
    }

    fun copyReward() {
        val clipboard: ClipboardManager? = activity.applicationContext.getSystemService(
            CLIPBOARD_SERVICE
        ) as ClipboardManager?
        val clip = ClipData.newPlainText("label", commerce.reward)
        clipboard?.setPrimaryClip(clip)
        showToast("복사 완료")
    }

    fun getCampaignName(): SpannableString {
        val content: SpannableString = SpannableString(commerce.extra2);
        content.setSpan(UnderlineSpan(), 0, content.length, 0);
        return content
    }

    fun moveCompanyWebsite() {
        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(commerce.extra3)))

        val bundle = Bundle()
        bundle.putString("campaign_id", donationData.campaignId.toString())
        FirebaseAnalytics.getInstance(activity.applicationContext).logEvent("value_consumption_commerce_homepage", bundle)
    }

    fun movePurchaseWebsite() {
        activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(commerce.purchaseUrl)))

        val bundle = Bundle()
        bundle.putString("campaign_id", donationData.campaignId.toString())
        FirebaseAnalytics.getInstance(activity.applicationContext).logEvent("value_consumption_commerce_link", bundle)
    }
}