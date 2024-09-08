package kr.co.bigwalk.app.data.community.dto.create

import com.google.gson.annotations.SerializedName

data class RegisterCrewResponse(
    @SerializedName("groupId")
    val crewId: Long
)