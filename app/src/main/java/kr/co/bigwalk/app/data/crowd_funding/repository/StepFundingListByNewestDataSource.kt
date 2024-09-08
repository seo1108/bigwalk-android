package kr.co.bigwalk.app.data.crowd_funding.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.crowd_funding.dto.FundingByNewestResponse
import kr.co.bigwalk.app.data.crowd_funding.dto.FundingListByNewestResponse
import kr.co.bigwalk.app.util.DebugLog

class StepFundingListByNewestDataSource(private val contestId: Long) : PageKeyedDataSource<Int, FundingByNewestResponse>() {

    private fun getCommentByCrewCampaign(page: Int, size: Int, callback: (FundingListByNewestResponse) -> Unit) {
        CrowdFundingRepository.getFundingListByNewest(
            contestId = contestId,
            page = page,
            size = size,
            successCallback = { response ->
                DebugLog.d(response.toString())
                callback(response ?: return@getFundingListByNewest)
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, FundingByNewestResponse>) {
        getCommentByCrewCampaign(FIRST_PAGE, PAGE_SIZE) {
            callback.onResult(it.content, null, FIRST_PAGE + 1)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, FundingByNewestResponse>) {
        getCommentByCrewCampaign(params.key, PAGE_SIZE) {
            callback.onResult(it.content, if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, FundingByNewestResponse>) {
        getCommentByCrewCampaign(params.key, PAGE_SIZE) {
            callback.onResult(it.content, if (params.key > FIRST_PAGE) params.key - 1 else null)
        }
    }

    companion object {
        const val PAGE_SIZE = 10
        const val FIRST_PAGE = 0
    }
}