package kr.co.bigwalk.app.data.feed.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class FeedLikePageDataSource(private val actionDonationHistoryId: Long) : PageKeyedDataSource<Int, UserProfileResponse>() {

    companion object {
        const val PAGE_SIZE = 10
        const val FIRST_PAGE = 0
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, UserProfileResponse>) {
            FeedRepository.getLikedUsers(actionDonationHistoryId, FIRST_PAGE, PAGE_SIZE, object : FeedDataSource.LikedUsersCallback {
                override fun onSuccess(users: List<UserProfileResponse>) {
                    callback.onResult(users, null, FIRST_PAGE + 1)
                }

                override fun onFailed(reason: String) {
                    showToast("챌린지 좋아요 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }

            })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, UserProfileResponse>) {
        FeedRepository.getLikedUsers(actionDonationHistoryId, params.key, PAGE_SIZE, object : FeedDataSource.LikedUsersCallback {
            override fun onSuccess(users: List<UserProfileResponse>) {
                callback.onResult(users, if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1)
            }

            override fun onFailed(reason: String) {
                showToast("챌린지 좋아요 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(CampaignException(reason))
            }

        })
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, UserProfileResponse>) {
        val keyPage = params.key
        FeedRepository.getLikedUsers(actionDonationHistoryId, keyPage, PAGE_SIZE, object : FeedDataSource.LikedUsersCallback {
            override fun onSuccess(users: List<UserProfileResponse>) {
                callback.onResult(users, if(params.key > FIRST_PAGE) params.key -1 else null)
            }

            override fun onFailed(reason: String) {
                showToast("챌린지 좋아요 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(CampaignException(reason))
            }

        })
    }
}