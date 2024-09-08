package kr.co.bigwalk.app.data.story.repository

import kr.co.bigwalk.app.data.story.dto.CampaignReservationRequest

object StoryLocalDataSource : StoryDataSource {

    override fun getStories(page: Int, size: Int, getStoriesCallback: StoryDataSource.GetStoriesCallback) {}

    override fun getStory(campaignId: Long, getStoryCallback: StoryDataSource.GetStoryCallback) {}

    override fun getStoriesForReady(page: Int, size: Int, callback: StoryDataSource.GetStoriesForReadyCallback) {}

    override fun reserveCampaign(request: CampaignReservationRequest, callback: StoryDataSource.ReserveCampaignCallback) {}

    override fun getMyReservedStories(page: Int, size: Int, callback: StoryDataSource.GetMyReservedStoriesCallback) {}

    override fun getMyReservedStoriesCount(callback: StoryDataSource.GetMyReservedStoriesCountCallback) {}

}