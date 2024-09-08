package kr.co.bigwalk.app.crowd_funding.detail.funding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.DEF_INT_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.crowd_funding.detail.FundingProgressItem
import kr.co.bigwalk.app.crowd_funding.detail.SendProgressItem
import kr.co.bigwalk.app.data.crowd_funding.repository.CrowdFundingRepository
import kr.co.bigwalk.app.data.funding.dto.FundingByStepRequest
import kr.co.bigwalk.app.data.funding.dto.FundingInfoOfCrewCampaignResponse
import kr.co.bigwalk.app.data.funding.dto.FundingMemberRole
import kr.co.bigwalk.app.data.funding.repository.FundingRepository
import kr.co.bigwalk.app.extension.valueToCommaString
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.Event
import kr.co.bigwalk.app.walk.alarm.StepManager
import kotlin.math.ceil
import kotlin.math.max

class StepFundingViewModel(private val progressItem: SendProgressItem) : ViewModel() {

    private val _fundingInfo = MutableLiveData<FundingInfoOfCrewCampaignResponse>()
    val fundingInfo: LiveData<FundingInfoOfCrewCampaignResponse> get() = _fundingInfo

    private val _isCouponVisible = MutableLiveData<Boolean>()
    val isCouponVisible: LiveData<Boolean> get() = _isCouponVisible

    private val _fundingProgress = MutableLiveData<FundingProgressItem>()
    val fundingProgress: LiveData<FundingProgressItem> get() = _fundingProgress

    val stepProgress = MutableLiveData<Int>()
    val minStep = MutableLiveData<Int>()
    val myFundingStepsToString = MutableLiveData(progressItem.myFundingSteps.valueToCommaString())

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> get() = _toastMessage

    private val _fundingCardImage = MutableLiveData<List<FundingCardItem>>()
    val fundingCardImage: LiveData<List<FundingCardItem>> get() = _fundingCardImage

    private val _successEvent = MutableLiveData<Event<Int>>()
    val successEvent: LiveData<Event<Int>> get() = _successEvent

    private val _failureEvent = MutableLiveData<Event<Unit>>()
    val failureEvent: LiveData<Event<Unit>> get() = _failureEvent

    init {
        StepManager.uploadAllWalksAndClear {
            fetchFundingInfoOfCrewCampaign()
        }
    }

    private fun fetchFundingInfoOfCrewCampaign() {
        FundingRepository.getFundingInfoOfCrewCampaign(
            crewCampaignId = progressItem.crewCampaignId,
            successCallback = { response ->
                if (response != null) {
                    setFundingCardImageList(
                        response.earlyFundingInfoImagePath.orEmpty(),
                        response.fundingInfoImagePath.orEmpty(),
                        response.earlyBirdRemainCount ?: DEF_INT_VALUE
                    )
                    setCouponVisible(response.groupRole)
                    _fundingInfo.value = response
                    minStep.value = if ((response.todayRemainingSteps ?: -1) <= 0) 0 else 1
                }
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
                _toastMessage.value = Event(it)
            }
        )
    }

    private fun setFundingCardImageList(earlyFundingInfoImagePath: String, fundingInfoImagePath: String, earlyBirdRemainCount: Int) {
        val list = mutableListOf<FundingCardItem>(
            FundingCardItem(earlyFundingInfoImagePath, earlyBirdRemainCount, true),
            FundingCardItem(fundingInfoImagePath, earlyBirdRemainCount, false)
        )
        _fundingCardImage.value = list
    }

    private fun setCouponVisible(groupRole: String?) {
        _isCouponVisible.value = when (groupRole) {
            FundingMemberRole.OWNER.name, FundingMemberRole.MEMBER.name, FundingMemberRole.GUEST.name -> {
                progressItem.isMemberBenefit
            }
            else -> {
                false
            }
        }
    }

    private fun setFundingStateInform(step: Int) {
        val percent = (((step + progressItem.myFundingSteps.toFloat()) / progressItem.targetFundingSteps.toFloat()) * 100)
        when {
            percent >= 100f -> {
                _fundingProgress.value = FundingProgressItem(
                    str = BigwalkApplication.context?.getString(R.string.funding_completed).orEmpty(),
                    percent = percent.toInt().toString(),
                    inProgress = true
                )
            }
            else -> {
                _fundingProgress.value = FundingProgressItem(
                    str = BigwalkApplication.context?.getString(R.string.funding_completed_progress).orEmpty(),
                    percent = percent.toInt().toString(),
                    inProgress = true
                )
            }
        }
    }

    fun fundingByStep() {
        if ((stepProgress.value ?: -1) <= 0) {
            _failureEvent.value = Event(Unit)
            return
        }
        CrowdFundingRepository.fundingByStep(
            crewCampaignId = progressItem.crewCampaignId,
            request = FundingByStepRequest(
                fundingSteps = stepProgress.value ?: -1
            ),
            successCallback = {
                _successEvent.value = Event(R.string.dialog_funding_completed_msg)
            }, failCallback = {
                DebugLog.d(it)
                _toastMessage.value = Event(it)
            }
        )
    }

    fun fundForFundingCoupon() {
        FundingRepository.fundForFundingCoupon(
            crewCampaignId = progressItem.crewCampaignId,
            successCallback = {
                _successEvent.value = Event(R.string.dialog_funding_coupon_completed_msg)
            }, failCallback = {
                DebugLog.d(it)
                _toastMessage.value = Event(it)
            }
        )
    }

    fun setMinimum() {
        stepProgress.value = minStep.value
    }

    fun setMiddle() {
        stepProgress.value = max(ceil(fundingInfo.value?.todayRemainingSteps?.toFloat()?.div(2) ?: -1f).toInt(), 0)
    }

    fun setMaximum() {
        stepProgress.value = fundingInfo.value?.todayRemainingSteps ?: DEF_INT_VALUE
    }

    fun setProgressFundingStep(step: Int) {
        stepProgress.value = max(step, minStep.value ?: 0)
        setMyFundingSteps(step)
        setFundingStateInform(step)
    }

    private fun setMyFundingSteps(step: Int) {
        val num = progressItem.targetFundingSteps - progressItem.myFundingSteps - step
        myFundingStepsToString.value = if (num <= 0) "" else num.valueToCommaString()
    }
}