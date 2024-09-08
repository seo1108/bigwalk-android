package kr.co.bigwalk.app.feed_guide

import android.app.Activity
import androidx.databinding.ObservableField

import kr.co.bigwalk.app.data.campaign.dto.ActionMissionResponse
import kr.co.bigwalk.app.data.campaign.repository.CampaignDataSource
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class FeedGuideViewModel(
    private val activity: Activity,
    private val campaignId: Long
) {

    val actionMission: ObservableField<ActionMissionResponse> = ObservableField()

    init {
        CampaignRepository.getAdditionalServiceActionMission(campaignId, object : CampaignDataSource.AdditionalServiceActionMissionCallback {
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

    fun closeClick() {
        activity.finish()
    }
}