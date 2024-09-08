package kr.co.bigwalk.app.data.story.repository

import kr.co.bigwalk.app.data.story.dto.CampaignReservationRequest
import kr.co.bigwalk.app.data.story.dto.ResponseOpenStory
import kr.co.bigwalk.app.data.story.dto.ResponseStory
import kr.co.bigwalk.app.data.story.dto.Story

interface StoryDataSource {
    interface GetStoriesCallback {
        fun onSuccess(postedStories : List<ResponseStory>)
        fun onFailed(reason: String)
    }

    fun getStories(page: Int, size: Int, getStoriesCallback: GetStoriesCallback)

    interface GetStoryCallback {
        fun onSuccess(story : ResponseStory)
        fun onFailed(reason: String)
    }

    fun getStory(campaignId: Long, getStoryCallback: GetStoryCallback)

    interface GetStoriesForReadyCallback {
        fun onSuccess(storiesForReady: List<Story>)
        fun onFailed(reason: String)
    }

    fun getStoriesForReady(page: Int, size: Int, callback: GetStoriesForReadyCallback)

    interface ReserveCampaignCallback {
        fun onSuccess()
        fun onFailed(reason: String)
    }

    fun reserveCampaign(request: CampaignReservationRequest, callback: ReserveCampaignCallback)

    interface GetMyReservedStoriesCallback {
        fun onSuccess(reservedStories: List<ResponseStory>)
        fun onFailed(reason: String)
    }

    fun getMyReservedStories(page: Int, size: Int, callback: GetMyReservedStoriesCallback)

    interface GetMyReservedStoriesCountCallback {
        fun onSuccess(responseOpenStory: ResponseOpenStory)
        fun onFailed(reason: String)
    }

    fun getMyReservedStoriesCount(callback: GetMyReservedStoriesCountCallback)
}