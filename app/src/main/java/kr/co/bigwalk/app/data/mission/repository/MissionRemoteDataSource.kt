package kr.co.bigwalk.app.data.mission.repository

import kr.co.bigwalk.app.data.ErrorMessage
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.mission.dto.MissionsResponse
import kr.co.bigwalk.app.data.mission.dto.MissionsStatusResponse
import kr.co.bigwalk.app.data.mission.dto.RewardsResponse
import kr.co.bigwalk.app.data.mission.dto.WelcomeMissionCompleteRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object MissionRemoteDataSource: MissionDataSource {
    override fun getMissions(missionsCallback: MissionDataSource.MissionsCallback) {
        RemoteApiManager.getService().getMissions().enqueue(object : Callback<List<MissionsResponse>> {
            override fun onResponse(
                call: Call<List<MissionsResponse>>,
                response: Response<List<MissionsResponse>>
            ) {
                when (response.code()) {
                    200 -> {
                        missionsCallback.onSuccess(response.body())
                    }
                    else -> missionsCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<List<MissionsResponse>>, t: Throwable) {
                missionsCallback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun postWelcomeRewards(rewardsCallback: MissionDataSource.RewardsCallback) {
        RemoteApiManager.getService().postRewards().enqueue(object : Callback<RewardsResponse> {
            override fun onResponse(
                call: Call<RewardsResponse>,
                response: Response<RewardsResponse>
            ) {
                when (response.code()) {
                    200 -> response.body()?.let { rewardsCallback.onSuccess(it) }
                    406 -> rewardsCallback.onFailed(
                        response.code(),
                        RemoteApiManager.getErrorResponse(response.errorBody()!!).message
                    )
                    else -> rewardsCallback.onFailed(response.code(), response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<RewardsResponse>, t: Throwable) {
                rewardsCallback.onFailed(null, t.localizedMessage)
            }
        })
    }

    override fun getMissionsStatus(missionsStatusCallback: MissionDataSource.MissionsStatusCallback) {
        RemoteApiManager.getService().getMissionsStatus().enqueue(object : Callback<MissionsStatusResponse> {
            override fun onResponse(
                call: Call<MissionsStatusResponse>,
                response: Response<MissionsStatusResponse>
            ) {
                when (response.code()) {
                    200 -> response.body()?.let { missionsStatusCallback.onSuccess(it) }
                    else -> missionsStatusCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<MissionsStatusResponse>, t: Throwable) {
                missionsStatusCallback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun completeWelcomeMission(
        completeRequest: WelcomeMissionCompleteRequest,
        missionsStatusCallback: MissionDataSource.MissionsStatusCallback
    ) {
        RemoteApiManager.getService().completeWelcomeMission(completeRequest.sequence, completeRequest.value).enqueue(object : Callback<MissionsStatusResponse> {
            override fun onResponse(
                call: Call<MissionsStatusResponse>,
                response: Response<MissionsStatusResponse>
            ) {
                when (response.code()) {
                    200 -> response.body()?.let { missionsStatusCallback.onSuccess(it) }
                    else -> missionsStatusCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<MissionsStatusResponse>, t: Throwable) {
                missionsStatusCallback.onFailed(t.localizedMessage)
            }
        })
    }
}