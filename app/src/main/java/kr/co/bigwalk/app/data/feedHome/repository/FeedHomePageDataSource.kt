package kr.co.bigwalk.app.data.feedHome.repository

import android.util.Log
import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.feedHome.dto.MissionCampaignResponse
import kr.co.bigwalk.app.exception.MissionCampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class FeedHomePageDataSource: PageKeyedDataSource<Int, MissionCampaignResponse>() {

    companion object {
        const val PAGE_SIZE = 10
        const val FIRST_PAGE = 0
    }

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<Int, MissionCampaignResponse>
    ) {
        FeedHomeRepository.getMissionCampaigns(FIRST_PAGE, PAGE_SIZE, object : FeedHomeDataSource.GetMissionCampaignCallback {
            override fun onSuccess(missionCampaignResponses: List<MissionCampaignResponse>) {
                callback.onResult(missionCampaignResponses, null, FIRST_PAGE + 1)
            }
            override fun onFailed(reason: String) {
                showToast("챌린지 캠페인 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(MissionCampaignException(reason))
            }
        })
    }

    override fun loadBefore(
        params: LoadParams<Int>,
        callback: LoadCallback<Int, MissionCampaignResponse>
    ) {
//        FeedHomeRepository.getMissionCampaigns(params.key, params.requestedLoadSize, object : FeedHomeDataSource.GetMissionCampaignCallback {
//            override fun onSuccess(missionCampaignResponses: List<MissionCampaignResponse>) {
//                DebugLog.d("FeedHomePageDataSource loadBefore, params.key = ${params.key}, ")
//                callback.onResult(missionCampaignResponses, if (params.key > FIRST_PAGE) params.key - 1 else null)
//            }
//            override fun onFailed(reason: String) {
//                showToast("챌린지 캠페인 목록을 불러올 수 없습니다. 다시 시도해주세요.")
//                DebugLog.e(MissionCampaignException(reason))
//            }
//        })
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, MissionCampaignResponse>) {
//        FeedHomeRepository.getMissionCampaigns(params.key, params.requestedLoadSize, object : FeedHomeDataSource.GetMissionCampaignCallback {
//            override fun onSuccess(missionCampaignResponses: List<MissionCampaignResponse>) {
//                DebugLog.d("FeedHomePageDataSource loadAfter, params.key = ${params.key}, params.requestedLoadSize = ${params.requestedLoadSize}")
//                callback.onResult(missionCampaignResponses, if (params.key < PAGE_SIZE) null else params.key + 1)
//            }
//            override fun onFailed(reason: String) {
//                showToast("챌린지 캠페인 목록을 불러올 수 없습니다. 다시 시도해주세요.")
//                DebugLog.e(MissionCampaignException(reason))
//            }
//        })
    }
}
