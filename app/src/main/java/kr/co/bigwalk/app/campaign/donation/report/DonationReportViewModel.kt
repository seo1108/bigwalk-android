package kr.co.bigwalk.app.campaign.donation.report

import androidx.databinding.ObservableField
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.campaign.dto.DonationReportResponse
import kr.co.bigwalk.app.data.campaign.repository.CampaignDataSource
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.util.showToast

class DonationReportViewModel(private val navigator: BaseNavigator) {

    val donatedTimes: ObservableField<String> = ObservableField()
    val donatedSteps: ObservableField<String> = ObservableField()

    init {
        CampaignRepository.getDonationReport(object : CampaignDataSource.GetDonationReportCallback {
            override fun onSuccess(donationReportResponse: DonationReportResponse) {
                donatedTimes.set(donationReportResponse.getDonatedTimesString())
                donatedSteps.set(donationReportResponse.getDonatedStepsString())
            }

            override fun onFailed(reason: String) {
                showToast("기부 리포트를 확인할 수 없습니다. 다시 시도해주세요.")
                navigator.finish()
            }

        })
    }

    fun finish() {
        navigator.finish()
    }
}