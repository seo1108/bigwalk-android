package kr.co.bigwalk.app.data.crowd_funding.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.crowd_funding.dto.TotalMyFundingListResponse
import kr.co.bigwalk.app.data.crowd_funding.dto.TotalMyFundingResponse
import kr.co.bigwalk.app.util.DebugLog

class TotalMyFundingDataSource(private val contestId: Long?) : PageKeyedDataSource<Int, TotalMyFundingResponse>() {

    private fun getTotalMyFunding(page: Int, size: Int, callback: (TotalMyFundingListResponse) -> Unit) {
        CrowdFundingRepository.getTotalMyFunding(
            contestId = contestId,
            page = page,
            size = size,
            successCallback = { response ->
                DebugLog.d(response.toString())
                callback(response ?: return@getTotalMyFunding)
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, TotalMyFundingResponse>) {
        getTotalMyFunding(FIRST_PAGE, PAGE_SIZE) {
            callback.onResult(it.content, null, FIRST_PAGE + 1)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, TotalMyFundingResponse>) {
        getTotalMyFunding(params.key, PAGE_SIZE) {
            callback.onResult(it.content, if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, TotalMyFundingResponse>) {
        getTotalMyFunding(params.key, PAGE_SIZE) {
            callback.onResult(it.content, if (params.key > FIRST_PAGE) params.key - 1 else null)
        }
    }

    companion object {
        const val PAGE_SIZE = 10
        const val FIRST_PAGE = 0
    }
}