package kr.co.bigwalk.app.data.campaign.repository.page

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.campaign.ranking.RankingPlusActivity.Companion.RANKING_PAGE_SIZE
import kr.co.bigwalk.app.data.campaign.dto.RankingResponse
import kr.co.bigwalk.app.data.campaign.repository.RankDataSource
import kr.co.bigwalk.app.data.campaign.repository.RankRepository
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class OrganizationRankingPageDataSource : PageKeyedDataSource<Int, RankingResponse>() {
    companion object {
        const val PAGE_SIZE = RANKING_PAGE_SIZE
        const val FIRST_PAGE = 0
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, RankingResponse>) {
        RankRepository.getOrganizationRank(
            FIRST_PAGE,
            PAGE_SIZE,
            object : RankDataSource.GetOrganizationRankCallback {
                override fun onSuccess(rankResponse: List<RankingResponse>) {
                    callback.onResult(rankResponse, null, FIRST_PAGE + 1)
                    DebugLog.d("랭킹 호출0: ${rankResponse}")
                }

                override fun onFailed(reason: String) {
                    showToast("기업 랭킹을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }

            })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, RankingResponse>) {
        RankRepository.getOrganizationRank(
            params.key,
            PAGE_SIZE,
            object : RankDataSource.GetOrganizationRankCallback {
                override fun onSuccess(rankResponse: List<RankingResponse>) {
                    callback.onResult(
                        rankResponse,
                        if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1
                    )
                }

                override fun onFailed(reason: String) {
                    showToast("기업 랭킹을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }

            })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, RankingResponse>) {
        RankRepository.getOrganizationRank(
            params.key,
            PAGE_SIZE,
            object : RankDataSource.GetOrganizationRankCallback {
                override fun onSuccess(rankResponse: List<RankingResponse>) {
                    callback.onResult(
                        rankResponse,
                        if (params.key > FIRST_PAGE) params.key - 1 else null
                    )
                }

                override fun onFailed(reason: String) {
                    showToast("기업 랭킹을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }

            })
    }
}