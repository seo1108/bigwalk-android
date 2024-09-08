package kr.co.bigwalk.app.campaign.detail

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.databinding.*
import androidx.fragment.app.FragmentManager
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.BuildConfig
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.donation.DonationSMSFragment
import kr.co.bigwalk.app.campaign.donation.makeBottomSheetDialogFragment
import kr.co.bigwalk.app.campaign.donation.makeDonationData
import kr.co.bigwalk.app.campaign.ranking.RankingActivity
import kr.co.bigwalk.app.crowd_funding.FundingRewardResultItem
import kr.co.bigwalk.app.crowd_funding.myfunding.MyFundingRewardDialogFragment
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.api.UserWalkLogRequest
import kr.co.bigwalk.app.data.campaign.CampaignStatus
import kr.co.bigwalk.app.data.campaign.dto.*
import kr.co.bigwalk.app.data.campaign.repository.CampaignDataSource
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.data.crowd_funding.dto.FundingState
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.share.ShareCampaignActivity
import kr.co.bigwalk.app.story.detail.StoryDetailFragment
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showDebugToast
import kr.co.bigwalk.app.util.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class CampaignDetailViewModel(
    private val navigator: BaseNavigator,
    private val fragmentManager: FragmentManager,
    val campaignId: Long
) {

    val campaign: ObservableField<CampaignAndStoryResponse> = ObservableField()
    val contents: ObservableList<CampaignContentResponse> =
        ObservableArrayList<CampaignContentResponse>()
    val userRanking: ObservableField<RankingResponse> = ObservableField()
    val isEnded: ObservableBoolean = ObservableBoolean(false)
    val isEligibleUser: ObservableBoolean = ObservableBoolean(true)// 캠페인에 참여할 수 있는 유저
    var donationButtonBackground: ObservableField<Int> = ObservableField(ContextCompat.getColor(navigator.getContext(), R.color.blue))
    var donationButtonImage: ObservableField<Drawable?> = ObservableField(ContextCompat.getDrawable(navigator.getContext(), R.drawable.ico_24_donation_step_white))
    var donationButtonText: ObservableField<String> = ObservableField("")
    var donationButtonType: CampaignStatus = CampaignStatus.DONATION_PROGRESS
    var donationButtonAlpha: ObservableFloat = ObservableFloat(1f)
    var donationSMSVisible: ObservableBoolean = ObservableBoolean()

    init {
        requestCampaign()
        requestCampaignContents()
        requestUserRanking()
    }

    private fun isEndedCampaign() {
        if (Date() < campaign.get()?.startDate || Date() > campaign.get()?.endDate) isEnded.set(true) else isEnded.set(false)
    }

    /* 기업 전용 캠페인 여부 확인 */
    private fun isOrganizationCampaign(): Boolean {
        return if (campaign.get()?.organizations.isNullOrEmpty()) {// 일반 캠페인일 때
            isEligibleUser.set(true)
            false
        } else {// 기업전용 캠페인일 때
            if (PreferenceManager.getOrganization() != -1L) {
                isEligibleUser.set(false)
                for (organization in campaign.get()?.organizations!!) {
                    if (organization.id == PreferenceManager.getOrganization()) isEligibleUser.set(true)
                }
            } else {
                isEligibleUser.set(false)
            }
            true
        }
    }

    fun finishActivity() {
        navigator.finish()
    }

    private fun checkIsEnableToDonate(): Boolean {
        val now = Date()
        val startDate = campaign.get()?.startDate
        val endDate = campaign.get()?.endDate
        if (startDate == null || endDate == null) {
            showToast("캠페인 참여 기간 확인 중 오류가 발생해 기부를 진행할 수 없습니다.\n 다시 시도해주세요")
            navigator.finish()
            return false
        }
        if (now < startDate || now > endDate) {
            showToast("캠페인 참여 기간이 아닙니다.")
            return false
        }

        if (isOrganizationCampaign() && !isEligibleUser.get()) {
            DebugLog.d("isOrganizationCampaign() ${isOrganizationCampaign()} isEligibleUser ${isEligibleUser.get()} ${PreferenceManager.getOrganization()}")
            showToast("기업 전용 캠페인입니다. 다른 캠페인에 함께 해주세요.")
            return false
        }
        if (PreferenceManager.getDonableStep() <= 0) {
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
                "donate failed : donableStep [" + PreferenceManager.getDonableStep() + "], reason [CampaignDetailViewModel: private fun checkIsEnableToDonate(): Boolean]"
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
            return false
        }
        return true
    }

    fun onClickedDonationButton() {
        when (donationButtonType) {
            CampaignStatus.RESULT_POST -> {
                moveToStory()
            }
            else -> {
                if (!checkIsEnableToDonate()) return
                campaign.get()?.let { campaign ->
                    val donationFragment = makeBottomSheetDialogFragment(makeDonationData(campaign))
                    donationFragment.show(fragmentManager, donationFragment.tag)
                    val bundle = Bundle()
                    bundle.putString("campaign_id", campaign.id.toString())
                    CampaignDetailActivity.firebaseAnalytics?.logEvent("campaign_detail_donate ${campaign.id}", bundle)
                }
            }
        }
    }

    fun SMSDonation() {
        val now = Date()
        val startDate = campaign.get()?.startDate
        val endDate = campaign.get()?.endDate

        if (startDate == null || endDate == null) {
            showToast("캠페인 참여 기간 확인 중 오류가 발생해 기부를 진행할 수 없습니다.\n 다시 시도해주세요")
            navigator.finish()
            return
        }

        if (now < startDate || now > endDate) {
            showToast("캠페인 참여 기간이 아닙니다.")
        }
        else if (isEligibleUser.get()) {
            requestSMSContent()
        }
        else {
            showToast("기업 전용 캠페인입니다. 다른 캠페인에 함께 해주세요.")
        }
    }

    fun moveToRanking() {
        val intent = Intent(navigator.getContext(), RankingActivity::class.java)
        intent.putExtra("CampaignId", campaignId)
        navigator.getContext().startActivity(intent)
    }

    private fun requestCampaign() {
        CampaignRepository.getCampaign(campaignId, object : CampaignDataSource.CampaignCallback {
            override fun onSuccess(postedCampaign: CampaignAndStoryResponse) {
                DebugLog.d("캠페인 상세: ${postedCampaign}")
                System.out.println("캠페인 참여인원: ${postedCampaign.participantCount}")
                campaign.set(postedCampaign)
                isEndedCampaign()
                isOrganizationCampaign()
                setDonationButton(isEnded.get(), isEligibleUser.get(), postedCampaign)
                if (postedCampaign.campaignFunding.isFundingDonated == true) {
                    showContestDetailDialog(
                        FundingRewardResultItem(
                            id = postedCampaign.campaignFunding.crewCampaignId ?: DEF_LONG_VALUE,
                            fundingState = FundingState.FUNDING_SUCCESS_AND_DONATE,
                            remainSteps = "",
                            myFundingSteps = ""
                        )
                    )
                }
            }

            override fun onFailed(reason: String) {
                showToast("상세 캠페인 페이지를 열 수 없습니다. 다시 시도해주세요!")
                navigator.finish()
            }
        })
    }

    private fun setDonationButton(isEnded: Boolean, isEligibleUser: Boolean, campaign: CampaignAndStoryResponse) {
        var backgroundColor = ContextCompat.getColor(navigator.getContext(), R.color.blue)
        var icon = ContextCompat.getDrawable(navigator.getContext(), R.drawable.ico_24_donation_step_white)
        var text = navigator.getContext().getString(R.string.donate_step_button)
        if (campaign.smsId > 0) {
            donationSMSVisible.set(true)
        }

        if (isEnded || !isEligibleUser) {
            if (campaign.stories.isEmpty()) {
                donationButtonType = CampaignStatus.DONATION_CLOSE
                donationButtonAlpha.set(0.3f)
            } else {
                donationButtonType = CampaignStatus.RESULT_POST
                icon = ContextCompat.getDrawable(navigator.getContext(), R.drawable.ui_icon_resultpost)
                text = navigator.getContext().getString(R.string.story_button)
                donationButtonAlpha.set(1f)
                if (campaign.smsId > 0) {
                    donationSMSVisible.set(false)
                }
            }
        } else {
            donationButtonAlpha.set(1f)
            donationButtonType = CampaignStatus.DONATION_PROGRESS
            text = navigator.getContext().getString(R.string.donate_step_button)
            campaign.service?.let { service ->
                if (service.type == ServiceType.MISSION.value) {
                    text = navigator.getContext().getString(R.string.step_mission_donate)
                }
            }
            campaign.event?.let { event ->
                if (event.type == CampaignEventType.HOTTIME.value) {
                    donationButtonType = CampaignStatus.HOTTIME
                    backgroundColor = ContextCompat.getColor(navigator.getContext(), R.color.water_melon)
                    text = campaign.getEventTypeName()
                    icon = ContextCompat.getDrawable(navigator.getContext(), R.drawable.ui_icon_hottime)
                }
            }
        }
        donationButtonImage.set(icon)
        donationButtonBackground.set(backgroundColor)
        donationButtonText.set(text)
    }

    private fun requestCampaignContents() {
        CampaignRepository.getCampaignContents(
            campaignId,
            object : CampaignDataSource.GetCampaignContentsCallback {
                override fun onSuccess(postedCampaignContents: List<CampaignContentResponse>) {
                    DebugLog.d("$postedCampaignContents")
                    if (postedCampaignContents.isNotEmpty()) {
                        contents.addAll(postedCampaignContents)
                    }
                }

                override fun onFailed(reason: String) {
                    showToast("캠페인 컨텐츠 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                    navigator.finish()
                    DebugLog.e(CampaignException(reason))
                }
            })
    }

    private fun requestUserRanking() {
        CampaignRepository.getMyRankInCampaign(
            campaignId,
            object : CampaignDataSource.GetMyRankInCampaignCallback {
                override fun onSuccess(rankingResponse: RankingResponse) {
                    userRanking.set(rankingResponse)
                }

                override fun onFailed(reason: String) {
                    showToast("나의 랭킹정보를 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }

            })
    }

    private fun requestSMSContent() {
        if (campaign.get()?.smsId != null) {
            CampaignRepository.getSMSContent(campaignId, campaign.get()?.smsId!!, object : CampaignDataSource.GetSMSCallBack {
                override fun onSuccess(SMSContent: CampaignSMSContentResponse) {
                    DebugLog.d("SMS contents $SMSContent")
                    val donationFragment = DonationSMSFragment(SMSContent)
                    donationFragment.show(fragmentManager, donationFragment.tag)
                }

                override fun onFailed(reason: String) {
                    showToast("SMS 문자 정보를 가져오는 데 실패했습니다. 나중에 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }
            })
        }
    }

    fun shareCampaign() {
        val bundle = Bundle()
        bundle.putString("campaign_id", campaignId.toString())
        CampaignDetailActivity.firebaseAnalytics?.logEvent("campaign_share_button $campaignId", bundle)

        val intent = Intent(navigator.getContext(), ShareCampaignActivity::class.java)
        intent.putExtra("campaignId",campaignId)
        intent.putExtra("campaignName",campaign.get()?.name)
        intent.putExtra("campaignImageUrl",campaign.get()?.detailThumbnailImagePath)
        navigator.getContext().startActivity(intent)
    }

    fun moveToStory() {
        val storyDetailFragment = StoryDetailFragment(campaignId, userRanking.get()?.donatedSteps ?: 0)
        storyDetailFragment.show(fragmentManager, storyDetailFragment.tag)
    }

    fun moveToBeneficiaryLink() {
        try {
            CampaignDetailActivity.firebaseAnalytics?.logEvent(
                "beneficiary_link $campaignId",
                Bundle()
            )
            val intent = Intent(ACTION_VIEW, Uri.parse(campaign.get()?.beneficiaryLink))
            navigator.getContext().startActivity(intent)
        } catch (e: Exception) {}
    }

    private fun showContestDetailDialog(item: FundingRewardResultItem) {
        val dialogFragment = MyFundingRewardDialogFragment.newInstance(item)
        dialogFragment.show(fragmentManager, dialogFragment.tag)
    }
}