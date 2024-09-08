package kr.co.bigwalk.app.data.funding.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.funding.dto.CrewCampaignRankingListResponse
import kr.co.bigwalk.app.data.funding.dto.CrewCampaignRankingResponse
import kr.co.bigwalk.app.util.DebugLog

class CrewCampaignRankingDataSource(private val crewCampaignId: Long) : PageKeyedDataSource<Int, CrewCampaignRankingResponse>() {

    private fun getTotalMyFunding(page: Int, size: Int, callback: (CrewCampaignRankingListResponse) -> Unit) {
        FundingRepository.getCrewCampaignRankings(
            crewCampaignId = crewCampaignId,
            page = page,
            size = size,
            successCallback = { response ->
                DebugLog.d(response.toString())
                callback(response ?: return@getCrewCampaignRankings)
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, CrewCampaignRankingResponse>) {
        getTotalMyFunding(FIRST_PAGE, PAGE_SIZE) {
            callback.onResult(it.data, null, FIRST_PAGE + 1)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, CrewCampaignRankingResponse>) {
        getTotalMyFunding(params.key, PAGE_SIZE) {
            callback.onResult(it.data, if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, CrewCampaignRankingResponse>) {
        getTotalMyFunding(params.key, PAGE_SIZE) {
            callback.onResult(it.data, if (params.key > FIRST_PAGE) params.key - 1 else null)
        }
    }

    companion object {
        const val PAGE_SIZE = 10
        const val FIRST_PAGE = 0
    }
}