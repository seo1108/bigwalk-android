package kr.co.bigwalk.app.data.organization.space

import java.util.*

data class SpaceOrganizationResponse(
    val id: Long,
    val organizationIdType: String,
    val name: String,
    val expiredDate: Date,
    val options: List<SpaceOptionResponse>
)