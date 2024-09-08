package kr.co.bigwalk.app.campaign

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableList
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.co.bigwalk.app.BuildConfig
import kr.co.bigwalk.app.campaign.category.CampaignByCategoryFragment
import kr.co.bigwalk.app.campaign.consumption.ConsumptionActivity
import kr.co.bigwalk.app.campaign.detail.CampaignDetailActivity
import kr.co.bigwalk.app.campaign.donation.DonationStepSMSFragment
import kr.co.bigwalk.app.campaign.donation.makeBottomSheetDialogFragment
import kr.co.bigwalk.app.campaign.donation.makeDonationData
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.api.UserWalkLogRequest
import kr.co.bigwalk.app.data.campaign.dto.CampaignSMSContentResponse
import kr.co.bigwalk.app.data.campaign.dto.Category
import kr.co.bigwalk.app.data.campaign.dto.ResponseCampaign
import kr.co.bigwalk.app.data.campaign.repository.CampaignDataSource
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.data.story.dto.Story
import kr.co.bigwalk.app.data.story.repository.StoriesForReadyPageDataSourceFactory
import kr.co.bigwalk.app.data.story.repository.StoryForReadyPageDataSource
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showDebugToast
import kr.co.bigwalk.app.util.showToast
import kr.co.bigwalk.app.walk.WalkActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class CampaignViewModel (val navigator: CampaignNavigator, private val fm: FragmentManager) {
    private val storiesDataSourceFactory = StoriesForReadyPageDataSourceFactory()
    private val config = PagedList.Config.Builder().setPageSize(StoryForReadyPageDataSource.PAGE_SIZE).setEnablePlaceholders(true).build()
    val myParticipatedCampaigns: ObservableList<ResponseCampaign> = ObservableArrayList()
    val popularCampaigns: ObservableList<ResponseCampaign> = ObservableArrayList()
    val storyLiveData: LiveData<PagedList<Story>> = LivePagedListBuilder(storiesDataSourceFactory, config).build()
    val storiesVisibility: ObservableBoolean = ObservableBoolean(false)
    val myParticipatedCampaignsVisibility: ObservableBoolean = ObservableBoolean(false)
    val campaignCategories: ObservableList<Category> = ObservableArrayList()
    val categoryFragments: ObservableList<CampaignByCategoryFragment> = ObservableArrayList()

    init {
        getPopularCampaigns()
        getCampaignCategories()
        getParticipatedCampaigns()
    }

    private fun getPopularCampaigns() {
        CampaignRepository.getPopularCampaigns(object : CampaignDataSource.GetPopularCampaignsCallback {
            override fun onSuccess(campaigns: List<ResponseCampaign>?) {
                DebugLog.d("캠페인 : $campaigns")
                if (!campaigns.isNullOrEmpty()) popularCampaigns.addAll(campaigns)
            }

            override fun onFailed(reason: String) {
                showToast("인기 캠페인을 확인할 수 없습니다. 다시 시도해주세요.")
            }
        })
    }

    private fun getCampaignCategories() {
        CampaignRepository.getCampaignCategories(object : CampaignDataSource.GetCampaignCategoriesCallback {
            override fun onSuccess(categories: List<Category>?) {
                if (!categories.isNullOrEmpty()) {
                    campaignCategories.addAll(categories)
                    categoryFragments.add(CampaignByCategoryFragment.newInstance(0))
                    for (category in campaignCategories) {
                        categoryFragments.add(CampaignByCategoryFragment.newInstance(category.id))
                    }
                }
            }

            override fun onFailed(reason: String) {
                showToast("캠페인 카테고리를 확인할 수 없습니다. 다시 시도해주세요.")
            }

        })
    }

    private fun getParticipatedCampaigns() {
        CampaignRepository.getParticipatedCampaigns(object : CampaignDataSource.GetParticipatedCampaignsCallback {
                override fun onSuccess(participatedCampaigns: List<ResponseCampaign>) {
                    if (!participatedCampaigns.isNullOrEmpty()) {
                        myParticipatedCampaigns.addAll(participatedCampaigns)
                        myParticipatedCampaignsVisibility.set(true)
                    } else {
                        myParticipatedCampaignsVisibility.set(false)
                    }
                }

                override fun onFailed(reason: String) {
                    showToast("내가 참여한 캠페인 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }
            }
        )
    }

    fun finish(){
        navigator.getContext().finish()
    }

    fun donate(campaign: ResponseCampaign) {
        DebugLog.d("캠페인 도네 : " + campaign.smsId)

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
                val bundle = Bundle()
                bundle.putString("campaign_id", campaign.id.toString())
                CampaignActivity.firebaseAnalytics?.logEvent("campaign_donate", bundle)
                val donationFragment = makeBottomSheetDialogFragment(makeDonationData(campaign))
                donationFragment.show(fm, donationFragment.tag)
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
                "donate failed : donableStep [" + PreferenceManager.getDonableStep() + "], reason [CampaignViewModel: fun donate(campaign: ResponseCampaign)]"
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
//            if(campaign.smsId != 0L) {
//                requestSMSContent(campaign, true)
//            } else {
//                showToast("기부할 수 있는 걸음이 없습니다. 함께 걸어볼까요?")
//                showDebugToast("기부할 수 있는 걸음이 없습니다. 함께 걸어볼까요?")
//            }
//        } else {
//            if(campaign.smsId != 0L) {
//                requestSMSContent(campaign, false)
//            } else {
//                val bundle = Bundle()
//                bundle.putString("campaign_id", campaign.id.toString())
//                CampaignActivity.firebaseAnalytics?.logEvent("campaign_donate", bundle)
//                val donationFragment = DonationFragment(campaign.id)
//                donationFragment.show(fm, donationFragment.tag)
//            }
//        }
    }

    fun moveToCampaign(campaignId: Long) {
        val intent = Intent(navigator.getContext(), CampaignDetailActivity::class.java)
        intent.putExtra("campaign_id", campaignId)
        navigator.getContext().startActivity(intent)
    }

    fun moveToConsumption(campaignId: Long) {
        val intent = Intent(navigator.getContext(), ConsumptionActivity::class.java)
        intent.putExtra(ConsumptionActivity.CAMPAIGN_ID, campaignId)
        navigator.getContext().startActivity(intent)
    }

    fun moveToStoryTab() {
        navigator.moveToStoryTab()
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
                donationFragment.show(fm, donationFragment.tag)
            }

            override fun onFailed(reason: String) {
                showToast("SMS 문자 정보를 가져오는 데 실패했습니다. 나중에 다시 시도해주세요.")
                DebugLog.e(CampaignException(reason))
            }
        })
    }

    fun moveToHome() {
        val intent = Intent(navigator.getContext(), WalkActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        navigator.getContext().startActivity(intent)
    }
}