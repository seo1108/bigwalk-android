package kr.co.bigwalk.app.feed_home

import android.content.Intent
import androidx.databinding.*
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.campaign.detail.CampaignDetailActivity
import kr.co.bigwalk.app.data.feedHome.dto.ChallengeHomeResponse
import kr.co.bigwalk.app.data.feedHome.dto.ChallengeInfoResponse
import kr.co.bigwalk.app.data.feed.repository.FeedDataSource
import kr.co.bigwalk.app.data.feed.repository.FeedRepository
import kr.co.bigwalk.app.data.feedHome.dto.ChallengeYearPagingResponse
import kr.co.bigwalk.app.data.feedHome.dto.ChallengeYearResponse
import kr.co.bigwalk.app.data.feedHome.repository.FeedHomeDataSource
import kr.co.bigwalk.app.data.feedHome.repository.FeedHomeRepository
import kr.co.bigwalk.app.feed.ChallengeDetailActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class ChallengeHomeViewModel(
    private val navigator: BaseNavigator?
) : BaseObservable() {


    var challengeHomeDetail: ObservableField<ChallengeHomeResponse> = ObservableField()
    var challengeActive: ObservableList<ChallengeInfoResponse> = ObservableArrayList()
    var challengeParticipated: ObservableList<ChallengeInfoResponse> = ObservableArrayList()
    var challengeClosed: ObservableList<ChallengeInfoResponse> = ObservableArrayList()
    var isActive: ObservableBoolean = ObservableBoolean(true)
    var isParticipated: ObservableBoolean = ObservableBoolean(true)
    var isClosed: ObservableBoolean = ObservableBoolean(true)

    var challengeActiveList: ObservableList<ChallengeInfoResponse> = ObservableArrayList()
    var firstYearList: ObservableList<ChallengeInfoResponse> = ObservableArrayList()
    var secondYearList: ObservableList<ChallengeInfoResponse> = ObservableArrayList()
    var thirdYearList: ObservableList<ChallengeInfoResponse> = ObservableArrayList()

    var isFirstYear: ObservableBoolean = ObservableBoolean(true)
    var isSecondYear: ObservableBoolean = ObservableBoolean(true)
    var isThirdYear: ObservableBoolean = ObservableBoolean(true)

    var firstYearTitle: ObservableField<String> = ObservableField()
    var secondYearTitle: ObservableField<String> = ObservableField()
    var thirdYearTitle: ObservableField<String> = ObservableField()

    var firstYear: ObservableField<String> = ObservableField()
    var secondYear: ObservableField<String> = ObservableField()
    var thirdYear: ObservableField<String> = ObservableField()

    var challengeList: ObservableList<ChallengeInfoResponse> = ObservableArrayList()
    var observablePage: ObservableField<Int> = ObservableField()

    var isLoadFinish: ObservableField<Boolean> = ObservableField(false)


    fun requestChallengeHome() {
        FeedHomeRepository.getChallengeHome(object : FeedHomeDataSource.GetChallengeHomeCallback {
            override fun onSuccess(challengeHomeResponse: ChallengeHomeResponse) {
                challengeHomeDetail.set(challengeHomeResponse)
                /*_challengeActive.value = challengeHomeResponse.actives
                _challengeParticipated.value = challengeHomeResponse.participations
                _challengeClosed.value = challengeHomeResponse.closed*/
                DebugLog.d("[ChallengeHomeResponse]$challengeHomeResponse")


                challengeActive.clear()
                challengeParticipated.clear()
                challengeClosed.clear()
                challengeActive.addAll(challengeHomeResponse.actives)
                challengeParticipated.addAll(challengeHomeResponse.participations)
                challengeClosed.addAll(challengeHomeResponse.closed)

                if (challengeHomeResponse.actives.isNullOrEmpty()) {
                    isActive.set(false)
                }
                if (challengeHomeResponse.participations.isNullOrEmpty()) isParticipated.set(false)
                if (challengeHomeResponse.closed.isNullOrEmpty()) isClosed.set(false)
            }

            override fun onFailed(reason: String) {
                DebugLog.d("challengeHome request failed: $reason")
            }
        })
    }

    fun requestChallengeActiveList() {
        FeedHomeRepository.getChallengeActiveList(object : FeedHomeDataSource.GetChallengeActiveListCallback {
            override fun onSuccess(challengeResponse: List<ChallengeInfoResponse>) {
                challengeActiveList.clear()
                challengeActiveList.addAll(challengeResponse)
            }

            override fun onFailed(reason: String) {
                DebugLog.d("challengeHome request failed: $reason")
            }
        })
    }

    fun requestChallengeTypeList(type: String) {
        FeedHomeRepository.getChallengeTypeList(type, object : FeedHomeDataSource.GetChallengeTypeListCallback {
            override fun onSuccess(challengeResponse: List<ChallengeYearResponse>) {
                firstYearList.clear()
                secondYearList.clear()
                thirdYearList.clear()

                if (challengeResponse.isEmpty()) {
                    isFirstYear.set(false)
                    isSecondYear.set(false)
                    isThirdYear.set(false)
                } else if (challengeResponse.size == 1) {
                    firstYearTitle.set(challengeResponse[0].year+"년")
                    firstYear.set(challengeResponse[0].year)
                    firstYearList.addAll(challengeResponse[0].content)

                    isSecondYear.set(false)
                    isThirdYear.set(false)
                } else if (challengeResponse.size == 2) {
                    firstYearTitle.set(challengeResponse[0].year+"년")
                    firstYear.set(challengeResponse[0].year)
                    firstYearList.addAll(challengeResponse[0].content)

                    secondYearTitle.set(challengeResponse[1].year+"년")
                    secondYear.set(challengeResponse[1].year)
                    secondYearList.addAll(challengeResponse[1].content)

                    isThirdYear.set(false)
                } else if (challengeResponse.size == 3) {
                    firstYearTitle.set(challengeResponse[0].year+"년")
                    firstYear.set(challengeResponse[0].year)
                    firstYearList.addAll(challengeResponse[0].content)

                    secondYearTitle.set(challengeResponse[1].year+"년")
                    secondYear.set(challengeResponse[1].year)
                    secondYearList.addAll(challengeResponse[1].content)

                    thirdYearTitle.set(challengeResponse[2].year+"년")
                    thirdYear.set(challengeResponse[2].year)
                    thirdYearList.addAll(challengeResponse[2].content)
                }
            }

            override fun onFailed(reason: String) {
                DebugLog.d("challengeHome request failed: $reason")
            }
        })
    }

    fun requestChallengeByYear(type: String, year: String, page: Int, size: Int) {
        FeedHomeRepository.getChallengePerYear(type, year, page, size, object : FeedHomeDataSource.GetChallengeYearCallback {
            override fun onSuccess(response: ChallengeYearPagingResponse) {
                //if (response.content.isEmpty()) return
                if (page == 0) {
                    isLoadFinish.set(false)
                    challengeList.clear()
                }

                if (response.content!!.isEmpty()) {
                    isLoadFinish.set(true)
                } else {
                    response.content?.let { challengeList.addAll(it) }
                }

            }

            override fun onFailed(reason: String) {
                DebugLog.d("requestChallengeByYear request failed: $reason")
            }
        }
        )
    }

    fun goActiveChallenge() {
        val intent = Intent(navigator!!.getContext(), ChallengeActiveListActivity::class.java)
        navigator.getContext().startActivity(intent)
    }

    fun goParticipateChallenge() {
        val intent = Intent(navigator!!.getContext(), ChallengeTypeListActivity::class.java)
        intent.putExtra("type", "participated")
        navigator.getContext().startActivity(intent)
    }

    fun goClosedChallenge() {
        val intent = Intent(navigator!!.getContext(), ChallengeTypeListActivity::class.java)
        intent.putExtra("type", "closed")
        navigator.getContext().startActivity(intent)
    }

    fun goChallengeDetail(id: Long) {
        val intent = Intent(navigator!!.getContext(), ChallengeDetailActivity::class.java)
        intent.putExtra("challengeId", id)
        navigator.getContext().startActivity(intent)
    }

    fun goParticipateYear(year: String) {
        DebugLog.d("goParticipateYear $year")
    }

    fun goClosedYear(year: String) {
        DebugLog.d("goClosedYear $year")
    }

    fun finishActivity() {
        navigator!!.finish()
    }

}