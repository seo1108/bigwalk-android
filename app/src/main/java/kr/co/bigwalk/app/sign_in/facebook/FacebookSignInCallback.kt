package kr.co.bigwalk.app.sign_in.facebook

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.facebook.AccessToken
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.user.ServiceProvider
import kr.co.bigwalk.app.data.user.dto.*
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.sign_in.SignInActivity
import kr.co.bigwalk.app.sign_up.SignUpActivity
import kr.co.bigwalk.app.util.BlackUser
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast
import kr.co.bigwalk.app.walk.WalkActivity


class FacebookSignInCallback(private val context: Activity): FacebookCallback<LoginResult> {

    override fun onSuccess(result: LoginResult) {
        DebugLog.d("Facebook Login Success!! ${AccessToken.getCurrentAccessToken()}")
        SignInActivity.firebaseAnalytics?.logEvent("facebook_login_button", Bundle())
        AccessToken.getCurrentAccessToken()?.let { authenticateUser(it.token) }
    }

    override fun onCancel() {
        DebugLog.d("페이스북 취소")
    }

    override fun onError(error: FacebookException) {
        FirebaseCrashlytics.getInstance().log("페이스북 로그인 실패 : ${error?.localizedMessage}")
        showToast("페이스북으로 로그인할 수 없습니다. 다시 시도해주세요.")
        DebugLog.e(kr.co.bigwalk.app.exception.FacebookException(error?.localizedMessage))
    }

    private fun authenticateUser(accessToken: String) {
        UserRepository.authenticateUser(SignInUserRequest(ServiceProvider.FACEBOOK, accessToken), object : UserDataSource.AuthenticateUserCallback {
            override fun onSignUp(userNotRegisteredResponse: UserNotRegisteredResponse) {
                val signUpUserRequest = SignUpUserRequest(
                    ServiceProvider.FACEBOOK,
                    accessToken,
                    userNotRegisteredResponse.name
                )
//                val request = Intent(context, AgreementActivity::class.java)
//                request.putExtra("SignUpUserRequest", signUpUserRequest)
//                request.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                context.startActivity(request)

                context.startActivity(SignUpActivity.getIntent(context, signUpUserRequest).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            }

            override fun onSignIn(signInUserResponse: SignInUserResponse) {
                DebugLog.d(signInUserResponse.accessToken)
                PreferenceManager.saveAccessToken(signInUserResponse.accessToken)
                getUserProfile()
            }

            override fun onFailed(reason: String) {
                FirebaseCrashlytics.getInstance().log("페이스북 프로필 불러오기 실패 : $reason")
                showToast("페이스북 프로필 정보를 불러올 수 없습니다. 앱 데이터 삭제 후 재시도해주세요.")
                DebugLog.e(kr.co.bigwalk.app.exception.FacebookException(reason))
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
                bundle.putString("provider", "FACEBOOK")
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