package kr.co.bigwalk.app.community

import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.community.info.GroupRankTextItem
import kr.co.bigwalk.app.community.info.RankChangeStatus
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.community.GoalType
import kr.co.bigwalk.app.data.community.GroupDetailResponse
import kr.co.bigwalk.app.data.community.GroupGoalRequest
import kr.co.bigwalk.app.data.community.PropensityType
import kr.co.bigwalk.app.data.community.TargetResponse.Companion.GOAL_POINT_UNIT
import kr.co.bigwalk.app.data.community.repository.CommunityRepository
import kr.co.bigwalk.app.extension.valueToCommaString
import kr.co.bigwalk.app.util.DebugLog
import java.util.*

class CommunityInfoViewModel(val groupId: Long) : ViewModel() {
    private val _communityInfo = MutableLiveData<GroupDetailResponse>()
    val communityInfo: LiveData<GroupDetailResponse> get() = _communityInfo
    val isIntroduceVisible = MutableLiveData<Boolean>(false)
    val goalSteps = MutableLiveData<String>("1")
    val ownerVisible = MutableLiveData<Boolean>()
    val memberVisible = MutableLiveData<Boolean>()
    val isSetGoals = MutableLiveData<Boolean>()
    val reportTitle = MutableLiveData<String>()
    val invalidMember = MutableLiveData<String>()
    val goalAnimationRow = MutableLiveData<Int>()
    val step = MutableLiveData<Int>()
    val groupRankTextItem = MutableLiveData<GroupRankTextItem>()
    val goalReactionRes = MutableLiveData<Int>()
    val isBarTracking = MutableLiveData<Boolean>()
    val goalSetRotate = MutableLiveData<Float>()
    val tooltipVisible = MutableLiveData<Boolean>()
    var isPlayAnimation = false

    fun setProgressGoalStep(step: Int) {
        this.step.value = step
        goalSteps.value = step.valueToCommaString(GOAL_POINT_UNIT)
        communityInfo.value?.let {
            goalReactionRes.value = when(step * 100 / it.target.maxPoint) {
                in 1..25 -> R.drawable.img_group_goal_25
                in 26..50 -> R.drawable.img_group_goal_50
                in 51..75 -> R.drawable.img_group_goal_75
                in 76..100 -> R.drawable.img_group_goal_100
                else -> Color.TRANSPARENT
            }
        }
    }
    fun setGoalSetRotate(step: Int) {
        goalSetRotate.value = if (step > 0) {
            if (isPlayAnimation) return
            isPlayAnimation = true
            90f
        } else {
            isPlayAnimation = false
            0f
        }
    }

    fun setIntroduceVisible() {
        isIntroduceVisible.value = isIntroduceVisible.value?.not()
    }

    fun setBarTrackingStatus(isTracking: Boolean) {
        isBarTracking.value = isTracking
    }

    fun getGroupDetail() {
        CommunityRepository.getGroupDetail(
            groupId = groupId,
            successCallback = {
                it?.let { response ->
                    _communityInfo.value = response
                    setTextByRankType(response)
                    setGoalVisible(response.isOwner, response.target.endDate)
                    setReportData(response)
                    setGoalAnimationView(response)
                    DebugLog.d(response.toString())
                }
            },
            failCallback = {
                DebugLog.d(it)
                invalidMember.postValue(it)
            }
        )
    }

    fun postGroupGoal() {
        this.step.value?.let { step ->
            if (step <= 0) return
            val goalPoint = (step.toLong() / GOAL_POINT_UNIT) * GOAL_POINT_UNIT // 1만 단위로 변환
            CommunityRepository.setGroupGoal(
                groupId = groupId,
                request = GroupGoalRequest(
                    goalPoint = goalPoint,
                    goalType = GoalType.STEP
                ),
                successCallback = {
                    getGroupDetail()
                    communityInfo.value?.let { response ->
                        setGoalVisible(response.isOwner, response.target.endDate)
                    }
                },
                failCallback = {
                    DebugLog.d(it)
                }
            )
        }
    }

    private fun setTextByRankType(response: GroupDetailResponse) {
        val res = BigwalkApplication.context?.resources
        val isSetGoals: Boolean = response.target.endDate.before(Date()).not()
        res ?: return

        if (response.rank == null) {
            if (isSetGoals) {
                groupRankTextItem.value = GroupRankTextItem(
                    groupCount = res.getString(R.string.group_rank_not_find1),
                    level = res.getString(R.string.group_rank_not_find2),
                    nextGapPoint = res.getString(R.string.group_rank_not_find3)
                )
            } else {
                groupRankTextItem.value = GroupRankTextItem(
                    groupCount = res.getString(R.string.group_rank_not_find1),
                    level = res.getString(R.string.group_rank_not_set_goal1),
                    nextGapPoint = res.getString(R.string.group_rank_not_set_goal2)
                )
            }
        } else {
            if (isSetGoals) {
                groupRankTextItem.value = GroupRankTextItem(
                    groupCount = res.getString(R.string.group_rank_did_all1).format(response.rank.participantsCount),
                    level = res.getString(R.string.group_rank_did_all2).format(response.rank.level),
                    nextGapPoint = if (response.rank.level == 1L) {
                        res.getString(R.string.group_rank_fst_msg)
                    } else {
                        res.getString(R.string.group_rank_did_all3).format(response.rank.level - 1, response.rank.getNextGapPoint())
                    },
                    rankChange = when {
                        PreferenceManager.getGroupRank(groupId) == -1L -> {
                            RankChangeStatus.NONE
                        }
                        PreferenceManager.getGroupRank(groupId) < response.rank.level -> {
                            RankChangeStatus.DOWN
                        }
                        PreferenceManager.getGroupRank(groupId) > response.rank.level -> {
                            RankChangeStatus.UP
                        }
                        else -> RankChangeStatus.NONE
                    }
                )
            } else {
                groupRankTextItem.value = GroupRankTextItem(
                    groupCount = res.getString(R.string.group_rank_did_all1).format(response.rank.participantsCount),
                    level = res.getString(R.string.group_rank_not_set_goal1),
                    nextGapPoint = res.getString(R.string.group_rank_not_set_goal2)
                )
            }
            PreferenceManager.saveGroupRank(response.rank.level, groupId)
        }
    }

    private fun setGoalVisible(owner: Boolean, endDate: Date) {
        if (endDate.before(Date())) {
            if (owner) {
                ownerVisible.postValue(true)
                memberVisible.postValue(false)
            } else {
                ownerVisible.postValue(false)
                memberVisible.postValue(true)
            }
            isSetGoals.postValue(false)
        } else {
            isSetGoals.postValue(true)
            memberVisible.postValue(true)
            ownerVisible.postValue(false)
        }
    }

    fun setTooltipVisible() {
        if (PreferenceManager.haveSeenCrewCampaignTooltip(groupId).not()){
            PreferenceManager.saveCrewCampaignTooltip(true, groupId)
            tooltipVisible.value = true
        } else {
            tooltipVisible.value = false
        }
    }

    private fun setReportData(groupDetail: GroupDetailResponse) {
        BigwalkApplication.context?.let { context ->
            reportTitle.postValue(
                when (groupDetail.propensity) {
                    PropensityType.HEALTH -> context.getString(R.string.report_title_health_propensity)
                    PropensityType.ENVIRONMENT -> context.getString(R.string.report_title_environment_propensity)
                    PropensityType.RUNNING -> context.getString(R.string.report_title_running_propensity)
                    PropensityType.NONE -> ""
                }
            )
        }
    }

    private fun setGoalAnimationView(groupDetail: GroupDetailResponse) {
        with(groupDetail) {
            goalAnimationRow.value = when {
                target.ratio <= 0 ->
                    when (propensity) {
                        PropensityType.HEALTH -> R.raw.health0_data
                        PropensityType.ENVIRONMENT -> R.raw.eco0_data
                        PropensityType.RUNNING -> R.raw.running0_data
                        PropensityType.NONE -> R.raw.health0_data
                    }
                target.ratio in 1..24 ->
                    when (propensity) {
                        PropensityType.HEALTH -> R.raw.health1_data
                        PropensityType.ENVIRONMENT -> R.raw.eco1_data
                        PropensityType.RUNNING -> R.raw.running1_data
                        PropensityType.NONE -> R.raw.health1_data
                    }
                target.ratio in 25..49 ->
                    when (propensity) {
                        PropensityType.HEALTH -> R.raw.health25_data
                        PropensityType.ENVIRONMENT -> R.raw.eco25_data
                        PropensityType.RUNNING -> R.raw.running25_data
                        PropensityType.NONE -> R.raw.health25_data

                    }
                target.ratio in 50..74 ->
                    when (propensity) {
                        PropensityType.HEALTH -> R.raw.health50_data
                        PropensityType.ENVIRONMENT -> R.raw.eco50_data
                        PropensityType.RUNNING -> R.raw.running50_data
                        PropensityType.NONE -> R.raw.health50_data
                    }
                target.ratio in 75..99 ->
                    when (propensity) {
                        PropensityType.HEALTH -> R.raw.health75_data
                        PropensityType.ENVIRONMENT -> R.raw.eco75_data
                        PropensityType.RUNNING -> R.raw.running75_data
                        PropensityType.NONE -> R.raw.health75_data
                    }
                target.ratio >= 100 ->
                    when (propensity) {
                        PropensityType.HEALTH -> R.raw.health100_data
                        PropensityType.ENVIRONMENT -> R.raw.eco100_data
                        PropensityType.RUNNING -> R.raw.running100_data
                        PropensityType.NONE -> R.raw.health100_data
                    }
                else -> R.raw.health0_data
            }
        }
    }
}