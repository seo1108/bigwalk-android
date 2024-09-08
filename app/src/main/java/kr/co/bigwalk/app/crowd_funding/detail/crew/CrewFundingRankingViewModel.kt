package kr.co.bigwalk.app.crowd_funding.detail.crew

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.funding.LabelSignUpMethod
import kr.co.bigwalk.app.data.funding.dto.*
import kr.co.bigwalk.app.data.funding.repository.CrewCampaignRankingDataSourceFactory
import kr.co.bigwalk.app.data.funding.repository.FundingRepository
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.Event

class CrewFundingRankingViewModel(private val crewCampaignId: Long) : ViewModel() {
    private val crewCampaignRankingDataSourceFactory = CrewCampaignRankingDataSourceFactory(crewCampaignId)

    private val _myFundingRank = MutableLiveData<CrewCampaignMyRankingResponse>()
    val myFundingRank: LiveData<CrewCampaignMyRankingResponse> get() = _myFundingRank

    private val _questionItem = MutableLiveData<QuestionForCrewCampaignResponse>()
    val questionItem: LiveData<QuestionForCrewCampaignResponse> get() = _questionItem

    private val _applySuccess = MutableLiveData<Event<Int>>()
    val applySuccess: LiveData<Event<Int>> get() = _applySuccess

    private val _applyFailureEvent = MutableLiveData<Event<String>>()
    val applyFailureEvent: LiveData<Event<String>> get() = _applyFailureEvent

    val isPrivate = MutableLiveData<Boolean>()
    val inCrew = MutableLiveData<Boolean>()
    val crewJoinEvent = MutableLiveData<Event<Triple<Int, Int, String>>>()

    val fundingRankList: LiveData<PagedList<CrewCampaignRankingResponse>> by lazy {
        LivePagedListBuilder(
            crewCampaignRankingDataSourceFactory,
            CrewCampaignRankingDataSourceFactory.pagedListConfig()
        ).build()
    }
    init {
        fetchCrewCampaignMyRanking()
        fetchQuestionForCrewCampaign()
    }

    fun fetchCrewCampaignMyRanking() {
        FundingRepository.getCrewCampaignMyRanking(
            crewCampaignId = crewCampaignId,
            successCallback = { response ->
                _myFundingRank.value = response
                if (response != null) {
                    inCrew.value = response.groupRole != FundingMemberRole.NORMAL.name
                }
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    private fun fetchQuestionForCrewCampaign() {
        FundingRepository.getQuestionForCrewCampaign(
            crewCampaignId = crewCampaignId,
            successCallback = { response ->
                _questionItem.value = response
                if (response != null) {
                    isPrivate.value = response.signUpMethod == LabelSignUpMethod.PRIVATE
                }
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    fun joinCrewByRecruitSetting() {
        when(questionItem.value?.signUpMethod) {
            LabelSignUpMethod.NOT_APPROVE ->{   //바로 가입
                crewJoinEvent.value = Event(Triple(R.string.success_apply_crew_join_now, R.string.success_crew_join, ""))
            }
            LabelSignUpMethod.APPROVE -> {  //승인 후 가입 (질문 유무에 따라 화면 이동 결정)
                val question = questionItem.value?.question
                if (question.isNullOrEmpty()) {
                    crewJoinEvent.value = Event(Triple(R.string.success_apply_crew_join_msg, R.string.success_apply_crew_join, ""))
                } else {
                    crewJoinEvent.value = Event(Triple(R.string.success_apply_crew_join_msg, R.string.success_apply_crew_join, question))
                }
            }
            else -> {
                return
            }
        }
    }

    fun applyForCrewCampaign(alertMsg: Int) {
        FundingRepository.applyForCrewCampaign(
            crewCampaignId = crewCampaignId,
            request = ApplyForCrewCampaignRequest(null),
            successCallback = {
                _applySuccess.value = Event(alertMsg)
            }, failCallback = {
                DebugLog.d(it)
                _applyFailureEvent.value = Event(it)
            }
        )
    }
}