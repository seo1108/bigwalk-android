package kr.co.bigwalk.app.data.campaign.repository

import kr.co.bigwalk.app.campaign.donation.additional_service.MissionDonationData
import kr.co.bigwalk.app.data.campaign.dto.*
import java.io.File

interface CampaignDataSource {

    interface GetCampaignCallback {
        fun onSuccess(postedCampaigns: List<CampaignsResponse>)
        fun onFailed(reason: String)
    }

    fun getCampaigns(page: Int, size: Int,getCampaignCallback: GetCampaignCallback)

    interface CampaignCallback {
        fun onSuccess(postedCampaign: CampaignAndStoryResponse)
        fun onFailed(reason: String)
    }

    fun getCampaign(campaignId: Long, getCampaignCallback: CampaignCallback)

    interface GetCampaignContentsCallback {
        fun onSuccess(postedCampaignContents: List<CampaignContentResponse>)
        fun onFailed(reason: String)
    }

    fun getSMSContent(campaignId: Long, smsId: Long, getSMSCallback: GetSMSCallBack)

    interface GetSMSCallBack {
        fun onSuccess(SMSContent: CampaignSMSContentResponse)
        fun onFailed(reason: String)
    }

    fun getCampaignContents(
        campaignId: Long,
        getCampaignContentsCallback: GetCampaignContentsCallback
    )
    
    interface DonateStepCallback {
        fun onSuccess(response: DonateResponse)
        fun onFailed(reason: String)
    }
    
    fun donateStep(
        donateRequest: DonateRequest,
        campaignId: Long,
        donateStepCallback: DonateStepCallback
    )
    
    interface DonateStepForHotTimeCallback {
        fun onSuccess(response: HotTimeDonateResponse)
        fun onFailed(reason: String)
    }

    fun donateStepForHottime(
        donateRequest: DonateRequest,
        campaignId: Long,
        donateStepCallback: DonateStepForHotTimeCallback
    )

    interface GetParticipatedCampaignsCallback {
        fun onSuccess(participatedCampaigns: List<ResponseCampaign>)
        fun onFailed(reason: String)
    }

    fun getParticipatedCampaigns(getParticipatedCampaignsCallback: GetParticipatedCampaignsCallback)

    interface GetUserRankingCallback {
        fun onSuccess(userRankingResponse: RankingResponse)
        fun onFailed(reason: String)
    }

    fun getUserRanking(campaignId: Long, getUserRankingCallback: GetUserRankingCallback)

    interface GetCampaignRankingCallback {
        fun onSuccess(campaignRanking: List<RankingResponse>)
        fun onFailed(reason: String)
    }

    fun getCampaignRanking(
        campaignId: Long,
        page: Int,
        size: Int,
        getCampaignRankingCallback: GetCampaignRankingCallback
    )

    interface GetDonationReportCallback {
        fun onSuccess(donationReportResponse: DonationReportResponse)
        fun onFailed(reason: String)
    }

    fun getDonationReport(getDonationReportCallback: GetDonationReportCallback)

    interface GetCampaignsRankCallback{
        fun onSuccess(campaignsRankResponse: List<RankingResponse>)
        fun onFailed(reason: String)
    }

    fun getCampaignsRank(page: Int, size: Int, getCampaignsRankCallback: GetCampaignsRankCallback)

    interface GetMyRankInCampaignsCallback{
        fun onSuccess(rankingResponse:RankingResponse)
        fun onFailed(reason: String)
    }

    fun getMyRankInCampaigns(getMyRankInCampaigns: GetMyRankInCampaignsCallback)

    interface GetMyRankInCampaignCallback{
        fun onSuccess(rankingResponse:RankingResponse)
        fun onFailed(reason: String)
    }

    fun getMyRankInCampaign(campaignId: Long,getMyRankInCampaignCallback: GetMyRankInCampaignCallback)

    interface GetPopularCampaignsCallback {
        fun onSuccess(campaigns: List<ResponseCampaign>?)
        fun onFailed(reason: String)
    }

    fun getPopularCampaigns(callback: GetPopularCampaignsCallback)

    interface GetCampaignCategoriesCallback {
        fun onSuccess(categories: List<Category>?)
        fun onFailed(reason: String)
    }

    fun getCampaignCategories(callback: GetCampaignCategoriesCallback)

    interface GetCampaignsByCategoryCallback {
        fun onSuccess(campaigns: List<ResponseCampaign>)
        fun onFailed(reason: String)
    }

    fun getCampaignsByCategory(categoryId: Long, page: Int, size: Int, callback: GetCampaignsByCategoryCallback)

    interface AdditionalServiceActionMissionCallback {
        fun onSuccess(response: ActionMissionResponse)
        fun onFailed(reason: String)
    }

    fun getAdditionalServiceActionMission(campaignId: Long, callback: AdditionalServiceActionMissionCallback)

    interface DonateMissionCallback {
        fun onSuccess(donationUploadResponse: DonationUploadResponse)
        fun onNotFound()
        fun onFailed(reason: String)
    }

    fun donateMission(missionDonationData: MissionDonationData,
                      achievementImage: File,
                      achievementImage2: File?,
                      achievementImage3: File?,
                      comment: String,
                      callback: DonateMissionCallback)

    interface AdditionalServiceValueConsumptionCommerceCallback {
        fun onSuccess(response: ValueConsumptionCommerceResponse)
        fun onFailed(reason: String)
    }

    fun getAdditionalServiceValueConsumptionCommerce(campaignId: Long, callback: AdditionalServiceValueConsumptionCommerceCallback)

    fun getRankersBySeason(seasonKey: String, successCallback: (RankerBySeasonResponse?) -> Unit, failCallback: (String) -> Unit)

    fun getSeasonTopRankers(successCallback: (List<SeasonTopRankerResponse>?) -> Unit, failCallback: (String) -> Unit)
}