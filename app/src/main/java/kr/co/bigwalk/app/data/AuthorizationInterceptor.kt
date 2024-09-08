package kr.co.bigwalk.app.data

import android.content.Intent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.data.mission.WelcomeMissionStatus
import kr.co.bigwalk.app.data.user.dto.MyProfileResponse
import kr.co.bigwalk.app.my_page.LogoutActivity
import kr.co.bigwalk.app.my_page.MyPageViewModel
import kr.co.bigwalk.app.sign_in.SignInActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast
import kr.co.bigwalk.app.walk.alarm.AlarmBroadcastReceiver
import kr.co.bigwalk.app.walk.sensor.WalkService
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import retrofit2.Call
import retrofit2.Callback
import java.security.InvalidKeyException

class AuthorizationInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request.Builder = chain.request().newBuilder()
        if (!PreferenceManager.getAccessToken().isNullOrBlank()) {
            request.addHeader("X-AUTH-TOKEN", PreferenceManager.getAccessToken()!!)
            //request.addHeader("X-AUTH-TOKEN", "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxNjA5NDEiLCJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaWF0IjoxNjMwNDIyNTQ4LCJleHAiOjE3MjUwMzA1NDh9.SR7U_pd4Stwq1Rh2NbvNR8J4o1u9y3UgEMJyHPHArSE")
            DebugLog.d("토큰 : " + PreferenceManager.getAccessToken())
            //validCheck()
            FirebaseCrashlytics.getInstance().log("토큰 : ${PreferenceManager.getAccessToken()}")
        }
        val response = chain.proceed(request.build())
        when (response.code) {
            401 -> {
                FirebaseCrashlytics.getInstance().log("intercept. response code : ${response.code}")
                FirebaseCrashlytics.getInstance().log("intercept. access token : ${PreferenceManager.getAccessToken()}")
                if(!PreferenceManager.getAccessToken().isNullOrBlank()){//이미 로그인했지만 토큰에 문제가 생긴 경우.
                    FirebaseCrashlytics.getInstance().log("intercept. invalid access token")
                    val intent = Intent(BigwalkApplication.context, SignInActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    BigwalkApplication.context?.startActivity(intent)
                }
                FirebaseCrashlytics.getInstance().recordException(InvalidKeyException())
            }

            403 -> {
                FirebaseCrashlytics.getInstance().log("intercept. response code : ${response.code}")
                FirebaseCrashlytics.getInstance().log("intercept. access token : ${PreferenceManager.getAccessToken()}")
                if(!PreferenceManager.getAccessToken().isNullOrBlank()) {
                    FirebaseCrashlytics.getInstance().log("intercept. duplicate login")
                    val intent = Intent(BigwalkApplication.context, LogoutActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    BigwalkApplication.context?.startActivity(intent)
                }
                FirebaseCrashlytics.getInstance().recordException(InvalidKeyException())
            }
        }
        return response
    }

    fun validCheck() {
        TokenApiManager.getService().getMyProfile(PreferenceManager.getAccessToken()!!)
            .enqueue(object : Callback<BaseResponse<MyProfileResponse>> {
                override fun onResponse(call: Call<BaseResponse<MyProfileResponse>>, response: retrofit2.Response<BaseResponse<MyProfileResponse>>) {
                    /*when (response.code()) {
                        403 ->
                        {

                        }
                    }*/
                }

                override fun onFailure(call: Call<BaseResponse<MyProfileResponse>>, t: Throwable) {
                }

            })
    }

}