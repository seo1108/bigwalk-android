package kr.co.bigwalk.app.data.campaign.dto

import java.util.*

data class ValueConsumptionCommerceResponse(
    val id: Long,
    val title: String,
    val content: String,
    val importantContent: String,
    val startDate: Date,
    val endDate: Date,
    val point: Int,
    val reward: String?,
    val purchaseUrl: String,
    val extra1: String,
    val extra2: String,
    val extra3: String,
    val imagePath: String
)