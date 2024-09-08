package kr.co.bigwalk.app.data.campaign.dto

data class RankGuide(
    val level: Long,
    val startPoint: Long,
    val endPoint: Long,
    val rank: String,
    val reward: String?,
    val description: String?
) {
    fun getLevelText(): String {
        return "Lv.$level"
    }
}
