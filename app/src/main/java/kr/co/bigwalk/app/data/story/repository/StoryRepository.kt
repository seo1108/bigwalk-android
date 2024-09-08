package kr.co.bigwalk.app.data.story.repository

import kr.co.bigwalk.app.BuildConfig
import kr.co.bigwalk.app.data.story.dto.CampaignReservationRequest

object StoryRepository : StoryDataSource {

    private val storyDataSource : StoryDataSource

    init {
        @Suppress("ConstantConditionIf")
        storyDataSource = if (BuildConfig.FLAVOR == "local") StoryLocalDataSource else StoryRemoteDataSource
    }

    override fun getStories(page: Int,
                            size: Int,
                            getStoriesCallback: StoryDataSource.GetStoriesCallback) {
        storyDataSource.getStories(page, size, getStoriesCallback)
    }

    override fun getStory(campaignId: Long, GetStoryCallback: StoryDataSource.GetStoryCallback) {
        storyDataSource.getStory(campaignId, GetStoryCallback)
    }

    override fun getStoriesForReady(
        page: Int,
        size: Int,
        callback: StoryDataSource.GetStoriesForReadyCallback
    ) {
        storyDataSource.getStoriesForReady(page, size, callback)
    }

    override fun reserveCampaign(
        request: CampaignReservationRequest,
        callback: StoryDataSource.ReserveCampaignCallback
    ) {
        storyDataSource.reserveCampaign(request, callback)
    }

    override fun getMyReservedStories(
        page: Int,
        size: Int,
        callback: StoryDataSource.GetMyReservedStoriesCallback
    ) {
        storyDataSource.getMyReservedStories(page, size, callback)
    }

    override fun getMyReservedStoriesCount(callback: StoryDataSource.GetMyReservedStoriesCountCallback) {
        storyDataSource.getMyReservedStoriesCount(callback)
    }

}