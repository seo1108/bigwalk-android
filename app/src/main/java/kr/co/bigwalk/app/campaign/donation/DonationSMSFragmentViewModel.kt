package kr.co.bigwalk.app.campaign.donation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.FrameLayout
import androidx.databinding.ObservableField
import androidx.databinding.ObservableLong
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kr.co.bigwalk.app.data.campaign.dto.CampaignSMSContentResponse
import kr.co.bigwalk.app.util.DebugLog


class DonationSMSFragmentViewModel(private val activity: Activity, private val SMSContent: CampaignSMSContentResponse) {

    lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    val amount: ObservableLong = ObservableLong()
    val heading: ObservableField<String> = ObservableField()
    val content: ObservableField<String> = ObservableField()
    val privacyPolicy: ObservableField<String> = ObservableField()

    init {
        amount.set(SMSContent.amount)
        heading.set(SMSContent.heading)
        content.set(SMSContent.content)
        privacyPolicy.set(SMSContent.privacyPolicy)
    }

    fun donateBySMS() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", SMSContent.number, null))
        intent.putExtra("sms_body", SMSContent.text)
        activity.startActivityForResult(intent, 1)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun hideSMSFragment() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

}