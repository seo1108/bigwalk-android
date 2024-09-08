package kr.co.bigwalk.app.crowd_funding.detail

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.funding.dto.ChallengeOfCrewCampaignResponse
import kr.co.bigwalk.app.data.funding.dto.CrewCampaignDetailResponse
import kr.co.bigwalk.app.data.funding.repository.FundingRepository
import kr.co.bigwalk.app.extension.numberToUnit
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.DeepLinkCallback
import kr.co.bigwalk.app.util.DeepLinkCreator
import kr.co.bigwalk.app.util.Event

class CrewCampaignDetailViewModel(private val crewCampaignId: Long) : ViewModel() {

    private val _crewCampaign = MutableLiveData<CrewCampaignDetailResponse>()
    val crewCampaign: LiveData<CrewCampaignDetailResponse> get() = _crewCampaign

    private val _challengeOfCrewCampaign = MutableLiveData<ChallengeOfCrewCampaignResponse>()
    val challengeOfCrewCampaign: LiveData<ChallengeOfCrewCampaignResponse> get() = _challengeOfCrewCampaign

    private val _hasActionMission = MutableLiveData<Boolean>()
    val hasActionMission: LiveData<Boolean> get() = _hasActionMission

    private val _fundingProgress = MutableLiveData<FundingProgressItem>()
    val fundingProgress: LiveData<FundingProgressItem> get() = _fundingProgress

    private val _toastMessage = MutableLiveData<Event<String>>()
    val toastMessage: LiveData<Event<String>> get() = _toastMessage

    private val _isLike = MutableLiveData<Boolean>()
    val isLike: LiveData<Boolean> get() = _isLike
    private val _crewLikeCount = MutableLiveData<String>()
    val crewLikeCount: LiveData<String> get() = _crewLikeCount

    private val _shareCrewCampaignDeepLink = MutableLiveData<String>()
    val shareCrewCampaignDeepLink: LiveData<String> get() = _shareCrewCampaignDeepLink

    private val _errorEvent = MutableLiveData<Event<String>>()
    val errorEvent: LiveData<Event<String>> get() = _errorEvent

    var likeCountToLong = -1L

    fun requestCrewCampaign() {
        FundingRepository.getCrewCampaignDetail(
            crewCampaignId = crewCampaignId,
            successCallback = { response ->
                if (response != null) {
                    _crewCampaign.value = response
                    _isLike.value = response.isLike
                    _crewLikeCount.value = response.getLikeCountToString()
                    likeCountToLong = response.likeCount
                    setFundingStateInform(response.myFundingSteps, response.targetFundingSteps)
                }
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
                _errorEvent.value = Event(it)
            })
    }

    private fun setFundingStateInform(myFundingSteps: Long, targetFundingSteps: Long) {
        val percent = ((myFundingSteps.toFloat() / targetFundingSteps.toFloat()) * 100)
        when {
            percent <= 0f -> {
                _fundingProgress.value = FundingProgressItem(
                    str = BigwalkApplication.context?.getString(R.string.unfunded).orEmpty(),
                    percent = "",
                    inProgress = false
                )
            }
            percent >= 100f -> {
                _fundingProgress.value = FundingProgressItem(
                    str = BigwalkApplication.context?.getString(R.string.funding_completed).orEmpty(),
                    percent = "",
                    inProgress = false
                )
            }
            else -> {
                _fundingProgress.value = FundingProgressItem(
                    str = BigwalkApplication.context?.getString(R.string.funding_progress).orEmpty(),
                    percent = percent.toInt().toString(),
                    inProgress = true
                )
            }
        }
    }

    fun requestChallengeOfCrewCampaign() {
        FundingRepository.getPreviewChallengeOfSupporters(
            crewCampaignId = crewCampaignId,
            successCallback = { response ->
                _challengeOfCrewCampaign.value = response
                if (response != null) {
                    _hasActionMission.value = true
                }
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
                _hasActionMission.value = false
            })
    }

    fun changeLikeState() {
        if (isLike.value == true) {
            BigwalkApplication.context?.let { FirebaseAnalytics.getInstance(it).logEvent("funding_campaign_button_dislike_click", Bundle()) }
            unlikeCrewCampaign()
        } else {
            BigwalkApplication.context?.let { FirebaseAnalytics.getInstance(it).logEvent("funding_campaign_button_like_click", Bundle()) }
            likeCrewCampaign()
        }
    }

    private fun likeCrewCampaign() {
        FundingRepository.likeCrewCampaign(
            crewCampaignId = crewCampaignId,
            successCallback = {
                _isLike.value = true
                likeCountToLong++
                _crewLikeCount.value = likeCountToLong.numberToUnit()
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    private fun unlikeCrewCampaign() {
        FundingRepository.unlikeCrewCampaign(
            crewCampaignId = crewCampaignId,
            successCallback = {
                _isLike.value = false
                likeCountToLong--
                _crewLikeCount.value = likeCountToLong.numberToUnit()
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    fun setCrewCampaignDeepLink() {
        DeepLinkCreator(null).getCrewCampaignDeepLink(
            crewCampaignId = crewCampaignId,
            crewCampaignTitle = crewCampaign.value?.title.orEmpty(),
            imagePath = crewCampaign.value?.mainImage.orEmpty(),
            callback = object : DeepLinkCallback {
                override fun onCreated(url: String) {
                    _shareCrewCampaignDeepLink.value = url
                }
            })
    }
}

data class FundingProgressItem(
    val str: String,
    val percent: String,
    val inProgress: Boolean
)