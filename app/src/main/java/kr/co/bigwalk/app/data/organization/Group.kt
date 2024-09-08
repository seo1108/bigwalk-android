package kr.co.bigwalk.app.data.organization

data class Group(
    val id: Long,
    val name: String,
    val type: GroupType,
    var department: Department? = null,
    val organizationIdType: OrganizationIdType? = null,
    val expiredDate: String? = null,
    val value1: String? = null,
    val value2: String? = null,
    val value3: String? = null,
    val value4: String? = null,
    val value5: String? = null
)