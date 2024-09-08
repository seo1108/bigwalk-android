package kr.co.bigwalk.app.data.share.repository

object ShareRepository: ShareDataSource {

    override fun getAllShareData(getAllShareDataCallback: ShareDataSource.GetAllShareDataCallback) {
        ShareRemoteDataSource.getAllShareData(getAllShareDataCallback)
    }

    override fun getCampaignShareData(
        campaignId: Long,
        getCampaignShareDataCallback: ShareDataSource.GetCampaignShareDataCallback
    ) {
        ShareRemoteDataSource.getCampaignShareData(campaignId, getCampaignShareDataCallback)
    }
}