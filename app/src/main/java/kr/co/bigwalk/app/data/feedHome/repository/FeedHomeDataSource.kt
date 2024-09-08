package kr.co.bigwalk.app.data.feedHome.repository

import kr.co.bigwalk.app.data.feedHome.dto.*

interface FeedHomeDataSource {

    interface GetMissionCampaignCallback {
        fun onSuccess(missionCampaignResponses: List<MissionCampaignResponse>)
        fun onFailed(reason: String)
    }
    fun getMissionCampaigns(
        page: Int,
        size: Int,
        getMissionCampaignCallback: GetMissionCampaignCallback
    )

    interface GetChallengeHomeCallback {
        fun onSuccess(challengeHome : ChallengeHomeResponse)
        fun onFailed(reason: String)
    }
    fun getChallengeHome(callback: GetChallengeHomeCallback)

    interface GetChallengeActiveListCallback {
        fun onSuccess(challengeInfo : List<ChallengeInfoResponse>)
        fun onFailed(reason: String)
    }
    fun getChallengeActiveList(callback: GetChallengeActiveListCallback)

    interface GetChallengeTypeListCallback {
        fun onSuccess(challengeInfo : List<ChallengeYearResponse>)
        fun onFailed(reason: String)
    }
    fun getChallengeTypeList(type: String, callback: GetChallengeTypeListCallback)

    interface GetChallengeYearCallback {
        fun onSuccess(challengeInfo : ChallengeYearPagingResponse)
        fun onFailed(reason: String)
    }
    fun getChallengePerYear(type: String, year: String, page: Int, size: Int,callback: GetChallengeYearCallback)
}