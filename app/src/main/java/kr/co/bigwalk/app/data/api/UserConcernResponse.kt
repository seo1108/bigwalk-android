package kr.co.bigwalk.app.data.api

import com.google.gson.annotations.SerializedName

data class UserConcernResponse(
    @SerializedName("characteristic")
    val characteristic: String,
    @SerializedName("tags")
    val tags: List<UserConcernTagResponse>
)

data class UserConcernTagResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("characteristic")
    val characteristic: String,
    @SerializedName("selected")
    val selected: Boolean
) {
    fun conversionOfSelectionToTag(response: List<UserConcernTagResponse>?): UserConcernTagResponse {
        return UserConcernTagResponse(
            id = id,
            name = name,
            characteristic = characteristic,
            selected = response?.map {
                it.id
            }?.contains(id) ?: false
        )
    }
}