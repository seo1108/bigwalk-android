package kr.co.bigwalk.app.data.community.dto.create

import com.google.gson.annotations.SerializedName

data class DuplicateCheckForCrewTitleResponse(
    @SerializedName("isDuplicate")
    val isDuplicate: Boolean
)