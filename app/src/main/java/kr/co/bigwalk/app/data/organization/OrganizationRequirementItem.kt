package kr.co.bigwalk.app.data.organization

interface OrganizationRequirementItem {
    val id: Long?
    val depth1: String?
    val depth2: String?
    val depth3: String?
    val depth4: String?
    val isFamilyRelations: Boolean?
    val isAge: Boolean?
    val isNickname: Boolean?
    val isName: Boolean?
    val isIdentification: Boolean?
    val isInstagramAccount: Boolean?
    val isAddress: Boolean?
    val isAttendanceNo: Boolean?
    val isStudentId: Boolean?
    val isOrganizationEmployeeNumber: Boolean?
    val isOrganizationEmail: Boolean?
    val isVisitDay: Boolean?
    val isInput: Boolean?
    val isSearchContent: Boolean?
    val isNumber: Boolean?
    val isCertNo: Boolean?
}