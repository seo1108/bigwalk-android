package kr.co.bigwalk.app.campaign.recent

import android.os.Build
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.BuildConfig
import kr.co.bigwalk.app.campaign.donation.DonationStepSMSFragment
import kr.co.bigwalk.app.campaign.donation.makeBottomSheetDialogFragment
import kr.co.bigwalk.app.campaign.donation.makeDonationData
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.api.UserWalkLogRequest
import kr.co.bigwalk.app.data.campaign.dto.CampaignSMSContentResponse
import kr.co.bigwalk.app.data.campaign.dto.ResponseCampaign
import kr.co.bigwalk.app.data.campaign.repository.CampaignDataSource
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showDebugToast
import kr.co.bigwalk.app.util.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class RecentCampaignViewModel(private val navigator: RecentCampaignNavigator): ViewModel() {

    val recentCampaigns: ObservableList<ResponseCampaign> = ObservableArrayList()

    init {
        getRecentCampaigns()
    }

    private fun getRecentCampaigns() {
        CampaignRepository.getParticipatedCampaigns(
            object : CampaignDataSource.GetParticipatedCampaignsCallback {
                override fun onSuccess(participatedCampaigns: List<ResponseCampaign>) {
                    if (!participatedCampaigns.isNullOrEmpty()) recentCampaigns.addAll(participatedCampaigns)
                }

                override fun onFailed(reason: String) {
                    showToast("내가 참여한 캠페인 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }
            }
        )
    }

    fun finish() {
        navigator.finish()
    }

    fun donateStep(campaign: ResponseCampaign) {
        val now = Date()
        if (now < campaign.startDate || now > campaign.endDate) {
            showToast("캠페인 참여 기간이 아닙니다.")
            showDebugToast("캠페인 참여 기간이 아닙니다.")
            return
        }

        if (!isEligibleUser(campaign)) {
            showToast("기업 전용 캠페인입니다. 다른 캠페인에 함께 해주세요.")
            showDebugToast("기업 전용 캠페인입니다. 다른 캠페인에 함께 해주세요.")
            return
        }

        if (PreferenceManager.getDonableStep() > 0) {
            if (campaign.smsId != 0L) {
                requestSMSContent(campaign)
            } else {
                val donationFragment = makeBottomSheetDialogFragment(makeDonationData(campaign))
                donationFragment.show(navigator.getActivityFragmentManager(), donationFragment.tag)
            }
        } else {
            showToast("기부할 수 있는 걸음이 없습니다. 함께 걸어볼까요?")
            showDebugToast("기부할 수 있는 걸음이 없습니다. 함께 걸어볼까요?")

            // 기부 할 수 없을 경우 로그 데이터 전송
            val name = PreferenceManager.getName()
            val userId = PreferenceManager.getUserId().toString()
            val logRequest = UserWalkLogRequest(
                name + "",
                userId + "",
                "A",
                Build.MODEL + " [Android" + Build.VERSION.RELEASE + "] [" + BuildConfig.VERSION_NAME+ "]",
                "donate failed : donableStep [" + PreferenceManager.getDonableStep() + "], reason [RecentCampaignViewModel]"
            )

            with(RemoteApiManager) {

                getUserApi().userReqLog(logRequest)
                    .enqueue(object : Callback<BaseResponse<Nothing>> {
                        override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                            when (response.body()?.result) {
                            }
                        }

                        override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                            //showToast(t.localizedMessage)
                        }
                    })
            }
        }

//        if (PreferenceManager.getDonableStep() <= 0) {
//            if (campaign.smsId != 0L) {
//                requestSMSContent(campaign, true)
//            } else {
//                showToast("기부할 수 있는 걸음이 없습니다. 함께 걸어볼까요?")
//                showDebugToast("기부할 수 있는 걸음이 없습니다. 함께 걸어볼까요?")
//            }
//        } else {
//            if (campaign.smsId != 0L) {
//                requestSMSContent(campaign, false)
//            } else {
//                val donationFragment = DonationFragment(campaign.id)
//                donationFragment.show(navigator.getActivityFragmentManager(), donationFragment.tag)
//            }
//        }
    }

    private fun isEligibleUser(campaign:ResponseCampaign): Boolean {
        if (campaign.organizations.isNullOrEmpty()) {// 일반 캠페인일 때
            return true
        } else {// 기업전용 캠페인이면서 참여가능한 유저이면 true
            if (PreferenceManager.getOrganization() != -1L) {
                for (organization in campaign.organizations) {
                    if (organization.id == PreferenceManager.getOrganization()) return true
                }
            }
        }
        return false
    }

    private fun requestSMSContent(responseCampaign: ResponseCampaign) {
        CampaignRepository.getSMSContent(responseCampaign.id, responseCampaign.smsId, object : CampaignDataSource.GetSMSCallBack {
            override fun onSuccess(SMSContent: CampaignSMSContentResponse) {
                val donationFragment = DonationStepSMSFragment(makeDonationData(responseCampaign), SMSContent)
                donationFragment.show(navigator.getActivityFragmentManager(), donationFragment.tag)
            }

            override fun onFailed(reason: String) {
                showToast("SMS 문자 정보를 가져오는 데 실패했습니다. 나중에 다시 시도해주세요.")
                DebugLog.e(CampaignException(reason))
            }
        })
    }

}