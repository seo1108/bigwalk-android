package kr.co.bigwalk.app.data.notification.repository

import kr.co.bigwalk.app.data.notification.Notification
import kr.co.bigwalk.app.data.notification.UserSetNotificationAgreementRequest

interface NotificationDataSource {
    interface SaveNotificationByTypeCallback {
        fun onSuccess()
        fun onFailed(reason: String)
    }

    fun saveNotificationByType(
        notiType: String,
        request: UserSetNotificationAgreementRequest,
        callback: SaveNotificationByTypeCallback
    )

    interface GetNotificationsCallback {
        fun onSuccess(receivedNotifications: List<Notification>)
        fun onFailed(reason: String)
    }

    fun getNotifications(page: Int, size: Int, callback: GetNotificationsCallback)
}