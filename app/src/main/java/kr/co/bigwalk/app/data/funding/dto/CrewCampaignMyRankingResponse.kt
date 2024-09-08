package kr.co.bigwalk.app.data.funding.dto

import kr.co.bigwalk.app.data.community.GroupMemberRole
import kr.co.bigwalk.app.util.longValueToCommaString

data class CrewCampaignMyRankingResponse(
    val userId: Long,
    val rank: Long,
    val name: String,
    val profilePath: String,
    val groupRole: String,
    val fundedSteps: Long,
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