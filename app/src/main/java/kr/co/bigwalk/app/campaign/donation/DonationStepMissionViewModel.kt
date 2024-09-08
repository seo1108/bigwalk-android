package kr.co.bigwalk.app.campaign.donation

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import kr.co.bigwalk.app.campaign.donation.additional_service.MissionCertificationActivity
import kr.co.bigwalk.app.campaign.donation.additional_service.MissionDonationData
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.campaign.dto.ActionMissionResponse
import kr.co.bigwalk.app.data.campaign.repository.CampaignDataSource
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast
import kotlin.collections.ArrayList

class DonationStepMissionViewModel(
    private val activity: Activity,
    private val donationData: DonationData,
    private val fromCampaign: Boolean = false
): DonationViewModel(activity, donationData) {

    val isStepClicked: ObservableBoolean = ObservableBoolean()
    val actionMission: ObservableField<ActionMissionResponse> = ObservableField()

    init {
        isStepClicked.set(PreferenceManager.hasSeenMission(donationData.campaignId))
        CampaignRepository.getAdditionalServiceActionMission(donationData.campaignId, object : CampaignDataSource.AdditionalServiceActionMissionCallback {
            override fun onSuccess(response: ActionMissionResponse) {
                DebugLog.d("미션: $response")
                actionMission.set(response)
            }

            override fun onFailed(reason: String) {
                DebugLog.d("$reason")
                showToast("진행하고 있는 챌린지가 없습니다.")
            }
        })
    }

    fun stepClick() {
        isStepClicked.set(true)
    }

    fun missionClick() {
        isStepClicked.set(false)
    }

    fun donateByMission() {
        actionMission.get()?.let { actionMission ->
            val intent = Intent(activity, MissionCertificationActivity::class.java)
            intent.putExtra(MissionCertificationActivity.PARAM_ACTION_MISSION, Gson().toJson(MissionDonationData(actionMission, donationData.campaignId)))
            intent.putExtra(MissionCertificationActivity.PARAM_FROM_CAMPAIGN, fromCampaign)
            activity.startActivity(intent)

            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    fun donateByMission(uri: ArrayList<String>) {
        actionMission.get()?.let { actionMission ->
            val intent = Intent(activity, MissionCertificationActivity::class.java)
            intent.putExtra(MissionCertificationActivity.PARAM_ACTION_MISSION, Gson().toJson(MissionDonationData(actionMission, donationData.campaignId)))
            intent.putStringArrayListExtra(MissionCertificationActivity.PARAM_IMAGE_URIS, uri)
            activity.startActivity(intent)
        }
    }

    fun dismiss() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }

    fun finishActivity() {

    }
}