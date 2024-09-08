package kr.co.bigwalk.app.data.campaign.repository

import kr.co.bigwalk.app.data.campaign.dto.RankData
import kr.co.bigwalk.app.data.campaign.dto.RankGuide
import kr.co.bigwalk.app.data.campaign.dto.RankingResponse

interface RankDataSource {
    interface RankCallback{
        fun onSuccessWithList(list: List<RankData>)
        fun onSuccessWithMyRank(myRank: RankData)
        fun onSuccessWithSeasonPeriod(season:String, startDate: String?, endDate: String?)
        fun onFailed(reason: String)
    }

    fun getRank(category: String, page: Int, size: Int, rankCallback: RankCallback)

    interface GetOrganizationRankCallback{
        fun onSuccess(rankResponse: List<RankingResponse>)
        fun onFailed(reason: String)
    }

    fun getOrganizationRank(page: Int, size: Int, getCampaignsRankCallback: GetOrganizationRankCallback)

    interface MyOrganizationRankCallback{
        fun onSuccess(rankResponse: RankingResponse)
        fun onFailed(reason: String)
    }

    fun getMyOrganizationRank(myOrganizationRankCallback: MyOrganizationRankCallback)

    interface RankGuideCallback{
        fun onSuccess(rankResponse: List<RankGuide>)
        fun onFailed(reason: String)
    }

    fun getRankGuide(category: String, rankGuideCallback: RankGuideCallback)
}