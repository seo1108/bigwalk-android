package kr.co.bigwalk.app.data.crowd_funding.repository

import kr.co.bigwalk.app.data.crowd_funding.dto.*
import kr.co.bigwalk.app.data.funding.dto.AddCommentCrewCampaignRequest
import kr.co.bigwalk.app.data.funding.dto.FundingByStepRequest

object CrowdFundingRepository {
    fun getTotalMyFunding(
        contestId: Long?,
        page: Int,
        size: Int,
        successCallback: (TotalMyFundingListResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CrowdFundingRemoteDataSource.getTotalMyFunding(contestId, page, size, successCallback, failCallback)
    }

    fun getRewardFundingInfo(
        crewCampaignId: Long,
        successCallback: (RewardFundingInfoResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CrowdFundingRemoteDataSource.getRewardFundingInfo(crewCampaignId, successCallback, failCallback)
    }

    fun getMyFundingByContest(
        contestId: Long,
        page: Int,
        size: Int,
        successCallback: (MyFundingListByContestResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CrowdFundingRemoteDataSource.getMyFundingByContest(contestId, page, size, successCallback, failCallback)
    }

    fun getMyFundingStepByContest(
        successCallback: (MyFundingStepByContestResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CrowdFundingRemoteDataSource.getMyFundingStepByContest(successCallback, failCallback)
    }

    fun getOccupationInfo(
        successCallback: (OccupationInfoResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CrowdFundingRemoteDataSource.getOccupationInfo(successCallback, failCallback)
    }

    fun getFundingListByNewest(
        contestId: Long,
        page: Int,
        size: Int,
        successCallback: (FundingListByNewestResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CrowdFundingRemoteDataSource.getFundingListByNewest(contestId, page, size, successCallback, failCallback)
    }

    fun getFundingListByHottest(
        contestId: Long,
        page: Int,
        size: Int,
        successCallback: (FundingListByHottestResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CrowdFundingRemoteDataSource.getFundingListByHottest(contestId, page, size, successCallback, failCallback)
    }

    fun getMyFundingDescription(
        contestId: Long,
        successCallback: (MyFundingDescriptionResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CrowdFundingRemoteDataSource.getMyFundingDescription(contestId, successCallback, failCallback)
    }

    fun getContestPoster(
        contestId: Long,
        successCallback: (ContestPostersResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CrowdFundingRemoteDataSource.getContestPoster(contestId, successCallback, failCallback)
    }

    fun refundForFundingSteps(
        crewCampaignId: Long,
        successCallback: (RefundForFundingStepsResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CrowdFundingRemoteDataSource.refundForFundingSteps(crewCampaignId, successCallback, failCallback)
    }

    fun fundForFundingSteps(
        crewCampaignId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CrowdFundingRemoteDataSource.fundForFundingSteps(crewCampaignId, successCallback, failCallback)
    }

    fun fundingByStep(
        crewCampaignId: Long,
        request: FundingByStepRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CrowdFundingRemoteDataSource.fundingByStep(crewCampaignId, request, successCallback, failCallback)
    }

    fun getCommentByCrewCampaign(
        crewCampaignId: Long,
        page: Int,
        size: Int,
        sort: String,
        successCallback: (CommentByCrewCampaignResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CrowdFundingRemoteDataSource.getCommentByCrewCampaign(crewCampaignId, page, size, sort, successCallback, failCallback)
    }

    fun addCommentCrewCampaign(
        crewCampaignId: Long,
        request: AddCommentCrewCampaignRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CrowdFundingRemoteDataSource.addCommentCrewCampaign(crewCampaignId, request, successCallback, failCallback)
    }

    fun deleteCommentCrewCampaign(
        crewCampaignId: Long,
        commentId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return CrowdFundingRemoteDataSource.deleteCommentCrewCampaign(crewCampaignId, commentId, successCallback, failCallback)
    }
}