package kr.co.bigwalk.app.my_page.withdraw

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityWithdrawBinding

class WithdrawActivity : AppCompatActivity(), BaseNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityWithdrawBinding = DataBindingUtil.setContentView(this, R.layout.activity_withdraw)
        binding.viewModel = WithdrawModel(this, this)
        FirebaseAnalytics.getInstance(this).logEvent("withdraw_view", Bundle())
    }

    override fun getContext(): Activity {
        return this
    }
}
