package kr.co.bigwalk.app.data.funding.repository

import kr.co.bigwalk.app.data.community.dto.MySupportersCampaignsResponse
import kr.co.bigwalk.app.data.funding.dto.*

object FundingRepository {

    fun getMyLabelList(
        groupId: Long,
        successCallback: (MySupportersCampaignsResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.getMyLabelList(groupId, successCallback, failCallback)
    }

    fun getLabelCategoryList(
        successCallback: (LabelCategoryListResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.getLabelCategoryList(successCallback, failCallback)
    }

    fun registerLabel(
        groupId: Long,
        request: RegisterLabelRequest,
        successCallback: (RegisterSupportersCampaignResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.registerLabel(groupId, request, successCallback, failCallback)
    }

    fun registerChallengeOfSupporters(
        supportersCampaignId: Long,
        request: RegisterChallengeOfSupportersRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.registerChallengeOfSupporters(supportersCampaignId, request, successCallback, failCallback)
    }

    fun getContestList(
        successCallback: (ContestsResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.getContestList(successCallback, failCallback)
    }

    fun getContestDetail(
        competitionId: Long,
        successCallback: (ContestDetailResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.getContestDetail(competitionId, successCallback, failCallback)
    }

    fun getCrewCampaign(
        crewCampaignId: Long,
        successCallback: (CrewCampaignResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.getCrewCampaign(crewCampaignId, successCallback, failCallback)
    }

    fun getBeforeLabelData(
        crewCampaignId: Long,
        successCallback: (LabelBeforeDataResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.getBeforeLabelData(crewCampaignId, successCallback, failCallback)
    }

    fun modifySupportersCampaign(
        supportersCampaignId: Long,
        crewId: Long,
        request: ModifySupportersCampaignRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.modifySupportersCampaign(supportersCampaignId, crewId, request, successCallback, failCallback)
    }

    fun modifyChallengeOfSupporters(
        supportersCampaignId: Long,
        request: ModifyChallengeOfSupportersRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.modifyChallengeOfSupporters(supportersCampaignId, request, successCallback, failCallback)
    }

    fun deleteSupportersCampaign(
        supportersCampaignId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.deleteSupportersCampaign(supportersCampaignId, successCallback, failCallback)
    }

    fun deleteChallengeOfSupporters(
        supportersCampaignId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.deleteChallengeOfSupporters(supportersCampaignId, successCallback, failCallback)
    }

    fun getPreviewChallengeOfSupporters(
        crewCampaignId: Long,
        successCallback: (ChallengeOfCrewCampaignResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.getPreviewChallengeOfSupporters(crewCampaignId, successCallback, failCallback)
    }

    fun judgeCrewCampaign(
        crewCampaignId: Long,
        groupId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.judgeCrewCampaign(crewCampaignId, groupId, successCallback, failCallback)
    }

    fun getLastModifier(
        crewCampaignId: Long,
        successCallback: (LastModifierResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.getLastModifier(crewCampaignId, successCallback, failCallback)
    }

    fun getCrewCampaignRankings(
        crewCampaignId: Long,
        page: Int,
        size: Int,
        successCallback: (CrewCampaignRankingListResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.getCrewCampaignRankings(crewCampaignId, page, size, successCallback, failCallback)
    }

    fun applyForCrewCampaign(
        crewCampaignId: Long,
        request: ApplyForCrewCampaignRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.applyForCrewCampaign(crewCampaignId, request, successCallback, failCallback)
    }

    fun getQuestionForCrewCampaign(
        crewCampaignId: Long,
        successCallback: (QuestionForCrewCampaignResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.getQuestionForCrewCampaign(crewCampaignId, successCallback, failCallback)
    }

    fun getFundingInfoOfCrewCampaign(
        crewCampaignId: Long,
        successCallback: (FundingInfoOfCrewCampaignResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.getFundingInfoOfCrewCampaign(crewCampaignId, successCallback, failCallback)
    }

    fun fundForFundingCoupon(
        crewCampaignId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.fundForFundingCoupon(crewCampaignId, successCallback, failCallback)
    }

    fun getCrewCampaignMyRanking(
        crewCampaignId: Long,
        successCallback: (CrewCampaignMyRankingResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.getCrewCampaignMyRanking(crewCampaignId, successCallback, failCallback)
    }

    fun getCrewCampaignDetail(
        crewCampaignId: Long,
        successCallback: (CrewCampaignDetailResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.getCrewCampaignDetail(crewCampaignId, successCallback, failCallback)
    }

    fun likeCrewCampaign(
        crewCampaignId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.likeCrewCampaign(crewCampaignId, successCallback, failCallback)
    }

    fun unlikeCrewCampaign(
        crewCampaignId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return FundingRemoteDataSource.unlikeCrewCampaign(crewCampaignId, successCallback, failCallback)
    }
}