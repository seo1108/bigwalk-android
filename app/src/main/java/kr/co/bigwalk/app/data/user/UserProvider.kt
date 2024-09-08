package kr.co.bigwalk.app.data.user

import java.io.Serializable

data class UserProvider(
    val serviceProvider: String,
    val id: Long,
    var createdTime: String? = null,
    val modifiedTime: String? = null
) : Serializable