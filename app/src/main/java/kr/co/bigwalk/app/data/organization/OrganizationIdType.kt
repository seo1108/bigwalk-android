package kr.co.bigwalk.app.data.organization

enum class OrganizationIdType(val id: String) {
    EMAIL("EMAIL"),
    EMPLOYEE_NUMBER("EMPLOYEE_NUMBER"),
    EMAIL_EMPLOYEE_NUMBER_BOTH("EMAIL_EMPLOYEE_NUMBER_BOTH"),
    SPACE("SPACE"),
    NONE("NONE");
}