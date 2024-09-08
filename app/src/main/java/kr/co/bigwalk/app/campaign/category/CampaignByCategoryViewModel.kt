package kr.co.bigwalk.app.campaign.category

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.co.bigwalk.app.BuildConfig
import kr.co.bigwalk.app.campaign.CampaignActivity
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
import kr.co.bigwalk.app.data.campaign.repository.page.CategoryCampaignPageDataSource.Companion.PAGE_SIZE
import kr.co.bigwalk.app.data.campaign.repository.page.CategoryCampaignPageDataSourceFactory
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showDebugToast
import kr.co.bigwalk.app.util.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class CampaignByCategoryViewModel(categoryId: Long, private val fragmentManager: FragmentManager) {

    private val categoryByCampaignDataSourceFactory =
        CategoryCampaignPageDataSourceFactory(categoryId)
    private val config =
        PagedList.Config.Builder().setPageSize(PAGE_SIZE).setEnablePlaceholders(true).build()
    val campaignsByCategory: LiveData<PagedList<ResponseCampaign>> =
        LivePagedListBuilder<Int, ResponseCampaign>(
            categoryByCampaignDataSourceFactory,
            config
        ).build()
    
    fun invalidateDataSource(){
        categoryByCampaignDataSourceFactory.invalidateDataSource()
    }

    fun donate(responseCampaign: ResponseCampaign) {
        DebugLog.d("캠페인 카테고리 도네  : " + responseCampaign.smsId)
        DebugLog.d("캠페인 카테고리 도네 데이터 : $responseCampaign")
        val now = Date()
        if (now < responseCampaign.startDate || now > responseCampaign.endDate) {
            showToast("캠페인 참여 기간이 아닙니다.")
            showDebugToast("캠페인 참여 기간이 아닙니다.")
            return
        }
        if (!isEligibleUser(responseCampaign)) {
            showToast("기업 전용 캠페인입니다. 다른 캠페인에 함께 해주세요.")
            showDebugToast("기업 전용 캠페인입니다. 다른 캠페인에 함께 해주세요.")
            return
        }

        if (PreferenceManager.getDonableStep() > 0) {
            if(responseCampaign.smsId != 0L) {
                requestSMSContent(responseCampaign)
            } else {
                val bundle = Bundle()
                bundle.putString("campaign_id", responseCampaign.id.toString())
                CampaignActivity.firebaseAnalytics?.logEvent("campaign_card_donate", bundle)
                val donationFragment = makeBottomSheetDialogFragment(makeDonationData(responseCampaign))
                donationFragment.show(fragmentManager, donationFragment.tag)
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
                "donate failed : donableStep [" + PreferenceManager.getDonableStep() + "], reason [CampaignByCategoryViewModel: fun donate(responseCampaign: ResponseCampaign)]"
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
//            if (responseCampaign.smsId != 0L) {
//                requestSMSContent(responseCampaign, true)
//            } else {
//                showToast("기부할 수 있는 걸음이 없습니다. 함께 걸어볼까요?")
//                showDebugToast("기부할 수 있는 걸음이 없습니다. 함께 걸어볼까요?")
//            }
//            return
//        } else {
//            if (responseCampaign.smsId != 0L) {
//                requestSMSContent(responseCampaign, false)
//            } else {
//                val bundle = Bundle()
//                bundle.putString("campaign_id", responseCampaign.id.toString())
//                CampaignActivity.firebaseAnalytics?.logEvent("campaign_card_donate", bundle)
//                val donationFragment = DonationFragment(responseCampaign.id)
//                donationFragment.show(fragmentManager, donationFragment.tag)
//            }
//        }
    }

    private fun isEligibleUser(responseCampaign: ResponseCampaign): Boolean {
        if (responseCampaign.organizations.isNullOrEmpty()) {// 일반 캠페인일 때
            return true
        } else {// 기업전용 캠페인이면서 참여가능한 유저이면 true
            if (PreferenceManager.getOrganization() != -1L) {
                for (organization in responseCampaign.organizations) {
                    if (organization.id == PreferenceManager.getOrganization()) return true
                }
            }
        }
        return false
    }

    private fun requestSMSContent(responseCampaign: ResponseCampaign) {
        CampaignRepository.getSMSContent(
            responseCampaign.id,
            responseCampaign.smsId,
            object : CampaignDataSource.GetSMSCallBack {
                override fun onSuccess(SMSContent: CampaignSMSContentResponse) {
                    val donationFragment = DonationStepSMSFragment(makeDonationData(responseCampaign), SMSContent)
                    donationFragment.show(fragmentManager, donationFragment.tag)
                }

                override fun onFailed(reason: String) {
                    showToast("SMS 문자 정보를 가져오는 데 실패했습니다. 나중에 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }
            })
    }
}