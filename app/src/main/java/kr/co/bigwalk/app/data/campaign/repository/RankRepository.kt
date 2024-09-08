package kr.co.bigwalk.app.data.campaign.repository

object RankRepository: RankDataSource {
    private val rankDataSource : RankDataSource

    init {
        rankDataSource = RankRemoteDataSource
    }
    override fun getRank(category: String, page: Int, size: Int, rankCallback: RankDataSource.RankCallback) {
        rankDataSource.getRank(category, page, size, rankCallback)
    }

    override fun getOrganizationRank(
        page: Int,
        size: Int,
        getCampaignsRankCallback: RankDataSource.GetOrganizationRankCallback
    ) {
        rankDataSource.getOrganizationRank(page, size, getCampaignsRankCallback)
    }

    override fun getMyOrganizationRank(myOrganizationRankCallback: RankDataSource.MyOrganizationRankCallback) {
        rankDataSource.getMyOrganizationRank(myOrganizationRankCallback)
    }

    override fun getRankGuide(
        category: String,
        rankGuideCallback: RankDataSource.RankGuideCallback
    ) {
        rankDataSource.getRankGuide(category, rankGuideCallback)
    }
}