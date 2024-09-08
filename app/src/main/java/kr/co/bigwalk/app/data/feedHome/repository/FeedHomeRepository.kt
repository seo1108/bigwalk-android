package kr.co.bigwalk.app.data.feedHome.repository

object FeedHomeRepository: FeedHomeDataSource {

    private val feedHomeRemoteDataSource = FeedHomeRemoteDataSource

    override fun getMissionCampaigns(
        page: Int,
        size: Int,
        getMissionCampaignCallback: FeedHomeDataSource.GetMissionCampaignCallback
    ) {
        feedHomeRemoteDataSource.getMissionCampaigns(page, size, getMissionCampaignCallback)
    }

    override fun getChallengeHome(callback: FeedHomeDataSource.GetChallengeHomeCallback) {
        feedHomeRemoteDataSource.getChallengeHome(callback)
    }

    override fun getChallengeActiveList(callback: FeedHomeDataSource.GetChallengeActiveListCallback) {
        feedHomeRemoteDataSource.getChallengeActiveList(callback)
    }

    override fun getChallengeTypeList(type: String, callback: FeedHomeDataSource.GetChallengeTypeListCallback) {
        feedHomeRemoteDataSource.getChallengeTypeList(type, callback)
    }

    override fun getChallengePerYear(
        type: String,
        year: String,
        page: Int,
        size: Int,
        callback: FeedHomeDataSource.GetChallengeYearCallback
    ) {
        feedHomeRemoteDataSource.getChallengePerYear(type, year, page, size, callback)
    }
}