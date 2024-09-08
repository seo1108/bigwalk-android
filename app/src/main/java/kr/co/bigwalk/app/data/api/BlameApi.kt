package kr.co.bigwalk.app.data.api

import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.DeclareFeedRequest
import kr.co.bigwalk.app.data.blame.dto.BlameFeedCommentRequest
import kr.co.bigwalk.app.data.blame.dto.UserBlockListResponse
import retrofit2.Call
import retrofit2.http.*

interface BlameApi {

    // 챌린지 콘텐츠 신고
    @POST("/api/blame/action-mission")
    fun declareFeed(
        @Body request: DeclareFeedRequest
    ): Call<BaseResponse<Nothing>>

    // 신고 기능 - 챌린지 댓글
    @POST("/api/blame/action-mission-comment")
    fun blameFeedComment(
        @Body request: BlameFeedCommentRequest
    ): Call<BaseResponse<Nothing>>

    // 신고 API - 크루 캠페인 댓글
    @POST("/api/blame/crew-campaign-comment")
    fun blameFundingComment(
        @Body request: BlameFeedCommentRequest
    ): Call<BaseResponse<Nothing>>

    // 신고 API - 유저
    @POST("/api/blame/user")
    fun blameUser(
        @Body request: BlameFeedCommentRequest
    ): Call<BaseResponse<Nothing>>

    // 신고 API - 유저 차단
    @POST("/api/blame/block/{userId}")
    fun userBlocking(
        @Path("userId") userId: Long
    ): Call<BaseResponse<Nothing>>

    // 신고 API - 유저 차단 해제
    @DELETE("/api/blame/block/{userId}")
    fun unblockUsers(
        @Path("userId") userId: Long
    ): Call<BaseResponse<Nothing>>

    // 유저 차단 리스트 API
    @GET("/api/blame/block")
    fun getUserBlockList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<BaseResponse<UserBlockListResponse>>
}