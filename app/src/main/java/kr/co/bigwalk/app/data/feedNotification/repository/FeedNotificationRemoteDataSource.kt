package kr.co.bigwalk.app.data.feedNotification.repository

import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.feedNotification.dto.FeedNotificationResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object FeedNotificationRemoteDataSource: FeedNotificationDataSource {

    override fun getFeedNotifications(
        page: Int,
        size: Int,
        campaignId: Long,
        getFeedNotificationCallback: FeedNotificationDataSource.GetFeedNotificationCallback
    ) {
        RemoteApiManager.getService().getFeedNotifications(campaignId, page, size).enqueue(object :
            Callback<List<FeedNotificationResponse>> {

            override fun onFailure(call: Call<List<FeedNotificationResponse>>, t: Throwable) {
                getFeedNotificationCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(
                call: Call<List<FeedNotificationResponse>>,
                response: Response<List<FeedNotificationResponse>>
            ) {
                when (response.code()) {
                    200 -> response.body()?.let { getFeedNotificationCallback.onSuccess(it) }
                    else -> getFeedNotificationCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

        })
    }

    override fun getAllNotifications(
        page: Int,
        size: Int,
        getFeedNotificationCallback: FeedNotificationDataSource.GetFeedNotificationCallback
    ) {
        RemoteApiManager.getService().getAllNotifications(page, size).enqueue(object :
            Callback<List<FeedNotificationResponse>> {

            override fun onFailure(call: Call<List<FeedNotificationResponse>>, t: Throwable) {
                getFeedNotificationCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(
                call: Call<List<FeedNotificationResponse>>,
                response: Response<List<FeedNotificationResponse>>
            ) {
                when (response.code()) {
                    200 -> response.body()?.let { getFeedNotificationCallback.onSuccess(it) }
                    else -> getFeedNotificationCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

        })
    }
}