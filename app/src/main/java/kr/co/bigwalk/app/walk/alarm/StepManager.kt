package kr.co.bigwalk.app.walk.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.BuildConfig
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.api.UserWalkLogRequest
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRemoteDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.data.walk.Walk
import kr.co.bigwalk.app.data.walk.WalkFilter
import kr.co.bigwalk.app.data.walk.WalkUtil.Companion.syncDailyWalk
import kr.co.bigwalk.app.data.walk.WalkUtil.Companion.getCurrentWalk
import kr.co.bigwalk.app.data.walk.dto.CurrentWalk
import kr.co.bigwalk.app.data.walk.repository.WalkDataSource
import kr.co.bigwalk.app.data.walk.repository.WalkRepository
import kr.co.bigwalk.app.exception.WalkException
import kr.co.bigwalk.app.notification.FCMChannels
import kr.co.bigwalk.app.splash.SplashActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.walk.sensor.StepCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.util.Calendar.*
import java.util.concurrent.TimeUnit


//걸음에 대한 로직을 담당하는 객체
object StepManager {

    fun calculateStep(currentStep: Int, stepCallback: StepCallback?) {//current step: 추가된 걸음 수
        if(currentStep <= 0) { //device reboot
            PreferenceManager.saveRecentStep(currentStep)
            return
        }

        val prevDonableStep = PreferenceManager.getDonableStep()

        //일자 넘어갈때 오늘 걸음 수 제거 (걸음 데이터는 시간으로만 저장)
        syncDailyWalk()
        Log.d("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT", "6 " + PreferenceManager.getDailyStep() + " " + currentStep)
        PreferenceManager.saveDailyStep(PreferenceManager.getDailyStep() + currentStep)
        PreferenceManager.saveDonableStep(PreferenceManager.getDonableStep() + currentStep)
        stepCallback?.onStepChanged(PreferenceManager.getDailyStep(), PreferenceManager.getDonableStep())

        // 만걸음 확인(기존 걸음이 만걸음이 아닐 때)
        if (PreferenceManager.getWalkPushSetting()) {
            if (prevDonableStep != 10000 && PreferenceManager.getDonableStep() == 10000) {
                sendWalkNotification()
            }
        }
    }

    fun uploadAllWalksAndClear(onSuccess: ()->Unit) {

        syncDailyWalk()

        // 포어그라운드일때만 서버 전송
        //if (!BigwalkApplication.isForeground) return

        WalkRepository.saveWalk(getCurrentWalk(PreferenceManager.getDailyStep()), object : WalkDataSource.SaveWalkCallback {
            override fun onSuccess(id: Long) {
                WalkRepository.getAllWalks(object : WalkDataSource.GetAllWalksCallback {
                    override fun onSuccess(walks: List<Walk>) {
                        uploadWalk(walks, onSuccess)
                    }

                    override fun onEmpty() {}
                })
            }
        })
    }

    private fun uploadWalk(walks: List<Walk>, onSuccess: ()->Unit) {
        var filterWalks = mutableListOf<WalkFilter>()
        var id = 0
        if (!walks.isNullOrEmpty()) {
            id = walks[0].id!!
        }
        // starttime과 endtime, steps가 같은 값 중복 제거
        walks.forEach {
            var wf = WalkFilter(
                it.startTime,
                it.endTime,
                it.steps
            )
            filterWalks.add(wf)
        }
        DebugLog.d("걸음 업로드 성공 가공중 1 $filterWalks")
        var filterWalks2 = filterWalks.distinct()
        DebugLog.d("걸음 업로드 성공 가공중 2 $filterWalks2")

        // 중복 제거된 데이터를 새로운 객체에 담음
        var newWalks = mutableListOf<Walk>()

        val today = Date()

        filterWalks2.forEachIndexed { index, it ->
            // 7일 이전의 데이터는 전송을 하지 않음
            val diffInMillies = today.time - it.startTime!!.time
            val diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillies)
            DebugLog.d("걸음 업로드 성공 가공중 3 $diffInDays")
            if (diffInDays <= 6) {
                var wf = Walk(
                    id + index,
                    it.startTime,
                    it.endTime,
                    it.steps
                )
                newWalks.add(wf)
            }
        }
        DebugLog.d("걸음 업로드 성공 가공중 4 $newWalks")
        // starttime과 endtime이 같으면, 제거
        val filteredWalks = newWalks.filter { it.startTime != it.endTime }

        DebugLog.d("걸음 업로드 성공 가공중 5 $filteredWalks")


        val name = PreferenceManager.getName()
        val userId = PreferenceManager.getUserId().toString()
        val logRequest = UserWalkLogRequest(
            name + "",
            userId + "",
            "A",
            Build.MODEL + " [Android" + Build.VERSION.RELEASE + "] [" + BuildConfig.VERSION_NAME+ "]",
            "uploadWalkData : dailyStep [" + PreferenceManager.getDailyStep() + "], donableStep [" + PreferenceManager.getDonableStep() + "], uploadData " + filteredWalks + ""
        )

        // 걸음수가 줄어드는 케이스가 있어, 서버로 전송하는 걸음수를 로컬에 저장된 dailyStep을 보내도록 처리
        Log.d("걸음 업로드", "pre " + filteredWalks.toString() + " " + filteredWalks[filteredWalks.size - 1].steps + " " + PreferenceManager.getDailyStep())
        if (filteredWalks[filteredWalks.size - 1].steps < PreferenceManager.getDailyStep()) {
            filteredWalks[filteredWalks.size - 1].steps = PreferenceManager.getDailyStep()
        }

        Log.d("걸음 업로드", "after " + filteredWalks.toString() + " " + PreferenceManager.getDailyStep())

        WalkRepository.uploadWalkData(filteredWalks, object : WalkDataSource.UploadWalkDataCallback {
            override fun onSuccess(currentWalk: CurrentWalk) {
                DebugLog.d("걸음 업로드 성공 원본 $walks")
                DebugLog.d("걸음 업로드 성공 $filteredWalks")
                DebugLog.d("걸음 업로드 성공 응답 $currentWalk")

                Log.d("걸음 업로드", filteredWalks.toString())
                Log.d("걸음 업로드 성공 응답",  currentWalk.toString())
                Log.d("걸음 업로드", "" + PreferenceManager.getDailyStep() + " " + PreferenceManager.getDonableStep())

                val prevSavedDailyStep = PreferenceManager.getDailyStep()
                PreferenceManager.saveDailyStep(currentWalk.dailySteps)
                PreferenceManager.saveDonableStep(currentWalk.donableSteps)
                //PreferenceManager.saveDailyStep(PreferenceManager.getDailyStep())
                //PreferenceManager.saveDonableStep(PreferenceManager.getDonableStep())
                //PreferenceManager.saveDonableStep(currentWalk.donableSteps)
                onSuccess.invoke()
                WalkRepository.initializeWalkTable(walks)

                val diffStep = prevSavedDailyStep-currentWalk.dailySteps
                if (diffStep != 0) {
                    DebugLog.d("last daily: $prevSavedDailyStep, diff daily: $diffStep")
                }
            }

            override fun onFailed(reason: String) {
                DebugLog.e(WalkException(reason))
            }

        })

        RemoteApiManager.getUserApi().userReqLog(logRequest)
            .enqueue(object : Callback<BaseResponse<Nothing>> {
                override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                    DebugLog.d("걸음 로그 업로드 " + response.body()?.result)
                    when (response.body()?.result) {
                        Result.SUCCESS -> {
                            DebugLog.d("걸음 로그 업로드 dailyStep [" + PreferenceManager.getDailyStep() + "], uploadData [" + filterWalks + "]")
                            //showToast(response.body()?.message.orEmpty())
                            //_successEvent.value = Event(Unit)
                        }
                        Result.FAIL -> {
                            DebugLog.d("걸음 로그 업로드 실패 " + response.body()?.message.orEmpty())
                            //showToast(response.body()?.message.orEmpty())
                            //_successEvent.value = Event(Unit)
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                    //showToast(t.localizedMessage)
                }
            })
    }

    private fun sendWalkNotification() {
//        val intent = Intent(BigwalkApplication.context, LauncherActivity::class.java)
        val intent = Intent(BigwalkApplication.context, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(BigwalkApplication.context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE)

        val channelData = FCMChannels.WALK_MAXED.channelData
        val notificationBuilder = NotificationCompat.Builder(BigwalkApplication.context!!, channelData.id)
                .setSmallIcon(R.mipmap.bw_launcher)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
        notificationBuilder.setContentTitle(BigwalkApplication.context?.getString(R.string.walk_noti_msg_title))
        notificationBuilder.setContentText(BigwalkApplication.context?.getString(R.string.walk_noti_msg_text))

        val notificationManager = BigwalkApplication.context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelData.id,
                    channelData.name,
                    NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(2 /* ID of notification */, notificationBuilder.build())
    }
}