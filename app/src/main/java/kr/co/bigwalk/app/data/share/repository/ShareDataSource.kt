package kr.co.bigwalk.app.data.share.repository

import kr.co.bigwalk.app.data.share.ResponseShare
import kr.co.bigwalk.app.data.share.ResponseShareCampaign

interface ShareDataSource {

    interface GetAllShareDataCallback {
        fun onSuccess(responseShare: ResponseShare)
        fun onFailed(reason: String)
    }

    fun getAllShareData(getAllShareDataCallback: GetAllShareDataCallback)

    interface GetCampaignShareDataCallback {
        fun onSuccess(responseShareCampaign: ResponseShareCampaign)
        fun onFailed(reason: String)
    }

    fun getCampaignShareData(campaignId: Long, getCampaignShareDataCallback: GetCampaignShareDataCallback)
}