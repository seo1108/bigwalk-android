package kr.co.bigwalk.app.data.user.dto

import kr.co.bigwalk.app.data.user.Gender
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


data class ModifyUserRequest(val name: String) {
    var departmentId: Long? = null
    var organizationId: Long? = null
    var organizationEmployeeNumber: String? = null
    var organizationEmail: String? = null
    var characterId: Int? = null
}

data class ModifyProfileRequest(
    val name: String? = "",
    val phoneNumber: String,
    val gender: Gender? = null,
    val birthday: String,
    val email: String,
    val profileImage: File? = null,
    val characterId: Int? = null,
    val organizationId: Long? = null,
    val departmentId: Long? = null,
    val organizationEmployeeNumber: String? = null,
    val organizationEmail: String? = null,
    val organizationEtc: String? = null,
    val firstDepthRegion: String? = null,
    val secondDepthRegion: String? = null,
    val userTagIds: List<Long>? = null
) {
    fun toMultipartBody(): List<MultipartBody.Part> {
        return MultipartBody.Builder().run {
            if (name != null) addFormDataPart("name", name)
            if (phoneNumber.isNotEmpty()) addFormDataPart("phoneNumber", phoneNumber)
            if (gender != null) addFormDataPart("gender", gender.gender)
            if (birthday.isNotEmpty()) addFormDataPart("birthday", birthday)
            if (email.isNotEmpty()) addFormDataPart("email", email)
            if (characterId != null) addFormDataPart("characterId", characterId.toString())
            if (organizationId != null && organizationId > 0) addFormDataPart("organizationId", organizationId.toString())
            if (departmentId != null && departmentId > 0) addFormDataPart("departmentId", departmentId.toString())
            if (organizationEmployeeNumber != null) addFormDataPart("organizationEmployeeNumber", organizationEmployeeNumber)
            if (organizationEmail != null) addFormDataPart("organizationEmail", organizationEmail)
            if (organizationEtc != null) addFormDataPart("organizationEtc", organizationEtc)
            if (firstDepthRegion != null) addFormDataPart("firstDepthRegion", firstDepthRegion)
            if (secondDepthRegion != null) addFormDataPart("secondDepthRegion", secondDepthRegion)
            if (profileImage != null) {
                addFormDataPart("profileImage", profileImage.name, profileImage.asRequestBody(MultipartBody.FORM))
            }
            if (userTagIds != null) {
                userTagIds.forEach { addFormDataPart("userTagIds", it.toString()) }
            }


            build().parts
        }
    }
}






