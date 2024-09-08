package kr.co.bigwalk.app.data.campaign.dto

import androidx.core.content.ContextCompat
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.campaign.CampaignStatus
import kr.co.bigwalk.app.util.longValueToCommaString
import java.util.*

// 캠페인 뷰 업데이트하면서 내 기부관련 내역도 같이 포함한 dto
data class ResponseCampaign (
    val id: Long,
    val smsId: Long,
    val name: String,
    val detailThumbnailImagePath: String,    //상세 썸네일용 이미지 720x720
    val largeListThumbnailImagePath: String, //카드뷰 큰 이미지 960x640
    val mediumListThumbnailImagePath: String,//카드뷰 중간 이미지 464x352
    val smallListThumbnailImagePath: String, //리스트 작은 썸네일 176x176
    val goalPoint: Long,
    val donatedSteps: Long,
    val startDate: Date,
    val endDate: Date,
    val participantCount: Long,
    val participationDate: Date?,
    var maxDonationPerDay: Long?,
    val campaignPromoter: CampaignPromoterResponse?,
    val beneficiary:String?,
    val beneficiaryLink:String?,
    val enabled: Boolean,
    val organizations: List<CampaignOrganizationResponse>,
    val my: DonationInfoResponse,
    val event: CampaignEventResponse,
    val service: ServiceResponse?,
    val remainedDaysToEnd: Int?
) {
    fun getStepAchievementRate(): String {
        if (goalPoint != 0L) {
            val achievementRate = donatedSteps * 100 / goalPoint
            return "${achievementRate}%"
        }
        return "0%"
    }

    fun getStepAchievmentProgress() : Int {
        if (goalPoint == 0L) return 0
        return (donatedSteps * 100 / goalPoint).toInt()
    }

    fun getDueDateString(): String {
        remainedDaysToEnd?.let { remainedDaysToEnd ->
            return if (remainedDaysToEnd in 0..2) {
                "D-${remainedDaysToEnd+1}"
            } else {
                ""
            }
        }
        return ""
    }

    fun getDonatedStepsString(): String {
        return longValueToCommaString(donatedSteps)
    }

    fun getPromoterName():String {
        return campaignPromoter?.name ?: ""
    }

    fun isEndedCampaign(): Boolean {
        return endDate < Date()
    }

    fun getCampaignType(): String {
        return if (organizations.isNullOrEmpty()) "공개형" else "기업형"
    }

    fun isOrganizationType(): Boolean {
        return !organizations.isNullOrEmpty()
    }

    private fun getStatus(): CampaignStatus {
        if (isEndedCampaign()) {
            if (my.story)
                return CampaignStatus.RESULT_POST
            else
                return CampaignStatus.DONATION_CLOSE
        } else {
            return CampaignStatus.DONATION_PROGRESS
        }
    }

    fun isResultPost(): Boolean {
        return getStatus() == CampaignStatus.RESULT_POST
    }

    fun getStatusText(): String {
        return when(getStatus()) {
            CampaignStatus.DONATION_PROGRESS, CampaignStatus.HOTTIME -> {
                "진행중"
            }
            CampaignStatus.DONATION_CLOSE -> {
                "종료"
            }
            CampaignStatus.RESULT_POST -> {
                "기부완료"
            }
        }
    }

    fun getStatusColor(): Int {
        BigwalkApplication.context?.let { context ->
            return when(getStatus()) {
                CampaignStatus.DONATION_PROGRESS, CampaignStatus.HOTTIME -> {
                    ContextCompat.getColor(context, R.color.dark_sky_blue)
                }
                CampaignStatus.DONATION_CLOSE -> {
                    ContextCompat.getColor(context, R.color.pale_red)
                }
                CampaignStatus.RESULT_POST -> {
                    ContextCompat.getColor(context, R.color.deep_lavender)
                }
            }
        }
        return 0
    }

    fun getEventTypeName(): String {
        event?.let { event ->
            return event.getEventTypeName()
        }
        return "?"
    }

    fun getAdditionalServiceTypeName(): String {
        service?.let { service ->
            return service.getServiceTypeName()
        }
        return "?"
    }

    fun isConsumption(): Boolean {
        service?.let {
            return it.type == ServiceType.VALUE_CONSUMPTION.value
        }
        return false
    }
    
    fun isMission(): Boolean {
        service?.let {
            return it.type == ServiceType.MISSION.value
        }
        return false
    }
}
