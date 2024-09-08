package kr.co.bigwalk.app.data.blame.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.blame.dto.UserBlockListResponse
import kr.co.bigwalk.app.data.blame.dto.UserBlockResponse
import kr.co.bigwalk.app.util.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UserBlockListDataSource() : PageKeyedDataSource<Int, UserBlockResponse>() {

    private fun fetchUserBlockList(page: Int, size: Int, callback: (UserBlockListResponse) -> Unit) {
        RemoteApiManager.getBlameApi().getUserBlockList(
            page = page,
            size = size
        ).enqueue(object : Callback<BaseResponse<UserBlockListResponse>> {
            override fun onResponse(call: Call<BaseResponse<UserBlockListResponse>>, response: Response<BaseResponse<UserBlockListResponse>>) {
                when (response.body()?.result) {
                    Result.SUCCESS -> {
                        response.body()?.data?.let { callback(it) }
                    }
                    Result.FAIL -> {
                        response.body()?.message?.let { showToast(it) }
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse<UserBlockListResponse>>, t: Throwable) {
                showToast(t.localizedMessage)
            }

        })
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, UserBlockResponse>) {
        fetchUserBlockList(FIRST_PAGE, PAGE_SIZE) {
            callback.onResult(it.content, null, FIRST_PAGE + 1)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, UserBlockResponse>) {
        fetchUserBlockList(params.key, PAGE_SIZE) {
            callback.onResult(it.content, if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, UserBlockResponse>) {
        fetchUserBlockList(params.key, PAGE_SIZE) {
            callback.onResult(it.content, if (params.key > FIRST_PAGE) params.key - 1 else null)
        }
    }

    companion object {
        const val PAGE_SIZE = 10
        const val FIRST_PAGE = 0
    }
}