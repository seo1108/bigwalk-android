package kr.co.bigwalk.app.all

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityAllBinding
import kr.co.bigwalk.app.my_page.MyPageActivity

class AllActivity : AppCompatActivity(), BaseNavigator {

    private lateinit var binding: ActivityAllBinding
    private lateinit var allViewModel: AllViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all)
        allViewModel = AllViewModel(this)
        binding.viewModel = allViewModel
        FirebaseAnalytics.getInstance(this).logEvent("all_view", Bundle())


        val groupId = intent.getLongExtra("deep_link_group_id", -1)
        if (groupId > 0) {
            val intent = Intent(this, MyPageActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        allViewModel.requestMyProfile()
    }

    override fun getContext(): Activity {
        return this
    }
}