package kr.co.bigwalk.app.data.campaign.repository.page

import kr.co.bigwalk.app.data.campaign.dto.RankData

interface RankInfoListener {
    fun loadMyRank(myRank: RankData)
    fun loadPeriod(season: String, startDate: String?, endDate: String?)
}