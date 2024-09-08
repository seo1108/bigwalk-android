package kr.co.bigwalk.app.data.funding.dto

import com.google.gson.annotations.SerializedName
import kr.co.bigwalk.app.data.community.GroupMemberRole
import kr.co.bigwalk.app.util.longValueToCommaString

data class CrewCampaignRankingListResponse(
    @SerializedName("data")
    val data: List<CrewCampaignRankingResponse>
)

data class CrewCampaignRankingResponse(
    @SerializedName("userID")
    val userId: Long,
    @SerializedName("rank")
    val rank: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("profilePath")
    val profilePath: String,
    @SerializedName("groupRole")
    val groupRole: String,
    @SerializedName("fundedSteps")
    val fundedSteps: Long,
    @SerializedName("earlyBird")
    val earlyBird: Boolean
) {
    fun getFundedStepsToUnit(): String {
        return if (fundedSteps > 10000) {
            longValueToCommaString(fundedSteps / 10000) + "만" + longValueToCommaString(fundedSteps % 10000)
        } else {
            longValueToCommaString(fundedSteps)
        }
    }

    fun getRoleName(): String {
        val earlyBirdTag = "/얼리버드"
        var name = when (groupRole) {
            FundingMemberRole.OWNER.name -> "마스터"
            FundingMemberRole.MEMBER.name -> "매니저"
            FundingMemberRole.GUEST.name -> "체인지메이커"
            FundingMemberRole.NORMAL.name -> "일반"
            else -> "일반"
        }
        if (earlyBird) name += earlyBirdTag
        return name
    }

    fun isOwner(): Boolean {
        return groupRole == GroupMemberRole.OWNER.name
    }
}

enum class FundingMemberRole {
    OWNER,
    MEMBER,
    GUEST,
    NORMAL
}