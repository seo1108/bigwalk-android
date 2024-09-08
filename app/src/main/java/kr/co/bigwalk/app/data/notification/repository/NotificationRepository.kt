package kr.co.bigwalk.app.data.notification.repository

import kr.co.bigwalk.app.data.notification.UserSetNotificationAgreementRequest

object NotificationRepository: NotificationDataSource {
    override fun saveNotificationByType(
        notiType: String,
        request: UserSetNotificationAgreementRequest,
        callback: NotificationDataSource.SaveNotificationByTypeCallback
    ) {
        NotificationRemoteDataSource.saveNotificationByType(notiType, request, callback)
    }

    override fun getNotifications(page: Int, size: Int, callback: NotificationDataSource.GetNotificationsCallback) {
        NotificationRemoteDataSource.getNotifications(page, size, callback)
    }
}