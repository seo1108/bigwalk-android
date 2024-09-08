package kr.co.bigwalk.app.data.user.dto

data class SignInResponse(
    var protocol: String? = "",
    var code: Int? = 0,
    var message: String? = "",
    var url: String? = ""
)
