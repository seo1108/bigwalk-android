package kr.co.bigwalk.app.data.organization

import java.io.Serializable

data class Organization(
    override val id: Long?,
    override val name: String?,
    override val organizationIdType: OrganizationIdType? = null,
    override val personInChargePhoneNumber: String? = null,
    override val personInChargeName: String? = null,
    override val organizationId: Long? = 0,
    override val enabled: Boolean? = false,
    override val depth: Long? = 1
) : OrganizationItem,Serializable