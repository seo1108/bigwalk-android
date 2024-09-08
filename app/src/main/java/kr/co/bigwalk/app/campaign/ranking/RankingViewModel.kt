package kr.co.bigwalk.app.campaign.ranking

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.campaign.dto.RankingResponse
import kr.co.bigwalk.app.data.campaign.repository.CampaignDataSource
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.data.campaign.repository.RankingPageDataSource.Companion.PAGE_SIZE
import kr.co.bigwalk.app.data.campaign.repository.RankingPageDataSourceFactory
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class RankingViewModel(
    private val campaignId: Long,
    private val navigator: BaseNavigator
) : ViewModel() {

    val myRank : ObservableField<RankingResponse> = ObservableField()
    private val rankingPageDataSourceFactory = RankingPageDataSourceFactory(campaignId)
    private val config = PagedList.Config.Builder().setPageSize(PAGE_SIZE).setEnablePlaceholders(true).build()
    val ranking: LiveData<PagedList<RankingResponse>> = LivePagedListBuilder<Int, RankingResponse>(rankingPageDataSourceFactory, config).build()

    init {
        requestMyRanking()
    }

    fun finish() {
        navigator.finish()
    }


    private fun requestMyRanking() {
        CampaignRepository.getMyRankInCampaign(campaignId, object : CampaignDataSource.GetMyRankInCampaignCallback {
            override fun onSuccess(rankingResponse: RankingResponse) {
                myRank.set(rankingResponse)
            }
            override fun onFailed(reason: String) {
                showToast("나의 랭킹정보를 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(CampaignException(reason))
            }
        })
    }
}