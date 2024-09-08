package kr.co.bigwalk.app.share

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityShareCampaignBinding
import kr.co.bigwalk.app.share.ShareViewModel.Companion.CAMPAIGN
import kr.co.bigwalk.app.util.REQUEST_PERMISSIONS
import kr.co.bigwalk.app.util.showToast

class ShareCampaignActivity : AppCompatActivity(), BaseNavigator {

    lateinit var viewModel: ShareViewModel

    companion object{
        var firebaseAnalytics : FirebaseAnalytics? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityShareCampaignBinding = DataBindingUtil.setContentView(this, R.layout.activity_share_campaign)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics?.logEvent("campaign_share_view", Bundle())
        viewModel = ShareViewModel(this)
        viewModel.getCampaignShareData(intent.getLongExtra("campaignId", -1), intent.getStringExtra("campaignName"), intent.getStringExtra("campaignImageUrl"))
        binding.viewModel = viewModel
    }

    override fun getContext(): Activity {
        return this
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isEmpty()) return
            permissions.forEach {
                if (it == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                    if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                        viewModel.galleryAddFile(CAMPAIGN)
                    } else {
                        showToast("이미지 저장을 위해 갤러리 이용 권한 허가가 필요합니다.")
                        return
                    }
                }
            }
        }
    }
}