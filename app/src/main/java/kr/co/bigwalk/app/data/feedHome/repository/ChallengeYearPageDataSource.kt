package kr.co.bigwalk.app.data.feedHome.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.feedHome.dto.ChallengeYearPagingResponse
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.util.DebugLog

class ChallengeYearPageDataSource(private val type: String, private val year: String) : PageKeyedDataSource<Int, ChallengeYearPagingResponse>() {

    companion object {
        const val PAGE_SIZE = 10
        const val FIRST_PAGE = 0
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ChallengeYearPagingResponse>) {
        FeedHomeRepository.getChallengePerYear(type, year, FIRST_PAGE, PAGE_SIZE,
            object : FeedHomeDataSource.GetChallengeYearCallback {
                override fun onSuccess(challengeInfo: ChallengeYearPagingResponse) {
                    /*callback.onResult(challengeInfo, null, FIRST_PAGE + 1)*/
                }

                override fun onFailed(reason: String) {
                    DebugLog.e(CampaignException(reason))
                }
            }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ChallengeYearPagingResponse>) {
        FeedHomeRepository.getChallengePerYear(type, year, params.key, PAGE_SIZE,
            object : FeedHomeDataSource.GetChallengeYearCallback {
                override fun onSuccess(challengeInfo: ChallengeYearPagingResponse) {
                    /*callback.onResult(
                        challengeInfo,
                        if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1
                    )*/
                }

                override fun onFailed(reason: String) {
                    DebugLog.e(CampaignException(reason))
                }
            }
        )
    }


    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ChallengeYearPagingResponse>) {
        val keyPage = params.key
        FeedHomeRepository.getChallengePerYear(type, year, keyPage, PAGE_SIZE,
            object : FeedHomeDataSource.GetChallengeYearCallback {
                override fun onSuccess(challengeInfo: ChallengeYearPagingResponse) {
                    /*callback.onResult(
                        challengeInfo,
                        if (params.key > FIRST_PAGE) params.key - 1 else null
                    )*/
                }

                override fun onFailed(reason: String) {
                    DebugLog.e(CampaignException(reason))
                }
            }
        )
    }
}