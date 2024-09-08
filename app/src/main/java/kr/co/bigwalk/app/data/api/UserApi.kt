package kr.co.bigwalk.app.data.api

import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.community.dto.create.CrewAddressResponse
import kr.co.bigwalk.app.data.community.dto.create.CrewConcernResponse
import kr.co.bigwalk.app.data.user.ServiceProvider
import kr.co.bigwalk.app.data.user.dto.MyProfileResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface UserApi {
    /**
     * 본인 인증 문자 요청
     */
    @POST("/api/users/identity-authentication")
    fun requestVerificationCode(
        @Body request: VerificationCodeRequest
    ): Call<BaseResponse<Nothing>>

    /**
     * 본인 인증 요청
     */
    @PUT("/api/users/identity-authentication")
    fun authenticatePhoneNumber(
        @Body request: AuthenticatePhoneNumberRequest
    ): Call<BaseResponse<Nothing>>

    /**
     * 소셜 로그인 정보
     */
    @GET("/api/users/social-info")
    fun getSocialAccountInfo(
        @Query("serviceProvider") serviceProvider: ServiceProvider,
        @Query("accessToken") accessToken: String
    ): Call<BaseResponse<SocialAccountInfoResponse>>

    /**
     * 관심사 조회
     * userId는 프로필 수정할 때 필요함
     */
    @GET("/api/users/concern")
    fun getUserConcern(
        @Query("userId") userId: Long?
    ): Call<BaseResponse<List<CrewConcernResponse>>>

    /**
     * 주소목록 조회
     */
    @GET("/api/users/address")
    fun getUserAddress(
        @Query("userId") userId: Long?
    ): Call<BaseResponse<CrewAddressResponse>>

    /**
     * 회원수정 조회
     */
    @GET("/api/users/my-profile")
    fun getMyProfile(): Call<BaseResponse<MyProfileResponse>>

    /**
     * 회원수정
     */
    @PUT("/api/users/profile")
    @Multipart
    fun modifyMyProfile(@Part body: List<MultipartBody.Part>): Call<BaseResponse<Nothing>>


    /**
     * 사용자 로그
     */
    /*@FormUrlEncoded
    @POST("/log/userReqLog")
    fun userReqLog(
        @Field("name")name: String?,
        @Field("userId")userId: String?,
        @Field("device")device: String?,
        @Field("deviceModel")deviceModel: String?,
        @Field("logDesc")logDesc: String?
    ): Call<BaseResponse<Nothing>>*/
    @POST("/log/userReqLog")
    fun userReqLog(
        @Body request: UserWalkLogRequest
    ): Call<BaseResponse<Nothing>>
}

data class VerificationCodeRequest(
    val toNumber: String
)

data class AuthenticatePhoneNumberRequest(
    val phoneNumber: String,
    val authenticationNumber: String
)

data class SocialAccountInfoRequest(
    val serviceProvider: String,
    val accessToken: String
)

data class UserWalkLogRequest(
    val name: String?,
    val userId: String?,
    val device: String?,
    val deviceModel: String?,
    val logDesc: String?
)
//// 신고 API - 유저 차단
//@POST("/api/blame/block/{userId}")
//fun userBlocking(
//    @Path("userId") userId: Long
//): Call<BaseResponse<Nothing>>
//
//// 신고 API - 유저 차단 해제
//@DELETE("/api/blame/block/{userId}")
//fun unblockUsers(
//    @Path("userId") userId: Long
//): Call<BaseResponse<Nothing>>
//
//// 유저 차단 리스트 API
//@GET("/api/blame/block")
//fun getUserBlockList(
//    @Query("page") page: Int,
//    @Query("size") size: Int
//): Call<BaseResponse<UserBlockListResponse>>