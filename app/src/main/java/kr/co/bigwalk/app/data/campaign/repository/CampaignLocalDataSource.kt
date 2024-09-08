package kr.co.bigwalk.app.data.campaign.repository

import kr.co.bigwalk.app.campaign.donation.additional_service.MissionDonationData
import kr.co.bigwalk.app.data.campaign.dto.*
import java.io.File

object CampaignLocalDataSource : CampaignDataSource {

    override fun getCampaigns(page: Int, size: Int,getCampaignCallback: CampaignDataSource.GetCampaignCallback) {}

    override fun getCampaign(campaignId: Long, getCampaignCallback: CampaignDataSource.CampaignCallback) {}

    override fun getSMSContent(campaignId: Long, smsId: Long, getSMSCallback: CampaignDataSource.GetSMSCallBack) {}

    override fun getCampaignContents(campaignId: Long, getCampaignContentsCallback: CampaignDataSource.GetCampaignContentsCallback) {
        val contents = listOf(
            CampaignContentResponse(
                0,
                "캠페인 소개해주세요.",
                "하반신 장애 2급 변정수(46·거제시 장평동) 씨는 지난 22일 3·15의거 기념사업회 주최, 마산시 육상경기연맹이 주관한 제19회 3·15마라톤 대회에 참가했다가 마라톤마저 장애인을 차별한다는 생각에 씁쓸했다.이 주관한 제19회 3·15마라톤 대회에 참가했다가 마라톤마저 장애인을 차별한다는 생각에 씁쓸했다.",
                CampaignContentType.IMAGES,
                listOf(CampaignContentImageResponse(
                    0,
                    "https://t1.daumcdn.net/cfile/tistory/21692B4E583FAF6321"),
                    CampaignContentImageResponse(
                        0,
                        "http://image.dongascience.com/Photo/2018/01/15159739972169[1].jpg"),
                    CampaignContentImageResponse(
                        0,
                        "https://image.store.bemypet.kr/content/uploads/2018/10/06022114/1-1.jpg")
                    ),
                ""
            ),
            CampaignContentResponse(
                0,
                "기부 소개해주세요.",
                "마라톤마저 장애인을 차별한다는 생각에 씁쓸했다.이 주관한 제19회 3·15마라톤 대회에 참가했다가 마라톤마저 장애인을 차별한다는 생각에 씁쓸했다.",
                CampaignContentType.VIDEO,
                listOf(),
                "https://www.youtube.com/watch?v=C6djjrUJw9c"
            ),
            CampaignContentResponse(
                0,
                "컨텐츠 아무것도 없어요.",
                "마라톤마저 장애인을 차별한다는 생각에 씁쓸했다.이 주관한 제19회 3·15마라톤 대회에 참가했다가 마라톤마저 장애인을 차별한다는 생각에 씁쓸했다.",
                CampaignContentType.NONE,
                listOf(),
                ""
            )
        )
        getCampaignContentsCallback.onSuccess(contents)
    }

    override fun donateStep(donateRequest: DonateRequest, campaignId: Long, donateStepCallback: CampaignDataSource.DonateStepCallback) {}

    override fun donateStepForHottime(donateRequest: DonateRequest, campaignId: Long, donateStepCallback: CampaignDataSource.DonateStepForHotTimeCallback) {}

    override fun getParticipatedCampaigns(getParticipatedCampaignsCallback: CampaignDataSource.GetParticipatedCampaignsCallback) {}

    override fun getUserRanking(campaignId: Long, getUserRankingCallback: CampaignDataSource.GetUserRankingCallback) {
        val userRanking = RankingResponse(
            0,
            "",
            "크리스피",
            null,
            3678
        )
        getUserRankingCallback.onSuccess(userRanking)
    }

    override fun getCampaignRanking(campaignId: Long, page: Int, size: Int, getCampaignRankingCallback: CampaignDataSource.GetCampaignRankingCallback) {
        val campaignRanking = listOf(
            RankingResponse(
                0,
                "",
                "하하",
                null,
                3678
            ),
            RankingResponse(
                1,
                "",
                "나나",
                null,
                3478
            ),
            RankingResponse(
                2,
                "",
                "조이",
                null,
                3078
            ),
            RankingResponse(
                3,
                "",
                "크레마",
                null,
                2778
            )
        )
        getCampaignRankingCallback.onSuccess(campaignRanking)
    }

    override fun getDonationReport(getDonationReportCallback: CampaignDataSource.GetDonationReportCallback) {
        val response = DonationReportResponse(100, 54323467890L)
        getDonationReportCallback.onSuccess(response)
    }

    override fun getCampaignsRank(page: Int, size: Int, getCampaignsRankCallback: CampaignDataSource.GetCampaignsRankCallback) {
        val campaignRanking = mutableListOf<RankingResponse>()

        for (i in 1..200){
            val rankingResponse = RankingResponse(
                0,
                "https://bigwalk-dev.s3.ap-northeast-2.amazonaws.com/test/img_character_01.png",
                "하하",
                i.toLong(),
                3678-i.toLong()
            )
            campaignRanking.add(rankingResponse)
        }
        getCampaignsRankCallback.onSuccess(campaignRanking)
    }

    override fun getMyRankInCampaigns(getMyRankInCampaigns: CampaignDataSource.GetMyRankInCampaignsCallback) {
        getMyRankInCampaigns.onSuccess(
            RankingResponse(0,
                "https://bigwalk-dev.s3.ap-northeast-2.amazonaws.com/test/img_character_01.png",
                "하하",
                3,
                1234)
        )
    }

    override fun getMyRankInCampaign(campaignId: Long, getMyRankInCampaignCallback: CampaignDataSource.GetMyRankInCampaignCallback) {
        RankingResponse(0,
            "https://bigwalk-dev.s3.ap-northeast-2.amazonaws.com/test/img_character_01.png",
            "페이이이이이",
            33,
            1234)
    }

    override fun getPopularCampaigns(callback: CampaignDataSource.GetPopularCampaignsCallback) {}
    override fun getCampaignCategories(callback: CampaignDataSource.GetCampaignCategoriesCallback) {}
    override fun getCampaignsByCategory(
        categoryId: Long,
        page: Int,
        size: Int,
        callback: CampaignDataSource.GetCampaignsByCategoryCallback
    ) {

    }

    override fun getAdditionalServiceActionMission(campaignId: Long, callback: CampaignDataSource.AdditionalServiceActionMissionCallback) { }
    override fun donateMission(
        missionDonationData: MissionDonationData,
        achievementImage: File,
        achievementImage2: File?,
        achievementImage3: File?,
        comment: String,
        callback: CampaignDataSource.DonateMissionCallback
    ) {

    }

    override fun getAdditionalServiceValueConsumptionCommerce(
        campaignId: Long,
        callback: CampaignDataSource.AdditionalServiceValueConsumptionCommerceCallback
    ) {

    }

    override fun getRankersBySeason(seasonKey: String, successCallback: (RankerBySeasonResponse?) -> Unit, failCallback: (String) -> Unit) {}
    override fun getSeasonTopRankers(successCallback: (List<SeasonTopRankerResponse>?) -> Unit, failCallback: (String) -> Unit) {}

}