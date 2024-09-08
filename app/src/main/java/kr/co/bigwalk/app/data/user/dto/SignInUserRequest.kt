package kr.co.bigwalk.app.data.user.dto

import kr.co.bigwalk.app.data.user.ServiceProvider

data class SignInUserRequest(
    val serviceProvider: ServiceProvider,
    val accessToken: String
)