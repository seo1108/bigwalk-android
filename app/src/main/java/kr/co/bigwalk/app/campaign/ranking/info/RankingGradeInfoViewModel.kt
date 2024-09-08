package kr.co.bigwalk.app.campaign.ranking.info

import android.content.Intent
import android.net.Uri
import androidx.databinding.BaseObservable
import androidx.lifecycle.MutableLiveData
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.campaign.dto.RankGuide
import kr.co.bigwalk.app.data.campaign.repository.RankDataSource
import kr.co.bigwalk.app.data.campaign.repository.RankRepository
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class RankingGradeInfoViewModel(
    private val navigator: BaseNavigator,
    private val category: String
    ): BaseObservable() {
    val gradeList: MutableLiveData<List<RankGuide>> = MutableLiveData()

    init {
        requestRankGuide(category)
    }

    private fun requestRankGuide(category: String) {
        RankRepository.getRankGuide(category, object : RankDataSource.RankGuideCallback {
            override fun onSuccess(rankResponse: List<RankGuide>) {
                gradeList.postValue(rankResponse)
            }

            override fun onFailed(reason: String) {
                showToast("랭킹 가이드를 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(CampaignException(reason))
            }
        })
    }

    fun finish() {
        navigator.finish()
    }

    fun moveToWeb() {
        navigator.getContext().startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(navigator.getContext().getString(R.string.abusing_policy_url))
            }
        )
    }
}