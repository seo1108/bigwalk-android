package kr.co.bigwalk.app.data.api

import kr.co.bigwalk.app.data.user.Gender

data class SocialAccountInfoResponse(
    val ageRange: String? = "",
    val birthYear: String? = "",
    val birthday: String? = "",
    val email: String? = "",
    val gender: Gender? = Gender.UNKNOWN,
    val name: String? = "",
    val phoneNumber: String? = "",
    val profilePath: String? = ""
)