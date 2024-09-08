package kr.co.bigwalk.app.feed_mypage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.blame.dto.UserBlockResponse
import kr.co.bigwalk.app.data.blame.repository.UserBlockListDataSourceFactory
import kr.co.bigwalk.app.feed.FeedManager
import kr.co.bigwalk.app.util.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserBlockListViewModel : ViewModel() {
    private val factory = UserBlockListDataSourceFactory()

    val emptyList = MutableLiveData<Unit>()

    val userBlockList: LiveData<PagedList<UserBlockResponse>> by lazy {
        LivePagedListBuilder(
            factory,
            UserBlockListDataSourceFactory.pagedListConfig()
        )
            .setBoundaryCallback(object : PagedList.BoundaryCallback<UserBlockResponse>() {
                override fun onZeroItemsLoaded() {
                    super.onZeroItemsLoaded()
                    emptyList.value = Unit
                }
            })
            .build()
    }

    fun unblockUser(userId: Long) {
        RemoteApiManager.getBlameApi().unblockUsers(userId)
            .enqueue(object : Callback<BaseResponse<Nothing>> {
                override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                    when (response.body()?.result) {
                        Result.SUCCESS -> {
                            response.body()?.message?.let {
                                showToast(it)
                                userBlockList.value?.dataSource?.invalidate()
                                FeedManager.dimFeed.value = -1L
                                FeedManager.blockUserId.value = -2L
                            }
                        }
                        Result.FAIL -> {
                            response.body()?.message?.let { showToast(it) }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                    showToast(t.localizedMessage)
                }

            })
    }

}