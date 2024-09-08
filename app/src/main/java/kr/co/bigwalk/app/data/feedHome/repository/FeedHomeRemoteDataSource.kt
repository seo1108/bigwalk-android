package kr.co.bigwalk.app.data.feedHome.repository

import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.feedHome.dto.*
import kr.co.bigwalk.app.util.DebugLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FeedHomeRemoteDataSource : FeedHomeDataSource {

    override fun getMissionCampaigns(
        page: Int,
        size: Int,
        getMissionCampaignCallback: FeedHomeDataSource.GetMissionCampaignCallback
    ) {
        RemoteApiManager.getService().getMissionCampaigns(page, size).enqueue(object :
            Callback<BaseResponse<List<MissionCampaignResponse>>> {
            override fun onFailure(call: Call<BaseResponse<List<MissionCampaignResponse>>>, t: Throwable) {
                getMissionCampaignCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(
                call: Call<BaseResponse<List<MissionCampaignResponse>>>,
                response: Response<BaseResponse<List<MissionCampaignResponse>>>
            ) {
//                when (response.code()) {
//                    200 -> response.body()?.data?.let { getMissionCampaignCallback.onSuccess(it) }
//                    else -> getMissionCampaignCallback.onFailed(response.errorBody()?.string()!!)
//                }
                when (response.body()?.result) {
                    Result.SUCCESS -> {
                        response.body()?.data?.let { getMissionCampaignCallback.onSuccess(it) }
                    }
                    Result.FAIL -> {
                        response.body()?.message?.let {
                            getMissionCampaignCallback.onFailed(response.body()?.message.orEmpty())
                        }
                    }
                }
            }
        })
    }

    override fun getChallengeHome(callback: FeedHomeDataSource.GetChallengeHomeCallback) {
        RemoteApiManager.getService().getChallengeHome().enqueue(object : Callback<ChallengeHomeResponse>{
            override fun onResponse(call: Call<ChallengeHomeResponse>, response: Response<ChallengeHomeResponse>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                } else {
                    callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<ChallengeHomeResponse>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getChallengeActiveList(callback: FeedHomeDataSource.GetChallengeActiveListCallback) {
        RemoteApiManager.getService().getChallengeActiveList().enqueue(object : Callback<List<ChallengeInfoResponse>>{
            override fun onResponse(call: Call<List<ChallengeInfoResponse>>, response: Response<List<ChallengeInfoResponse>>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                } else {
                    callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<List<ChallengeInfoResponse>>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getChallengeTypeList(type: String, callback: FeedHomeDataSource.GetChallengeTypeListCallback) {
        RemoteApiManager.getService().getChallengeTypeList(type).enqueue(object : Callback<List<ChallengeYearResponse>>{
            override fun onResponse(call: Call<List<ChallengeYearResponse>>, response: Response<List<ChallengeYearResponse>>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                } else {
                    callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<List<ChallengeYearResponse>>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getChallengePerYear(
        type: String,
        year: String,
        page: Int,
        size: Int,
        callback: FeedHomeDataSource.GetChallengeYearCallback) {
        RemoteApiManager.getService().getChallengePerYear(type, year, page, size).enqueue(object : Callback<ChallengeYearPagingResponse> {
            override fun onResponse(call: Call<ChallengeYearPagingResponse>, response: Response<ChallengeYearPagingResponse>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { callback.onSuccess(it) }
                } else {
                    callback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<ChallengeYearPagingResponse>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }
        })
    }

}