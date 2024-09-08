package kr.co.bigwalk.app.notification

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.notification.Notification
import kr.co.bigwalk.app.databinding.ActivityReceivedNotificationBinding

class ReceivedNotificationActivity: AppCompatActivity(), BaseNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAnalytics.getInstance(this).logEvent("notification_view", Bundle())
        val binding: ActivityReceivedNotificationBinding = DataBindingUtil.setContentView(this, R.layout.activity_received_notification)
        val viewModelFactory = NotificationViewModelFactory(this)
        val viewModel =
            ViewModelProvider(this, viewModelFactory).get(ReceivedNotificationViewModel::class.java)
        binding.viewModel = viewModel
        val adapter = ReceivedNotificationAdapter()
        binding.receivedNotificationRecycler.adapter = adapter
        viewModel.notification.observe(this, Observer<PagedList<Notification>> { pagedList ->
            adapter.submitList(pagedList)
            pagedList.addWeakCallback(null, object: PagedList.Callback() {
                override fun onChanged(position: Int, count: Int) {
                }
                override fun onInserted(position: Int, count: Int) {
                    if (count > 0) viewModel.visibility.set(false)
                    else viewModel.visibility.set(true)
                }
                override fun onRemoved(position: Int, count: Int) {}
            })
        })
        RealtimeNotification.getInstance().disable()
        resetBadgeCounterOfPushMessages(this)
    }

    override fun getContext(): Activity {
        return this
    }

}