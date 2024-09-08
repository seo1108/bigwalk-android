package kr.co.bigwalk.app.data.community.dto

import com.google.gson.annotations.SerializedName
import kr.co.bigwalk.app.data.community.GroupMemberRole

data class MyRoleFromGroupResponse(
    @SerializedName("myRole")
    val myRole: GroupMemberRole
)
