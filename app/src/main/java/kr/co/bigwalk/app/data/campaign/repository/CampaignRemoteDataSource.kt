package kr.co.bigwalk.app.data.campaign.repository

import kr.co.bigwalk.app.campaign.donation.additional_service.MissionDonationData
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.campaign.dto.*
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.prepareFilePart
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

object CampaignRemoteDataSource : CampaignDataSource {

    override fun getCampaigns(page: Int, size: Int,getCampaignCallback: CampaignDataSource.GetCampaignCallback) {
        RemoteApiManager.getService().getCampaigns(page, size).enqueue(object : Callback<List<CampaignsResponse>> {
            override fun onFailure(call: Call<List<CampaignsResponse>>, t: Throwable) {
                getCampaignCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<CampaignsResponse>>, response: Response<List<CampaignsResponse>>) {
                when (response.code()) {
                    200 -> response.body()?.let { getCampaignCallback.onSuccess(it) }
                    else -> getCampaignCallback.onFailed(response.errorBody()?.string()!!)
                }
            }
        })
    }

    override fun getCampaign(campaignId: Long, getCampaignCallback: CampaignDataSource.CampaignCallback) {
        RemoteApiManager.getService().getCampaign(campaignId).enqueue(object : Callback<CampaignAndStoryResponse> {
            override fun onFailure(call: Call<CampaignAndStoryResponse>, t: Throwable) {
                getCampaignCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<CampaignAndStoryResponse>, response: Response<CampaignAndStoryResponse>) {
                when (response.code()) {
                    200 -> response.body()?.let { getCampaignCallback.onSuccess(it) }
                    else -> getCampaignCallback.onFailed(response.errorBody()?.string()!!)
                }
            }
        })
    }

    override fun getSMSContent(
        campaignId: Long,
        smsId: Long,
        getSMSCallback: CampaignDataSource.GetSMSCallBack
    ) {
        RemoteApiManager.getService().getSMSContent(campaignId, smsId).enqueue(object : Callback<CampaignSMSContentResponse> {
            override fun onResponse(
                call: Call<CampaignSMSContentResponse>,
                response: Response<CampaignSMSContentResponse>
            ) {
                when (response.code()) {
                    200 -> response.body()?.let { getSMSCallback.onSuccess(it) }
                    else -> getSMSCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<CampaignSMSContentResponse>, t: Throwable) {
                getSMSCallback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getCampaignContents(campaignId: Long, getCampaignContentsCallback: CampaignDataSource.GetCampaignContentsCallback) {
        RemoteApiManager.getService().getCampaignContents(campaignId).enqueue(object : Callback<List<CampaignContentResponse>> {
            override fun onFailure(call: Call<List<CampaignContentResponse>>, t: Throwable) {
                getCampaignContentsCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<CampaignContentResponse>>, response: Response<List<CampaignContentResponse>>) {
                when (response.code()) {
                    200 -> response.body()?.let { getCampaignContentsCallback.onSuccess(it) }
                    else -> getCampaignContentsCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

        })
    }

    override fun donateStep(donateRequest: DonateRequest, campaignId: Long, donateStepCallback: CampaignDataSource.DonateStepCallback) {
        RemoteApiManager.getService().donateStep(donateRequest, campaignId).enqueue(object : Callback<DonateResponse> {
            override fun onFailure(call: Call<DonateResponse>, t: Throwable) {
                donateStepCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<DonateResponse>, response: Response<DonateResponse>) {
                when (response.code()) {
                    200 -> response.body()?.let { donateStepCallback.onSuccess(it) }
                    else -> donateStepCallback.onFailed(response.errorBody()?.string()!!)
                }
            }
        })
    }

    override fun donateStepForHottime(donateRequest: DonateRequest, campaignId: Long, donateStepCallback: CampaignDataSource.DonateStepForHotTimeCallback) {
        RemoteApiManager.getService().donateStepForHottime(donateRequest, campaignId).enqueue(object : Callback<HotTimeDonateResponse> {
            override fun onFailure(call: Call<HotTimeDonateResponse>, t: Throwable) {
                donateStepCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<HotTimeDonateResponse>, response: Response<HotTimeDonateResponse>) {
                when (response.code()) {
                    200 -> response.body()?.let { donateStepCallback.onSuccess(it) }
                    else -> donateStepCallback.onFailed(response.errorBody()?.string()!!)
                }
            }
        })
    }

    override fun getParticipatedCampaigns(getParticipatedCampaignsCallback: CampaignDataSource.GetParticipatedCampaignsCallback) {
        RemoteApiManager.getService().getParticipatedCampaigns().enqueue(object : Callback<List<ResponseCampaign>> {
            override fun onFailure(call: Call<List<ResponseCampaign>>, t: Throwable) {
                getParticipatedCampaignsCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<ResponseCampaign>>, response: Response<List<ResponseCampaign>>) {
                when (response.code()) {
                    200 -> response.body()?.let { getParticipatedCampaignsCallback.onSuccess(it) }
                    else -> getParticipatedCampaignsCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

        })
    }

    override fun getUserRanking(campaignId: Long, getUserRankingCallback: CampaignDataSource.GetUserRankingCallback) {
        RemoteApiManager.getService().getUserRanking(campaignId).enqueue(object : Callback<RankingResponse> {
            override fun onFailure(call: Call<RankingResponse>, t: Throwable) {
                getUserRankingCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<RankingResponse>, response: Response<RankingResponse>) {
                when (response.code()) {
                    200 -> {
                        response.body()?.let { getUserRankingCallback.onSuccess(it) }
                    }
                    else -> getUserRankingCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

        })
    }

    override fun getCampaignRanking(campaignId: Long, page: Int, size: Int, getCampaignRankingCallback: CampaignDataSource.GetCampaignRankingCallback) {
        RemoteApiManager.getService().getCampaignRanking(campaignId, page, size)
            .enqueue(object : Callback<List<RankingResponse>> {
                override fun onFailure(call: Call<List<RankingResponse>>, t: Throwable) {
                    getCampaignRankingCallback.onFailed(t.localizedMessage)
                }

                override fun onResponse(call: Call<List<RankingResponse>>, response: Response<List<RankingResponse>>) {
                    when (response.code()) {
                        200 -> {
                            response.body()?.let { getCampaignRankingCallback.onSuccess(it) }
                        }
                        else -> {
                            getCampaignRankingCallback.onFailed(response.errorBody()?.string()!!)
                        }
                    }
                }

            })
    }
    override fun getDonationReport(getDonationReportCallback: CampaignDataSource.GetDonationReportCallback) {
        RemoteApiManager.getService().getDonationReport().enqueue(object : Callback<DonationReportResponse> {
            override fun onFailure(call: Call<DonationReportResponse>, t: Throwable) {
                getDonationReportCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<DonationReportResponse>, response: Response<DonationReportResponse>) {
                when (response.code()) {
                    200 -> {
                        response.body()?.let { getDonationReportCallback.onSuccess(it) }
                    }
                    else -> {
                        getDonationReportCallback.onFailed(response.errorBody()?.string()!!)
                    }
                }
            }

        })
    }

    override fun getCampaignsRank(page: Int, size: Int, getCampaignsRankCallback: CampaignDataSource.GetCampaignsRankCallback) {
        RemoteApiManager.getService().getCampaignsRanking(page,size)
            .enqueue(object : Callback<List<RankingResponse>> {
                override fun onFailure(call: Call<List<RankingResponse>>, t: Throwable) {
                    getCampaignsRankCallback.onFailed(t.localizedMessage)
                }

                override fun onResponse(call: Call<List<RankingResponse>>, response: Response<List<RankingResponse>>) {
                    when (response.code()) {
                        200 -> {
                            response.body()?.let { getCampaignsRankCallback.onSuccess(it) }
                        }
                        else -> {
                            getCampaignsRankCallback.onFailed(response.errorBody()?.string()!!)
                        }
                    }
                }

            })
    }

    override fun getMyRankInCampaigns(getMyRankInCampaigns: CampaignDataSource.GetMyRankInCampaignsCallback) {
        RemoteApiManager.getService().getMyRankInCampaigns()
            .enqueue(object : Callback<RankingResponse> {
                override fun onFailure(call: Call<RankingResponse>, t: Throwable) {
                    getMyRankInCampaigns.onFailed(t.localizedMessage)
                }
                override fun onResponse(call: Call<RankingResponse>, response: Response<RankingResponse>) {
                    when (response.code()) {
                        200 -> {
                            response.body()?.let { getMyRankInCampaigns.onSuccess(it) }
                        }
                        else -> {
                            getMyRankInCampaigns.onFailed(response.errorBody()?.string()!!)
                        }
                    }
                }
            })
    }

    override fun getMyRankInCampaign(campaignId: Long, getMyRankInCampaignCallback: CampaignDataSource.GetMyRankInCampaignCallback) {
        RemoteApiManager.getService().getMyRankInCampaign(campaignId)
            .enqueue(object : Callback<RankingResponse>{
                override fun onFailure(call: Call<RankingResponse>, t: Throwable) {
                    getMyRankInCampaignCallback.onFailed(t.localizedMessage)
                }

                override fun onResponse(call: Call<RankingResponse>, response: Response<RankingResponse>) {
                    when (response.code()) {
                        200 ->{
                            response.body()?.let { getMyRankInCampaignCallback.onSuccess(it) }
                        }
                        else ->{
                            getMyRankInCampaignCallback.onFailed(response.errorBody()?.string()!!)
                        }
                    }
                }
            })
    }

    override fun getPopularCampaigns(callback: CampaignDataSource.GetPopularCampaignsCallback) {
        RemoteApiManager.getService().getPopularCampaigns()
            .enqueue(object : Callback<List<ResponseCampaign>> {
                override fun onFailure(call: Call<List<ResponseCampaign>>, t: Throwable) {
                    DebugLog.e(CampaignException(t.localizedMessage))
                    callback.onFailed(t.localizedMessage)
                }

                override fun onResponse(call: Call<List<ResponseCampaign>>, response: Response<List<ResponseCampaign>>) {
                    when(response.code()) {
                        200 -> callback.onSuccess(response.body())
                        else -> {
                            DebugLog.e(CampaignException(response.code().toString()))
                            callback.onFailed(response.code().toString())
                        }
                    }
                }

            })
    }

    override fun getCampaignCategories(callback: CampaignDataSource.GetCampaignCategoriesCallback) {
        RemoteApiManager.getService().getCampaignCategories()
            .enqueue(object : Callback<List<Category>> {
                override fun onFailure(call: Call<List<Category>>, t: Throwable) {
                    DebugLog.e(CampaignException(t.localizedMessage))
                    callback.onFailed(t.localizedMessage)
                }

                override fun onResponse(call: Call<List<Category>>, response: Response<List<Category>>) {
                    when(response.code()) {
                        200 -> callback.onSuccess(response.body())
                        else -> {
                            DebugLog.e(CampaignException(response.code().toString()))
                            callback.onFailed(response.code().toString())
                        }
                    }
                }

            })
    }

    override fun getCampaignsByCategory(
        categoryId: Long,
        page: Int,
        size: Int,
        callback: CampaignDataSource.GetCampaignsByCategoryCallback
    ) {
        RemoteApiManager.getService().getCampaignsByCategory(categoryId, page, size)
            .enqueue(object : Callback<List<ResponseCampaign>> {
                override fun onFailure(call: Call<List<ResponseCampaign>>, t: Throwable) {
                    DebugLog.e(CampaignException(t.localizedMessage))
                    callback.onFailed(t.localizedMessage)
                }

                override fun onResponse(call: Call<List<ResponseCampaign>>, response: Response<List<ResponseCampaign>>) {
                    when(response.code()) {
                        200 -> response.body()?.let { callback.onSuccess(it) }
                        else -> {
                            DebugLog.e(CampaignException(response.code().toString()))
                            callback.onFailed(response.code().toString())
                        }
                    }
                }

            })
    }

    override fun getAdditionalServiceActionMission(campaignId: Long, callback: CampaignDataSource.AdditionalServiceActionMissionCallback) {
        RemoteApiManager.getService().getAdditionalServiceActionMission(campaignId).enqueue(object : Callback<List<ActionMissionResponse>> {
            override fun onFailure(call: Call<List<ActionMissionResponse>>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<ActionMissionResponse>>, response: Response<List<ActionMissionResponse>>) {
                when (response.code()) {
                    200 -> response.body()?.let { list ->
                        if (list.isNotEmpty())
                            callback.onSuccess(list.first())
                        else
                            callback.onFailed("Empty")
                    }
                    else -> callback.onFailed(response.errorBody()?.string()!!)
                }
            }
        })
    }

    override fun donateMission(
            missionDonationData: MissionDonationData,
            achievementImage: File,
            achievementImage2: File?,
            achievementImage3: File?,
            comment: String,
            callback: CampaignDataSource.DonateMissionCallback) {

        DebugLog.d(missionDonationData.toString())
        val part = prepareFilePart(achievementImage, "achievementImage")
        val part2 = achievementImage2?.let { prepareFilePart(it, "achievementImage2") }
        val part3 = achievementImage3?.let { prepareFilePart(it, "achievementImage3") }
        val comment = MultipartBody.Builder().addFormDataPart("comment", comment).build().part(0)

        RemoteApiManager
            .getService()
            .donateMission(part, part2, part3, comment,
                           missionDonationData.campaignId,
                           missionDonationData.actionMission.id)
            .enqueue(object : Callback<DonationUploadResponse>{

            override fun onFailure(call: Call<DonationUploadResponse>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
            override fun onResponse(call: Call<DonationUploadResponse>, response: Response<DonationUploadResponse>) {
                when(response.code()){
                    200 -> response.body().let { res ->
                        res?.let {
                            callback.onSuccess(res)
                        }
                    }
                    404 -> callback.onNotFound()
                    else -> callback.onFailed(response.code().toString())
                }
            }
        })
    }

    override fun getAdditionalServiceValueConsumptionCommerce(campaignId: Long, callback: CampaignDataSource.AdditionalServiceValueConsumptionCommerceCallback) {
        RemoteApiManager.getService().getAdditionalServiceValueConsumptionCommerce(campaignId).enqueue(object : Callback<List<ValueConsumptionCommerceResponse>> {
            override fun onFailure(call: Call<List<ValueConsumptionCommerceResponse>>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<ValueConsumptionCommerceResponse>>, response: Response<List<ValueConsumptionCommerceResponse>>) {
                when (response.code()) {
                    200 -> response.body()?.let { list ->
                        if (list.isNotEmpty())
                            callback.onSuccess(list.first())
                        else
                            callback.onFailed("Empty")
                    }
                    else -> callback.onFailed(response.errorBody()?.string()!!)
                }
            }
        })
    }

    override fun getRankersBySeason(seasonKey: String, successCallback: (RankerBySeasonResponse?) -> Unit, failCallback: (String) -> Unit) {
        RemoteApiManager.getService().getRankersBySeason(seasonKey)
            .enqueue(object : Callback<RankerBySeasonResponse> {
                override fun onResponse(call: Call<RankerBySeasonResponse>, response: Response<RankerBySeasonResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<RankerBySeasonResponse>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }

            })
    }

    override fun getSeasonTopRankers(successCallback: (List<SeasonTopRankerResponse>?) -> Unit, failCallback: (String) -> Unit) {
        RemoteApiManager.getService().getSeasonTopRankers()
            .enqueue(object : Callback<List<SeasonTopRankerResponse>> {
                override fun onResponse(call: Call<List<SeasonTopRankerResponse>>, response: Response<List<SeasonTopRankerResponse>>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> response.errorBody()?.let { errorBody -> failCallback(RemoteApiManager.getErrorResponse(errorBody).message) }
                    }
                }

                override fun onFailure(call: Call<List<SeasonTopRankerResponse>>, t: Throwable) {
                    failCallback(t.localizedMessage)
                }

            })
    }
}