package kr.co.bigwalk.app.data

import kr.co.bigwalk.app.data.user.dto.MyProfileResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header

interface TokenApiService {
    /**
     * 회원수정 조회
     */
    @GET("/api/users/my-profile")
    fun getMyProfile(@Header("X-AUTH-TOKEN") token: String?): Call<BaseResponse<MyProfileResponse>>
}