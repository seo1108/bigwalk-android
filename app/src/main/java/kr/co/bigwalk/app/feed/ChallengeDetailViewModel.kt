package kr.co.bigwalk.app.feed

import android.content.Intent
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.feed.dto.*
import kr.co.bigwalk.app.data.feed.repository.FeedDataSource
import kr.co.bigwalk.app.data.feed.repository.FeedRepository
import kr.co.bigwalk.app.feed_mypage.FeedMyPageActivity
import kr.co.bigwalk.app.feed_notification.FeedNotificationActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class ChallengeDetailViewModel(
    private val navigator: BaseNavigator?
) : BaseObservable() {

    var challengeDetail: ObservableField<ChallengeDetailResponse> = ObservableField()
    var selectedFeedType: ObservableField<String> = ObservableField()
    var isPopular: ObservableField<Boolean> = ObservableField(false)
    var isRecent: ObservableField<Boolean> = ObservableField(false)
    var isMy: ObservableField<Boolean> = ObservableField(false)
    var challengeImageList: ObservableList<ChallengeImageItem> = ObservableArrayList()

    var isOrganizationVisible: ObservableField<Boolean> = ObservableField(false)
    var isLoadFinish: ObservableField<Boolean> = ObservableField(false)

    var feedList: ObservableList<Feed> = ObservableArrayList()

    var challengeId: ObservableField<Long> = ObservableField()
    var imageId: ObservableField<Long> = ObservableField()
    var currentPage: ObservableField<Int> = ObservableField()
    var size: ObservableField<Int> = ObservableField()
//    var campaignId: ObservableField<Long> = ObservableField()

    fun requestChallengeDetail(challengeId: Long) {
        FeedRepository.getChallengeDetail(challengeId, object : FeedDataSource.GetChallengeDetailCallback {
            override fun onSuccess(challengeResponse: ChallengeDetailResponse) {
                challengeResponse.startDate!!.split("T")[0].also { challengeResponse.startDate = it }
                challengeResponse.endDate!!.split("T")[0].also { challengeResponse.endDate = it }
                challengeDetail.set(challengeResponse)

                DebugLog.d("challengeDetail Response $challengeId $challengeResponse")

                if (isOrganization()) isOrganizationVisible.set(true)

 //               organizationId.set(challengeResponse.organizationId)
   //             campaignId.set(challengeResponse.campaignId)
            }

            override fun onFailed(reason: String) {
                DebugLog.d("requestChallengeDetail failed: $reason")
            }
        })
    }

    fun requestChallengeImage(challengeId: Long, type: String, page: Int, size: Int) {
        FeedRepository.getChallengeImage(challengeId, type, page, size, object : FeedDataSource.GetChallengeImageCallback {
            override fun onSuccess(response: ChallengeImageResponse) {
                if (page == 0) {
                    isLoadFinish.set(false)
                    //challengeImageList.clear()
                }

                if (response.content!!.isEmpty()) {
                    isLoadFinish.set(true)
                } else {
                    response.content?.let {
                        challengeImageList.addAll(it)
                    }
                }

                PreferenceManager.saveFeedTotalPage(response.totalPages)
                PreferenceManager.saveFeedTotalCount(response.totalElements)

            }

            override fun onFailed(reason: String) {
                DebugLog.d("requestChallengeImage failed: $reason")
            }
        })
    }

    fun requestHotFeedList(challengeId: Long, id: Long, department: Long?, page: Int, size: Int) {
        FeedRepository.getHotFeedListByChallenge(challengeId, id, department, page, size, object : FeedDataSource.GetHotFeedListByChallengeCallback {
            override fun onSuccess(response: FeedListResponse) {
                DebugLog.d("requestFeedList : $response")
                feedList.addAll(response.feeds)
            }

            override fun onFailed(reason: String) {
                DebugLog.d("requestFeedList failed: $reason")
            }
        })
    }

    fun goFeedNotification() {
        val intent = Intent(navigator!!.getContext(), FeedNotificationActivity::class.java).apply {
            DebugLog.d("goCampaignDetail ${challengeDetail.get()!!.campaignId}" )
            putExtra(FeedNotificationActivity.KEY_CAMPAIGN_ID, challengeDetail.get()!!.campaignId)
        }
        navigator!!.getContext().startActivity(intent)
    }

    fun goFeedMyPage() {
        val name = PreferenceManager.getName()

        val intent = Intent(navigator!!.getContext(), FeedMyPageActivity::class.java).apply {
            DebugLog.d("goCampaignDetail ${challengeDetail.get()!!.campaignId}" )
            putExtra(FeedMyPageActivity.EXTRA_ORGANIZATION_ID, challengeDetail.get()!!.organizationId)
            putExtra(FeedMyPageActivity.EXTRA_CAMPAIGN_ID, challengeDetail.get()!!.campaignId)
            putExtra(FeedMyPageActivity.EXTRA_USER_NAME, name)
            putExtra(FeedMyPageActivity.EXTRA_IS_MIND, true)
        }
        navigator!!.getContext().startActivity(intent)
    }



    fun goFeedDetail() {

        val isEnableLike = challengeDetail.get()!!.organizationId?.let { isEnableLike(it) }

        var sortType = ""
        if (isPopular.get() == true) {
            sortType = "hot"
        }
        if (isRecent.get() == true) {
            sortType = "recent"
        } else if (isMy.get() == true) {
            sortType = "my"
        }

        DebugLog.d("department=0_________sortType $sortType")



/*        val intent = Intent(navigator!!.getContext(), ChallengeFeedListActivity::class.java).apply {
            putExtra(ChallengeFeedListActivity.CAMPAIGN_ID, challengeDetail.get()!!.campaignId)
            putExtra(ChallengeFeedListActivity.ORGANIZATION_ID, challengeDetail.get()!!.organizationId)
            putExtra(ChallengeFeedListActivity.IS_ENABLE_LIKE, isEnableLike)
        }*/

        val intent = Intent(navigator!!.getContext(), FeedDetailActivity::class.java).apply {
            putExtra(FeedDetailActivity.KEY_CAMPAIGN_ID, challengeDetail.get()!!.campaignId)
            putExtra(FeedDetailActivity.KEY_ORGANIZATION_ID, challengeDetail.get()!!.organizationId)
            putExtra(FeedDetailActivity.KEY_SORT_TYPE, sortType)
        }
        navigator!!.getContext().startActivity(intent)
    }

    fun isOrganization() : Boolean {
        DebugLog.d("isOrganizationNotice ${challengeDetail.get()!!.organizationId}")

        val prefOrganizationId = PreferenceManager.getOrganization()
        return if (challengeDetail.get()!!.organizationId == null || challengeDetail.get()!!.organizationId!! <= 0) {
            true
        } else prefOrganizationId != -1L && prefOrganizationId == challengeDetail.get()!!.organizationId

    }


}