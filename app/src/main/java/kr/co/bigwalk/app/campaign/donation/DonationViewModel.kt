package kr.co.bigwalk.app.campaign.donation

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.FrameLayout
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.Observable
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kr.co.bigwalk.app.BuildConfig
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignDonateDataManager
import kr.co.bigwalk.app.campaign.detail.CampaignDetailActivity
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.NetworkState
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.api.UserWalkLogRequest
import kr.co.bigwalk.app.data.campaign.dto.*
import kr.co.bigwalk.app.data.campaign.repository.CampaignDataSource
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.event.alarm.EventManager
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.share.ShareCampaignActivity
import kr.co.bigwalk.app.util.BlackUser
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast
import kr.co.bigwalk.app.walk.alarm.StepManager
import kr.co.bigwalk.app.walk.sensor.WalkServiceManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

open class DonationViewModel(private val activity: Activity, private val donationData: DonationData) {

    val maxDonableSteps: ObservableInt = ObservableInt(PreferenceManager.getDonableStep())
    val stepsToDonate : ObservableInt = ObservableInt(1)
    val stepsToDonateLabel : ObservableInt = ObservableInt(1)
    lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    var donationCompleteBottomSheetBehavior: BottomSheetBehavior<FrameLayout>? = null
    val profile : ObservableField<UserProfileResponse> = ObservableField()
    val basicColor: ObservableField<Int> = ObservableField(ContextCompat.getColor(activity, R.color.blue))
    val progressDrawable: ObservableField<Drawable> = ObservableField()
    val seekBarThumbDrawable: ObservableField<Drawable> = ObservableField()
    val donationButtonIconImage: ObservableField<Drawable> = ObservableField()
    var eventNameLabel: ObservableField<String> = ObservableField("")
    var eventDescriptionLabel: ObservableField<String> = ObservableField("")
    var completeMessage: ObservableField<SpannableStringBuilder> = ObservableField()
    val event = donationData.event
    var commerce: ObservableField<ValueConsumptionCommerceResponse> = ObservableField()
    val networkState: ObservableField<NetworkState> = ObservableField()
    val donationItem = donationData
    val isCompleteCampaignTerms: ObservableField<Boolean> = ObservableField(false)
    val playAnimation = MutableLiveData<Unit>()
    val notPlayAnimation = MutableLiveData<Unit>()

    init{
        StepManager.uploadAllWalksAndClear() {
            DebugLog.d("캠페인 카테고리 도네 walk upload")
            setMaxDonableSteps()
        }

        initialize()
    }
    
    private fun setMaxDonableSteps() {
        if (donationData.todayDonatedSteps != null && donationData.maxDonationPerDay != null) {
            val remainingSteps = donationData.maxDonationPerDay - donationData.todayDonatedSteps
            val minResult = min(remainingSteps.toInt(), PreferenceManager.getDonableStep())
            if (remainingSteps <= 0L) {
                maxDonableSteps.set(0)
                isCompleteCampaignTerms.set(true)
                return
            }
            if (donationData.event != null) {
                donationData.event.let { event ->
                    if (event.type == CampaignEventType.HOTTIME.value) {
                        event.extra1?.let { multiple ->
                            maxDonableSteps.set(
                                min(
                                    ceil(remainingSteps.toDouble() / multiple.toDouble()),
                                    PreferenceManager.getDonableStep().toDouble()
                                ).toInt()
                            )
                            return
                        }
                    }
                }
            }
            maxDonableSteps.set(minResult)
        } else {
            maxDonableSteps.set(PreferenceManager.getDonableStep())
        }
    }

    private fun initialize() {
        requestMyProfile()

        basicColor.set(ContextCompat.getColor(activity, R.color.blue))
        progressDrawable.set(ContextCompat.getDrawable(activity, R.drawable.seek_bar_progress))
        seekBarThumbDrawable.set(ContextCompat.getDrawable(activity, R.drawable.seek_bar_thumb))
        donationButtonIconImage.set(ContextCompat.getDrawable(activity, R.drawable.ico_24_donation_step_white))
        val completeMessageText = activity.getString(R.string.donation_complete_message)
        completeMessage.set(getSpannableString(completeMessageText, ContextCompat.getColor(activity, R.color.main_black), 0, completeMessageText.length))

        if (donationData.event != null) {
            eventNameLabel.set(donationData.event.getEventTypeName())
            eventDescriptionLabel.set(donationData.event.description)

            if (donationData.event.type == CampaignEventType.HOTTIME.value) {
                basicColor.set(ContextCompat.getColor(activity, R.color.water_melon))
                progressDrawable.set(ContextCompat.getDrawable(activity, R.drawable.seek_bar_progress_water_melon))
                seekBarThumbDrawable.set(ContextCompat.getDrawable(activity, R.drawable.seek_bar_thumb_water_melon))
                donationButtonIconImage.set(ContextCompat.getDrawable(activity, R.drawable.ui_icon_hottime))
                val completeMessageTopText = "걸음x${donationData.event.extra1}"
                completeMessage.set(getSpannableString("$completeMessageTopText\n기부 성공!", ContextCompat.getColor(activity, R.color.water_melon), 0, completeMessageTopText.length))
            }
        }
        
        if (donationData.maxDonationPerDay != null) {
            if (donationData.event != null) {
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        playAnimation.postValue(Unit)
                    }
                }, 1000, 3000)
            } else {
                notPlayAnimation.value = Unit
            }
        }

        if (donationData.service != null && donationData.service.type == ServiceType.VALUE_CONSUMPTION.value) {
            commerce.addOnPropertyChangedCallback(object : Observable.OnPropertyChangedCallback() {
                override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
                    setMinimum()
                }
            })
            requestValueConsumptionCommerce()
        }
    }

    private fun getSpannableString(text: String, color: Int, startIndex: Int, endIndex: Int): SpannableStringBuilder {
        val ssb = SpannableStringBuilder(text)
        ssb.setSpan(ForegroundColorSpan(color), startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ssb
    }

    private fun requestMyProfile(){
        UserRepository.getMyProfile(object : UserDataSource.GetMyProfileCallback{
            override fun onSuccess(userProfileResponse: UserProfileResponse) {
                profile.set(userProfileResponse)
            }

            override fun onBlackUser(userProfileResponse: UserProfileResponse) {
                BlackUser(activity).showBlackUserMessageAlert(userProfileResponse)
                activity.finish()
            }

            override fun onFailed(reason: String) {
                showToast("프로필 정보를 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(UserProfileException(reason))
            }
        })
    }

    private fun requestValueConsumptionCommerce() {
        CampaignRepository.getAdditionalServiceValueConsumptionCommerce(donationData.campaignId, object : CampaignDataSource.AdditionalServiceValueConsumptionCommerceCallback {
            override fun onSuccess(response: ValueConsumptionCommerceResponse) {
                DebugLog.d("가치소비: $response")
                commerce.set(response)
            }

            override fun onFailed(reason: String) {
                DebugLog.d("$reason")
                showToast("진행하고 있는 챌린지가 없습니다.")
            }
        })
    }

    private fun setDonableStepLabel(step: Int) {
        var multiple = 1
        donationData.event?.let { event ->
            if (event.type == CampaignEventType.HOTTIME.value) {
                event.extra1?.let {
                    if (Integer.parseInt(it) > 0)
                        multiple = Integer.parseInt(it)
                }
            }
        }
        if (donationData.todayDonatedSteps != null && donationData.maxDonationPerDay != null) {
            val result = step * multiple
            val remainingSteps = donationData.maxDonationPerDay - donationData.todayDonatedSteps
            if (result > remainingSteps) {
                stepsToDonate.set(step)
                stepsToDonateLabel.set(remainingSteps.toInt())
                return
            }
        }
        stepsToDonate.set(step)
        stepsToDonateLabel.set(step * multiple)
    }

    val seekBarChangeListener = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if(progress < 1){
                if (donationData.todayDonatedSteps != null && donationData.maxDonationPerDay != null) {
                    if (donationData.todayDonatedSteps >= donationData.maxDonationPerDay) {
                        setDonableStepLabel(0)
                        return
                    }
                }
                setDonableStepLabel(1)
            } else {
                setDonableStepLabel(progress)
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
        }
    }

    fun setMinimum(){
        setDonableStepLabel(1)
        commerce.get()?.let {
            val step = if (PreferenceManager.getDonableStep() >= it.extra1.toInt()) it.extra1.toInt() else PreferenceManager.getDonableStep()
            setDonableStepLabel(step)
        }
    }

    fun setMiddle(){
        setDonableStepLabel(max(maxDonableSteps.get()/2, 1))
    }

    fun setMaximum(){
        setDonableStepLabel(maxDonableSteps.get())
    }

    fun checkCampaignAchieved(step: Int) {
        /*if (donationData.achievmentProgress!=null && donationData.achievmentProgress >= 100 &&
            !PreferenceManager.hasSeenCampaign100(donationData.campaignId.toString())) {
            AlertDialog.Builder(activity)
                .setMessage(activity.resources.getString(R.string.campaign_100_msg))
                .setPositiveButton(R.string.donate) { dialog, which ->
                    PreferenceManager.saveCampaign100(donationData.campaignId.toString())
                    dialog.dismiss()
                    donateStep(step)
                }.setNegativeButton(R.string.close) { dialog, which ->
                    PreferenceManager.saveCampaign100(donationData.campaignId.toString())
                    dialog.dismiss()
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }.setOnCancelListener {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                }.create().show()
        } else {
            donateStep(step)
        }*/
        if (isCompleteCampaignTerms.get() == true) {
            showCompleteCampaignTermsDialog()
        } else {
            donateStep(step)
        }
    }

    private fun donateStep(step: Int) {
        DebugLog.d("캠페인 스텝 아이디 : $donationData.campaignId")
        networkState.set(NetworkState.LOADING)
        if (donationData.event != null) {
            donationData.event.let { event ->
                when (event.type) {
                    CampaignEventType.HOTTIME.value -> {
                        // 기부 성공시 로그 데이터 전송
                        val name = PreferenceManager.getName()
                        val userId = PreferenceManager.getUserId().toString()
                        val logRequest = UserWalkLogRequest(
                            name + "",
                            userId + "",
                            "A",
                            Build.MODEL + " [Android" + Build.VERSION.RELEASE + "] [" + BuildConfig.VERSION_NAME+ "]",
                            "donate request : donate step [$step], donableStep [ " + PreferenceManager.getDonableStep() + "], from [DonationViewModel: private fun donate HOTTIME]"
                        )

                        with(RemoteApiManager) {

                            getUserApi().userReqLog(logRequest)
                                .enqueue(object : Callback<BaseResponse<Nothing>> {
                                    override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                                        when (response.body()?.result) {
                                        }
                                    }

                                    override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                                        //showToast(t.localizedMessage)
                                    }
                                })
                        }

                        CampaignRepository.donateStepForHottime(DonateRequest(step), donationData.campaignId, object : CampaignDataSource.DonateStepForHotTimeCallback {
                            override fun onSuccess(response: HotTimeDonateResponse) {
                                DebugLog.d("$step")
                                if (response.maxDonationPerDay != null) {
                                    CampaignDonateDataManager.updateCampaignDonateData.value =
                                        Triple(response.campaignId.toLong(), response.todayDonatedSteps, response.maxDonationPerDay)
                                }
                                networkState.set(NetworkState.LOADED)
                                afterUploadDonatedStep(step)

                                // 기부 성공시 로그 데이터 전송
                                val name = PreferenceManager.getName()
                                val userId = PreferenceManager.getUserId().toString()
                                val logRequest = UserWalkLogRequest(
                                    name + "",
                                    userId + "",
                                    "A",
                                    Build.MODEL + " [Android" + Build.VERSION.RELEASE + "] [" + BuildConfig.VERSION_NAME+ "]",
                                    "donate success : donate step [$step], donableStep [ " + PreferenceManager.getDonableStep() + "], from [DonationViewModel: private fun donate HOTTIME]"
                                )

                                with(RemoteApiManager) {

                                    getUserApi().userReqLog(logRequest)
                                        .enqueue(object : Callback<BaseResponse<Nothing>> {
                                            override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                                                when (response.body()?.result) {
                                                }
                                            }

                                            override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                                                //showToast(t.localizedMessage)
                                            }
                                        })
                                }
                            }

                            override fun onFailed(reason: String) {
                                handleWithFailureInRequestDonateStep(reason)
                                networkState.set(NetworkState.LOADED)
                            }
                        })
                    }
                    CampaignEventType.EVENT.value -> {
                        donate(step)
                    }
                    else -> {
                        donate(step)
                    }
                }
            }
        } else {
            donate(step)
        }

        val bundle = Bundle()
        bundle.putString("campaign_id", donationData.campaignId.toString())
        FirebaseAnalytics.getInstance(activity).logEvent("donate", bundle)
    }
    private fun donate(step: Int) {
        // 기부 시도시 로그 데이터 전송
        val name = PreferenceManager.getName()
        val userId = PreferenceManager.getUserId().toString()
        val logRequest = UserWalkLogRequest(
            name + "",
            userId + "",
            "A",
            Build.MODEL + " [Android" + Build.VERSION.RELEASE + "] [" + BuildConfig.VERSION_NAME+ "]",
            "donate request : donate step [$step], donableStep [" + PreferenceManager.getDonableStep() + "], from [DonationViewModel: private fun donate]"
        )

        with(RemoteApiManager) {

            getUserApi().userReqLog(logRequest)
                .enqueue(object : Callback<BaseResponse<Nothing>> {
                    override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                        when (response.body()?.result) {
                        }
                    }

                    override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                        //showToast(t.localizedMessage)
                    }
                })
        }

        CampaignRepository.donateStep(DonateRequest(step), donationData.campaignId, object : CampaignDataSource.DonateStepCallback {
            override fun onSuccess(response: DonateResponse) {
                DebugLog.d("$step")
                if (response.maxDonationPerDay != null) {
                    CampaignDonateDataManager.updateCampaignDonateData.value =
                        Triple(response.campaignId.toLong(), response.todayDonatedSteps, response.maxDonationPerDay)
                }
                networkState.set(NetworkState.LOADED)
                afterUploadDonatedStep(step)

                // 기부 성공시 로그 데이터 전송
                val name = PreferenceManager.getName()
                val userId = PreferenceManager.getUserId().toString()
                val logRequest = UserWalkLogRequest(
                    name + "",
                    userId + "",
                    "A",
                    Build.MODEL + " [Android" + Build.VERSION.RELEASE + "] [" + BuildConfig.VERSION_NAME+ "]",
                    "donate success : donate step [$step], donableStep [ " + PreferenceManager.getDonableStep() + "], from [DonationViewModel: private fun donate]"
                )

                with(RemoteApiManager) {

                    getUserApi().userReqLog(logRequest)
                        .enqueue(object : Callback<BaseResponse<Nothing>> {
                            override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                                when (response.body()?.result) {
                                }
                            }

                            override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                                //showToast(t.localizedMessage)
                            }
                        })
                }
            }

            override fun onFailed(reason: String) {
                handleWithFailureInRequestDonateStep(reason)
                networkState.set(NetworkState.LOADED)
            }
        })
    }

    private fun handleWithFailureInRequestDonateStep(reason: String) {
        showToast("캠페인에 기부할 수 없습니다. 다시 시도해주세요.")

        // 기부 할 수 없을 경우 로그 데이터 전송
        val name = PreferenceManager.getName()
        val userId = PreferenceManager.getUserId().toString()
        val logRequest = UserWalkLogRequest(
            name + "",
            userId + "",
            "A",
            Build.MODEL + " [Android" + Build.VERSION.RELEASE + "] [" + BuildConfig.VERSION_NAME+ "]",
            "donate failed : donableStep [" + PreferenceManager.getDonableStep() + "], reason [DonationViewModel: private fun handleWithFailureInRequestDonateStep(reason: String) -> " + reason + "]"
        )

        with(RemoteApiManager) {

            getUserApi().userReqLog(logRequest)
                .enqueue(object : Callback<BaseResponse<Nothing>> {
                    override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                        when (response.body()?.result) {
                        }
                    }

                    override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                        //showToast(t.localizedMessage)
                    }
                })
        }



        DebugLog.e(CampaignException(reason))
        val crashlytics = FirebaseCrashlytics.getInstance()
        val currentDateTime = Calendar.getInstance().time
        crashlytics.setCustomKey("currentDateTime", currentDateTime.toString())
        /**
         * DAILY_STEP,// 하루 단위로 갱신되는 걸음 수를 저장
         * TIMELY_STEP,// 한시간 단위로 갱신되는 걸음 수를 저장
         * RECENT_STEP,// 센서로 들어오는 raw한 걸음 수를 저장
         * CURRENT_DAY,//최근 저장 날짜 (지금이랑 다를 경우, 오늘 걸음수 초기화), 1~31
         * CURRENT_HOUR, //최근 저장 시간 (지금이랑 다를 경우, 지난 시간 걸음 수 저장), 0~23
         * DONABLE_STEP,// 기부가능한 걸음 수를 저장
         */
        crashlytics.setCustomKey("DAILY_STEP", PreferenceManager.getDailyStep())
        crashlytics.setCustomKey("TIMELY_STEP", PreferenceManager.getTimelyStep())
        crashlytics.setCustomKey("RECENT_STEP", PreferenceManager.getRecentStep())
        val currentDay : Int = if(PreferenceManager.getCurrentDay() != null) PreferenceManager.getCurrentDay()!! else 0
        crashlytics.setCustomKey("CURRENT_DAY", currentDay)
        crashlytics.setCustomKey("CURRENT_HOUR", PreferenceManager.getCurrentHour())
        crashlytics.setCustomKey("DONABLE_STEP", PreferenceManager.getDonableStep())
        crashlytics.setCustomKey("reason", reason)
        crashlytics.recordException(RuntimeException("Unabel to donate"))
    }

    fun share(){
        val intent = Intent(activity, ShareCampaignActivity::class.java)
        intent.putExtra("campaignId", donationData.campaignId)
        activity.startActivity(intent)
        donationCompleteBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        activity.finish()
    }

    fun confirmAfterDonate(){
        EventManager.calculateDonationCount()
        donationCompleteBottomSheetBehavior?.state = BottomSheetBehavior.STATE_HIDDEN
        if (activity is CampaignDetailActivity){
            activity.finish()
        }
    }

    private fun afterUploadDonatedStep(step: Int) {
        PreferenceManager.saveDonableStep(PreferenceManager.getDonableStep() - step)
        sendDonableStepToWalkService(PreferenceManager.getDonableStep())

        commerce.get()?.let { commerce ->
            if (commerce.extra1.toInt() == step)
                showValueConsumptionCommerceView(commerce)
            else
                showDonationCompleteView()
            afterUploadDonatedStep@return
        }

        showDonationCompleteView()
    }

    private fun showDonationCompleteView() {
        try {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            val donationCompleteFragment = DonationCompleteFragment(this)
            donationCompleteFragment.show(
                (activity as AppCompatActivity).supportFragmentManager,
                donationCompleteFragment.tag
            )
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log("DonationViewModel showDonationCompleteView error : ${e.stackTrace}")
        }
    }

    private fun showValueConsumptionCommerceView(commerce: ValueConsumptionCommerceResponse) {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        val fragment = DonationValueConsumptionCommerceFragment(donationData, commerce)
        fragment.show((activity as AppCompatActivity).supportFragmentManager, fragment.tag)
    }
    
    private fun showCompleteCampaignTermsDialog() {
        val dialog = AlertDialog.Builder(activity)
            .setMessage(
                "고생하셨습니다!\n" +
                    "이 캠페인의 1일 걸음 기부를 모두 달성했어요.\n" +
                    "다른 캠페인에도 기부해보세요!"
            )
            .setPositiveButton(activity.getString(R.string.confirm)) { _, _ ->
            }
            .create()
        
        dialog.show()
    }

    private fun sendDonableStepToWalkService(donableStep: Int) {
        WalkServiceManager(activity).syncDonableStepToForeground(donableStep)
    }
}