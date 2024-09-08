package kr.co.bigwalk.app.lock_screen

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.campaign.dto.RankingResponse
import kr.co.bigwalk.app.data.campaign.repository.CampaignDataSource
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.util.showDebugToast


class LockScreenViewModel(private val navigator: LockScreenNavigator) : BaseObservable() {
    val todayStep: ObservableField<String> =
        ObservableField(PreferenceManager.getDailyStep().toString())
    val donableStep: ObservableField<String> =
        ObservableField(PreferenceManager.getDonableStep().toString())
    val kcalText: ObservableField<String> =
        ObservableField("${PreferenceManager.getKcalorie()}kcal")
    val randomText: ObservableField<String> = ObservableField("단 한 걸음으로\n세상을 바꿀 수 있어요.")
    val donationRank: ObservableField<String> = ObservableField("-")
    val totalDonationStep: ObservableField<String> = ObservableField("0")

    init {
        refreshMyRank()
    }

    fun refreshMyRank(){
        CampaignRepository.getMyRankInCampaigns(object : CampaignDataSource.GetMyRankInCampaignsCallback {
            override fun onSuccess(rankingResponse: RankingResponse) {
                donationRank.set(rankingResponse.rank.toString())
                totalDonationStep.set(rankingResponse.getTotalDonatedSteps())
            }

            override fun onFailed(reason: String) {
                donationRank.set("-")
                totalDonationStep.set("-")
            }
        })
    }

    fun notifyStepChanged() {
        this.todayStep.set(PreferenceManager.getDailyStep().toString())
        this.donableStep.set(PreferenceManager.getDonableStep().toString())
        this.kcalText.set("${PreferenceManager.getKcalorie()}kcal")
        this.navigator.setProgressPercentage(PreferenceManager.getDonableStepPercentage())
    }

    fun onClickListener () {
        showDebugToast("translate")
    }

}