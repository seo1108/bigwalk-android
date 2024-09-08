package kr.co.bigwalk.app.data.community.dto.create

import com.google.gson.annotations.SerializedName
import kr.co.bigwalk.app.data.user.dto.UserConcernTagResponse
import java.io.Serializable

data class CrewConcernResponse(
    @SerializedName("characteristic")
    val characteristic: String,
    @SerializedName("tags")
    val tags: List<CrewConcernTagResponse>
)

data class CrewConcernTagResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("characteristic")
    val characteristic: String,
    @SerializedName("selected")
    val selected: Boolean
): Serializable {
    fun conversionOfSelectionToTag(response: List<CrewConcernTagResponse>?): CrewConcernTagResponse {
        return CrewConcernTagResponse(
            id = id,
            name = name,
            characteristic = characteristic,
            selected = response?.map {
                it.id
            }?.contains(id) ?: false
        )
    }
    fun toUserConcernTagResponse() = UserConcernTagResponse(
        id = id.toLong(),
        name = name
    )
}