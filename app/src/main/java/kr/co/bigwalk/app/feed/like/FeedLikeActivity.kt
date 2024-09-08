package kr.co.bigwalk.app.feed.like

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import com.google.gson.Gson
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.databinding.ActivityFeedLikeBinding
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.feed.adapter.FeedLikeRecyclerAdapter
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class FeedLikeActivity : AppCompatActivity(), BaseNavigator {

    companion object {
        const val ACTION_DONATION_HISTORY_ID = "ActionDonationHistoryId"
        const val IS_LIKE_ME = "IS_LIKE_ME"
        const val IS_PUBLIC = "IS_PUBLIC"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionDonationHistoryId = intent.getLongExtra(ACTION_DONATION_HISTORY_ID, -1L)
        val isLikeMe = intent.getBooleanExtra(IS_LIKE_ME, false)
        val isPublic = intent.getBooleanExtra(IS_PUBLIC, false)

        if (actionDonationHistoryId == -1L) {
            showToast("챌린지를 찾을 수 없습니다. 다시 시도해주세요")
            finish()
        }
        try {
            val binding : ActivityFeedLikeBinding = DataBindingUtil.setContentView(this, R.layout.activity_feed_like)
            val viewModelFactory = FeedLikeViewModelFactory(actionDonationHistoryId, this)
            val viewModel = ViewModelProvider(this, viewModelFactory).get(FeedLikeViewModel::class.java)
            val adapter = FeedLikeRecyclerAdapter(isPublic)

            viewModel.isLikeMe.value = isLikeMe

            val profilePath = PreferenceManager.getProfilePath()
            val departmentName = PreferenceManager.getDepartmentName()
            val userName = PreferenceManager.getName()
            viewModel.profilePath.value = profilePath
            viewModel.departmentName.value = departmentName
            viewModel.userName.value = userName

            binding.tvFeedLikeDepartment.isVisible = (!isPublic && departmentName.isNotEmpty())

            viewModel.likedUsers.observe(this, Observer<PagedList<UserProfileResponse>> { pagedList ->
                adapter.submitList(pagedList)
            })
            binding.feedLikeRecycler.adapter = adapter
            binding.viewModel = viewModel
        } catch (exception: CampaignException) {
            DebugLog.e(exception)
            showToast("캠페인을 찾을 수 없습니다. 다시 시도해주세요")
            finish()
        }
    }

    override fun getContext(): Activity {
        return this
    }
}