package kr.co.bigwalk.app.my_page.withdraw

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityWithdrawCompleteBinding

class WithdrawCompleteActivity : AppCompatActivity(), BaseNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityWithdrawCompleteBinding = DataBindingUtil.setContentView(this, R.layout.activity_withdraw_complete)
        binding.viewModel = WithdrawCompleteModel(this, this)
        FirebaseAnalytics.getInstance(this).logEvent("withdraw_complete_view", Bundle())
    }

    override fun getContext(): Activity {
        return this
    }
}
