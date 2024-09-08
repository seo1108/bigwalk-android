package kr.co.bigwalk.app.data.story.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.story.dto.ResponseStory
import kr.co.bigwalk.app.exception.StoryException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class StoryPageDataSource : PageKeyedDataSource<Int, ResponseStory>() {

    companion object {
        const val PAGE_SIZE = 20
        const val FIRST_PAGE = 0
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ResponseStory>) {
        DebugLog.d("loadInitial")
        StoryRepository.getStories(FIRST_PAGE, PAGE_SIZE,
            object : StoryDataSource.GetStoriesCallback {
                override fun onSuccess(postedStories: List<ResponseStory>) {
                    DebugLog.d("스토리 전체: $postedStories")
                    callback.onResult(postedStories, null, FIRST_PAGE + 1)
                }

                override fun onFailed(reason: String) {
                    showToast("스토리 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(StoryException(reason))
                }
            }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ResponseStory>) {
        DebugLog.d("loadAfter")
        StoryRepository.getStories(params.key, params.requestedLoadSize,
            object : StoryDataSource.GetStoriesCallback {
                override fun onSuccess(postedStories: List<ResponseStory>) {
                    callback.onResult(postedStories, if (params.key < PAGE_SIZE) null else params.key + 1)
                }

                override fun onFailed(reason: String) {
                    showToast("스토리 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(StoryException(reason))
                }
            }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ResponseStory>) {
        DebugLog.d("loadBefore")
        val keyPage = params.key
        StoryRepository.getStories(keyPage, params.requestedLoadSize,
            object : StoryDataSource.GetStoriesCallback {
                override fun onSuccess(postedStories: List<ResponseStory>) {
                    callback.onResult(postedStories, if (params.key > FIRST_PAGE) params.key - 1 else null)
                }

                override fun onFailed(reason: String) {
                    showToast("스토리 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(StoryException(reason))
                }
            }
        )
    }
}