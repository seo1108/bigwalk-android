package kr.co.bigwalk.app.data.crowd_funding.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.crowd_funding.dto.MyFundingByContestResponse
import kr.co.bigwalk.app.data.crowd_funding.dto.MyFundingListByContestResponse
import kr.co.bigwalk.app.util.DebugLog

class MyStepFundingListDataSource(private val contestId: Long) : PageKeyedDataSource<Int, MyFundingByContestResponse>() {

    private fun getMyFundingByContest(page: Int, size: Int, callback: (MyFundingListByContestResponse) -> Unit) {
        CrowdFundingRepository.getMyFundingByContest(
            contestId = contestId,
            page = page,
            size = size,
            successCallback = { response ->
                DebugLog.d(response.toString())
                callback(response ?: return@getMyFundingByContest)
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, MyFundingByContestResponse>) {
        getMyFundingByContest(FIRST_PAGE, PAGE_SIZE) {
            callback.onResult(it.content, null, FIRST_PAGE + 1)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MyFundingByContestResponse>) {
        getMyFundingByContest(params.key, PAGE_SIZE) {
            callback.onResult(it.content, if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, MyFundingByContestResponse>) {
        getMyFundingByContest(params.key, PAGE_SIZE) {
            callback.onResult(it.content, if (params.key > FIRST_PAGE) params.key - 1 else null)
        }
    }

    companion object {
        const val PAGE_SIZE = 10
        const val FIRST_PAGE = 0
    }
}