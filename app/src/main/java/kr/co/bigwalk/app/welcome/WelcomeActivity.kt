package kr.co.bigwalk.app.welcome

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityWelcomeBinding

class WelcomeActivity: AppCompatActivity(), BaseNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityWelcomeBinding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        binding.viewModel = WelcomeViewModel(this)
        FirebaseAnalytics.getInstance(this).logEvent("welcome_view", Bundle())
    }

    override fun getContext(): Activity {
        return this
    }

}