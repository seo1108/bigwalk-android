package kr.co.bigwalk.app.campaign.ranking

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.adapter.RankingRecyclerAdapter
import kr.co.bigwalk.app.data.campaign.dto.RankingResponse
import kr.co.bigwalk.app.databinding.ActivityRankingBinding
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class RankingActivity : AppCompatActivity(), BaseNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val campaignId = intent.getLongExtra("CampaignId", -1L)
        if (campaignId == -1L) {
            showToast("캠페인을 찾을 수 없습니다. 다시 시도해주세요")
            finish()
        }
        try {
            val binding : ActivityRankingBinding = DataBindingUtil.setContentView(this, R.layout.activity_ranking)
            val viewModelFactory = RankingViewModelFactory(campaignId, this)
            val viewModel = ViewModelProvider(this, viewModelFactory).get(RankingViewModel::class.java)
            val adapter = RankingRecyclerAdapter()
            viewModel.ranking.observe(this, Observer<PagedList<RankingResponse>> {
                    pagedList -> adapter.submitList(pagedList)
            })
            binding.rankingRecycler.adapter = adapter
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