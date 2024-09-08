package kr.co.bigwalk.app.data.feed.dto

import kr.co.bigwalk.app.data.campaign.dto.ResponseCampaign
import kr.co.bigwalk.app.data.organization.Department
import kr.co.bigwalk.app.data.organization.Organization

data class FeedInfoResponse(
    val campaign: ResponseCampaign?,
    val organization: Organization?,
    val departments: List<Department> = arrayListOf()
)