package kr.co.bigwalk.app.data.feedComment.repository.inDb

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.PagedList
import androidx.paging.toLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.data.AppDatabase
import kr.co.bigwalk.app.data.Listing
import kr.co.bigwalk.app.data.NetworkState
import kr.co.bigwalk.app.data.feedComment.dto.FeedCommentResponse
import kr.co.bigwalk.app.data.feedComment.repository.FeedCommentDataSource
import kr.co.bigwalk.app.exception.StoryException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class CommentBoundaryRepository(
    private val actionDonationHistoryId: Long
) {

    companion object {
        const val FIRST_PAGE = 0
        const val PAGE_SIZE = 10
    }

    private val callback = object : FeedCommentDataSource.GetFeedCommentCallback {
        override fun onSuccess(comments: List<FeedCommentResponse>) {
            CoroutineScope(Dispatchers.IO).launch  {
                AppDatabase(BigwalkApplication.context!!).runInTransaction {
                    AppDatabase(BigwalkApplication.context!!).commentDao().insertCommentList(comments)
                }
                networkState.postValue(NetworkState.LOADED)
            }
        }
        override fun onFailed(reason: String) {
            showToast("댓글 목록을 불러올 수 없습니다. 다시 시도해주세요.")
            DebugLog.e(StoryException(reason))
            networkState.value = NetworkState.error(reason)
        }
    }

    private val networkState = MutableLiveData<NetworkState>()

    @MainThread
    private fun refresh(): LiveData<NetworkState> {
        networkState.value = NetworkState.LOADING
        CommentBoundaryCallback.requestCommentList(actionDonationHistoryId, FIRST_PAGE, PAGE_SIZE, callback)
        return networkState
    }

    @MainThread
    fun getCommentList(): Listing<FeedCommentResponse> {
        val boundaryCallback = CommentBoundaryCallback(actionDonationHistoryId)

        val refreshTrigger = MutableLiveData<Unit?>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh()
        }

        val config = PagedList.Config.Builder().setPageSize(PAGE_SIZE).setEnablePlaceholders(true).build()

        val livePagedList = AppDatabase(BigwalkApplication.context!!).commentDao().getComment(actionDonationHistoryId).toLiveData(
            config = config,
            boundaryCallback = boundaryCallback)

        return Listing(
            pagedList = livePagedList,
            networkState = boundaryCallback.networkState,
            refresh = {
                refreshTrigger.value = null
            },
            refreshState = refreshState
        )
    }
}