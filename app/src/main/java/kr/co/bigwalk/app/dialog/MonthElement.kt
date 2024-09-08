package kr.co.bigwalk.app.dialog

import java.text.SimpleDateFormat
import java.util.*

data class MonthElement(val date: Date): ListElement {

    override fun getElementTitle(): String {
        val simpleDateFormat = SimpleDateFormat("yyyy년 MM월", Locale.KOREA)
        return simpleDateFormat.format(date)
    }

    fun getMonthTitle(): String {
        return SimpleDateFormat("MM월", Locale.KOREA).format(date)
    }

}