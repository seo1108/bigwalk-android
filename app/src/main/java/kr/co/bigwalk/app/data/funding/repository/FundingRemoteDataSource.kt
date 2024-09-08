package kr.co.bigwalk.app.data.funding.repository

import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.community.dto.MySupportersCampaignsResponse
import kr.co.bigwalk.app.data.funding.dto.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FundingRemoteDataSource {

    fun getMyLabelList(
        groupId: Long,
        successCallback: (MySupportersCampaignsResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getMyLabelList(groupId)
            .enqueue(object : Callback<MySupportersCampaignsResponse> {
                override fun onResponse(call: Call<MySupportersCampaignsResponse>, response: Response<MySupportersCampaignsResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<MySupportersCampaignsResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getLabelCategoryList(
        successCallback: (LabelCategoryListResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getLabelCategoryList()
            .enqueue(object : Callback<LabelCategoryListResponse> {
                override fun onResponse(call: Call<LabelCategoryListResponse>, response: Response<LabelCategoryListResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<LabelCategoryListResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun registerLabel(
        groupId: Long,
        request: RegisterLabelRequest,
        successCallback: (RegisterSupportersCampaignResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().registerLabel(groupId, request.toMultipartBody())
            .enqueue(object : Callback<RegisterSupportersCampaignResponse> {
                override fun onResponse(call: Call<RegisterSupportersCampaignResponse>, response: Response<RegisterSupportersCampaignResponse>) {
                    when (response.code()) {
                        201 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<RegisterSupportersCampaignResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun registerChallengeOfSupporters(
        supportersCampaignId: Long,
        request: RegisterChallengeOfSupportersRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().registerChallengeOfSupporters(supportersCampaignId, request.toMultipartBody())
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

    fun getContestList(
        successCallback: (ContestsResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getContestList()
            .enqueue(object : Callback<ContestsResponse> {
                override fun onResponse(call: Call<ContestsResponse>, response: Response<ContestsResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<ContestsResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getContestDetail(
        competitionId: Long,
        successCallback: (ContestDetailResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getContestDetail(competitionId)
            .enqueue(object : Callback<ContestDetailResponse> {
                override fun onResponse(call: Call<ContestDetailResponse>, response: Response<ContestDetailResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<ContestDetailResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getCrewCampaign(
        crewCampaignId: Long,
        successCallback: (CrewCampaignResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getCrewCampaign(crewCampaignId)
            .enqueue(object : Callback<CrewCampaignResponse> {
                override fun onResponse(call: Call<CrewCampaignResponse>, response: Response<CrewCampaignResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<CrewCampaignResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getBeforeLabelData(
        crewCampaignId: Long,
        successCallback: (LabelBeforeDataResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getBeforeLabelData(crewCampaignId)
            .enqueue(object : Callback<LabelBeforeDataResponse> {
                override fun onResponse(call: Call<LabelBeforeDataResponse>, response: Response<LabelBeforeDataResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<LabelBeforeDataResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun modifySupportersCampaign(
        supportersCampaignId: Long,
        crewId: Long,
        request: ModifySupportersCampaignRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().modifyCrewCampaign(supportersCampaignId, crewId, request.toMultipartBody())
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

    fun modifyChallengeOfSupporters(
        supportersCampaignId: Long,
        request: ModifyChallengeOfSupportersRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().modifyChallengeOfSupporters(supportersCampaignId, request.toMultipartBody())
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

    fun deleteSupportersCampaign(
        supportersCampaignId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().deleteCrewCampaign(supportersCampaignId)
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

    fun deleteChallengeOfSupporters(
        supportersCampaignId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().deleteChallengeOfSupporters(supportersCampaignId)
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

    fun getPreviewChallengeOfSupporters(
        crewCampaignId: Long,
        successCallback: (ChallengeOfCrewCampaignResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getChallengeOfCrewCampaign(crewCampaignId)
            .enqueue(object : Callback<ChallengeOfCrewCampaignResponse> {
                override fun onResponse(call: Call<ChallengeOfCrewCampaignResponse>, response: Response<ChallengeOfCrewCampaignResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<ChallengeOfCrewCampaignResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun judgeCrewCampaign(
        crewCampaignId: Long,
        groupId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().judgeCrewCampaign(crewCampaignId, groupId)
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

    fun getLastModifier(
        crewCampaignId: Long,
        successCallback: (LastModifierResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getLastModifier(crewCampaignId)
            .enqueue(object : Callback<LastModifierResponse> {
                override fun onResponse(call: Call<LastModifierResponse>, response: Response<LastModifierResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<LastModifierResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getCrewCampaignRankings(
        crewCampaignId: Long,
        page: Int,
        size: Int,
        successCallback: (CrewCampaignRankingListResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getCrewCampaignRankings(crewCampaignId, page, size)
            .enqueue(object : Callback<CrewCampaignRankingListResponse> {
                override fun onResponse(call: Call<CrewCampaignRankingListResponse>, response: Response<CrewCampaignRankingListResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<CrewCampaignRankingListResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun applyForCrewCampaign(
        crewCampaignId: Long,
        request: ApplyForCrewCampaignRequest,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().applyForCrewCampaign(crewCampaignId, request)
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

    fun getQuestionForCrewCampaign(
        crewCampaignId: Long,
        successCallback: (QuestionForCrewCampaignResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getQuestionForCrewCampaign(crewCampaignId)
            .enqueue(object : Callback<QuestionForCrewCampaignResponse> {
                override fun onResponse(call: Call<QuestionForCrewCampaignResponse>, response: Response<QuestionForCrewCampaignResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<QuestionForCrewCampaignResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getFundingInfoOfCrewCampaign(
        crewCampaignId: Long,
        successCallback: (FundingInfoOfCrewCampaignResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getFundingInfoOfCrewCampaign(crewCampaignId)
            .enqueue(object : Callback<FundingInfoOfCrewCampaignResponse> {
                override fun onResponse(call: Call<FundingInfoOfCrewCampaignResponse>, response: Response<FundingInfoOfCrewCampaignResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<FundingInfoOfCrewCampaignResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun fundForFundingCoupon(
        crewCampaignId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().fundForFundingCoupon(crewCampaignId)
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

    fun getCrewCampaignMyRanking(
        crewCampaignId: Long,
        successCallback: (CrewCampaignMyRankingResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getCrewCampaignMyRanking(crewCampaignId)
            .enqueue(object : Callback<CrewCampaignMyRankingResponse> {
                override fun onResponse(call: Call<CrewCampaignMyRankingResponse>, response: Response<CrewCampaignMyRankingResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<CrewCampaignMyRankingResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun getCrewCampaignDetail(
        crewCampaignId: Long,
        successCallback: (CrewCampaignDetailResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().getCrewCampaignDetail(crewCampaignId)
            .enqueue(object : Callback<CrewCampaignDetailResponse> {
                override fun onResponse(call: Call<CrewCampaignDetailResponse>, response: Response<CrewCampaignDetailResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<CrewCampaignDetailResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }
            })
    }

    fun likeCrewCampaign(
        crewCampaignId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().likeCrewCampaign(crewCampaignId)
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

    fun unlikeCrewCampaign(
        crewCampaignId: Long,
        successCallback: () -> Unit,
        failCallback: (String) -> Unit
    ) {
        return RemoteApiManager.getService().unlikeCrewCampaign(crewCampaignId)
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