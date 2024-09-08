package kr.co.bigwalk.app.report

import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.report.dto.*
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.intValueToCommaString
import kr.co.bigwalk.app.util.longValueToCommaString
import kr.co.bigwalk.app.util.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.UnknownHostException

class Report2ViewModel : ViewModel() {

    private val _dayOfWeekReportResponse = MutableLiveData<DayOfWeekReportResponse>()
    val dayOfWeekReportResponse: LiveData<DayOfWeekReportResponse> get() = _dayOfWeekReportResponse

    private val _dayOfMonthReportResponse = MutableLiveData<DayOfMonthReportResponse>()
    val dayOfMonthReportResponse: LiveData<DayOfMonthReportResponse> get() = _dayOfMonthReportResponse

    private val _totalDonationSteps = MutableLiveData<SpannableString>()
    val totalDonationSteps: LiveData<SpannableString> get() = _totalDonationSteps

    private val _stepItem = MutableLiveData<Pair<String, String>>()
    val stepItem: LiveData<Pair<String, String>> get() = _stepItem

    val isShowingSelectData = MutableLiveData<Boolean>()

    private var diffFormThisWeek: Long = 0
    private var diffFormThisMonth: Long = 0
    val isCurrentWeek = MutableLiveData<Boolean>()
    val isCurrentMonth = MutableLiveData<Boolean>()
    val isExistPreviousWeek = MutableLiveData<Boolean>()
    val isExistPreviousMonth = MutableLiveData<Boolean>()

    val startGif = MutableLiveData<Boolean>(false)

    init {
        getStepReportFromWeek(0)
        fetchEnergyEffect()
        fetchCarbonEffect()
        fetchDonationRatio()
    }

    fun changeDataByPeriodShown(position: Int) {
        isShowingSelectData.value = false
        when (position) {
            0 -> {
                _totalDonationSteps.value =
                    setSpannableString("일주일 동안\n총 ${intValueToCommaString(dayOfWeekReportResponse.value?.totalDonationStep ?: 0)} 걸음 기부했어요!")
            }
            1 -> {
                _totalDonationSteps.value =
                    setSpannableString("한 달 동안\n총 ${longValueToCommaString(dayOfMonthReportResponse.value?.totalDonationStep ?: 0)} 걸음 기부했어요!")
            }
        }
    }

    fun replaceSelectStepReport(dayNumber: Int) {
        val selectDay = dayOfWeekReportResponse.value?.reportWalks?.find { dayNumber == it.dayOfWeek.numOfDay }
        _stepItem.value = Pair(intValueToCommaString(selectDay?.dailySteps ?: 0), intValueToCommaString(selectDay?.dailyDonationSteps ?: 0))
    }

    fun replaceSelectStepReport(dailySteps: Int, dailyDonationSteps: Int) {
        _stepItem.value = Pair(intValueToCommaString(dailySteps), intValueToCommaString(dailyDonationSteps))
    }

    fun selectStepReport() {
        isShowingSelectData.value = true
    }

    fun unSelectStepReport() {
        isShowingSelectData.value = false
    }

    fun getStepReportFromWeek(changeNum: Long) {
        val showNum = diffFormThisWeek - changeNum
        isCurrentWeek.value = showNum <= 0
        if (showNum < 0) return
        diffFormThisWeek = showNum
        RemoteApiManager.getReportApi().getStepReportFromWeek(diffFormThisWeek)
            .enqueue(object : Callback<BaseResponse<DayOfWeekReportResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<DayOfWeekReportResponse>>,
                    response: Response<BaseResponse<DayOfWeekReportResponse>>
                ) {
                    when (response.body()?.result) {
                        Result.SUCCESS -> {
                            response.body()?.data?.let {
                                _dayOfWeekReportResponse.value = it
                                changeDataByPeriodShown(0)
                                isExistPreviousWeek.value = it.existPrevious
                            }
                        }
                        Result.FAIL -> {
                            showToast(response.body()?.message.orEmpty())
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<DayOfWeekReportResponse>>, t: Throwable) {
                    DebugLog.d(t.localizedMessage)
                    when (t) {
                        is UnknownHostException -> {
                            showToast("리포트를 확인할 수 없습니다. 다시 시도해주세요.")
                        }
                    }
                }
            })
    }

    fun getStepReportFromMonth(changeNum: Long) {
        val showNum = diffFormThisMonth - changeNum
        isCurrentMonth.value = showNum <= 0
        if (showNum < 0) return
        diffFormThisMonth = showNum
        RemoteApiManager.getReportApi().getStepReportFromMonth(diffFormThisMonth)
            .enqueue(object : Callback<BaseResponse<DayOfMonthReportResponse>> {
                override fun onResponse(
                    call: Call<BaseResponse<DayOfMonthReportResponse>>,
                    response: Response<BaseResponse<DayOfMonthReportResponse>>
                ) {
                    when (response.body()?.result) {
                        Result.SUCCESS -> {
                            response.body()?.data?.let {
                                _dayOfMonthReportResponse.value = it
                                changeDataByPeriodShown(1)
                                isExistPreviousMonth.value = it.existPrevious
                            }
                        }
                        Result.FAIL -> {
                            showToast(response.body()?.message.orEmpty())
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<DayOfMonthReportResponse>>, t: Throwable) {
                    DebugLog.d(t.localizedMessage)
                }
            })
    }

    private val _energyEffect = MutableLiveData<EnergyEffectResponse>()
    val energyEffect: LiveData<EnergyEffectResponse> get() = _energyEffect

    private val _carbonEffect = MutableLiveData<CarbonEffectResponse>()
    val carbonEffect: LiveData<CarbonEffectResponse> get() = _carbonEffect

    private val _donationRatio = MutableLiveData<DonationRatioResponse>()
    val donationRatio: LiveData<DonationRatioResponse> get() = _donationRatio

    private fun fetchEnergyEffect() {
        RemoteApiManager.getReportApi().getEnergyEffect()
            .enqueue(object : Callback<BaseResponse<EnergyEffectResponse>> {
                override fun onResponse(call: Call<BaseResponse<EnergyEffectResponse>>, response: Response<BaseResponse<EnergyEffectResponse>>) {
                    when (response.body()?.result) {
                        Result.SUCCESS -> {
                            response.body()?.data?.let { _energyEffect.value = it }
                        }
                        Result.FAIL -> {
                            showToast(response.body()?.message.orEmpty())
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<EnergyEffectResponse>>, t: Throwable) {
                    DebugLog.d(t.localizedMessage)
                }
            })
    }

    private fun fetchCarbonEffect() {
        RemoteApiManager.getReportApi().getCarbonEffect()
            .enqueue(object : Callback<BaseResponse<CarbonEffectResponse>> {
                override fun onResponse(call: Call<BaseResponse<CarbonEffectResponse>>, response: Response<BaseResponse<CarbonEffectResponse>>) {
                    when (response.body()?.result) {
                        Result.SUCCESS -> {
                            response.body()?.data?.let { _carbonEffect.value = it }
                        }
                        Result.FAIL -> {
                            showToast(response.body()?.message.orEmpty())
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<CarbonEffectResponse>>, t: Throwable) {
                    DebugLog.d(t.localizedMessage)
                }
            })
    }

    private fun setSpannableString(str: String): SpannableString {
        val spannable = SpannableString(str)
        val startIndex = str.indexOfFirst { it.isDigit() }
        val lastIndex = str.indexOfLast { it.isDigit() } + " 걸음".length
        spannable.setSpan(ForegroundColorSpan(Color.parseColor("#174dfe")), startIndex, lastIndex + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return spannable
    }

    private fun fetchDonationRatio() {
        RemoteApiManager.getReportApi().getDonationRatio()
            .enqueue(object : Callback<BaseResponse<DonationRatioResponse>> {
                override fun onResponse(call: Call<BaseResponse<DonationRatioResponse>>, response: Response<BaseResponse<DonationRatioResponse>>) {
                    when (response.body()?.result) {
                        Result.SUCCESS -> {
                            response.body()?.data?.let { _donationRatio.value = it }
                        }
                        Result.FAIL -> {
                            showToast(response.body()?.message.orEmpty())
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<DonationRatioResponse>>, t: Throwable) {
                    DebugLog.d(t.localizedMessage)
                }

            })
    }

    fun setStartGif(play: Boolean) {
        startGif.value = play
    }
}