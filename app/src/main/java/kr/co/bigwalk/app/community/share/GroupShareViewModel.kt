package kr.co.bigwalk.app.community.share

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.community.GroupShareResponse
import kr.co.bigwalk.app.data.community.PropensityType
import kr.co.bigwalk.app.data.community.repository.CommunityRepository
import kr.co.bigwalk.app.util.DebugLog
import java.text.SimpleDateFormat

class GroupShareViewModel(val groupId: Long) : ViewModel() {
    private val _shareContents = MutableLiveData<GroupShareResponse>()
    val shareContents: LiveData<GroupShareResponse> get() = _shareContents
    val currentDate = MutableLiveData<String>(SimpleDateFormat("yyyy.MM.dd(EE)").format(System.currentTimeMillis()))
    val goalImgBg = MutableLiveData<Int>()
    val goalImgRes = MutableLiveData<Int>()
    
    init {
        getGroupShareContents()
    }
    
    private fun getGroupShareContents() {
        CommunityRepository.getGroupShareContents(
            groupId = groupId,
            successCallback = { response ->
                if (response != null) {
                    _shareContents.value = response
                    setImgBg(response.propensity)
                    setImgRes(response.ratio, response.propensity)
                }
                DebugLog.d(response.toString())
            },
            failCallback = {
                DebugLog.d(it)
            }
        )
    }
    
    private fun setImgBg(propensity: PropensityType) {
        goalImgBg.value = when(propensity) {
            PropensityType.HEALTH -> R.color.comHealthCover
            PropensityType.ENVIRONMENT -> R.color.comEcoCover
            PropensityType.RUNNING -> R.color.comRunningCover
            PropensityType.NONE -> R.color.comHealthCover
        }
    }
    
    private fun setImgRes(ratio: Int, propensity: PropensityType) {
        goalImgRes.value = when {
            ratio <= 0 ->
                when (propensity) {
                    PropensityType.HEALTH -> R.drawable.img_health0
                    PropensityType.ENVIRONMENT -> R.drawable.img_eco0
                    PropensityType.RUNNING -> R.drawable.img_running0
                    PropensityType.NONE -> R.drawable.img_health0
                }
            ratio in 1..24 ->
                when (propensity) {
                    PropensityType.HEALTH -> R.drawable.img_health1
                    PropensityType.ENVIRONMENT -> R.drawable.img_eco1
                    PropensityType.RUNNING -> R.drawable.img_running1
                    PropensityType.NONE -> R.drawable.img_health1
                }
            ratio in 25..49 ->
                when (propensity) {
                    PropensityType.HEALTH -> R.drawable.img_health25
                    PropensityType.ENVIRONMENT -> R.drawable.img_eco25
                    PropensityType.RUNNING -> R.drawable.img_running25
                    PropensityType.NONE -> R.drawable.img_health25
                }
            ratio in 50..74 ->
                when (propensity) {
                    PropensityType.HEALTH -> R.drawable.img_health50
                    PropensityType.ENVIRONMENT -> R.drawable.img_eco50
                    PropensityType.RUNNING -> R.drawable.img_running50
                    PropensityType.NONE -> R.drawable.img_health50
                }
            ratio in 75..99 ->
                when (propensity) {
                    PropensityType.HEALTH -> R.drawable.img_health75
                    PropensityType.ENVIRONMENT -> R.drawable.img_eco75
                    PropensityType.RUNNING -> R.drawable.img_running75
                    PropensityType.NONE -> R.drawable.img_health75
                }
            ratio >= 100 ->
                when (propensity) {
                    PropensityType.HEALTH -> R.drawable.img_health100
                    PropensityType.ENVIRONMENT -> R.drawable.img_eco100
                    PropensityType.RUNNING -> R.drawable.img_running100
                    PropensityType.NONE -> R.drawable.img_health100
                }
            else -> R.drawable.img_health0
        }
    }
}