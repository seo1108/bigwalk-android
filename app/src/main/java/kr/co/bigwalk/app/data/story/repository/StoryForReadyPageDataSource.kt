package kr.co.bigwalk.app.data.story.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.story.dto.Story
import kr.co.bigwalk.app.exception.StoryException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class StoryForReadyPageDataSource : PageKeyedDataSource<Int, Story>() {

    companion object {
        const val PAGE_SIZE = 40
        const val FIRST_PAGE = 0
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Story>) {
        DebugLog.d("loadInitial")
        StoryRepository.getStoriesForReady(
            FIRST_PAGE,
            PAGE_SIZE, object : StoryDataSource.GetStoriesForReadyCallback {
            override fun onSuccess(storiesForReady: List<Story>) {
                callback.onResult(storiesForReady, null, FIRST_PAGE + 1)
            }

            override fun onFailed(reason: String) {
                showToast("공개 예정 포스트를 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(StoryException(reason))
            }

        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Story>) {
        DebugLog.d("loadAfter")
        StoryRepository.getStoriesForReady(params.key, params.requestedLoadSize, object : StoryDataSource.GetStoriesForReadyCallback {
            override fun onSuccess(storiesForReady: List<Story>) {
                callback.onResult(storiesForReady, if (params.key < PAGE_SIZE) null else params.key + 1)
            }

            override fun onFailed(reason: String) {
                showToast("공개 예정 포스트를 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(StoryException(reason))
            }

        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Story>) {
        DebugLog.d("loadBefore")
        val keyPage = params.key
        StoryRepository.getStoriesForReady(keyPage, params.requestedLoadSize, object : StoryDataSource.GetStoriesForReadyCallback {
            override fun onSuccess(storiesForReady: List<Story>) {
                callback.onResult(storiesForReady, if(params.key > FIRST_PAGE) params.key -1 else null)
            }

            override fun onFailed(reason: String) {
                showToast("공개 예정 포스트를 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(StoryException(reason))
            }

        })
    }
}