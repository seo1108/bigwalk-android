package kr.co.bigwalk.app.data.notification.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.notification.Notification

class NotificationPageDataSourceFactory : DataSource.Factory<Int, Notification>() {

    private val notificationLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, Notification>>()

    override fun create(): DataSource<Int, Notification> {
        val notificationPageDataSource = NotificationPageDataSource()
        notificationLiveDataSource.postValue(notificationPageDataSource)
        return notificationPageDataSource
    }
}