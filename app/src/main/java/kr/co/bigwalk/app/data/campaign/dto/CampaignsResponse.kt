package kr.co.bigwalk.app.data.campaign.dto

import android.view.View
import kr.co.bigwalk.app.extension.valueToCommaString
import kr.co.bigwalk.app.util.showDebugToast
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

data class CampaignPromoterResponse(val id: Long, val name: String) : Serializable
data class CampaignOrganizationResponse(val id: Long): Serializable

data class CampaignsResponse(
    val id: Long,
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
    val campaignPromoter: CampaignPromoterResponse?,
    val beneficiary:String?,
    val beneficiaryLink:String?,
    val enabled: Boolean,
    val organizations: List<CampaignOrganizationResponse>
) : Serializable {

    fun getStepAchievementRate(): String {
        val achievementRate = donatedSteps * 100 / goalPoint
        return "${achievementRate}%"
    }

    fun getStepAchievmentProgress() : Int {
        return (donatedSteps * 100 / goalPoint).toInt()
    }

    fun getGoalStepString(): String {
        return goalPoint.valueToCommaString()
    }

    fun getCampaignPeriod(): String {
        val simpleDateFormatter = SimpleDateFormat("yy.MM.dd", Locale.KOREA)
        return simpleDateFormatter.format(startDate) + "~" + simpleDateFormatter.format(endDate)
    }

    fun getDonatedStepsString(): String {
        return donatedSteps.valueToCommaString()
    }

    fun getDueDateString(): String {
        val today = Date()
        var difference: Long = (endDate.time - today.time) / (24 * 60 * 60 * 1000)
        return if (difference in 1..7) {
            difference+=1
            "D-$difference"
        } else {
            ""
        }
    }

    fun goDonationPage(){
        showDebugToast(this.id.toString())
    }

    fun getParticipantCountString() : String {
        return "${participantCount}명 참여"
    }

    fun getPromoterName():String {
        return campaignPromoter?.name ?: ""
    }

    fun isCampaignOver(): Boolean {
        return endDate.before(Date())
    }

    fun getBeneficiaryExistsVisibility(): Int{
        if(beneficiary.isNullOrBlank()) return View.GONE
        return View.VISIBLE
    }

    fun getBeneficiaryLinkExistsVisibility(): Int{
        if(beneficiaryLink.isNullOrBlank()) return View.GONE
        return View.VISIBLE
    }
}