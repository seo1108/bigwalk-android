package kr.co.bigwalk.app.data.community

import com.google.gson.annotations.SerializedName

data class GroupGoalRequest(
    @SerializedName("goalPoint")
    val goalPoint: Long,
    @SerializedName("goalType")
    val goalType: GoalType
)