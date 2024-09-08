package kr.co.bigwalk.app.crowd_funding.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.crowd_funding.dto.ContentResponse
import kr.co.bigwalk.app.data.crowd_funding.repository.CommentByCrewCampaignDataSourceFactory
import kr.co.bigwalk.app.data.crowd_funding.repository.CrowdFundingRepository
import kr.co.bigwalk.app.data.funding.dto.AddCommentCrewCampaignRequest
import kr.co.bigwalk.app.util.DebugLog

class StepFundingCommentsViewModel(private val crewCampaignId: Long) : ViewModel() {
    val factory = CommentByCrewCampaignDataSourceFactory(
        crewCampaignId = crewCampaignId,
        commentCountCallback = {
            commentCount.value = it
        })

    val list: LiveData<PagedList<ContentResponse>> by lazy {
        LivePagedListBuilder(
            factory,
            CommentByCrewCampaignDataSourceFactory.pagedListConfig()
        ).build()
    }

    val profilePath: LiveData<String?> = MutableLiveData(PreferenceManager.getProfilePath())
    val commentCount = MutableLiveData<Int>(0)

    fun addCommentCrewCampaign(comment: String) {
        CrowdFundingRepository.addCommentCrewCampaign(
            crewCampaignId = crewCampaignId,
            request = AddCommentCrewCampaignRequest(
                comment = comment,
                parentId = null
            ),
            successCallback = {
                factory.rankingLiveDataSource.value?.invalidate()
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    fun deleteCommentCrewCampaign(commentId: Long) {
        CrowdFundingRepository.deleteCommentCrewCampaign(
            crewCampaignId = crewCampaignId,
            commentId = commentId,
            successCallback = {
                factory.rankingLiveDataSource.value?.invalidate()
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }
}