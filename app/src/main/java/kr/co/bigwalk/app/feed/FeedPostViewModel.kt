package kr.co.bigwalk.app.feed

import android.content.Intent
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.feed.dto.Feed
import kr.co.bigwalk.app.data.feed.dto.FeedListResponse
import kr.co.bigwalk.app.data.feed.repository.FeedDataSource
import kr.co.bigwalk.app.data.feed.repository.FeedRepository
import kr.co.bigwalk.app.feed.category.FeedByCategoryFragment
import kr.co.bigwalk.app.feed.like.FeedLikeActivity
import kr.co.bigwalk.app.feed_mypage.FeedMyPageActivity
import kr.co.bigwalk.app.util.DebugLog

open class FeedPostViewModel(
    private val navigator: BaseNavigator?
) : ViewModel() {
    val isLoadFinish: ObservableField<Boolean> = ObservableField(false)

    var feedList: ObservableList<Feed> = ObservableArrayList()
    var isFeedExist: ObservableField<Boolean> = ObservableField(false)

    val loadMoreOneItem: MutableLiveData<Boolean> = MutableLiveData()

    var pagerPos = 0
    val data = MutableLiveData<Long>()
    val clickMoreButton = MutableLiveData<Feed>()
    val position = MutableLiveData<Int>()


    fun requestLikeFeed(actionDonationHistoryId: Long, likeToChange: Boolean, callback: FeedDataSource.LikeFeedCallback) {
        if (likeToChange) FeedRepository.likeFeed(actionDonationHistoryId, callback)
        else FeedRepository.unlikeFeed(actionDonationHistoryId, callback)
    }

    /*fun requestFeedDelete(actionDonationHistoryId: Long, pos: Int) {
        FeedRepository.deleteFeed(actionDonationHistoryId, object : FeedDataSource.DeleteFeedCallback {
            override fun onSuccess() {
                FeedManager.removeFeed.value = actionDonationHistoryId
            }

            override fun onFailed(reason: String) {
                DebugLog.d("delete feed request failed: $reason")
            }
        })
    }*/

    fun requestFeedDelete(actionDonationHistoryId: Long) {
        FeedRepository.deleteFeed(actionDonationHistoryId, object : FeedDataSource.DeleteFeedCallback {
            override fun onSuccess() {
                FeedManager.removeFeed.value = actionDonationHistoryId

                PreferenceManager.saveFeedInfo(actionDonationHistoryId, 0, false, 0, "", true)
            }

            override fun onFailed(reason: String) {
                DebugLog.d("delete feed request failed: $reason")
            }
        })
    }

    fun moveToFeedLike(actionDonationHistoryId: Long, isLikeMe: Boolean, isPublic: Boolean) {
        if (navigator == null) return
        val intent = Intent(navigator.getContext(), FeedLikeActivity::class.java)
        intent.putExtra(FeedLikeActivity.ACTION_DONATION_HISTORY_ID, actionDonationHistoryId)
        intent.putExtra(FeedLikeActivity.IS_LIKE_ME, isLikeMe)
        intent.putExtra(FeedLikeActivity.IS_PUBLIC, isPublic)
        navigator.getContext().startActivity(intent)
    }

    fun moveToMyPage(organizationId: Long, campaignId: Long, userId: Long, userName: String, isMine: Boolean, pos: Int) {
        if (navigator == null) return
        val intent = Intent(navigator.getContext(), FeedMyPageActivity::class.java).apply {
            putExtra(FeedMyPageActivity.EXTRA_ORGANIZATION_ID, organizationId)
            putExtra(FeedMyPageActivity.EXTRA_CAMPAIGN_ID, campaignId)
            putExtra(FeedMyPageActivity.EXTRA_USER_ID, userId)
            putExtra(FeedMyPageActivity.EXTRA_USER_NAME, userName)
            putExtra(FeedMyPageActivity.EXTRA_IS_MIND, isMine)
        }
        navigator.getContext().startActivity(intent)
    }

    fun showDialogAction(feed: Feed) {
        clickMoreButton.value = feed
    }















    fun requestHotFeedList(challengeId: Long, id: Long, department: Long?, page: Int, size: Int) {
        DebugLog.d("requestHitFeedList : $page $size")
        FeedByCategoryFragment.getDataFinished.value = false
        FeedRepository.getHotFeedListByChallenge(challengeId, id, department, page, size, object : FeedDataSource.GetHotFeedListByChallengeCallback {
            override fun onSuccess(response: FeedListResponse) {
                if (page > 0 && response.feeds.isNotEmpty() && response.feeds.size == 1) {
                    loadMoreOneItem.value = false
                    FeedByCategoryFragment.SCROLL_TO_POSITION_LOADED = true
                }

                //if (page == 0 && response.feeds.isNotEmpty()) isFeedExist.set(true)
                if (response.feeds.isNotEmpty()) isFeedExist.set(true)
                sortList(response)
            }

            override fun onFailed(reason: String) {
                DebugLog.d("requestHitFeedList failed: $reason")
                FeedByCategoryFragment.isLoading = false
            }
        })
    }

    fun requestRecentFeedList(challengeId: Long, id: Long, department: Long?, page: Int, size: Int) {
        FeedByCategoryFragment.getDataFinished.value = false
        FeedRepository.getRecentFeedListByChallenge(challengeId, id, department, page, size, object : FeedDataSource.GetRecentFeedListByChallengeCallback {
            override fun onSuccess(response: FeedListResponse) {
                if (page > 0 && response.feeds.isNotEmpty() && response.feeds.size == 1) {
                    loadMoreOneItem.value = false
                    FeedByCategoryFragment.SCROLL_TO_POSITION_LOADED = true
                }

                //if (page == 0 && response.feeds.isNotEmpty()) isFeedExist.set(true)
                if (response.feeds.isNotEmpty()) isFeedExist.set(true)
                sortList(response)


            }

            override fun onFailed(reason: String) {
                DebugLog.d("requestHitFeedList failed: $reason")

                FeedByCategoryFragment.isLoading = false
            }
        })
    }

    fun requestMyFeedList(challengeId: Long, id: Long, department: Long?, page: Int, size: Int) {
        FeedByCategoryFragment.getDataFinished.value = false
        FeedRepository.getMyFeedListByChallenge(challengeId, id, department, page, size, object : FeedDataSource.GetMyFeedListByChallengeCallback {
            override fun onSuccess(response: FeedListResponse) {
                //if (page == 0 && response.feeds.isNotEmpty()) isFeedExist.set(true)
                if (page > 0 && response.feeds.isNotEmpty() && response.feeds.size == 1) {
                    loadMoreOneItem.value = false
                    FeedByCategoryFragment.SCROLL_TO_POSITION_LOADED = true
                }

                if (response.feeds.isNotEmpty()) isFeedExist.set(true)
                sortList(response)


            }

            override fun onFailed(reason: String) {
                DebugLog.d("requestHitFeedList failed: $reason")

                FeedByCategoryFragment.isLoading = false
            }
        })
    }

    fun sortList(response: FeedListResponse) {
        if (response.feeds.isEmpty()) {
            isLoadFinish.set(true)
            FeedByCategoryFragment.isLoading = false
        }

        if (FeedByCategoryFragment.appendDirection == "bottom" && response.feeds.isEmpty()) {
            FeedByCategoryFragment.isFinalDataLoad = true
        }

        if (FeedByCategoryFragment.appendDirection == "top") {
            response.feeds.forEachIndexed { idx, it ->
                feedList.add(idx, it)
            }
        } else if (FeedByCategoryFragment.appendDirection == "bottom") {
            feedList.addAll(response.feeds)
        }


        FeedByCategoryFragment.isLoading = false
        FeedByCategoryFragment.getDataFinished.value = true
        FeedByCategoryFragment.totalCount = response.totalCount
    }
}