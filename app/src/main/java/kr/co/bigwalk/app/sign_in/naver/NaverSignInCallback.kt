package kr.co.bigwalk.app.sign_in.naver

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.nhn.android.naverlogin.OAuthLogin
import com.nhn.android.naverlogin.OAuthLoginHandler
import kr.co.bigwalk.app.BuildConfig
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.api.UserWalkLogRequest
import kr.co.bigwalk.app.data.user.ServiceProvider
import kr.co.bigwalk.app.data.user.dto.*
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.exception.NaverException
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.sign_in.SignInActivity
import kr.co.bigwalk.app.sign_up.SignUpActivity
import kr.co.bigwalk.app.util.BlackUser
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast
import kr.co.bigwalk.app.walk.WalkActivity
import retrofit2.Call
import retrofit2.Response

class NaverSignInCallback(private val context: Activity, private val oAuthLogin: OAuthLogin) :
    OAuthLoginHandler() {

    override fun run(success: Boolean) {
        if (success) {
            DebugLog.d("Naver Authentication Success!! ${oAuthLogin.getAccessToken(context)}")
            authenticateUser(oAuthLogin.getAccessToken(context))
            SignInActivity.firebaseAnalytics?.logEvent("naver_login_button", Bundle())
        } else {
            FirebaseCrashlytics.getInstance().log("네이버 계정 인증 실패 : ${oAuthLogin.getLastErrorCode(context)}")
            showToast("네이버 계정을 인증할 수 없습니다. 다시 시도해주세요.")
            DebugLog.e(NaverException("${oAuthLogin.getLastErrorCode(context)}"))
        }
    }

    private fun authenticateUser(accessToken: String) {
        UserRepository.authenticateUser(
            SignInUserRequest(ServiceProvider.NAVER, accessToken),
            object : UserDataSource.AuthenticateUserCallback {

                override fun onSignUp(userNotRegisteredResponse: UserNotRegisteredResponse) {
                    val signUpUserRequest = SignUpUserRequest(
                        ServiceProvider.NAVER,
                        oAuthLogin.getAccessToken(context),
                        userNotRegisteredResponse.name
                    )
                    context.startActivity(SignUpActivity.getIntent(context, signUpUserRequest).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                }

                override fun onSignIn(signInUserResponse: SignInUserResponse) {
                    DebugLog.d(signInUserResponse.accessToken)
                    PreferenceManager.saveAccessToken(signInUserResponse.accessToken)
                    getUserProfile()
                }

                override fun onFailed(reason: String) {
                    val name = PreferenceManager.getName()
                    val userId = PreferenceManager.getUserId().toString()
                    val logRequest = UserWalkLogRequest(
                        name + "",
                        userId + "",
                        "A",
                        Build.MODEL + " [Android" + Build.VERSION.RELEASE + "] [" + BuildConfig.VERSION_NAME+ "]",
                        "naver login failed : [" + reason + "]"
                    )
                    RemoteApiManager.getUserApi().userReqLog(logRequest)
                        .enqueue(object : retrofit2.Callback<BaseResponse<Nothing>> {
                            override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                                when (response.body()?.result) {
                                    Result.SUCCESS -> {
                                    }
                                    Result.FAIL -> {
                                    }
                                }
                            }

                            override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                                //showToast(t.localizedMessage)
                            }
                        })

                    FirebaseCrashlytics.getInstance().log("네이버 프로필 불러오기 실패 : $reason")
                    showToast("네이버 프로필 정보를 불러올 수 없습니다. 앱 데이터 삭제 후 재시도해주세요.")
                    DebugLog.e(NaverException(reason))
                }

            })
    }

    private fun getUserProfile() {
        UserRepository.getMyProfile(object : UserDataSource.GetMyProfileCallback {
            override fun onSuccess(userProfileResponse: UserProfileResponse) {
                PreferenceManager.saveUserId(userProfileResponse.id)
                PreferenceManager.saveName(userProfileResponse.name)
                userProfileResponse.megaOrganization?.let { organization ->
                    PreferenceManager.saveOrganization(organization.id?:-1L)
                    organization.name?.let { PreferenceManager.saveOrganizationName(it) }
                    userProfileResponse.megaDepartment?.let { department ->
                        department.name?.let { PreferenceManager.saveDepartmentName(it) }
                    }
                }
                userProfileResponse.getSmallOrMediumGroup().let {
                    PreferenceManager.saveGroupId(it?.id ?: -1)
                }
                if (userProfileResponse.profilePath.isNullOrBlank() || userProfileResponse.profilePath == "0")
                    PreferenceManager.saveCharacter("1")
                else PreferenceManager.saveCharacter(userProfileResponse.profilePath!!)

                val bundle = Bundle()
                bundle.putString("provider", "NAVER")
                SignInActivity.firebaseAnalytics?.logEvent("login", bundle)
                context.startActivity(Intent(context, WalkActivity::class.java))
                context.finish()
            }

            override fun onBlackUser(userProfileResponse: UserProfileResponse) {
                BlackUser(context).showBlackUserMessageAlert(userProfileResponse)
            }

            override fun onFailed(reason: String) {
                FirebaseCrashlytics.getInstance().log("유저 프로필 불러오기 실패 : $reason")
                DebugLog.e(UserProfileException("유저 프로필 불러오기 실패: $reason"))
            }

        })
    }
}

private fun <T> Call<T>.enqueue(any: Any) {

}
