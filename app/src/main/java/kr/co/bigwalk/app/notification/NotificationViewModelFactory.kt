package kr.co.bigwalk.app.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.exception.NotificationException

class NotificationViewModelFactory (
    private val navigator: BaseNavigator
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(ReceivedNotificationViewModel::class.java)) {
            ReceivedNotificationViewModel(navigator) as T
        } else {
            navigator.finish()
            throw NotificationException("ReceivedNotificationViewModel Not Found")
        }
    }
}