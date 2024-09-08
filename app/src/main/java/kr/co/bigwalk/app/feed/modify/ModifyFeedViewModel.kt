package kr.co.bigwalk.app.feed.modify

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.feed.dto.ModifyFeedRequest
import kr.co.bigwalk.app.util.Event
import kr.co.bigwalk.app.util.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModifyFeedViewModel: ViewModel() {

    private val _successEvent = MutableLiveData<Event<Unit>>()
    val successEvent: LiveData<Event<Unit>> get() = _successEvent

    fun modifyFeed(feedId: Long, comment: String) {
        RemoteApiManager.getService().modifyFeed(
            feedId = feedId,
            request = ModifyFeedRequest(
                comment = comment
            )
        )
            .enqueue(object : Callback<BaseResponse<Nothing>> {
                override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                    when (response.body()?.result) {
                        Result.SUCCESS -> {
                            PreferenceManager.saveFeedComment(feedId, comment)
                            _successEvent.value = Event(Unit)

                        }
                        else -> {
                            response.errorBody()?.let { errorBody ->
                                showToast(RemoteApiManager.getErrorResponse(errorBody).message)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                    showToast(t.localizedMessage)
                }

            })
    }
}