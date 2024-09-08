package kr.co.bigwalk.app.data.notification

enum class NotiType(val notiType: String) {
    CAMPAIGN("campaign"),
    RANKING("ranking"),
    STORY("story"),
    WALK("walk"),
    MARKETING("marketing")
}

enum class NotiTypeId(val id: Int) {
    CAMPAIGN(1),
    STORY(2),
    EVENT(5),
    EVENT_WIN(7),
    FEED_LIKE(8),
    FEED_COMMENT(9),
    CREW_CAMPAIGN_AUDIT(14),
}