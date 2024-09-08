package kr.co.bigwalk.app.share

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.share.ResponseShare
import kr.co.bigwalk.app.databinding.ActivityShareBinding
import kr.co.bigwalk.app.share.ShareViewModel.Companion.ALL
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.REQUEST_PERMISSIONS
import kr.co.bigwalk.app.util.longValueToCommaString
import kr.co.bigwalk.app.util.showToast

class ShareActivity : AppCompatActivity(), BaseNavigator {
    lateinit var viewModel: ShareViewModel
    private var currentShareType = Type.NORMAL

    companion object{
        var firebaseAnalytics : FirebaseAnalytics? = null
        const val SHARE_TYPE = "share_type"
        const val SHARE_RANK = "share_rank"
        const val SHARE_GRADE = "share_grade"
        const val SHARE_DONATED_STEP = "share_donated_step"
    }

    enum class Type {
        NORMAL,
        ALL,
        TODAY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityShareBinding = DataBindingUtil.setContentView(this, R.layout.activity_share)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics?.logEvent("all_share_view", Bundle())
        currentShareType = Type.valueOf(intent.getStringExtra(SHARE_TYPE)?:Type.NORMAL.name)
        viewModel = ShareViewModel(this)
        if (currentShareType == Type.NORMAL) {
            viewModel.getAllShareData()
        } else {
            val shareData = ResponseShare("", 0,
                intent.getLongExtra(SHARE_DONATED_STEP, 0),
                intent.getLongExtra(SHARE_RANK, 0))
            viewModel.allShare.set(shareData)
        }
        viewModel.requestMyProfile()
        binding.viewModel = viewModel

        updateView(binding, currentShareType)
    }

    private fun updateView(binding: ActivityShareBinding, currentShareType: Type) {
        when (currentShareType) {
            Type.ALL -> {
                binding.shareRankTitle.visibility = View.VISIBLE
                binding.shareRank.visibility = View.VISIBLE
                binding.shareMyGradeTitle.visibility = View.VISIBLE
                binding.shareMyGrade.visibility = View.VISIBLE
                binding.shareTodaysStepTitle.visibility = View.GONE
                binding.shareTodaysStep.visibility = View.GONE
                binding.shareMyGrade.text = intent.getStringExtra(SHARE_GRADE)
            }
            Type.TODAY -> {
                binding.shareRankTitle.text = resources.getString(R.string.todays_ranking)
                binding.shareRankTitle.visibility = View.VISIBLE
                binding.shareRank.visibility = View.VISIBLE
                binding.shareMyGradeTitle.visibility = View.VISIBLE
                binding.shareMyGrade.visibility = View.VISIBLE
                binding.shareTodaysStepTitle.visibility = View.GONE
                binding.shareTodaysStep.visibility = View.GONE
                binding.shareMyGrade.text = intent.getStringExtra(SHARE_GRADE)
                binding.shareDonationTitle.text = resources.getString(R.string.today_donate_walk)
            }
            else -> {
                binding.shareRankTitle.visibility = View.GONE
                binding.shareRank.visibility = View.GONE
                binding.shareMyGradeTitle.visibility = View.GONE
                binding.shareMyGrade.visibility = View.GONE
                binding.shareTodaysStepTitle.visibility = View.VISIBLE
                binding.shareTodaysStep.visibility = View.VISIBLE
            }
        }
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
                        viewModel.galleryAddFile(ALL)
                    } else {
                        showToast("이미지 저장을 위해 갤러리 이용 권한 허가가 필요합니다.")
                        return
                    }
                }
            }
        }
    }

    private fun getRankString(rank: Long): String {
        return longValueToCommaString(rank) + "위"
    }

    private fun getTotalDonatedStepsString(donatedSteps: Long): String {
        val steps = if(donatedSteps > 10000) {
            longValueToCommaString(donatedSteps / 10000) + "만 " + donatedSteps % 10000
        } else {
            longValueToCommaString(donatedSteps)
        }
        return "${steps}걸음"
    }

}