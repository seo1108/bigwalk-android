package kr.co.bigwalk.app.data.report.dto

data class OrdinalWeekStatsResponse(
    val order: Int,
    val range: String,
    val totalCalories: Long,// 주간 총 소모칼로리
    val avgCalories: Long,// 주간 평균 소모칼로리
    val totalSteps: Long,// 주간 총 걸음수
    val avgSteps: Long,// 주간 평균 걸음수
    val totalDonationCount: Long,// 주간 총 기부횟수
    val totalDonationSteps: Long// 주간 총 기부걸음수
) {
    fun getWeekOrder(): String {
        return "${order}주차"
    }

}