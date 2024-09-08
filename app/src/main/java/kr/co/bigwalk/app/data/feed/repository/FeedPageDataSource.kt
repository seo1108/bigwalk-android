package kr.co.bigwalk.app.data.feed.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.feed.dto.Feed
import kr.co.bigwalk.app.data.feed.dto.FeedListResponse
import kr.co.bigwalk.app.exception.StoryException
import kr.co.bigwalk.app.feed.FeedViewModel
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class FeedPageDataSource(
    val campaignId: Long,
    val type: FeedViewModel.FeedCategoryType,
    val departmentId: Long?,
    val actionId: Long?,
    val userId: Long
) : PageKeyedDataSource<Int, Feed>() {

    companion object {
        const val PAGE_SIZE = 10
        const val FIRST_PAGE = 0
    }

    private fun requestFeedList(
        campaignId: Long,
        type: FeedViewModel.FeedCategoryType,
        departmentId: Long?,
        actionId: Long?,
        userId: Long,
        page: Int, size: Int, callback: FeedDataSource.GetFeedListCallback) {
        when (type) {
            FeedViewModel.FeedCategoryType.ALL -> {
                if (campaignId > 0) FeedRepository.getFeedList(campaignId, null, null, page, size, callback)
                else FeedRepository.getFeedList(page, size, callback)
            }
            FeedViewModel.FeedCategoryType.ME ->
                FeedRepository.getMyFeedList(campaignId, page, size, callback)
            FeedViewModel.FeedCategoryType.DEPARTMENT ->
                FeedRepository.getFeedList(campaignId, departmentId, actionId, page, size, callback)
            FeedViewModel.FeedCategoryType.USER ->
                FeedRepository.getUserFeedList(campaignId, userId, page, size, callback)
        }
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Feed>) {
        DebugLog.d("loadInitial")
        requestFeedList(campaignId, type, departmentId, actionId, userId, FIRST_PAGE, PAGE_SIZE,
            object : FeedDataSource.GetFeedListCallback {
                override fun onSuccess(response: FeedListResponse) {
                    DebugLog.d("피드 전체: $response")
                    callback.onResult(response.feeds, null, FIRST_PAGE + 1)
                }

                override fun onFailed(reason: String) {
                    showToast("피드 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(StoryException(reason))
                }
            }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Feed>) {
        DebugLog.d("loadAfter")
        requestFeedList(campaignId, type, departmentId, actionId, userId, params.key, params.requestedLoadSize,
            object : FeedDataSource.GetFeedListCallback {
                override fun onSuccess(response: FeedListResponse) {
                    callback.onResult(response.feeds, if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1)
                }

                override fun onFailed(reason: String) {
                    showToast("피드 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(StoryException(reason))
                }
            }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Feed>) {
        DebugLog.d("loadBefore")
        requestFeedList(campaignId, type, departmentId, actionId, userId, params.key, params.requestedLoadSize,
            object : FeedDataSource.GetFeedListCallback {
                override fun onSuccess(response: FeedListResponse) {
                    callback.onResult(response.feeds, if (params.key > FIRST_PAGE) params.key - 1 else null)
                }

                override fun onFailed(reason: String) {
                    showToast("피드 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(StoryException(reason))
                }
            }
        )
    }
}