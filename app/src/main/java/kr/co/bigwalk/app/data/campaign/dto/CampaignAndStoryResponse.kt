package kr.co.bigwalk.app.data.campaign.dto

import android.view.View
import kr.co.bigwalk.app.data.story.dto.Story
import kr.co.bigwalk.app.util.longValueToCommaString
import java.text.SimpleDateFormat
import java.util.*

data class CampaignAndStoryResponse(
    val id: Long,
    val name: String,
    val smsId: Long,
    val detailThumbnailImagePath: String,    //상세 썸네일용 이미지 720x720
    val largeListThumbnailImagePath: String, //카드뷰 큰 이미지 960x640
    val mediumListThumbnailImagePath: String,//카드뷰 중간 이미지 464x352
    val smallListThumbnailImagePath: String, //리스트 작은 썸네일 176x176
    val goalPoint: Long,
    val donatedSteps: Long,
    val startDate: Date,
    val endDate: Date,
    val participantCount: Long,
    val enabled: Boolean,
    val todayDonatedSteps: Long,
    val maxDonationPerDay: Long?,
    val campaignPromoter: CampaignPromoterResponse?,
    val beneficiary: String?,
    val beneficiaryLink: String?,
    val beneficiaryBtn: String?,
    val stories: List<Story>,
    val organizations: List<CampaignOrganizationResponse>,
    val event: CampaignEventResponse?,
    val service: ServiceResponse?,
    val campaignFunding: CampaignFundingResponse
) {

    fun getStepAchievementRate(): String {
        if (goalPoint != 0L) {
            val achievementRate = donatedSteps * 100 / goalPoint
            return "${achievementRate}%"
        }
        return "0%"
    }

    fun getStepAchievmentProgress() : Int {
        if (goalPoint != 0L) {
            return (donatedSteps * 100 / goalPoint).toInt()
        }
        return 0
    }

    fun getGoalStepString(): String {
        return longValueToCommaString(goalPoint)
    }

    fun getCampaignPeriod(): String {
        val simpleDateFormatter = SimpleDateFormat("yy.MM.dd", Locale.KOREA)
        return simpleDateFormatter.format(startDate) + "~" + simpleDateFormatter.format(endDate)
    }

    fun getDonatedStepsString(): String {
        return longValueToCommaString(donatedSteps)
    }

    fun getPromoterName():String {
        return campaignPromoter?.name ?: ""
    }

    fun getParticipantCountString() : String {
        return "${participantCount}명 참여"
    }

    fun getBeneficiaryExistsVisibility(): Int{
        if(beneficiary.isNullOrBlank()) return View.GONE
        return View.VISIBLE
    }

    fun getBeneficiaryLinkExistsVisibility(): Int{
        if(beneficiaryLink.isNullOrBlank()) return View.GONE
        return View.VISIBLE
    }

    fun getBeneficiaryBtnString(): String {
        if(beneficiaryBtn.isNullOrBlank()) return "바로가기"
        return beneficiaryBtn
    }

    fun getEventTypeName(): String {
        event?.let { event ->
            if (event.type == CampaignEventType.HOTTIME.value) {
                event.extra1?.let {
                    return "걸음x${it}"
                }
            } else if (event.type == CampaignEventType.EVENT.value) {
                event.extra1?.let {
                    return it
                }
                return "이벤트"
            } else {
                return event.type
            }
        }
        return ""
    }
}

data class CampaignFundingResponse (
    val crewCampaignId: Long?,
    val isFundingDonated: Boolean?
)