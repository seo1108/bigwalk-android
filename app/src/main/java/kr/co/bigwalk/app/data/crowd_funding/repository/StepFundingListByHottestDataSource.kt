package kr.co.bigwalk.app.data.crowd_funding.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.crowd_funding.dto.FundingByHottestResponse
import kr.co.bigwalk.app.data.crowd_funding.dto.FundingListByHottestResponse
import kr.co.bigwalk.app.util.DebugLog

class StepFundingListByHottestDataSource(private val contestId: Long) : PageKeyedDataSource<Int, FundingByHottestResponse>() {

    private fun getCommentByCrewCampaign(page: Int, size: Int, callback: (FundingListByHottestResponse) -> Unit) {
        CrowdFundingRepository.getFundingListByHottest(
            contestId = contestId,
            page = page,
            size = size,
            successCallback = { response ->
                DebugLog.d(response.toString())
                callback(response ?: return@getFundingListByHottest)
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, FundingByHottestResponse>) {
        getCommentByCrewCampaign(FIRST_PAGE, PAGE_SIZE) {
            callback.onResult(it.data, null, FIRST_PAGE + 1)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, FundingByHottestResponse>) {
        getCommentByCrewCampaign(params.key, PAGE_SIZE) {
            callback.onResult(it.data, if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, FundingByHottestResponse>) {
        getCommentByCrewCampaign(params.key, PAGE_SIZE) {
            callback.onResult(it.data, if (params.key > FIRST_PAGE) params.key - 1 else null)
        }
    }

    companion object {
        const val PAGE_SIZE = 10
        const val FIRST_PAGE = 0
    }
}