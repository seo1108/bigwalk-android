package kr.co.bigwalk.app.campaign.ranking.category

import androidx.paging.LivePagedListBuilder
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.ranking.RankingPlusActivity
import kr.co.bigwalk.app.campaign.ranking.RankingPlusViewModel
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.campaign.GradeIcon
import kr.co.bigwalk.app.data.campaign.dto.RankData
import kr.co.bigwalk.app.data.campaign.repository.page.RankInfoListener
import kr.co.bigwalk.app.data.campaign.repository.page.RankPageDataSourceFactory
import kr.co.bigwalk.app.extension.getDisplayPeriod
import kr.co.bigwalk.app.extension.getServerDateStringToDate
import kr.co.bigwalk.app.extension.toDisplayString
import kr.co.bigwalk.app.util.DebugLog
import java.util.*
import kotlin.math.abs

class TotalRankingViewModel(private val navigator: BaseNavigator) :
    RankingPlusViewModel(navigator) {
    companion object {
        const val TOTAL_MAX_LEVEL = 15
    }

    init {
        category = RankingPlusActivity.Category.TOTAL.categoryName
        rankPageDataSourceFactory = RankPageDataSourceFactory(
            category!!,
            object : RankInfoListener {
                override fun loadMyRank(myRank: RankData) {
                    myReport.set(myRank)
                    gradeIcon.set(GradeIcon(category!!, myRank.rank.level))
                    isProgressing.set(myRank.rank.level < TOTAL_MAX_LEVEL)
                    areAtTheTop.set(myRank.number in 1..100)
                }

                override fun loadPeriod(season: String, startDate: String?, endDate: String?) {
                    currentSeason = season
                    val seasonEndDate = getServerDateStringToDate(endDate!!) ?: Date()
                    val isSeason = seasonEndDate.time >= Date().time
                    areOnSeason.postValue(isSeason)

                    if (myReport.get() != null) {
                        handleRankNumber(season)
                    }

                    val periodText = if (isSeason) {
                        if (startDate != null) {
                            getDisplayPeriod(startDate, endDate)
                        } else {
                            Calendar.getInstance().toDisplayString(true)
                        }
                    } else (navigator.getContext()).resources.getString(R.string.season_end)
                    totalRankPeriod.set(periodText)

                    val description =
                        if (isSeason) (navigator.getContext()).resources.getString(R.string.on_season_report_description)
                        else (navigator.getContext()).resources.getString(R.string.season_end_report_description)
                    rankingReportDescription.set(description)
                }
            })
        ranking = LivePagedListBuilder<Int, RankData>(rankPageDataSourceFactory, config).build()
    }

    private fun handleRankNumber(keyName: String) {
        val localData = PreferenceManager.getRankNumber(keyName)
        val serverData = myReport.get()!!
        if (localData != null) {
            val localRank = localData.split("-")[0].toLong()
            val localNumber = localData.split("-")[1].toLong()
            if (serverData.number in 1..100) {
                val difference = serverData.number - localNumber
                showUpDownIcon.set(difference != 0L)
                isUpIcon.set(difference < 0)
                differenceValue.set(abs(difference).toString())
                DebugLog.d("serverNumber:${serverData.number} / localNumber: $localNumber / difference: $difference")
            } else {
                val difference = serverData.rank.level - localRank
                showUpDownIcon.set(difference > 0L)
                DebugLog.d("serverLevel:${serverData.rank.level} / localLevel: $localRank / difference: $difference")
            }
        } else {
            showUpDownIcon.set(false)
        }
    }

    fun manageTotalRankNumber(season: String) {
        if (PreferenceManager.getRankNumber(season)==null) {   // 전 시즌 랭크 저장소를 지운다.
            val seasonData = season.split("-")
            val lastSeason = "${seasonData[0]}-${seasonData[1].toInt()-1}"
            PreferenceManager.removeRankNumber(lastSeason)
        }

        if (isRefreshingData) {     // 현 시즌 랭크 저장소에 저장한다.
            val rankNumber = "${myReport.get()!!.rank.level}-${myReport.get()!!.number}"
            PreferenceManager.saveRankNumber(season, rankNumber)
            isRefreshingData = false
        }
    }
}