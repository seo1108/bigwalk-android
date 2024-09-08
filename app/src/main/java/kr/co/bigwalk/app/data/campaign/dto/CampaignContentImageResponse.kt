package kr.co.bigwalk.app.data.campaign.dto

import kr.co.bigwalk.app.data.ImageResource

data class CampaignContentImageResponse (
    val id: Long,
    override val imagePath: String
) : ImageResource