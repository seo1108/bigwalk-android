package kr.co.bigwalk.app.campaign.ranking.category

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.co.bigwalk.app.data.campaign.dto.RankingResponse
import kr.co.bigwalk.app.data.campaign.repository.RankDataSource
import kr.co.bigwalk.app.data.campaign.repository.RankRepository
import kr.co.bigwalk.app.data.campaign.repository.page.OrganizationRankingPageDataSource
import kr.co.bigwalk.app.data.campaign.repository.page.OrganizationRankingPageDataSourceFactory
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class OrganizationRankingViewModel : ViewModel() {
    private val rankingPageDataSourceFactory = OrganizationRankingPageDataSourceFactory()
    private val config = PagedList.Config.Builder().setPageSize(OrganizationRankingPageDataSource.PAGE_SIZE).setEnablePlaceholders(true).build()
    val ranking: LiveData<PagedList<RankingResponse>> = LivePagedListBuilder<Int, RankingResponse>(rankingPageDataSourceFactory, config).build()
    val myRank : ObservableField<RankingResponse> = ObservableField()

    init {
        requestMyRanking()
    }

    fun invalidateDataSource(){
        rankingPageDataSourceFactory.rankingLiveDataSource.value?.invalidate()
    }

    fun requestMyRanking() {
        RankRepository.getMyOrganizationRank(object : RankDataSource.MyOrganizationRankCallback {
            override fun onSuccess(rankResponse: RankingResponse) {
                myRank.set(rankResponse)
            }

            override fun onFailed(reason: String) {
                showToast("나의 랭킹정보를 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(CampaignException(reason))
            }
        })
    }
}