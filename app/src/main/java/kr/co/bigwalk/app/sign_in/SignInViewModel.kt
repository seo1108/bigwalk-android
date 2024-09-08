package kr.co.bigwalk.app.sign_in

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.nhn.android.naverlogin.OAuthLogin
import kr.co.bigwalk.app.BaseNavigator
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
import kr.co.bigwalk.app.exception.KakaoException
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.sign_in.naver.NaverSignInCallback
import kr.co.bigwalk.app.sign_up.SignUpActivity
import kr.co.bigwalk.app.util.BlackUser
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast
import kr.co.bigwalk.app.walk.WalkActivity
import retrofit2.Call
import retrofit2.Response

class SignInViewModel(private val navigator: BaseNavigator) : BaseObservable() {

    val steps  : ObservableField<String> = ObservableField("10")
    private val oAuthLogin = OAuthLogin.getInstance()
    
    fun kakaoSignIn() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(navigator.getContext())) {
            UserApiClient.instance.loginWithKakaoTalk(navigator.getContext()) { token, error ->
                if (error != null) {
                    if (error.toString().contains("statusCode=302")) {
                        UserApiClient.instance.loginWithKakaoAccount(navigator.getContext()) { to, err ->
                            if (err != null) {
                                if (err.toString().contains("statusCode=302")) {
                                    showToast(err.toString())
                                } else {
                                    showToast("Kakao: 로그인 실패 -> $err")
                                }
                            } else {
                                try {
                                    authenticateUser(token!!.accessToken)
                                } catch (e: Exception) {
                                    showToast("Kakao: 로그인 실패 -> $e")
                                    FirebaseCrashlytics.getInstance().log("Kakao isKakaoTalkLoginAvailable login error : ${e.stackTrace}")
                                }
                            }
                        }
                    } else {
                        showToast("Kakao: 로그인 실패")
                    }

                } else {
                    authenticateUser(token!!.accessToken)
                }
            }
        } else {
            UserApiClient.instance.loginWithKakaoAccount(navigator.getContext()) { token, error ->
                if (error != null) {
                    if (error.toString().contains("statusCode=302")) {
                        showToast(error.toString())
                    } else {
                        showToast("Kakao: 로그인 실패 -> $error")
                    }
                } else {
                    try {
                        authenticateUser(token!!.accessToken)
                    } catch (e: Exception) {
                        showToast("Kakao: 로그인 실패 -> $e")
                        FirebaseCrashlytics.getInstance().log("Kakao loginWithKakaoAccount login error : ${e.stackTrace}")
                    }
                }
            }
        }
    }

    private fun kakaoAuth(token: OAuthToken?) {
        Log.d("TTTTTTTTTTTTTTTTTTTTTT", "kakao AUTH")
        showToast("SNSLogin KAKAO AUTH $token")
        if (token != null) {
            Log.d("SNSLogin","Kakao: 로그인 성공 -> ${token.accessToken}")
            UserApiClient.instance.accessTokenInfo { tokenInfo, err ->
                if (err != null) {
                    // Unknown error
                    if (err.toString().contains("statusCode=302")) {
                        showToast(err.toString())
                    } else {
                        Log.d("SNSLogin","Kakao: 로그인 실패 -> $err")
                        showToast("Kakao: 로그인 실패 -> $err")
                    }
                } else if (tokenInfo != null) {
                    Log.d("SNSLogin","Kakao: 로그인 성공 -> ${token.accessToken}")
                    UserApiClient.instance.me { user, error ->
                        if (error != null) {
                            // Unknown error
                            if (error.toString().contains("statusCode=302")) {
                                showToast(error.toString())
                            } else {
                                Log.d("SNSLogin","Kakao: 사용자 정보 요청 실패 -> $error")
                                showToast("Kakao: 로그인 실패 -> $err")
                            }
                        } else if (user != null) {
                            
                            var scopes = mutableListOf<String>()
                            if (user.kakaoAccount?.emailNeedsAgreement == true) { scopes.add("account_email") }

                            if (scopes.count() > 0) {
                                Log.d("Kakao_login", "사용자에게 추가 동의를 받아야 합니다.")

                                // OpenID Connect 사용 시
                                // scope 목록에 "openid" 문자열을 추가하고 요청해야 함
                                // 해당 문자열을 포함하지 않은 경우, ID 토큰이 재발급되지 않음
                                // scopes.add("openid")

                                //scope 목록을 전달하여 카카오 로그인 요청
                                UserApiClient.instance.loginWithNewScopes(
                                    navigator.getContext(),
                                    scopes
                                ) { token, error ->
                                    if (error != null) {
                                        Log.e("Kakao_login", "사용자 추가 동의 실패", error)
                                    } else {
                                        Log.d("Kakao_login", "allowed scopes: ${token!!.scopes}")

                                        // 사용자 정보 재요청
                                        UserApiClient.instance.me { user, error ->
                                            if (error != null) {
                                                Log.e("Kakao_login", "kakao 사용자 정보 요청 실패", error)
                                            } else if (user != null) {
                                                Log.i("Kakao_login", "kakao 사용자 정보 요청 성공")
                                            }
                                        }
                                    }
                                }
                            }


                            user.kakaoAccount?.let { account ->
                                account.profile?.let { kakaoProfile ->
                                    kakaoProfile.nickname?.let {  }
                                }
                                account.email?.let {
                               
                                }
                               
                                

                                
                            }
                        }
                    }
                }
            }
        }
    }


    fun naverSignIn() {
        oAuthLogin.startOauthLoginActivity(navigator.getContext(), NaverSignInCallback(navigator.getContext(), oAuthLogin))
    }

    fun onStepChanged(){
        steps.set(PreferenceManager.getDonableStep().toString())
    }

    private fun authenticateUser(accessToken: String) {
        UserRepository.authenticateUser(SignInUserRequest(ServiceProvider.KAKAO, accessToken), object : UserDataSource.AuthenticateUserCallback {
            override fun onSignUp(userNotRegisteredResponse: UserNotRegisteredResponse) {
                val signUpUserRequest = SignUpUserRequest(
                    ServiceProvider.KAKAO,
                    accessToken,
                    userNotRegisteredResponse.name
                )
                navigator.getContext().startActivity(
                    SignUpActivity.getIntent(navigator.getContext(), signUpUserRequest).addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK))
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
                    "kakao login failed : [" + reason + "]"
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

                FirebaseCrashlytics.getInstance().log("카카오 프로필 불러오기 실패 : $reason")
                showToast("카카오 프로필 정보를 불러올 수 없습니다. 앱 데이터 삭제 후 재시도해주세요.")
                DebugLog.e(KakaoException(reason))
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
                bundle.putString("provider", "KAKAO")
                SignInActivity.firebaseAnalytics?.logEvent("login", bundle)

                navigator.getContext().startActivity(Intent(navigator.getContext(), WalkActivity::class.java))
                navigator.getContext().finish()
            }

            override fun onBlackUser(userProfileResponse: UserProfileResponse) {
                BlackUser(navigator.getContext()).showBlackUserMessageAlert(userProfileResponse)
            }

            override fun onFailed(reason: String) {
                FirebaseCrashlytics.getInstance().log("유저 프로필 불러오기 실패 : $reason")
                DebugLog.e(UserProfileException("유저 프로필 불러오기 실패: $reason"))
            }

        })
    }

}