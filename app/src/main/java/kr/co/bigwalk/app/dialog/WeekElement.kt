package kr.co.bigwalk.app.dialog

data class WeekElement(val selectedMonth: MonthElement?, val weekOfMonth: Int): ListElement {

    override fun getElementTitle(): String {
        if (weekOfMonth == 0) {
            return "${selectedMonth?.getElementTitle()} 전체"
        }
        return "${selectedMonth?.getElementTitle()} ${weekOfMonth}주차"
    }

    fun getWeekTitle(): String {
        if (weekOfMonth == 0) {
            return "전체"
        }
        return "${weekOfMonth}주차"
    }

}