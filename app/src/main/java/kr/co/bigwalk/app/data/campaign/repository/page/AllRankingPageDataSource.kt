package kr.co.bigwalk.app.data.campaign.repository.page

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.campaign.dto.RankingResponse
import kr.co.bigwalk.app.data.campaign.repository.CampaignDataSource
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class AllRankingPageDataSource : PageKeyedDataSource<Int, RankingResponse>() {

    companion object {
        const val PAGE_SIZE = 30
        const val FIRST_PAGE = 0
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, RankingResponse>) {
        CampaignRepository.getCampaignsRank(
            FIRST_PAGE,
            PAGE_SIZE,
            object : CampaignDataSource.GetCampaignsRankCallback {
                override fun onSuccess(campaignsRankResponse: List<RankingResponse>) {
                    callback.onResult(campaignsRankResponse, null, FIRST_PAGE + 1)
                    DebugLog.d("랭킹 호출0: ${campaignsRankResponse}")
                }

                override fun onFailed(reason: String) {
                    showToast("캠페인 랭킹을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }

            })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, RankingResponse>) {
        CampaignRepository.getCampaignsRank(
            params.key,
            PAGE_SIZE,
            object : CampaignDataSource.GetCampaignsRankCallback {
                override fun onSuccess(campaignsRankResponse: List<RankingResponse>) {
                    callback.onResult(
                        campaignsRankResponse,
                        if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1
                    )
                }

                override fun onFailed(reason: String) {
                    showToast("캠페인 랭킹을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }

            })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, RankingResponse>) {
        CampaignRepository.getCampaignsRank(
            params.key,
            PAGE_SIZE,
            object : CampaignDataSource.GetCampaignsRankCallback {
                override fun onSuccess(campaignsRankResponse: List<RankingResponse>) {
                    callback.onResult(
                        campaignsRankResponse,
                        if (params.key > FIRST_PAGE) params.key - 1 else null
                    )
                }

                override fun onFailed(reason: String) {
                    showToast("캠페인 랭킹을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }

            })
    }
}