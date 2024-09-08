package kr.co.bigwalk.app.data.campaign.dto

import java.util.*

data class CampaignEventResponse(
    val type: String,
    val description: String,
    val startDate: Date?,
    val endDate: Date,
    val extra1: String?,
    val extra2: String?
) {
    fun getEventTypeName(): String {
        if (type == CampaignEventType.HOTTIME.value) {
            extra1?.let {
                return "걸음x${it}"
            }
        } else if (type == CampaignEventType.EVENT.value) {
            return "이벤트"
        }
        return type
    }
}