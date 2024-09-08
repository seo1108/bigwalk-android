package kr.co.bigwalk.app.notification

import android.content.Intent
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.data.notification.Notification
import kr.co.bigwalk.app.data.notification.repository.NotificationPageDataSource
import kr.co.bigwalk.app.data.notification.repository.NotificationPageDataSourceFactory

class ReceivedNotificationViewModel(private val navigator: BaseNavigator) : ViewModel() {

    private val notificationPageDataSourceFactory = NotificationPageDataSourceFactory()
    private val config = PagedList.Config.Builder().setPageSize(NotificationPageDataSource.PAGE_SIZE).setEnablePlaceholders(true).build()
    val notification: LiveData<PagedList<Notification>> = LivePagedListBuilder(notificationPageDataSourceFactory, config).build()
    val visibility: ObservableField<Boolean> = ObservableField(false)


    fun finishActivity() {
        navigator.finish()
    }

    fun moveToCampaign() {
        val intent = Intent(navigator.getContext(), CampaignActivity::class.java)
        navigator.getContext().startActivity(intent)
    }

}