package kr.co.bigwalk.app.data

import kr.co.bigwalk.app.data.campaign.dto.*
import kr.co.bigwalk.app.data.campaign.dto.ActionMissionResponse
import kr.co.bigwalk.app.data.campaign.dto.CampaignContentResponse
import kr.co.bigwalk.app.data.community.*
import kr.co.bigwalk.app.data.community.dto.MyRoleFromGroupResponse
import kr.co.bigwalk.app.data.community.dto.MySupportersCampaignsResponse
import kr.co.bigwalk.app.data.community.dto.create.CrewAddressResponse
import kr.co.bigwalk.app.data.community.dto.create.CrewConcernResponse
import kr.co.bigwalk.app.data.community.dto.create.DuplicateCheckForCrewTitleResponse
import kr.co.bigwalk.app.data.community.dto.create.RegisterCrewResponse
import kr.co.bigwalk.app.data.crowd_funding.dto.*
import kr.co.bigwalk.app.data.feed.dto.*
import kr.co.bigwalk.app.data.feedComment.dto.FeedCommentListResponse
import kr.co.bigwalk.app.data.feedComment.dto.FeedCommentResponse
import kr.co.bigwalk.app.data.feedHome.dto.*
import kr.co.bigwalk.app.data.feedNotification.dto.FeedNotificationResponse
import kr.co.bigwalk.app.data.funding.dto.*
import kr.co.bigwalk.app.data.mission.dto.MissionsResponse
import kr.co.bigwalk.app.data.mission.dto.MissionsStatusResponse
import kr.co.bigwalk.app.data.mission.dto.RewardsResponse
import kr.co.bigwalk.app.data.notification.Notification
import kr.co.bigwalk.app.data.notification.UserSetNotificationAgreementRequest
import kr.co.bigwalk.app.data.organization.*
import kr.co.bigwalk.app.data.organization.dto.CertNoRequest
import kr.co.bigwalk.app.data.organization.dto.OrganizationRequest
import kr.co.bigwalk.app.data.organization.space.SpaceOrganizationResponse
import kr.co.bigwalk.app.data.report.dto.MonthStatsRequest
import kr.co.bigwalk.app.data.report.dto.MonthStatsResponse
import kr.co.bigwalk.app.data.report.dto.UserStatisticsResponse
import kr.co.bigwalk.app.data.report.dto.UserStepHistoryResponse
import kr.co.bigwalk.app.data.share.ResponseShare
import kr.co.bigwalk.app.data.share.ResponseShareCampaign
import kr.co.bigwalk.app.data.signal.SignalResponse
import kr.co.bigwalk.app.data.story.dto.CampaignReservationRequest
import kr.co.bigwalk.app.data.story.dto.ResponseOpenStory
import kr.co.bigwalk.app.data.story.dto.ResponseStory
import kr.co.bigwalk.app.data.story.dto.Story
import kr.co.bigwalk.app.data.user.dto.*
import kr.co.bigwalk.app.data.walk.Walk
import kr.co.bigwalk.app.data.walk.dto.CurrentWalk
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*


interface RemoteApiService {
    @POST("/api/users/sign-in")
    fun authenticateUser(@Body signInUserRequest: SignInUserRequest): Call<Any>

    @GET("/api/users/check-name")
    fun existsNickname(@Query("name") name: String): Call<Void>

    @Streaming
    @GET
    fun downloadSocialProfile(@Url fileUrl: String): Call<ResponseBody>

    @POST("/api/users/sign-up/v2")
    @Multipart
    fun signUp(@Part profileImage: MultipartBody.Part, @PartMap partMap: HashMap<String, RequestBody>): Call<SignInUserResponse>

    @POST("/api/users/sign-up")
    @Multipart
    fun signUp(@Part body: List<MultipartBody.Part>): Call<BaseResponse<SignInUserResponse>>


    /*@POST("/api/users/sign-up/v4")
    @Multipart
    fun signUpV3(@Part profileImage: MultipartBody.Part, @PartMap partMap: HashMap<String, RequestBody>): Call<SignInUserResponse>*/

    /*@POST("/api/users/sign-up/v3")
    @Multipart
    fun signUpV3(@Part body: MultipartBody.Part, @PartMap partMap: HashMap<String, RequestBody>): Call<SignInUserResponse>*/

    @POST("/api/users/sign-up/v3")
    @Multipart
    //fun signUpV3(@Part body: List<MultipartBody.Part>): Call<BaseResponse<SignInUserResponse>>
    fun signUpV3(@Part body: List<MultipartBody.Part>): Call<SignInUserResponse>

    @POST("/api/walk/v2")
    fun uploadWalks(@Body walks: List<Walk>): Call<CurrentWalk>

    @POST("/api/walk/v2")
    fun uploadWalk(@Body walk: Walk): Call<CurrentWalk>

    @GET("/api/organizations")
    fun getOrganizations(): Call<List<Organization>>

    @GET("/api/organizations/{organizationId}")
    fun getOrganization(@Path("organizationId") organizationId: Long): Call<Organization>

    @GET("/api/organizations/{organizationId}/requirements")
    fun getOrganizationRequirement(@Path("organizationId") organizationId: Long): Call<OrganizationRequirement>

    @GET("/api/organizations/{organizationId}/group")
    fun getSpaceOrganization(@Path("organizationId") organizationId: Long): Call<SpaceOrganizationResponse>

    @GET("/api/organizations/{organizationId}/departments?page=0&size=1000")
    fun getDepartments(@Path("organizationId") organizationId: Long): Call<List<Department>>

    @GET("/api/organizations/{organizationId}/depths/{depth}/parents/{parentId}")
    fun getSubDepartments(@Path("organizationId") organizationId: Long, @Path("depth") depth: Long, @Path("parentId") parentId: Long): Call<List<Department>>

    @GET("/api/organizations/{organizationId}/search")
    fun getSearchKeyword(@Path("organizationId") organizationId: Long): Call<List<Content>>

    @POST("/api/organizations/{organizationId}/certNo")
    fun verifyAuthNum(@Path("organizationId") organizationId: Long, @Body request: CertNoRequest): Call<Boolean>

    @POST("/api/users/v2/employer")
    fun saveOrganizationInfo(@Body request: OrganizationRequest): Call<Void>

    @GET("/api/campaigns")
    fun getCampaigns(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<CampaignsResponse>>

    @GET("/api/campaigns/{campaignId}")
    fun getCampaign(@Path("campaignId") campaignId: Long): Call<CampaignAndStoryResponse>

    @GET("/api/campaigns/{campaignId}/contents")
    fun getCampaignContents(@Path("campaignId") campaignId: Long): Call<List<CampaignContentResponse>>

    @GET("/api/campaigns/{campaignId}/sms/{smsId}")
    fun getSMSContent(@Path("campaignId") campaignId: Long, @Path("smsId") smsId: Long): Call<CampaignSMSContentResponse>

    @POST("/api/campaigns/{campaignId}/donate")
    fun donateStep(@Body donateRequest: DonateRequest, @Path("campaignId") campaignId: Long): Call<DonateResponse>

    @POST("/api/campaigns/{campaignId}/donate/event/hottime")
    fun donateStepForHottime(@Body donateRequest: DonateRequest, @Path("campaignId") campaignId: Long): Call<HotTimeDonateResponse>

    @GET("/api/campaigns/participated")
    fun getParticipatedCampaigns(): Call<List<ResponseCampaign>>

    @GET("/api/campaigns/{campaignId}/user-rank")
    fun getUserRanking(@Path("campaignId") campaignId: Long): Call<RankingResponse>

    @GET("/api/campaigns/rank")
    fun getCampaignRanking(
        @Query("campaign-id") campaignId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<RankingResponse>>

    @GET("/api/campaigns/rank")
    fun getCampaignsRanking(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<RankingResponse>>

    @GET("/api/users/report/donation")
    fun getDonationReport(): Call<DonationReportResponse>

    @GET("/api/statistics/walks")
    fun getWalkStatistics(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Call<UserStatisticsResponse>

    @GET("/api/statistics/donations")
    fun getDonationStatistics(
        @Query("startDate") startDate: String,
        @Query("endDate") endDate: String
    ): Call<UserStatisticsResponse>

    @GET("/api/campaigns/rank/my")
    fun getMyRankInCampaigns(): Call<RankingResponse>

    @GET("/api/users/v2/profile")
    fun getMyProfile(): Call<UserProfileResponse>

    @GET("/api/users/auto-login")
    fun checkAutoLogin(): Call<BaseResponse<AutoLoginResponse>>

    @PUT("/api/users/profile/character")
    fun modifyProfileImage(@Query("characterId") characterId: Int): Call<Void>

    @GET("/api/campaigns/{campaignId}/rank/my")
    fun getMyRankInCampaign(@Path("campaignId") campaignId: Long): Call<RankingResponse>

    @GET("/api/stories")
    fun getStories(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<ResponseStory>>

    @GET("/api/stories/campaigns/{campaignId}")
    fun getStory(@Path("campaignId") campaignId: Long): Call<ResponseStory>

    @POST("/api/users/v2/profile")
    @Multipart
    fun modifyProfileUserInformation(
        @Part profileImage: MultipartBody.Part,
        @PartMap partMap: HashMap<String, RequestBody>
    ): Call<UserProfileResponse>

    @POST("/api/users/profile/organizations/add")
    @Multipart
    fun addProfileByOrganization(@PartMap partMap: HashMap<String, RequestBody>): Call<Void>

    @POST("/api/users/profile/organizations/edit")
    @Multipart
    fun modifyProfileByOrganization(@PartMap partMap: HashMap<String, RequestBody>): Call<Void>

    @POST("/api/users/profile/organizations/remove")
    fun removeOrganizationFromProfile(): Call<Void>

    @POST("/api/users/push-token")
    fun sendPushToken(@Body token: SaveTokenRequest): Call<Void>

    @POST("/api/users/notification-agreement/{notiType}")
    fun saveNotificationByType(
        @Path("notiType") notiType: String,
        @Body request: UserSetNotificationAgreementRequest
    ): Call<Void>

    @GET("/api/users/notifications")
    fun getNotifications(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<Notification>>

    @GET("/api/campaigns/favorite")
    fun getPopularCampaigns(): Call<List<ResponseCampaign>>

    @GET("/api/stories/open")
    fun getStoriesForReady(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<Story>>

    @GET("/api/campaigns/category")
    fun getCampaignCategories(): Call<List<Category>>

    @GET("/api/campaigns/category/{categoryId}/story")
    fun getCampaignsByCategory(
        @Path("categoryId") categoryId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<ResponseCampaign>>

    @POST("/api/stories/open/reserve")
    fun reserveCampaign(@Body request: CampaignReservationRequest): Call<Void>

    @GET("/api/stats/step/history/month")
    fun getUserStepHistory(): Call<UserStepHistoryResponse>

    @POST("/api/stats/month")
    fun getMonthlyReport(@Body request: MonthStatsRequest): Call<MonthStatsResponse>

    @GET("/api/stories/open/my")
    fun getMyReservedStories(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<ResponseStory>>

    @GET("/api/stories/open/my/count")
    fun getMyReservedStoriesCount(): Call<ResponseOpenStory>

    @GET("/api/share")
    fun getAllShareData(): Call<ResponseShare>

    @GET("/api/share/{campaignId}")
    fun getCampaignShareData(@Path("campaignId") campaignId: Long): Call<ResponseShareCampaign>

    @POST("/api/users/v2/profile/character/{characterId}")
    fun setCharacter(@Path("characterId") characterId: Int): Call<UserProfileResponse>

    @POST("/api/users/withdraw")
    fun withdraw(): Call<Void>

    @GET("/api/users/notice")
    fun getNotice(): Call<NoticeResponse>


    @GET("/api/campaigns/{campaignId}/additional-service/action/mission")
    fun getAdditionalServiceActionMission(@Path("campaignId") campaignId: Long): Call<List<ActionMissionResponse>>

    /**
     * 챌린지 인증 이미지 3장 업로드
     */
    @POST("/api/campaigns/{campaignId}/additional-service/action/mission/{missionId}/donation")
    @Multipart
    fun donateMission(
        @Part achievementImage: MultipartBody.Part,
        @Part achievementImage2: MultipartBody.Part?,
        @Part achievementImage3: MultipartBody.Part?,
        @Part comment: MultipartBody.Part?,
        @Path("campaignId") campaignId: Long,
        @Path("missionId") missionId: Long
    ): Call<DonationUploadResponse>

    @GET("/api/feed/info")
    fun getFeedInfo(): Call<FeedInfoResponse>

    @GET("/api/feed")
    fun getFeedList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<FeedListResponse>

    /** 챌린지 홈 **/
    @GET("/api/action-mission")
    fun getChallengeHome(): Call<ChallengeHomeResponse>

    /** 챌린지 진행중 리스트 **/
    @GET("/api/action-mission/ing")
    fun getChallengeActiveList(): Call<List<ChallengeInfoResponse>>

    /** 챌린지 진행중 리스트 **/
    @GET("/api/action-mission/types/{type}")
    fun getChallengeTypeList(@Path("type") type: String): Call<List<ChallengeYearResponse>>

    /** 챌린지 연도별 리스트 **/
    @GET("/api/action-mission/types/{type}/{year}")
    fun getChallengePerYear(
        @Path("type") type: String,
        @Path("year") year: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<ChallengeYearPagingResponse>

    /** 챌린지 상세 **/
    @GET("/api/action-mission/{challengeId}/feed")
    fun getChallengeDetail(@Path("challengeId") challengeId: Long): Call<ChallengeDetailResponse>

    /** 챌린지 이미지 리스트 **/
    @GET("/api/action-mission/{challengeId}/feed/types/{type}")
    fun getChallengeImages(
        @Path("challengeId") challengeId: Long,
        @Path("type") type: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<ChallengeImageResponse>


    /**
     * 타입별 피드 리스트 가져오기
     */
    @GET("/api/action-mission/{challengeId}/types/{type}/{id}")
    fun getFeedListByChallenge(
        @Path("challengeId") challengeId: Long,
        @Path("id") id: Long,
        @Path("type") type: String,
        @Query("department") departmentId: Long?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<FeedListResponse>


    /**
     * 인기별 피드 리스트 가져오기
     */
    @GET("/api/action-mission/{challengeId}/types/hit/{id}")
    fun getHotFeedListByChallenge(
        @Path("challengeId") challengeId: Long,
        @Path("id") id: Long,
        @Query("department") departmentId: Long?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<FeedListResponse>

    /**
     * 최신 피드 리스트 가져오기
     */
    @GET("/api/action-mission/{challengeId}/types/recent/{id}")
    fun getRecentFeedListByChallenge(
        @Path("challengeId") challengeId: Long,
        @Path("id") id: Long,
        @Query("department") departmentId: Long?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<FeedListResponse>


    /**
     * 내 피드 리스트 가져오기
     */
    @GET("/api/action-mission/{challengeId}/types/my/{id}")
    fun getMyFeedListByChallenge(
        @Path("challengeId") challengeId: Long,
        @Path("id") id: Long,
        @Query("department") departmentId: Long?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<FeedListResponse>

    /**
     * 내가 올린 피드 리스트 가져오기
     */
    @GET("/api/feed/campaign/{campaignId}/my")
    fun getMyFeedList(
        @Path("campaignId") campaignId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<FeedListResponse>

    /**
     * 다른 사람의 피드 리스트 가져오기
     */
    @GET("/api/feed/campaign/{campaignId}/user/{userId}")
    fun getUserFeedList(
        @Path("campaignId") campaignId: Long,
        @Path("userId") userId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<FeedListResponse>

    /**
     * 부서 별 피드 리스트 가져오기
     */
    @GET("/api/feed/campaign/{campaignId}")
    fun getFeedListByCampaign(
        @Path("campaignId") campaignId: Long,
        @Query("department") departmentId: Long?,
        @Query("action") actionId: Long?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<FeedListResponse>

    @PUT("/api/feed/{actionDonationHistoryId}")
    fun modifyFeed(
        @Path("actionDonationHistoryId") feedId: Long,
        @Body request: ModifyFeedRequest
    ): Call<BaseResponse<Nothing>>

    @POST("/api/feed/{actionDonationHistoryId}/like")
    fun likeFeed(@Path("actionDonationHistoryId") actionDonationHistoryId: Long): Call<Void>

    @POST("/api/feed/{actionDonationHistoryId}/unlike")
    fun unlikeFeed(@Path("actionDonationHistoryId") actionDonationHistoryId: Long): Call<Void>

    @DELETE("/api/feed/{actionDonationHistoryId}")
    fun deleteFeed(@Path("actionDonationHistoryId") actionDonationHistoryId: Long): Call<Void>

    @GET("/api/feed/{actionDonationHistoryId}/like/users")
    fun getLikedUsers(
        @Path("actionDonationHistoryId") actionDonationHistoryId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<UserProfileResponse>>

    @GET("/api/campaigns/{campaignId}/additional-service/commerce/value-consumption")
    fun getAdditionalServiceValueConsumptionCommerce(@Path("campaignId") campaignId: Long): Call<List<ValueConsumptionCommerceResponse>>

    @GET("/api/additional-service/challenges")
    fun getMissionCampaigns(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<BaseResponse<List<MissionCampaignResponse>>>

    @GET("/api/feed/{actionDonationHistoryId}/comment/v2")
    fun getFeedComments(
        @Path("actionDonationHistoryId") actionDonationHistoryId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<BaseResponse<FeedCommentListResponse>>

    @FormUrlEncoded
    @POST("/api/feed/{actionDonationHistoryId}/comment")
    fun postFeedComment(
        @Path("actionDonationHistoryId") actionDonationHistoryId: Long,
        @Field("comment") comment: String
    ): Call<FeedCommentResponse>

    @DELETE("/api/feed/{actionDonationHistoryId}/comment/{id}")
    fun deleteFeedComment(
        @Path("actionDonationHistoryId") actionDonationHistoryId: Long,
        @Path("id") id: Long
    ): Call<Void>

    @GET("/api/feed/{actionDonationHistoryId}")
    fun getFeedInfo(
        @Path("actionDonationHistoryId") actionDonationHistoryId: Long
    ): Call<Feed>

    @GET("/api/feed/campaign/{campaignId}/notifications")
    fun getFeedNotifications(
        @Path("campaignId") campaignId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<FeedNotificationResponse>>

    @GET("/api/feed/notifications")
    fun getAllNotifications(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<FeedNotificationResponse>>

    // 미션 목록 가져오기
    @GET("/api/missions")
    fun getMissions(): Call<List<MissionsResponse>>

    @POST("/api/missions/welcome/rewards")
    fun postRewards(): Call<RewardsResponse>

    @GET("/api/missions/status")
    fun getMissionsStatus(): Call<MissionsStatusResponse>

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("/api/missions/welcome/complete")
    fun completeWelcomeMission(
        @Field("sequence") sequence: Int,
        @Field("value") value: String
    ): Call<MissionsStatusResponse>

    @GET("api/rank/v2/{category}")
    fun getRank(
        @Path("category") category: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<RankResponse>

    @GET("api/rank/v2/group")
    fun getGroupRank(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<List<RankingResponse>>

    @GET("api/rank/v2/group/my")
    fun getMyGroupRank(): Call<RankingResponse>

    @GET("api/rank/v2/{category}/guide")
    fun getRankGuide(@Path("category") category: String): Call<List<RankGuide>>

    @GET("api/rank/v2/season/{seasonKey}")
    fun getRankersBySeason(
        @Path("seasonKey") seasonKey: String
    ): Call<RankerBySeasonResponse>

    @GET("api/rank/v2/season/")
    fun getSeasonTopRankers(): Call<List<SeasonTopRankerResponse>>

    @GET("api/groups/{group_id}")
    fun getGroupDetail(@Path("group_id") groupId: Long): Call<GroupDetailResponse>

    @POST("/api/groups/{group_id}/target")
    fun setGroupGoal(
        @Path("group_id") groupId: Long,
        @Body groupGoalRequest: GroupGoalRequest
    ): Call<Void>

    @GET("api/groups/report/all")
    fun getAllGroupReport(): Call<AllGroupReportResponse>

    @GET("/api/groups/{group_id}/users")
    fun getGroupMemberList(
        @Path("group_id") groupId: Long
    ): Call<List<GroupMemberResponse>>

    @POST("/api/groups/{group_id}/authority/role")
    fun delegateGroupOwner(
        @Path("group_id") groupId: Long,
        @Body delegateOwnerRequest: DelegateOwnerRequest
    ): Call<Void>

    @POST("/api/groups/{group_id}/join/dry")
    fun joinGroupDry(
        @Path("group_id") groupId: Long
    ): Call<GroupSummaryInfoResponse>

    @POST("/api/groups/{group_id}/join/link")
    fun joinGroup(
        @Path("group_id") groupId: Long,
        @Body groupJoinRequest: GroupJoinRequest
    ): Call<Void>

    @POST("/api/groups/{group_id}/users/{user_id}/out")
    fun kickGroupMember(
        @Path("group_id") groupId: Long,
        @Path("user_id") userId: Long
    ): Call<List<GroupMemberResponse>>

    @POST("/api/groups/{group_id}/out/myself")
    fun leaveGroup(
        @Path("group_id") groupId: Long
    ): Call<Void>

    @GET("/api/groups/{group_id}/invitation/key")
    fun getGroupInviteKey(
        @Path("group_id") groupId: Long
    ): Call<GroupInviteKeyResponse>

    @GET("/api/groups/{group_id}/info/share")
    fun getGroupShareContents(
        @Path("group_id") groupId: Long
    ): Call<GroupShareResponse>

    @GET("/api/signals")
    fun getSignal(): Call<SignalResponse>

    @GET("/api/groups")
    fun getMyCrewList(): Call<MyCommunityListResponse>

    @GET("/api/crew-campaigns/group/{groupId}")
    fun getMyLabelList(
        @Path("groupId") groupId: Long
    ): Call<MySupportersCampaignsResponse>

    @PUT("/api/groups/{groupId}/modify")
    @Multipart
    fun modifyCrew(
        @Path("groupId") groupId: Long,
        @Part body: List<MultipartBody.Part>
    ): Call<Void>

    @GET("/api/groups/{groupId}/modify")
    fun getModifyCrewInfo(
        @Path("groupId") groupId: Long
    ): Call<ModifyCrewInfoResponse>

    @GET("/api/crew-campaigns/category")
    fun getLabelCategoryList(): Call<LabelCategoryListResponse>

    @POST("/api/crew-campaigns/group/{groupId}")
    @Multipart
    fun registerLabel(
        @Path("groupId") groupId: Long,
        @Part body: List<MultipartBody.Part>
    ): Call<RegisterSupportersCampaignResponse>

    @GET("/api/crew-campaigns/{crewCampaignId}/form")
    fun getBeforeLabelData(
        @Path("crewCampaignId") crewCampaignId: Long
    ): Call<LabelBeforeDataResponse>

    @PUT("/api/crew-campaigns/{crewCampaignId}/group/{groupId}")
    @Multipart
    fun modifyCrewCampaign(
        @Path("crewCampaignId") crewCampaignId: Long,
        @Path("groupId") groupId: Long,
        @Part body: List<MultipartBody.Part>
    ): Call<Void>

    @POST("/api/crew-campaigns/{crewCampaignId}/action-mission")
    @Multipart
    fun registerChallengeOfSupporters(
        @Path("crewCampaignId") supportersCampaignId: Long,
        @Part body: List<MultipartBody.Part>
    ): Call<Void>

    @PUT("/api/crew-campaigns/{crewCampaignId}/action-mission")
    @Multipart
    fun modifyChallengeOfSupporters(
        @Path("crewCampaignId") supportersCampaignId: Long,
        @Part body: List<MultipartBody.Part>
    ): Call<Void>

    @DELETE("/api/crew-campaigns/{crewCampaignId}/additional-service")
    fun deleteChallengeOfSupporters(
        @Path("crewCampaignId") supportersCampaignId: Long
    ): Call<Void>

    @GET("/api/competitions")
    fun getContestList(): Call<ContestsResponse>

    @GET("/api/competitions/{competitionId}")
    fun getContestDetail(
        @Path("competitionId") competitionId: Long
    ): Call<ContestDetailResponse>

    @GET("/api/crew-campaigns/{crewCampaignId}")
    fun getCrewCampaign(
        @Path("crewCampaignId") crewCampaignId: Long
    ): Call<CrewCampaignResponse>

    @GET("/api/crew-campaigns/{crewCampaignId}/action-mission")
    fun getChallengeOfCrewCampaign(
        @Path("crewCampaignId") crewCampaignId: Long
    ): Call<ChallengeOfCrewCampaignResponse>

    @DELETE("/api/crew-campaigns/{crewCampaignId}/group")
    fun deleteCrewCampaign(
        @Path("crewCampaignId") crewCampaignId: Long
    ): Call<Void>

    @PUT("/api/crew-campaigns/{crewCampaignId}/group/{groupId}/audit")
    fun judgeCrewCampaign(
        @Path("crewCampaignId") crewCampaignId: Long,
        @Path("groupId") groupId: Long
    ): Call<Void>

    @GET("/api/groups/{groupId}/my-role")
    fun getMyRoleFromGroup(
        @Path("groupId") groupId: Long
    ): Call<MyRoleFromGroupResponse>

    @GET("/api/crew-campaigns/{crewCampaignId}/last-modified-by")
    fun getLastModifier(
        @Path("crewCampaignId") crewCampaignId: Long
    ): Call<LastModifierResponse>

    //(CC-11) 크루 캠페인 - 좋아요 API
    @POST("/api/crew-campaigns/{crewCampaignId}/like")
    fun likeCrewCampaign(
        @Path("crewCampaignId") crewCampaignId: Long
    ): Call<Void>

    //(CC-12) 크루 캠페인 - 좋아요(해제) API
    @POST("/api/crew-campaigns/{crewCampaignId}/unlike")
    fun unlikeCrewCampaign(
        @Path("crewCampaignId") crewCampaignId: Long
    ): Call<Void>

    //(CC-13) 크루 캠페인 - 댓글 추가
    @POST("/api/crew-campaigns/{crewCampaignId}/comment")
    fun addCommentCrewCampaign(
        @Path("crewCampaignId") crewCampaignId: Long,
        @Body request: AddCommentCrewCampaignRequest
    ): Call<Void>

    //(CC-14) 크루 캠페인 - 댓글 삭제
    @DELETE("/api/crew-campaigns/{crewCampaignId}/comment/{commentId}")
    fun deleteCommentCrewCampaign(
        @Path("crewCampaignId") crewCampaignId: Long,
        @Path("commentId") commentId: Long
    ): Call<Void>

    //(CC-14) 크루 캠페인 - 댓글 조회
    @GET("/api/crew-campaigns/{crewCampaignId}/comment")
    fun getCommentByCrewCampaign(
        @Path("crewCampaignId") crewCampaignId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String
    ): Call<CommentByCrewCampaignResponse>

    //(CC-15) 걸음 펀딩
    @POST("/api/crew-campaigns/{crewCampaignId}/walk-funding")
    fun fundingByStep(
        @Path("crewCampaignId") crewCampaignId: Long,
        @Body request: FundingByStepRequest
    ): Call<Void>

    //(CC-17) 펀딩 랭크 리스트
    @GET("/api/crew-campaigns/{crewCampaignId}/rank")
    fun getCrewCampaignRankings(
        @Path("crewCampaignId") crewCampaignId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<CrewCampaignRankingListResponse>

    //(CC-18) 신청 필수인 체인지 메이커 가입 신청
    @POST("/api/crew-campaigns/{crewCampaignId}/join")
    fun applyForCrewCampaign(
        @Path("crewCampaignId") crewCampaignId: Long,
        @Body request: ApplyForCrewCampaignRequest
    ): Call<Void>

    //(CC-19) 신청 필수인 체인지 메이커 가입 질문 조회
    @GET("/api/crew-campaigns/{crewCampaignId}/join")
    fun getQuestionForCrewCampaign(
        @Path("crewCampaignId") crewCampaignId: Long
    ): Call<QuestionForCrewCampaignResponse>

    //(CC-20) 크루 캠페인 상세 조회
    @GET("/api/crew-campaigns/{crewCampaignId}/detail")
    fun getCrewCampaignDetail(
        @Path("crewCampaignId") crewCampaignId: Long
    ): Call<CrewCampaignDetailResponse>

    //(CC-21) My-Funding
    @GET("/api/crew-campaigns/my-funding")
    fun getTotalMyFunding(
        @Query("competitionId") competitionId: Long?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<TotalMyFundingListResponse>

    //(CC-22) 환불 API
    @POST("/api/crew-campaigns/{crewCampaignId}/refund")
    fun refundForFundingSteps(
        @Path("crewCampaignId") crewCampaignId: Long
    ): Call<RefundForFundingStepsResponse>

    //(CC-23) 펀딩 적용 API
    @POST("/api/crew-campaigns/{crewCampaignId}/fund")
    fun fundForFundingSteps(
        @Path("crewCampaignId") crewCampaignId: Long
    ): Call<Void>

    //(CC-24) 걸음 펀딩 정보
    @GET("/api/crew-campaigns/{crewCampaignId}/walk-funding")
    fun getFundingInfoOfCrewCampaign(
        @Path("crewCampaignId") crewCampaignId: Long
    ): Call<FundingInfoOfCrewCampaignResponse>

    //(CC-25) 크루 캠페인. 쿠폰
    @POST("/api/crew-campaigns/{crewCampaignId}/member-benefit")
    fun fundForFundingCoupon(
        @Path("crewCampaignId") crewCampaignId: Long
    ): Call<Void>

    //(CC-26) 펀딩 랭킹 ( My )
    @GET("/api/crew-campaigns/{crewCampaignId}/rank-my")
    fun getCrewCampaignMyRanking(
        @Path("crewCampaignId") crewCampaignId: Long
    ): Call<CrewCampaignMyRankingResponse>

    //(CC-27) 캠페인에서 펀딩 화면
    @GET("/api/crew-campaigns/{crewCampaignId}/campaign-funding-info")
    fun getRewardFundingInfo(
        @Path("crewCampaignId") crewCampaignId: Long
    ): Call<RewardFundingInfoResponse>

    //(G-4) 내 공모전의 참여 리스트
    @GET("/api/competitions/{competitionId}/crew-campaigns/my-fund")
    fun getMyFundingByContest(
        @Path("competitionId") competitionId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<MyFundingListByContestResponse>

    //(G-5) 공모전 기부 목록
    @GET("/api/competitions/crew-campaigns/my-fund-info")
    fun getMyFundingStepByContest(): Call<MyFundingStepByContestResponse>

    //(G-6) 점령중인 공모전 출력 API
    @GET("/api/competitions/info")
    fun getOccupationInfo(): Call<OccupationInfoResponse>

    //(G-7) 최신순 크루 캠페인 조회
    @GET("/api/competitions/{competitionId}/crew-campaigns/latest")
    fun getFundingListByNewest(
        @Path("competitionId") competitionId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<FundingListByNewestResponse>

    //(G-8) 인기순으로 크루 캠페인 조회
    @GET("/api/competitions/{competitionId}/crew-campaigns/popularity")
    fun getFundingListByHottest(
        @Path("competitionId") competitionId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<FundingListByHottestResponse>

    //(G-9) My-Funding Description
    @GET("/api/competitions/{competitionId}/my-funding/description")
    fun getMyFundingDescription(
        @Path("competitionId") competitionId: Long
    ): Call<MyFundingDescriptionResponse>

    //(C-10) 공모전 포스터 검색
    @GET("/api/competitions/{competitionId}/poster")
    fun getContestPoster(
        @Path("competitionId") competitionId: Long
    ): Call<BaseResponse<ContestPostersResponse>>

    //(O-2) 체인지 메이커 가입 신청 리스트
    @GET("/api/groups/{groupId}/join-request")
    fun getCrewRequestList(
        @Path("groupId") groupId: Long,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<CrewRequestListResponse>

    //(O-3) 그룹 가입 승인
    @POST("/api/groups/{groupId}/join-request/{requestId}/approve")
    fun approveCrewMember(
        @Path("groupId") groupId: Long,
        @Path("requestId") requestId: Long
    ): Call<Void>

    //(O-4) 그룹 가입 거절
    @PUT("/api/groups/{groupId}/join-request/{requestId}/reject")
    fun rejectCrewMember(
        @Path("groupId") groupId: Long,
        @Path("requestId") requestId: Long
    ): Call<Void>

    // 그룹 관심사 종류 보여주기
    @GET("/api/groups/concern")
    fun getCrewConcern(
        @Query("groupId") groupId: Long?
    ): Call<BaseResponse<List<CrewConcernResponse>>>

    // 그룹 주소 목록 확인하기
    @GET("/api/groups/address")
    fun getCrewAddress(): Call<BaseResponse<CrewAddressResponse>>

    // 그룹 생성시 제목 중복체크
    @GET("/api/groups/duplicate-check")
    fun duplicateCheckForCrewTitle(
        @Query("groupTitle") groupTitle: String
    ): Call<BaseResponse<DuplicateCheckForCrewTitleResponse>>

    // 그룹 생성
    @POST("/api/groups")
    @Multipart
    fun registerCrew(
        @Part body: List<MultipartBody.Part>
    ): Call<BaseResponse<RegisterCrewResponse>>
}
data class DeclareFeedRequest(
    val blameId: Long,
    val blameMessage: String
) {
    fun toMultipartBody(): List<MultipartBody.Part> {
        return MultipartBody.Builder().run {
            if (blameId > -1) addFormDataPart("blameId", blameId.toString())
            if (blameMessage.isNotEmpty()) addFormDataPart("blameMessage", blameMessage)
            build().parts
        }
    }
}