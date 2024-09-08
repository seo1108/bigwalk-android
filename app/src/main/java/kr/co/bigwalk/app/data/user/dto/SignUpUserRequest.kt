package kr.co.bigwalk.app.data.user.dto

import kr.co.bigwalk.app.data.user.ServiceProvider
import java.io.Serializable


data class SignUpUserRequest(
    var serviceProvider: ServiceProvider,
    var accessToken: String,
    var name: String?
) : Serializable {
    var marketingAgreement: Boolean = false
    var organizationId: Long? = null
    var departmentId: Long? = null
    var organizationEmployeeNumber: String? = null
    var organizationEmail: String? = null
    var characterId: Int? = null
    var profileCharacterId: Int? = null
    var profileType: String? = null

    var depth1: Long? = 0
    var depth2: Long? = 0
    var depth3: Long? = 0
    var depth4: Long? = 0
    var searchContent: String? = ""
    var visitDay: String?= ""
    var certNo: String?= ""
    var familyRelations: String?= ""
    var age: String?= ""
    var nickname: String?= ""
    var identification: String?= ""
    var instagramAccount: String?= ""
    var address: String?= ""
    var attendanceNo: String?= ""
    var studentId: String?= ""
    var number: String?= ""
    var input: String?= ""
    var organizationEmployeeName: String?= ""
}