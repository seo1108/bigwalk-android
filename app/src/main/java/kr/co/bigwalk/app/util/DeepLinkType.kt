package kr.co.bigwalk.app.util

class SafetyDeepLinkType {
    companion object {
        fun from(type: String?): DeepLinkType = DeepLinkType.values().find { it.name == type } ?: DeepLinkType.NONE
    }
}
enum class DeepLinkType(val value: String) {
    SPACE_GROUP("SPACE_GROUP"),
    CAMPAIGN_DETAIL("CAMPAIGN_DETAIL"),
    GROUP_JOIN("GROUP_JOIN"),
    CREW_CAMPAIGN("CREW_CAMPAIGN"),
    CREW_JOIN("CREW_JOIN"),
    NONE("")
}
