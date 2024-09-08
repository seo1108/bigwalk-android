package kr.co.bigwalk.app.data.community

import com.google.gson.annotations.SerializedName

data class GroupInviteKeyResponse(
    @SerializedName("key")
    val key: String
)
