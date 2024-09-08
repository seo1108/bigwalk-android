package kr.co.bigwalk.app.customer_center

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.BuildConfig
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityCustomerCenterBinding

class CustomerCenterActivity: AppCompatActivity(), BaseNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityCustomerCenterBinding = DataBindingUtil.setContentView(this, R.layout.activity_customer_center)
        binding.viewModel = CustomerCenterViewModel(this)
        FirebaseAnalytics.getInstance(this).logEvent("customer_center_view", Bundle())
        binding.versionCode.text = "앱 버전 ${BuildConfig.VERSION_NAME}"
    }

    override fun getContext(): Activity {
        return this
    }
}