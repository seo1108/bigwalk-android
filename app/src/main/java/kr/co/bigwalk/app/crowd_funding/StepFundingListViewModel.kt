package kr.co.bigwalk.app.crowd_funding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kr.co.bigwalk.app.data.crowd_funding.dto.ContestPostersResponse
import kr.co.bigwalk.app.data.crowd_funding.dto.FundingByHottestResponse
import kr.co.bigwalk.app.data.crowd_funding.dto.MyFundingByContestResponse
import kr.co.bigwalk.app.data.crowd_funding.dto.MyFundingStepByContestResponse
import kr.co.bigwalk.app.data.crowd_funding.repository.CrowdFundingRepository
import kr.co.bigwalk.app.data.crowd_funding.repository.MyStepFundingListDataSourceFactory
import kr.co.bigwalk.app.data.crowd_funding.repository.StepFundingListByHottestDataSourceFactory
import kr.co.bigwalk.app.data.crowd_funding.repository.StepFundingListByNewestDataSourceFactory
import kr.co.bigwalk.app.util.DebugLog
import java.util.*
import kotlin.concurrent.schedule

class StepFundingListViewModel(private val contestId: Long) : ViewModel() {
    private val newestFactory = StepFundingListByNewestDataSourceFactory(contestId)
    private val hottestFactory = StepFundingListByHottestDataSourceFactory(contestId)
    private val myStepFundingFactory = MyStepFundingListDataSourceFactory(contestId)

    val listByNewest: LiveData<PagedList<FundingByHottestResponse>> by lazy {
        LivePagedListBuilder(
            newestFactory,
            StepFundingListByNewestDataSourceFactory.pagedListConfig()
        ).build()
    }
    val listByHottest: LiveData<PagedList<FundingByHottestResponse>> by lazy {
        LivePagedListBuilder(
            hottestFactory,
            StepFundingListByHottestDataSourceFactory.pagedListConfig()
        ).build()
    }
    val myFundingList: LiveData<PagedList<MyFundingByContestResponse>> by lazy {
        LivePagedListBuilder(
            myStepFundingFactory,
            MyStepFundingListDataSourceFactory.pagedListConfig()
        ).build()
    }

    private val _contestDetail = MutableLiveData<ContestPostersResponse>()
    val contestDetail: LiveData<ContestPostersResponse> get() = _contestDetail

    private val _todayFundInfo = MutableLiveData<MyFundingStepByContestResponse>()
    val todayFundInfo: LiveData<MyFundingStepByContestResponse> get() = _todayFundInfo

    val isDay = MutableLiveData<Boolean>()

    private var isClick = true
    val leftClick = MutableLiveData<Unit>()
    val rightClick = MutableLiveData<Unit>()
    private var sortType: Int = 0


    init {
        initViewByTime()
        fetchContestDetail()
    }


    private fun initViewByTime() {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 6..17 -> {
                isDay.value = true
            }
            else -> {
                isDay.value = false
            }
        }
    }

    private fun fetchContestDetail() {
        CrowdFundingRepository.getContestPoster(
            contestId = contestId,
            successCallback = { response ->
                _contestDetail.value = response
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            })
    }

    private fun fetchMyFundingStepByContest() {
        CrowdFundingRepository.getMyFundingStepByContest(
            successCallback = { response ->
                _todayFundInfo.value = response
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    fun setSortTypeToPosition(position: Int) {
        sortType = position
    }

    fun refreshData() {
        fetchMyFundingStepByContest()
        when (sortType) {
            0 -> newestFactory.liveData.value?.invalidate()
            1 -> hottestFactory.liveData.value?.invalidate()
        }
        myStepFundingFactory.liveData.value?.invalidate()
    }


    fun sideClick(sidePosition: String) {
        if (isClick) {
            isClick = false
            Timer().schedule(300L) {
                isClick = true
            }
            when (sidePosition) {
                KEY_LEFT_CLICK -> {
                    leftClick.postValue(Unit)
                }
                KEY_RIGHT_CLICK -> {
                    rightClick.postValue(Unit)
                }
            }
        }
    }

    companion object {
        const val KEY_LEFT_CLICK = "LEFT_CLICK"
        const val KEY_RIGHT_CLICK = "RIGHT_CLICK"
    }
}