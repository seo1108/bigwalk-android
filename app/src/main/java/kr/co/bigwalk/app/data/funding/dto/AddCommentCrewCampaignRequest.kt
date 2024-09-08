package kr.co.bigwalk.app.data.funding.dto

import com.google.gson.annotations.SerializedName

data class AddCommentCrewCampaignRequest(
    @SerializedName("comment")
    val comment: String,
    @SerializedName("parentId")
    val parentId: Long?
)
