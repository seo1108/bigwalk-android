package kr.co.bigwalk.app.data

import com.google.gson.annotations.SerializedName

data class ErrorResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String
)

data class BaseResponse<T>(
    @SerializedName("result") val result: Result? = null,
    @SerializedName("code") val code: Int? = null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null,
    @SerializedName("accessToken") val accessToken: String? = null
)

enum class Result {
    @SerializedName("SUCCESS")
    SUCCESS,
    @SerializedName("FAIL")
    FAIL
}

