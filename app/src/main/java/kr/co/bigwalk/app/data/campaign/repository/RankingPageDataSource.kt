package kr.co.bigwalk.app.data.campaign.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.campaign.ranking.RankingPlusActivity.Companion.RANKING_PAGE_SIZE
import kr.co.bigwalk.app.data.campaign.dto.RankingResponse
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showDebugToast
import kr.co.bigwalk.app.util.showToast

class RankingPageDataSource(private val campaignId: Long) : PageKeyedDataSource<Int, RankingResponse>() {

    companion object {
        const val PAGE_SIZE = RANKING_PAGE_SIZE
        const val FIRST_PAGE = 0
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, RankingResponse>) {
            CampaignRepository.getCampaignRanking(campaignId, FIRST_PAGE, PAGE_SIZE, object : CampaignDataSource.GetCampaignRankingCallback {
                override fun onSuccess(campaignRanking: List<RankingResponse>) {
                    callback.onResult(campaignRanking, null, FIRST_PAGE + 1)
                }

                override fun onFailed(reason: String) {
                    showToast("캠페인 랭킹을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }

            })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, RankingResponse>) {
        CampaignRepository.getCampaignRanking(campaignId, params.key, PAGE_SIZE, object : CampaignDataSource.GetCampaignRankingCallback {
            override fun onSuccess(campaignRanking: List<RankingResponse>) {
                callback.onResult(campaignRanking, if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1)
            }

            override fun onFailed(reason: String) {
                showToast("캠페인 랭킹을 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(CampaignException(reason))
            }

        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, RankingResponse>) {
        val keyPage = params.key
        CampaignRepository.getCampaignRanking(campaignId, keyPage, PAGE_SIZE, object : CampaignDataSource.GetCampaignRankingCallback {
            override fun onSuccess(campaignRanking: List<RankingResponse>) {
                callback.onResult(campaignRanking, if(params.key > FIRST_PAGE) params.key -1 else null)
            }

            override fun onFailed(reason: String) {
                showToast("캠페인 랭킹을 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(CampaignException(reason))
            }

        })
    }
}