package kr.co.bigwalk.app.data.api

import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.report.dto.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ReportApi {
    // 리포트 API - 주간 통합
    @GET("/api/report/week")
    fun getStepReportFromWeek(
        @Query("diffFromThisWeek") diffFromThisWeek: Long
    ): Call<BaseResponse<DayOfWeekReportResponse>>

    // 리포트 API - 월간 통합
    @GET("/api/report/month")
    fun getStepReportFromMonth(
        @Query("diffFromThisMonth") diffFromThisMonth: Long
    ): Call<BaseResponse<DayOfMonthReportResponse>>

    // 리포트 API - 에너지 효과
    @GET("/api/report/energy-effect")
    fun getEnergyEffect(): Call<BaseResponse<EnergyEffectResponse>>

    // 리포트 API - 탄소량 효과
    @GET("/api/report/carbon-effect")
    fun getCarbonEffect(): Call<BaseResponse<CarbonEffectResponse>>

    // 리포트 API - 기부 비율
    @GET("/api/report/donation-ratio")
    fun getDonationRatio(): Call<BaseResponse<DonationRatioResponse>>

}