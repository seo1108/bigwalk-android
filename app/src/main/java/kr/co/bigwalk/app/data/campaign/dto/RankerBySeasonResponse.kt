package kr.co.bigwalk.app.data.campaign.dto

import com.google.gson.annotations.SerializedName

data class RankerBySeasonResponse(
    @SerializedName("myRankInfo")
    val myRankInfo: RankingResponse,
    @SerializedName("rankers")
    val rankers: List<RankingResponse>
)
