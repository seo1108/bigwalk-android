package kr.co.bigwalk.app.data.notification.repository

import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.notification.Notification
import kr.co.bigwalk.app.data.notification.UserSetNotificationAgreementRequest
import kr.co.bigwalk.app.exception.NotificationException
import kr.co.bigwalk.app.util.DebugLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object NotificationRemoteDataSource: NotificationDataSource {
    override fun saveNotificationByType(
        notiType: String,
        request: UserSetNotificationAgreementRequest,
        callback: NotificationDataSource.SaveNotificationByTypeCallback
    ) {
        RemoteApiManager.getService().saveNotificationByType(notiType, request).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                DebugLog.e(NotificationException(t.message))
                callback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when(response.code()) {
                    200 -> callback.onSuccess()
                    else -> {
                        DebugLog.e(NotificationException("알림 설정 오류: " + response.code().toString()))
                        callback.onFailed(response.code().toString())
                    }
                }
            }

        })
    }

    override fun getNotifications(page: Int, size: Int, callback: NotificationDataSource.GetNotificationsCallback) {
        RemoteApiManager.getService().getNotifications(page, size).enqueue(object : Callback<List<Notification>> {
            override fun onFailure(call: Call<List<Notification>>, t: Throwable) {
                DebugLog.e(NotificationException(t.message))
                callback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<List<Notification>>, response: Response<List<Notification>>) {
                when (response.code()) {
                    200 -> response.body()?.let { callback.onSuccess(it) }
                    else -> {
                        DebugLog.e(NotificationException("알림 목록 오류: " + response.code().toString()))
                        callback.onFailed(response.code().toString())
                    }
                }
            }

        })
    }
}