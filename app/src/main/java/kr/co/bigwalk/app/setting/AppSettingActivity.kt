package kr.co.bigwalk.app.setting

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityAppSettingBinding

class AppSettingActivity : AppCompatActivity(), BaseNavigator {

    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var binding: ActivityAppSettingBinding
    private lateinit var viewModel: AppSettingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.logEvent("app_setting_view", Bundle())

        binding = DataBindingUtil.setContentView(this, R.layout.activity_app_setting)
        viewModel = AppSettingViewModel(this)
        binding.viewModel = viewModel
    }

    override fun getContext(): Activity {
        return this
    }

//    @RequiresApi(Build.VERSION_CODES.M)
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
//            REQUEST_OVERLAY_PERMISSION -> {
//                if (Settings.canDrawOverlays(this)) {
//                    viewModel.setLockScreenActivate()
//                }
//            }
//        }
//    }
}