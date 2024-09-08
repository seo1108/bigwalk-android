package kr.co.bigwalk.app.notification

data class ChannelData(val id: String, val name: String)

enum class FCMChannels(val channelData: ChannelData) {
    WALK(ChannelData("STEP_ID", "걸음 측정")),
    CAMPAIGN(ChannelData("CAMPAIGN_ID", "캠페인")),
    WALK_MAXED(ChannelData("WALK_MAXED_ID", "만보 달성")),
    NORMAL(ChannelData("NORMAL", "일반")),
    EVENT(ChannelData("EVENT", "이벤트")),
    MISSION(ChannelData("MISSION", "미션"));
}