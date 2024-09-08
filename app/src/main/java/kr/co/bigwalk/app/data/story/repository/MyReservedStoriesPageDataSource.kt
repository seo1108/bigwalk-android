package kr.co.bigwalk.app.data.story.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.story.dto.ResponseStory
import kr.co.bigwalk.app.exception.StoryException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class MyReservedStoriesPageDataSource : PageKeyedDataSource<Int, ResponseStory>() {

    companion object {
        const val PAGE_SIZE = 20
        const val FIRST_PAGE = 0
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ResponseStory>) {
        StoryRepository.getMyReservedStories(FIRST_PAGE, PAGE_SIZE,
            object : StoryDataSource.GetMyReservedStoriesCallback {
                override fun onSuccess(reservedStories: List<ResponseStory>) {
                    callback.onResult(reservedStories, null, FIRST_PAGE + 1)
                }

                override fun onFailed(reason: String) {
                    showToast("내가 예약한 포스트를 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(StoryException(reason))
                }

            }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ResponseStory>) {
        StoryRepository.getMyReservedStories(params.key, params.requestedLoadSize,
            object : StoryDataSource.GetMyReservedStoriesCallback {
                override fun onSuccess(reservedStories: List<ResponseStory>) {
                    callback.onResult(reservedStories, if (params.key < PAGE_SIZE) null else params.key + 1)
                }

                override fun onFailed(reason: String) {
                    showToast("내가 예약한 포스트를 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(StoryException(reason))
                }

            }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ResponseStory>) {
        val keyPage = params.key
        StoryRepository.getMyReservedStories(keyPage, params.requestedLoadSize,
            object : StoryDataSource.GetMyReservedStoriesCallback {
                override fun onSuccess(reservedStories: List<ResponseStory>) {
                    callback.onResult(reservedStories, if(params.key > FIRST_PAGE) params.key -1 else null)
                }

                override fun onFailed(reason: String) {
                    showToast("내가 예약한 포스트를 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(StoryException(reason))
                }

            }
        )
    }
}