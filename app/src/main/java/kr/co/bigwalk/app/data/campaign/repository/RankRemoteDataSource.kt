package kr.co.bigwalk.app.data.campaign.repository

import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.campaign.dto.RankGuide
import kr.co.bigwalk.app.data.campaign.dto.RankResponse
import kr.co.bigwalk.app.data.campaign.dto.RankingResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object RankRemoteDataSource: RankDataSource {

    override fun getRank(category: String, page: Int, size: Int, rankCallback: RankDataSource.RankCallback) {
        RemoteApiManager.getService().getRank(category, page, size).enqueue(object : Callback<RankResponse> {
            override fun onResponse(call: Call<RankResponse>, response: Response<RankResponse>) {
                when (response.code()) {
                    200 -> response.body()?.let { rankResponse ->
                        rankCallback.onSuccessWithList(rankResponse.ranking)
                        rankCallback.onSuccessWithMyRank(rankResponse.my)
                        rankCallback.onSuccessWithSeasonPeriod(rankResponse.season, rankResponse.startDate, rankResponse.endDate)
                    }
                    else -> rankCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<RankResponse>, t: Throwable) {
                rankCallback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getOrganizationRank(
        page: Int,
        size: Int,
        getCampaignsRankCallback: RankDataSource.GetOrganizationRankCallback
    ) {
        RemoteApiManager.getService().getGroupRank(page, size).enqueue(object : Callback<List<RankingResponse>> {
            override fun onResponse(call: Call<List<RankingResponse>>, response: Response<List<RankingResponse>>) {
                when (response.code()) {
                    200 -> response.body()?.let { rankResponse ->
                        getCampaignsRankCallback.onSuccess(rankResponse)
                    }
                    else -> getCampaignsRankCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<List<RankingResponse>>, t: Throwable) {
                getCampaignsRankCallback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getMyOrganizationRank(myOrganizationRankCallback: RankDataSource.MyOrganizationRankCallback) {
        RemoteApiManager.getService().getMyGroupRank().enqueue(object : Callback<RankingResponse> {
            override fun onResponse(
                call: Call<RankingResponse>,
                response: Response<RankingResponse>
            ) {
                when (response.code()) {
                    200 -> response.body()?.let { rankResponse ->
                        myOrganizationRankCallback.onSuccess(rankResponse)
                    }
                    else -> myOrganizationRankCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<RankingResponse>, t: Throwable) {
                myOrganizationRankCallback.onFailed(t.localizedMessage)
            }
        })
    }

    override fun getRankGuide(
        category: String,
        rankGuideCallback: RankDataSource.RankGuideCallback
    ) {
        RemoteApiManager.getService().getRankGuide(category).enqueue(object : Callback<List<RankGuide>> {
            override fun onResponse(
                call: Call<List<RankGuide>>,
                response: Response<List<RankGuide>>
            ) {
                when (response.code()) {
                    200 -> response.body()?.let { rankResponse ->
                        rankGuideCallback.onSuccess(rankResponse)
                    }
                    else -> rankGuideCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

            override fun onFailure(call: Call<List<RankGuide>>, t: Throwable) {
                rankGuideCallback.onFailed(t.localizedMessage)
            }
        })
    }
}