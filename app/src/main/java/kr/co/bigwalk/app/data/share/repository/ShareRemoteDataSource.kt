package kr.co.bigwalk.app.data.share.repository

import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.share.ResponseShare
import kr.co.bigwalk.app.data.share.ResponseShareCampaign
import kr.co.bigwalk.app.exception.ShareException
import kr.co.bigwalk.app.util.DebugLog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ShareRemoteDataSource: ShareDataSource {

    override fun getAllShareData(getAllShareDataCallback: ShareDataSource.GetAllShareDataCallback) {
        RemoteApiManager.getService().getAllShareData().enqueue(object : Callback<ResponseShare> {
            override fun onFailure(call: Call<ResponseShare>, t: Throwable) {
                getAllShareDataCallback.onFailed(t.localizedMessage)
                DebugLog.e(ShareException(t.localizedMessage))
            }

            override fun onResponse(call: Call<ResponseShare>, response: Response<ResponseShare>) {
                when (response.code()) {
                    200 -> response.body()?.let { getAllShareDataCallback.onSuccess(it) }
                    else -> getAllShareDataCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

        })
    }

    override fun getCampaignShareData(
        campaignId: Long,
        getCampaignShareDataCallback: ShareDataSource.GetCampaignShareDataCallback
    ) {
        RemoteApiManager.getService().getCampaignShareData(campaignId)
            .enqueue(object : Callback<ResponseShareCampaign> {
                override fun onFailure(call: Call<ResponseShareCampaign>, t: Throwable) {
                    getCampaignShareDataCallback.onFailed(t.localizedMessage)
                    DebugLog.e(ShareException(t.localizedMessage))
                }

                override fun onResponse(call: Call<ResponseShareCampaign>, response: Response<ResponseShareCampaign>) {
                    when (response.code()) {
                        200 -> response.body()?.let { getCampaignShareDataCallback.onSuccess(it) }
                        else -> getCampaignShareDataCallback.onFailed(response.errorBody()?.string()!!)
                    }
                }

            })
    }
}