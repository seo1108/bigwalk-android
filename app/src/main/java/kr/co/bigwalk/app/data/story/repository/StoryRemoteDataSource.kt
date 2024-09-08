package kr.co.bigwalk.app.data.story.repository

import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.story.dto.CampaignReservationRequest
import kr.co.bigwalk.app.data.story.dto.ResponseOpenStory
import kr.co.bigwalk.app.data.story.dto.ResponseStory
import kr.co.bigwalk.app.data.story.dto.Story
import kr.co.bigwalk.app.util.DebugLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object StoryRemoteDataSource : StoryDataSource {
    override fun getStories(page: Int,
                            size: Int,
                            getStoriesCallback: StoryDataSource.GetStoriesCallback) {
        RemoteApiManager.getService().getStories(page, size).enqueue(object : Callback<List<ResponseStory>>{
            override fun onResponse(call: Call<List<ResponseStory>>, response: Response<List<ResponseStory>>) {
                DebugLog.d(response.body().toString())
                if (response.isSuccessful) {
                    response.body()?.let { getStoriesCallback.onSuccess(it) }
                } else {
                    getStoriesCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<List<ResponseStory>>, t: Throwable) {
                getStoriesCallback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getStory(campaignId: Long, getStoriesCallback: StoryDataSource.GetStoryCallback) {
        RemoteApiManager.getService().getStory(campaignId).enqueue(object : Callback<ResponseStory> {
            override fun onFailure(call: Call<ResponseStory>, t: Throwable) {
                getStoriesCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<ResponseStory>, response: Response<ResponseStory>) {
                when (response.code()) {
                    200 -> response.body()?.let { getStoriesCallback.onSuccess(it) }
                    else -> getStoriesCallback.onFailed(response.errorBody()?.string()!!)
                }
            }
        })
    }

    override fun getStoriesForReady(
        page: Int,
        size: Int,
        callback: StoryDataSource.GetStoriesForReadyCallback
    ) {
        RemoteApiManager.getService().getStoriesForReady(page, size).enqueue(object : Callback<List<Story>> {
            override fun onFailure(call: Call<List<Story>>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<Story>>, response: Response<List<Story>>) {
                when (response.code()) {
                    200 -> response.body()?.let { callback.onSuccess(it) }
                    else -> callback.onFailed(response.errorBody()?.string()!!)
                }
            }
        })
    }

    override fun reserveCampaign(
        request: CampaignReservationRequest,
        callback: StoryDataSource.ReserveCampaignCallback
    ) {
        RemoteApiManager.getService().reserveCampaign(request).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    200 -> callback.onSuccess()
                    else -> callback.onFailed(response.errorBody()?.string()!!)
                }
            }

        })
    }

    override fun getMyReservedStories(
        page: Int,
        size: Int,
        callback: StoryDataSource.GetMyReservedStoriesCallback
    ) {
        RemoteApiManager.getService().getMyReservedStories(page, size).enqueue(object : Callback<List<ResponseStory>> {
            override fun onFailure(call: Call<List<ResponseStory>>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<ResponseStory>>, response: Response<List<ResponseStory>>) {
                when (response.code()) {
                    200 -> response.body()?.let { callback.onSuccess(it) }
                    else -> callback.onFailed(response.errorBody()?.string()!!)
                }
            }

        })
    }

    override fun getMyReservedStoriesCount(callback: StoryDataSource.GetMyReservedStoriesCountCallback) {
        RemoteApiManager.getService().getMyReservedStoriesCount().enqueue(object : Callback<ResponseOpenStory> {
            override fun onFailure(call: Call<ResponseOpenStory>, t: Throwable) {
                callback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<ResponseOpenStory>, response: Response<ResponseOpenStory>) {
                when (response.code()) {
                    200 -> response.body()?.let { callback.onSuccess(it) }
                    else -> callback.onFailed(response.errorBody()?.string()!!)
                }
            }

        })
    }

}