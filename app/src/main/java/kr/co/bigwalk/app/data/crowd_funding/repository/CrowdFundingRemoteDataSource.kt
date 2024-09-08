package kr.co.bigwalk.app.data.crowd_funding.repository

import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.crowd_funding.dto.*
import kr.co.bigwalk.app.data.funding.dto.AddCommentCrewCampaignRequest
import kr.co.bigwalk.app.data.funding.dto.FundingByStepRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object CrowdFundingRemoteDataSource {

    fun getTotalMyFunding(
        contestId: Long?,
        page: Int,
        size: Int,
        successCallback: (TotalMyFundingListResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getTotalMyFunding(contestId, page, size)
            .enqueue(object : Callback<TotalMyFundingListResponse> {
                override fun onResponse(call: Call<TotalMyFundingListResponse>, response: Response<TotalMyFundingListResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<TotalMyFundingListResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getRewardFundingInfo(
        crewCampaignId: Long,
        successCallback: (RewardFundingInfoResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getRewardFundingInfo(crewCampaignId)
            .enqueue(object : Callback<RewardFundingInfoResponse> {
                override fun onResponse(call: Call<RewardFundingInfoResponse>, response: Response<RewardFundingInfoResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<RewardFundingInfoResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getMyFundingByContest(
        contestId: Long,
        page: Int,
        size: Int,
        successCallback: (MyFundingListByContestResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getMyFundingByContest(contestId, page, size)
            .enqueue(object : Callback<MyFundingListByContestResponse> {
                override fun onResponse(call: Call<MyFundingListByContestResponse>, response: Response<MyFundingListByContestResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<MyFundingListByContestResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getMyFundingStepByContest(
        successCallback: (MyFundingStepByContestResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getMyFundingStepByContest()
            .enqueue(object : Callback<MyFundingStepByContestResponse> {
                override fun onResponse(call: Call<MyFundingStepByContestResponse>, response: Response<MyFundingStepByContestResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<MyFundingStepByContestResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getOccupationInfo(
        successCallback: (OccupationInfoResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getOccupationInfo()
            .enqueue(object : Callback<OccupationInfoResponse> {
                override fun onResponse(call: Call<OccupationInfoResponse>, response: Response<OccupationInfoResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<OccupationInfoResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getFundingListByNewest(
        contestId: Long,
        page: Int,
        size: Int,
        successCallback: (FundingListByNewestResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getFundingListByNewest(contestId, page, size)
            .enqueue(object : Callback<FundingListByNewestResponse> {
                override fun onResponse(call: Call<FundingListByNewestResponse>, response: Response<FundingListByNewestResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<FundingListByNewestResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getFundingListByHottest(
        contestId: Long,
        page: Int,
        size: Int,
        successCallback: (FundingListByHottestResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getFundingListByHottest(contestId, page, size)
            .enqueue(object : Callback<FundingListByHottestResponse> {
                override fun onResponse(call: Call<FundingListByHottestResponse>, response: Response<FundingListByHottestResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<FundingListByHottestResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getMyFundingDescription(
        contestId: Long,
        successCallback: (MyFundingDescriptionResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getMyFundingDescription(contestId)
            .enqueue(object : Callback<MyFundingDescriptionResponse> {
                override fun onResponse(call: Call<MyFundingDescriptionResponse>, response: Response<MyFundingDescriptionResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<MyFundingDescriptionResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getContestPoster(
        contestId: Long,
        successCallback: (ContestPostersResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getContestPoster(contestId)
            .enqueue(object : Callback<BaseResponse<ContestPostersResponse>> {
                override fun onResponse(call: Call<BaseResponse<ContestPostersResponse>>, response: Response<BaseResponse<ContestPostersResponse>>) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.result == Result.SUCCESS) {
                                successCallback(response.body()?.data)
                            } else {
                                failCallback(response.body()?.message.orEmpty())
                            }
                        }
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<ContestPostersResponse>>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun refundForFundingSteps(
        crewCampaignId: Long,
        successCallback: (RefundForFundingStepsResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().refundForFundingSteps(crewCampaignId)
            .enqueue(object : Callback<RefundForFundingStepsResponse> {
                override fun onResponse(call: Call<RefundForFundingStepsResponse>, response: Response<RefundForFundingStepsResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<RefundForFundingStepsResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun fundForFundingSteps(
        crewCampaignId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().fundForFundingSteps(crewCampaignId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {
                        in 200..299 -> successCallback()
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }


    fun fundingByStep(
        crewCampaignId: Long,
        request: FundingByStepRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().fundingByStep(crewCampaignId, request)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {
                        201 -> successCallback()
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getCommentByCrewCampaign(
        crewCampaignId: Long,
        page: Int,
        size: Int,
        sort: String,
        successCallback: (CommentByCrewCampaignResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getCommentByCrewCampaign(crewCampaignId, page, size, sort)
            .enqueue(object : Callback<CommentByCrewCampaignResponse> {
                override fun onResponse(call: Call<CommentByCrewCampaignResponse>, response: Response<CommentByCrewCampaignResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<CommentByCrewCampaignResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun addCommentCrewCampaign(
        crewCampaignId: Long,
        request: AddCommentCrewCampaignRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().addCommentCrewCampaign(crewCampaignId, request)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {
                        201 -> successCallback()
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun deleteCommentCrewCampaign(
        crewCampaignId: Long,
        commentId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().deleteCommentCrewCampaign(crewCampaignId, commentId)
            .enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    when (response.code()) {
                        204 -> successCallback()
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }
}