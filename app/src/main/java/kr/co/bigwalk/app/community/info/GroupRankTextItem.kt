package kr.co.bigwalk.app.community.info

data class GroupRankTextItem(
    val groupCount: String,
    val level: String,
    val nextGapPoint: String,
    val rankChange: RankChangeStatus = RankChangeStatus.NONE
)

enum class RankChangeStatus {
    UP,
    DOWN,
    NONE
}