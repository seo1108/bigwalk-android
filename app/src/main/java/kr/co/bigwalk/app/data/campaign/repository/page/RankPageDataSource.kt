package kr.co.bigwalk.app.data.campaign.repository.page

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.campaign.ranking.RankingPlusActivity.Companion.RANKING_PAGE_SIZE
import kr.co.bigwalk.app.data.campaign.dto.RankData
import kr.co.bigwalk.app.data.campaign.repository.RankDataSource
import kr.co.bigwalk.app.data.campaign.repository.RankRepository
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class RankPageDataSource(
    private val rankCategory: String,
    private val rankInfoListener: RankInfoListener
) : PageKeyedDataSource<Int, RankData>() {
    companion object {
        const val PAGE_SIZE = RANKING_PAGE_SIZE
        const val FIRST_PAGE = 0
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, RankData>
    ) {
        RankRepository.getRank(
            rankCategory,
            FIRST_PAGE,
            PAGE_SIZE,
            object : RankDataSource.RankCallback {
                override fun onSuccessWithList(list: List<RankData>) {
                    callback.onResult(list, null, FIRST_PAGE + 1)
                }

                override fun onSuccessWithMyRank(myRank: RankData) {
                    rankInfoListener.loadMyRank(myRank)
                }

                override fun onSuccessWithSeasonPeriod(
                    season: String,
                    startDate: String?,
                    endDate: String?
                ) {
                    rankInfoListener.loadPeriod(season, startDate, endDate)
                }

                override fun onFailed(reason: String) {
                    showToast("$rankCategory 랭킹을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }
            })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, RankData>) {
        RankRepository.getRank(
            rankCategory,
            params.key,
            PAGE_SIZE,
            object : RankDataSource.RankCallback {
                override fun onSuccessWithList(list: List<RankData>) {
                    callback.onResult(
                        list,
                        if (params.key > FIRST_PAGE) params.key - 1 else null
                    )
                }

                override fun onSuccessWithMyRank(myRank: RankData) {
                    rankInfoListener.loadMyRank(myRank)
                }

                override fun onSuccessWithSeasonPeriod(
                    season: String,
                    startDate: String?,
                    endDate: String?
                ) {
                    rankInfoListener.loadPeriod(season, startDate, endDate)
                }

                override fun onFailed(reason: String) {
                    showToast("$rankCategory 랭킹을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }
            })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, RankData>) {
        RankRepository.getRank(
            rankCategory,
            params.key,
            PAGE_SIZE,
            object : RankDataSource.RankCallback {
                override fun onSuccessWithList(list: List<RankData>) {
                    callback.onResult(
                        list,
                        if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1
                    )
                }

                override fun onSuccessWithMyRank(myRank: RankData) {
                    rankInfoListener.loadMyRank(myRank)
                }

                override fun onSuccessWithSeasonPeriod(
                    season: String,
                    startDate: String?,
                    endDate: String?
                ) {
                    rankInfoListener.loadPeriod(season, startDate, endDate)
                }

                override fun onFailed(reason: String) {
                    showToast("$rankCategory 랭킹을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }
            })
    }
}