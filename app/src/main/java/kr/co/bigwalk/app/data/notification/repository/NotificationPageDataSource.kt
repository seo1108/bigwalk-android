package kr.co.bigwalk.app.data.notification.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.notification.Notification
import kr.co.bigwalk.app.exception.NotificationException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class NotificationPageDataSource : PageKeyedDataSource<Int, Notification>() {

    companion object {
        const val PAGE_SIZE = 20
        const val FIRST_PAGE = 0
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Notification>) {
        NotificationRepository.getNotifications(FIRST_PAGE, PAGE_SIZE, object: NotificationDataSource.GetNotificationsCallback {
            override fun onSuccess(receivedNotifications: List<Notification>) {
                callback.onResult(receivedNotifications, null, FIRST_PAGE + 1)
            }

            override fun onFailed(reason: String) {
                showToast("알림을 확인할 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(NotificationException(reason))
            }

        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Notification>) {
        NotificationRepository.getNotifications(params.key, params.requestedLoadSize, object: NotificationDataSource.GetNotificationsCallback {
            override fun onSuccess(receivedNotifications: List<Notification>) {
                callback.onResult(receivedNotifications, if (params.key < PAGE_SIZE) null else params.key + 1)
            }

            override fun onFailed(reason: String) {
                showToast("알림을 확인할 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(NotificationException(reason))
            }

        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Notification>) {
        val keyPage = params.key
        NotificationRepository.getNotifications(keyPage, PAGE_SIZE, object: NotificationDataSource.GetNotificationsCallback {
            override fun onSuccess(receivedNotifications: List<Notification>) {
                callback.onResult(receivedNotifications, if (params.key > FIRST_PAGE) params.key - 1 else null)
            }

            override fun onFailed(reason: String) {
                showToast("알림을 확인할 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(NotificationException(reason))
            }

        })
    }
}