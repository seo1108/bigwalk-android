package kr.co.bigwalk.app.data.organization

interface OrganizationItem {
    val id: Long?
    val name: String?
    val organizationIdType: OrganizationIdType?
    val personInChargePhoneNumber: String?
    val personInChargeName: String?
    val organizationId: Long?
    val enabled: Boolean?
    val depth: Long?
}