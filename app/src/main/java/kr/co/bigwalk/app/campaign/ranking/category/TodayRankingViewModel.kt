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
import kr.co.bigwalk.app.extension.toDisplayString
import kr.co.bigwalk.app.extension.toTagString
import kr.co.bigwalk.app.util.DebugLog
import java.util.*
import kotlin.math.abs

class TodayRankingViewModel(private val navigator: BaseNavigator) :
    RankingPlusViewModel(navigator) {
    val TODAY_MAX_LEVEL = 10

    init {
        category = RankingPlusActivity.Category.TODAY.categoryName
        rankPageDataSourceFactory = RankPageDataSourceFactory(
            category!!,
            object : RankInfoListener {
                override fun loadMyRank(myRank: RankData) {
                    myReport.set(myRank)
                    gradeIcon.set(GradeIcon(category!!, myRank.rank.level))
                    isProgressing.set(myRank.rank.level < TODAY_MAX_LEVEL)
                    areAtTheTop.set(myRank.number in 1..100)
                }

                override fun loadPeriod(season: String, startDate: String?, endDate: String?) {
                    currentSeason = season
                    areOnSeason.postValue(true)

                    val cal = Calendar.getInstance()
                    val keyName = cal.toTagString()

                    if (myReport.get()!=null) {
                        handleRankNumber(keyName)
                    }

                    val periodText = Calendar.getInstance().toDisplayString(true)
                    totalRankPeriod.set(periodText)

                    val description = (navigator.getContext()).resources.getString(R.string.on_season_report_description)
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
                showUpDownIcon.set(localNumber != 0L && difference != 0L)
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

    fun manageTodayRankNumber(cal: Calendar) {
        val keyName = cal.toTagString()

        if (PreferenceManager.getRankNumber(keyName)==null) {   // 어제 랭크 저장소를 지운다.
            cal.add(Calendar.DAY_OF_MONTH, -1)
            val yesterday = cal.toTagString()
            PreferenceManager.removeRankNumber(yesterday)
        }

        if (isRefreshingData) {     // 오늘 랭크 저장소에 저장한다.
            try {
                val rankNumber = "${myReport.get()!!.rank.level}-${myReport.get()!!.number}"
                PreferenceManager.saveRankNumber(keyName, rankNumber)
                isRefreshingData = false
            } catch (e: Exception) { }
        }
    }
}