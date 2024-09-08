package kr.co.bigwalk.app.feed_home

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.feedHome.dto.ChallengeHomeResponse
import kr.co.bigwalk.app.data.feedHome.dto.ChallengeInfoResponse
import kr.co.bigwalk.app.data.feedHome.dto.ChallengeYearPagingResponse
import kr.co.bigwalk.app.data.feedHome.repository.FeedHomeDataSource
import kr.co.bigwalk.app.data.feedHome.repository.FeedHomeRepository
import kr.co.bigwalk.app.util.DebugLog

class ChallengeByYearPagingViewModel(
    private val navigator: BaseNavigator?
) : BaseObservable() {

    var challengeList: ObservableList<ChallengeInfoResponse> = ObservableArrayList()
    var observablePage: ObservableField<Int> = ObservableField()
    var size: ObservableField<Int> = ObservableField()

    fun requestChallengeByYear(type: String, year: String, page: Int, size: Int) {
        FeedHomeRepository.getChallengePerYear(type, year, page, size, object : FeedHomeDataSource.GetChallengeYearCallback {
                override fun onSuccess(response: ChallengeYearPagingResponse) {
                    if (response.content!!.isEmpty()) return
                    if (observablePage.get() == 0) challengeList.clear()
                    challengeList.addAll(response.content)
                }

                override fun onFailed(reason: String) {
                    DebugLog.d("requestChallengeByYear request failed: $reason")
                }
            }
        )
    }

    fun finishActivity() {
        navigator!!.finish()
    }
}