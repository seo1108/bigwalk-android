package kr.co.bigwalk.app.campaign.donation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.databinding.ObservableLong
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.campaign.dto.CampaignSMSContentResponse
import kr.co.bigwalk.app.data.campaign.dto.DonateRequest
import kr.co.bigwalk.app.data.campaign.repository.CampaignDataSource
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.share.ShareCampaignActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast
import kr.co.bigwalk.app.walk.alarm.StepManager
import java.util.*
import kotlin.math.max

class DonationStepSMSViewModel(private val activity: Activity,  private val donationData: DonationData, private val SMSContent: CampaignSMSContentResponse):
    DonationViewModel(activity, donationData) {

    val isStepClicked: ObservableBoolean = ObservableBoolean()
    val amount: ObservableLong = ObservableLong()
    val heading: ObservableField<String> = ObservableField()
    val content: ObservableField<String> = ObservableField()
    val privacyPolicy: ObservableField<String> = ObservableField()

    init {
        isStepClicked.set(true)
        amount.set(SMSContent.amount)
        heading.set(SMSContent.heading)
        content.set(SMSContent.content)
        privacyPolicy.set(SMSContent.privacyPolicy)
    }

    fun stepClick() {
        isStepClicked.set(true)
    }

    fun smsClick() {
        isStepClicked.set(false)
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