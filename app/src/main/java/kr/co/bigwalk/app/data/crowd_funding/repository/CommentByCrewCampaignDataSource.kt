package kr.co.bigwalk.app.data.crowd_funding.repository

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.crowd_funding.dto.CommentByCrewCampaignResponse
import kr.co.bigwalk.app.data.crowd_funding.dto.ContentResponse
import kr.co.bigwalk.app.util.DebugLog

class CommentByCrewCampaignDataSource(
    private val crewCampaignId: Long,
    private val commentCountCallback: (Int) -> Unit
) : PageKeyedDataSource<Int, ContentResponse>() {

    private fun getCommentByCrewCampaign(page: Int, size: Int, callback: (CommentByCrewCampaignResponse) -> Unit) {
        CrowdFundingRepository.getCommentByCrewCampaign(
            crewCampaignId = crewCampaignId,
            page = page,
            size = size,
            sort = "createdTime,desc",
            successCallback = { response ->
                callback(response ?: return@getCommentByCrewCampaign)
                if (!response.content.isNullOrEmpty()) {
                    commentCountCallback(response.totalElements)
                } else {
                    if (page <= 0) {
                        commentCountCallback(response.totalElements)
                    }
                }
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ContentResponse>) {
        getCommentByCrewCampaign(FIRST_PAGE, PAGE_SIZE) {
            callback.onResult(it.content, null, FIRST_PAGE + 1)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ContentResponse>) {
        getCommentByCrewCampaign(params.key, PAGE_SIZE) {
            callback.onResult(it.content, if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ContentResponse>) {
        getCommentByCrewCampaign(params.key, PAGE_SIZE) {
            callback.onResult(it.content, if (params.key > FIRST_PAGE) params.key - 1 else null)
        }
    }

    companion object {
        const val PAGE_SIZE = 10
        const val FIRST_PAGE = 0
    }
}