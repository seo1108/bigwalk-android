package kr.co.bigwalk.app.data.notification

import com.google.gson.Gson
import kr.co.bigwalk.app.R
import java.text.SimpleDateFormat
import java.util.*

data class Notification(
    val notiType: Int,
    val title: String,
    val content: String,
    val notiTypeName: String,
    val data: String,
    val createdTime: Date
) {

    fun notiDrawable(): Int {
        return when(notiType) {
            1 -> R.drawable.ico_16_notice_campaign
            2 -> R.drawable.ico_16_notice_story
            3 -> R.drawable.ico_16_notice_ranking
            4 -> R.drawable.ico_16_notice_advertise
            5 -> R.drawable.ico_16_notice_event
            7 -> R.drawable.ico_16_notice_event
            14, 15 -> R.drawable.aos_icon_16_notice_guide
            else -> R.drawable.ico_16_modify
        }
    }

    fun notiTypeString(): String {
        return when(notiTypeName) {
            "CAMPAIGN" -> "캠페인"
            "STORY" -> "포스트"
            "RANKING" -> "랭킹"
            "AD" -> "광고"
            "EVENT" -> "이벤트"
            "EVENT_WIN" -> "이벤트 당첨"
            "CREW_CAMPAIGN_AUDIT" -> "안내"
            else -> "일반"
        }
    }

    fun receivedDate(): String {
        val dateFormatForCompare = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)
        val today = Date()
        val beforeTodayDateFormat = SimpleDateFormat("yy.MM.dd", Locale.KOREA)
        val todayDateFormat = SimpleDateFormat("a hh:mm", Locale.KOREA)
        return if (dateFormatForCompare.format(createdTime) != dateFormatForCompare.format(today)) {
            beforeTodayDateFormat.format(createdTime)
        } else {
            todayDateFormat.format(createdTime)
        }
    }

    fun getDataMap(): HashMap<String, String> {
        if (data == null) return HashMap()
        val map = Gson().fromJson(data, HashMap::class.java) as HashMap<String, String>
        return map
    }
}