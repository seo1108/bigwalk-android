package kr.co.bigwalk.app.campaign.donation

import kr.co.bigwalk.app.data.campaign.dto.*
import kr.co.bigwalk.app.extension.valueToCommaString

data class DonationData(
    val campaignId: Long,
    val achievmentProgress: Int? = null,
    val event: CampaignEventResponse? = null,
    val service: ServiceResponse? = null,
    val todayDonatedSteps: Long? = null,
    val maxDonationPerDay: Long? = null
) {
    fun getTodayDonatedStepsToString() = todayDonatedSteps?.valueToCommaString()
    fun getMaxDonationPerDayToString() = maxDonationPerDay?.valueToCommaString()
}

fun makeDonationData(campaign: CampaignAndStoryResponse): DonationData {
    return DonationData(
        campaign.id,
        campaign.getStepAchievmentProgress(),
        campaign.event,
        campaign.service,
        campaign.todayDonatedSteps,
        campaign.maxDonationPerDay
    )
}

fun makeDonationData(campaign: ResponseCampaign): DonationData {
    return DonationData(
        campaign.id,
        campaign.getStepAchievmentProgress(),
        campaign.event,
        campaign.service,
        campaign.my.todayDonatedSteps,
        campaign.maxDonationPerDay
    )
}
