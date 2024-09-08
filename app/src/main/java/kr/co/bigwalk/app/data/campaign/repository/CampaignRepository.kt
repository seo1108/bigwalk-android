package kr.co.bigwalk.app.data.campaign.repository

import kr.co.bigwalk.app.BuildConfig
import kr.co.bigwalk.app.campaign.donation.additional_service.MissionDonationData
import kr.co.bigwalk.app.data.campaign.dto.DonateRequest
import kr.co.bigwalk.app.data.campaign.dto.RankerBySeasonResponse
import kr.co.bigwalk.app.data.campaign.dto.SeasonTopRankerResponse
import java.io.File

object CampaignRepository : CampaignDataSource {

    private val campaignDataSource : CampaignDataSource

    init {
        @Suppress("ConstantConditionIf")
        campaignDataSource = if (BuildConfig.FLAVOR == "local") CampaignLocalDataSource else CampaignRemoteDataSource
    }

    override fun getCampaigns(page: Int, size: Int,getCampaignCallback: CampaignDataSource.GetCampaignCallback) {
        campaignDataSource.getCampaigns(page,size,getCampaignCallback)
    }

    override fun getCampaign(campaignId: Long, getCampaignCallback: CampaignDataSource.CampaignCallback) {
        campaignDataSource.getCampaign(campaignId, getCampaignCallback)
    }

    override fun getSMSContent(
        campaignId: Long,
        getSmsId: Long,
        getSMSCallback: CampaignDataSource.GetSMSCallBack
    ) {
        campaignDataSource.getSMSContent(campaignId, getSmsId, getSMSCallback)
    }

    override fun getCampaignContents(campaignId: Long, getCampaignContentsCallback: CampaignDataSource.GetCampaignContentsCallback) {
        campaignDataSource.getCampaignContents(campaignId, getCampaignContentsCallback)
    }

    override fun donateStep(donateRequest: DonateRequest, campaignId: Long, donateStepCallback: CampaignDataSource.DonateStepCallback) {
        campaignDataSource.donateStep(donateRequest, campaignId, donateStepCallback)
    }

    override fun donateStepForHottime(donateRequest: DonateRequest, campaignId: Long, donateStepCallback: CampaignDataSource.DonateStepForHotTimeCallback) {
        campaignDataSource.donateStepForHottime(donateRequest, campaignId, donateStepCallback)
    }

    override fun getParticipatedCampaigns(getParticipatedCampaignsCallback: CampaignDataSource.GetParticipatedCampaignsCallback) {
        campaignDataSource.getParticipatedCampaigns(getParticipatedCampaignsCallback)
    }

    override fun getUserRanking(campaignId: Long, getUserRankingCallback: CampaignDataSource.GetUserRankingCallback) {
        campaignDataSource.getUserRanking(campaignId, getUserRankingCallback)
    }

    override fun getCampaignRanking(campaignId: Long, page: Int, size: Int, getCampaignRankingCallback: CampaignDataSource.GetCampaignRankingCallback) {
        campaignDataSource.getCampaignRanking(campaignId, page, size, getCampaignRankingCallback)
    }

    override fun getDonationReport(getDonationReportCallback: CampaignDataSource.GetDonationReportCallback) {
        campaignDataSource.getDonationReport(getDonationReportCallback)
    }

    override fun getCampaignsRank(page: Int, size: Int, getCampaignsRankCallback: CampaignDataSource.GetCampaignsRankCallback) {
        campaignDataSource.getCampaignsRank(page,size,getCampaignsRankCallback)
    }

    override fun getMyRankInCampaigns(getMyRankInCampaigns: CampaignDataSource.GetMyRankInCampaignsCallback) {
        campaignDataSource.getMyRankInCampaigns(getMyRankInCampaigns)
    }

    override fun getMyRankInCampaign(campaignId: Long, getMyRankInCampaignCallback: CampaignDataSource.GetMyRankInCampaignCallback) {
        campaignDataSource.getMyRankInCampaign(campaignId,getMyRankInCampaignCallback)
    }

    override fun getPopularCampaigns(callback: CampaignDataSource.GetPopularCampaignsCallback) {
        campaignDataSource.getPopularCampaigns(callback)
    }

    override fun getCampaignCategories(callback: CampaignDataSource.GetCampaignCategoriesCallback) {
        campaignDataSource.getCampaignCategories(callback)
    }

    override fun getCampaignsByCategory(
        categoryId: Long,
        page: Int,
        size: Int,
        callback: CampaignDataSource.GetCampaignsByCategoryCallback
    ) {
        campaignDataSource.getCampaignsByCategory(categoryId, page, size, callback)
    }

    override fun getAdditionalServiceActionMission(campaignId: Long, callback: CampaignDataSource.AdditionalServiceActionMissionCallback) {
        campaignDataSource.getAdditionalServiceActionMission(campaignId, callback)
    }

    override fun donateMission(
        missionDonationData: MissionDonationData,
        achievementImage: File,
        achievementImage2: File?,
        achievementImage3: File?,
        comment: String,
        callback: CampaignDataSource.DonateMissionCallback
    ) {
        campaignDataSource.donateMission(missionDonationData, achievementImage, achievementImage2, achievementImage3, comment, callback)
    }

    override fun getAdditionalServiceValueConsumptionCommerce(campaignId: Long, callback: CampaignDataSource.AdditionalServiceValueConsumptionCommerceCallback) {
        campaignDataSource.getAdditionalServiceValueConsumptionCommerce(campaignId, callback)
    }

    override fun getRankersBySeason(seasonKey: String, successCallback: (RankerBySeasonResponse?) -> Unit, failCallback: (String) -> Unit) {
        campaignDataSource.getRankersBySeason(seasonKey, successCallback, failCallback)
    }

    override fun getSeasonTopRankers(successCallback: (List<SeasonTopRankerResponse>?) -> Unit, failCallback: (String) -> Unit) {
        campaignDataSource.getSeasonTopRankers(successCallback, failCallback)
    }
}