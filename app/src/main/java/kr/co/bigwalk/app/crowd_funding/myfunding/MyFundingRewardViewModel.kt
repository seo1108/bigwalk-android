package kr.co.bigwalk.app.crowd_funding.myfunding

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.crowd_funding.FundingRewardResultItem
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.crowd_funding.dto.FundingState
import kr.co.bigwalk.app.data.crowd_funding.dto.RewardFundingInfoResponse
import kr.co.bigwalk.app.data.crowd_funding.repository.CrowdFundingRepository
import kr.co.bigwalk.app.extension.valueToCommaString
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.Event
import kr.co.bigwalk.app.walk.alarm.StepManager

class MyFundingRewardViewModel(private val item: FundingRewardResultItem) : ViewModel() {
    private val _rewardFundingInfo = MutableLiveData<RewardFundingInfoResponse>()
    val rewardFundingInfo: LiveData<RewardFundingInfoResponse> get() = _rewardFundingInfo
    private val _refundSuccessEvent = MutableLiveData<Event<String>>()
    val refundSuccessEvent: LiveData<Event<String>> get() = _refundSuccessEvent
    private val _fundSuccessEvent = MutableLiveData<Event<Unit>>()
    val fundSuccessEvent: LiveData<Event<Unit>> get() = _fundSuccessEvent

    private val _failureEvent = MutableLiveData<Event<String>>()
    val failureEvent: LiveData<Event<String>> get() = _failureEvent

    private val _fundingResultType = MutableLiveData<FundingResultType>(
        FundingResultType(
            title = R.string.dialog_funding_result_title4,
            content = R.string.dialog_funding_result_content4,
            rewardStep = "",
            myFundingStep = "",
            icon = R.drawable.aos_icon_funding_returnfund_pop_3,
            btnColor = R.color.maize,
            btnMsg = R.string.return_step,
            isSuccess = false
        )
    )
    val fundingResultType: LiveData<FundingResultType> get() = _fundingResultType
    val donatedStep = MutableLiveData(PreferenceManager.getDonableStep().valueToCommaString())

    init {
        fetchRewardFundingInfo()
    }

    fun refundForFundingSteps() {
        StepManager.uploadAllWalksAndClear {
            CrowdFundingRepository.refundForFundingSteps(
                crewCampaignId = item.id,
                successCallback = { response ->
                    BigwalkApplication.context?.let { FirebaseAnalytics.getInstance(it).logEvent("my_funding_fail_button_get_back_click", Bundle()) }
                    if (response != null) {
                        _refundSuccessEvent.value = Event(response.refundedSteps.valueToCommaString())
                        PreferenceManager.saveDonableStep(response.donableSteps)
                    }
                    DebugLog.d(response.toString())
                }, failCallback = {
                    DebugLog.d(it)
                    _failureEvent.value = Event(it)
                }
            )
        }
    }

    fun fundForFundingSteps() {
        CrowdFundingRepository.fundForFundingSteps(
            crewCampaignId = item.id,
            successCallback = {
                BigwalkApplication.context?.let { FirebaseAnalytics.getInstance(it).logEvent("campaign_button_apply_reward_step_click", Bundle()) }
                _fundSuccessEvent.value = Event(Unit)
            }, failCallback = {
                DebugLog.d(it)
                _failureEvent.value = Event(it)
            }
        )
    }

    private fun fetchRewardFundingInfo() {
        CrowdFundingRepository.getRewardFundingInfo(
            crewCampaignId = item.id,
            successCallback = { response ->
                _rewardFundingInfo.value = response
                setFundingResultType()
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    private fun setFundingResultType() {
        if (rewardFundingInfo.value == null) return
        when (item.fundingState) {
            FundingState.FUNDING -> {

            }
            FundingState.FUNDING_SUCCESS_AND_DONATE -> {
                BigwalkApplication.context?.let { FirebaseAnalytics.getInstance(it).logEvent("campaign_reward_steps_view", Bundle()) }
                _fundingResultType.value = FundingResultType(
                    title = R.string.dialog_funding_result_title5,
                    content = R.string.dialog_funding_result_content5,
                    rewardStep = (rewardFundingInfo.value!!.rewardSteps + rewardFundingInfo.value!!.fundingSteps).valueToCommaString(),
                    myFundingStep = rewardFundingInfo.value!!.getFundingStepsToString(),
                    icon = if (rewardFundingInfo.value?.isEarlyBird == true) R.drawable.i_os_icon_funding_reward_pop_early_bird else R.drawable.aos_icon_funding_reward_pop,
                    btnColor = R.color.theme_174dfe,
                    btnMsg = R.string.apply_step,
                    isSuccess = true
                )
            }
            FundingState.FUNDING_SUCCESS -> {
                BigwalkApplication.context?.let { FirebaseAnalytics.getInstance(it).logEvent("my_funding_succeed_detail_view", Bundle()) }
                _fundingResultType.value = FundingResultType(
                    title = R.string.dialog_funding_result_title1,
                    content = R.string.dialog_funding_result_content1,
                    rewardStep = item.remainSteps,
                    myFundingStep = item.myFundingSteps,
                    icon = if (rewardFundingInfo.value?.isEarlyBird == true) R.drawable.i_os_icon_funding_reward_pop_early_bird else R.drawable.aos_icon_funding_reward_pop,
                    btnColor = R.color.theme_174dfe,
                    btnMsg = R.string.confirm,
                    isSuccess = true
                )
            }
            FundingState.FUNDING_FAIL -> {
                BigwalkApplication.context?.let { FirebaseAnalytics.getInstance(it).logEvent("my_funding_fail_detail_view", Bundle()) }
                _fundingResultType.value = FundingResultType(
                    title = R.string.dialog_funding_result_title2,
                    content = R.string.dialog_funding_result_content2,
                    rewardStep = item.remainSteps,
                    myFundingStep = item.myFundingSteps,
                    icon = R.drawable.aos_icon_funding_returnfund_pop_1,
                    btnColor = R.color.calm_red,
                    btnMsg = R.string.return_step,
                    isSuccess = false
                )
            }
            FundingState.FUNDING_FAILURE_TO_ACHIEVE -> {
                BigwalkApplication.context?.let { FirebaseAnalytics.getInstance(it).logEvent("my_funding_fail_detail_view", Bundle()) }
                _fundingResultType.value = FundingResultType(
                    title = R.string.dialog_funding_result_title3,
                    content = R.string.dialog_funding_result_content3,
                    rewardStep = item.remainSteps,
                    myFundingStep = item.myFundingSteps,
                    icon = R.drawable.aos_icon_funding_returnfund_pop_2,
                    btnColor = R.color.groove_purple,
                    btnMsg = R.string.return_step,
                    isSuccess = false
                )
            }
            FundingState.DELETE_FUNDING -> {
                BigwalkApplication.context?.let { FirebaseAnalytics.getInstance(it).logEvent("my_funding_fail_detail_view", Bundle()) }
                _fundingResultType.value = FundingResultType(
                    title = R.string.dialog_funding_result_title4,
                    content = R.string.dialog_funding_result_content4,
                    rewardStep = item.remainSteps,
                    myFundingStep = item.myFundingSteps,
                    icon = R.drawable.aos_icon_funding_returnfund_pop_3,
                    btnColor = R.color.maize,
                    btnMsg = R.string.return_step,
                    isSuccess = false
                )
            }
        }
    }
}

data class FundingResultType(
    val title: Int,
    val content: Int,
    val rewardStep: String,
    val myFundingStep: String,
    val icon: Int,
    val btnColor: Int,
    val btnMsg: Int,
    val isSuccess: Boolean
)