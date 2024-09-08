package kr.co.bigwalk.app.campaign.ranking

import android.content.Intent
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.campaign.ranking.category.TotalRankingViewModel
import kr.co.bigwalk.app.campaign.ranking.info.RankingGradeInfoActivity
import kr.co.bigwalk.app.campaign.ranking.info.RankingGradeInfoActivity.Companion.GUIDE_CATEGORY
import kr.co.bigwalk.app.campaign.ranking.info.RankingInformationActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.campaign.GradeIcon
import kr.co.bigwalk.app.data.campaign.dto.RankData
import kr.co.bigwalk.app.data.campaign.repository.RankDataSource
import kr.co.bigwalk.app.data.campaign.repository.RankRepository
import kr.co.bigwalk.app.data.campaign.repository.page.RankPageDataSource
import kr.co.bigwalk.app.data.campaign.repository.page.RankPageDataSourceFactory
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.extension.*
import kr.co.bigwalk.app.share.ShareActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.longValueToCommaString
import kr.co.bigwalk.app.util.showToast
import java.util.*
import kotlin.math.abs

open class RankingPlusViewModel(
    private val navigator: BaseNavigator
): ViewModel() {
    lateinit var rankPageDataSourceFactory: RankPageDataSourceFactory
    val config = PagedList.Config.Builder()
        .setPageSize(RankPageDataSource.PAGE_SIZE)
        .setEnablePlaceholders(false)
        .build()
    lateinit var ranking: LiveData<PagedList<RankData>>
    val myReport = ObservableField<RankData>()
    val gradeIcon = ObservableField<GradeIcon>()
    val totalRankPeriod = ObservableField<String>()
    var category: String? = null
    val areOnSeason = MutableLiveData<Boolean>()
    val rankingReportDescription = ObservableField<String>()
    val isProgressing: ObservableField<Boolean> = ObservableField()
    val areAtTheTop: ObservableField<Boolean> = ObservableField()
    val showUpDownIcon: ObservableField<Boolean> = ObservableField()
    val isUpIcon: ObservableField<Boolean> = ObservableField()
    val differenceValue: ObservableField<String> = ObservableField()
    var isRefreshingData = true
    lateinit var currentSeason: String

    fun invalidateDataSource(){
        rankPageDataSourceFactory.rankLiveDataSource.value?.invalidate()
    }

    fun requestMyRanking() {        // 튜토리얼에 정보를 보여주기 위해 요청
        RankRepository.getRank(RankingPlusActivity.Category.TOTAL.categoryName,0, 1,
            object : RankDataSource.RankCallback {
                override fun onSuccessWithList(list: List<RankData>) {}

                override fun onSuccessWithMyRank(myRank: RankData) {
                    myReport.set(myRank)
                    gradeIcon.set(GradeIcon(RankingPlusActivity.Category.TOTAL.categoryName, myRank.rank.level))
                    isProgressing.set(myRank.rank.level < TotalRankingViewModel.TOTAL_MAX_LEVEL)
                    areAtTheTop.set(myRank.number in 1..100)
                }

                override fun onSuccessWithSeasonPeriod(
                    season: String,
                    startDate: String?,
                    endDate: String?
                ) {
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
                }

                override fun onFailed(reason: String) {
                    showToast("마이 랭킹을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }
            })
    }

    fun moveToNext() {
        navigator.getContext().startActivity(Intent(navigator.getContext(), SeasonRankingActivity::class.java))
    }

    fun moveToGradeInfo() {
        category?.let {
            navigator.getContext().startActivity(
                Intent(navigator.getContext(), RankingGradeInfoActivity::class.java).apply {
                    this.putExtra(GUIDE_CATEGORY, it)
                }
            )
        }
    }

    fun moveToCampaign() {
        navigator.getContext().startActivity(Intent(navigator.getContext(), CampaignActivity::class.java))
    }

    fun moveToShare() {
        category?.let {
            navigator.getContext().startActivity(
                Intent(navigator.getContext(), ShareActivity::class.java).apply {
                    putExtra(ShareActivity.SHARE_TYPE, it.toUpperCase(Locale.ROOT))
                    putExtra(ShareActivity.SHARE_RANK, myReport.get()?.number)
                    putExtra(ShareActivity.SHARE_GRADE, myReport.get()?.rank?.name)
                    putExtra(ShareActivity.SHARE_DONATED_STEP, myReport.get()?.donatedSteps)
                }
            )
        }
    }

    fun moveToRankInfo() {
        navigator.getContext().startActivityForResult(
            Intent(navigator.getContext(), RankingInformationActivity::class.java),
            RankingPlusActivity.REQUEST_RANKING_INFO
        )
    }

    fun finish() {
        /*when (category) {       // 종료 전 카테고리 별로 현 랭크 정보를 저장한다.
            RankingPlusActivity.Category.TODAY.categoryName -> {
                val cal = Calendar.getInstance()
                val keyName = cal.toTagString()

                if (myReport.get()!=null) {
                    manageTodayRankNumber(cal, keyName)
                }
            }
            else -> {   // RankingPlusActivity.Category.TOTAL.categoryName
                if (myReport.get()!=null) {
                    manageTotalRankNumber(currentSeason)
                }
            }
        }*/
        navigator.finish()
    }

    fun getTotalDonatedStepsText() = longValueToCommaString(myReport.get()?.report?.totalDonatedSteps?:0) + navigator.getContext().resources.getString(R.string.step)
    fun getReducedCarbonAmountText() = longValueToCommaString(myReport.get()?.report?.reducedCarbonAmount?:0) + navigator.getContext().resources.getString(R.string.unit_kg)
    fun getPlantingAmountText() = longValueToCommaString(myReport.get()?.report?.plantingAmount?:0) + navigator.getContext().resources.getString(R.string.unit_tree)
    fun getMyReducedCarbonAmountText() = longValueToCommaString(myReport.get()?.report?.myReducedCarbonAmount?:0) + navigator.getContext().resources.getString(R.string.unit_kg)
    fun getMyPlantingAmountText() = longValueToCommaString(myReport.get()?.report?.myPlantingAmount?:0) + navigator.getContext().resources.getString(R.string.unit_tree)

    private fun handleRankNumber(keyName: String) {
        val localData = PreferenceManager.getRankNumber(keyName)
        val serverData = myReport.get()!!
        if (localData!=null) {
            val localRank = localData.split("-")[0].toLong()
            val localNumber = localData.split("-")[1].toLong()
            if (serverData.number<=100) {
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
}