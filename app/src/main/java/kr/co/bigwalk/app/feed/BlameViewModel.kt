package kr.co.bigwalk.app.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.blame.BlameType
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.blame.dto.BlameFeedCommentRequest
import kr.co.bigwalk.app.data.feed.repository.FeedDataSource
import kr.co.bigwalk.app.data.feed.repository.FeedRepository
import kr.co.bigwalk.app.util.Event
import kr.co.bigwalk.app.util.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BlameViewModel : ViewModel() {
    private val _successEvent = MutableLiveData<Event<Unit>>()
    val successEvent: LiveData<Event<Unit>> get() = _successEvent

    //init field
    private var contentId: Long = -1
    private var userId: Long = -1
    var blameType: BlameType = BlameType.NONE

    private var blameMessage: Pair<Boolean, String> = Pair(false, "")
    private var isCheckedBlocking: Boolean? = null

    fun initId(contentId: Long, userId: Long, blameType: BlameType) {
        this.contentId = contentId
        this.userId = userId
        this.blameType = blameType
    }

    fun setBlameMsg(isEtc: Boolean, msg: String) {
        this.blameMessage = Pair(isEtc, msg)
    }

    fun setCheckedBlocking(checkState: Boolean) {
        this.isCheckedBlocking = checkState
    }

    private fun validationCheckOfBlame(): Boolean {
        if (blameMessage.first && blameMessage.second.length < 20) {
            showToast("내용 작성 시 20자 이상 작성이 필요합니다.")
            return false
        }
        if (blameMessage.first.not() && blameMessage.second.isEmpty()) {
            showToast("신고 항목을 선택해주세요.")
            return false
        }
        return true
    }

    private fun validationCheckOfUserBlocking(): Boolean {
        return when (isCheckedBlocking) {
            true -> {
                true
            }
            false -> {
                _successEvent.value = Event(Unit)
                false
            }
            else -> {
                showToast("항목을 선택해주세요.")
                false
            }
        }
    }

    fun requestDeclare() {
        if (validationCheckOfBlame()) {
            when (blameType) {
                BlameType.FEED -> {
                    requestFeedDeclare()
                }
                BlameType.FEED_COMMENT -> {
                    requestFeedCommentDeclare()
                }
                BlameType.FUNDING_COMMENT -> {
                    requestFundingCommentDeclare()
                }
                BlameType.USER -> {
                    requestUserDeclare()
                }
                BlameType.NONE -> {

                }
            }
        }
    }

    private fun requestFeedDeclare() {
        FeedRepository.declareFeed(contentId, blameMessage.second, object : FeedDataSource.DeclareFeedCallback {
            override fun onSuccess(response: BaseResponse<Nothing>) {
                showToast(response.message.orEmpty())
                _successEvent.value = Event(Unit)
            }

            override fun onFailed(reason: String) {
                showToast(reason)
                _successEvent.value = Event(Unit)
            }
        })
    }

    private fun requestFeedCommentDeclare() {
        RemoteApiManager.getBlameApi().blameFeedComment(BlameFeedCommentRequest(contentId, blameMessage.second))
            .enqueue(object : Callback<BaseResponse<Nothing>> {
                override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                    when (response.body()?.result) {
                        Result.SUCCESS -> {
                            showToast(response.body()?.message.orEmpty())
                            _successEvent.value = Event(Unit)
                        }
                        Result.FAIL -> {
                            showToast(response.body()?.message.orEmpty())
                            _successEvent.value = Event(Unit)
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                    showToast(t.localizedMessage)
                }
            })
    }

    private fun requestFundingCommentDeclare() {
        RemoteApiManager.getBlameApi().blameFundingComment(BlameFeedCommentRequest(contentId, blameMessage.second))
            .enqueue(object : Callback<BaseResponse<Nothing>> {
                override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                    when (response.body()?.result) {
                        Result.SUCCESS -> {
                            showToast(response.body()?.message.orEmpty())
                            _successEvent.value = Event(Unit)
                        }
                        Result.FAIL -> {
                            showToast(response.body()?.message.orEmpty())
                            _successEvent.value = Event(Unit)
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                    showToast(t.localizedMessage)
                }
            })
    }

    private fun requestUserDeclare() {
        RemoteApiManager.getBlameApi().blameUser(BlameFeedCommentRequest(userId, blameMessage.second))
            .enqueue(object : Callback<BaseResponse<Nothing>> {
                override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                    when (response.body()?.result) {
                        Result.SUCCESS -> {
                            showToast(response.body()?.message.orEmpty())
                            _successEvent.value = Event(Unit)
                        }
                        Result.FAIL -> {
                            showToast(response.body()?.message.orEmpty())
                            _successEvent.value = Event(Unit)
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                    showToast(t.localizedMessage)
                }
            })
    }

    fun requestUserBlocking() {
        if (validationCheckOfUserBlocking()) {
            RemoteApiManager.getBlameApi().userBlocking(userId).enqueue(object : Callback<BaseResponse<Nothing>> {
                override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                    when (response.body()?.result) {
                        Result.SUCCESS -> {
                            showToast("알려주셔서 감사힙니다.")
                            when (blameType) {
                                BlameType.USER -> {
                                    FeedManager.blockUserId.value = userId
                                }
                                else -> {
                                    FeedManager.dimFeed.value = contentId
                                }
                            }
                            _successEvent.value = Event(Unit)
                        }
                        Result.FAIL -> {
                            showToast(response.message())
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                    showToast(t.localizedMessage)
                }

            })
        }
    }
}