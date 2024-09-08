package kr.co.bigwalk.app.data.user.repository

import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.api.UserWalkLogRequest
import kr.co.bigwalk.app.data.user.dto.*
import java.io.File

interface UserDataSource {

    interface AuthenticateUserCallback {
        fun onSignUp(userNotRegisteredResponse: UserNotRegisteredResponse)
        fun onSignIn(signInUserResponse: SignInUserResponse)
        fun onFailed(reason: String)
    }

    interface SignUpCallback {
        fun onSuccess(signInUser: SignInUserResponse?)
        fun onExistsNickname()
        fun onDepartmentError()
        fun onFailed(reason: String)
    }

    interface SignUpV3Callback {
        fun onSuccess(signInUser: SignInUserResponse?)
        fun onExistsNickname()
        fun onDepartmentError()
        fun onFailed(reason: String)
    }

    interface SignUpV3BetaCallback {
        fun onSuccess(signInUser: SignInUserResponse?)
        fun onFailed(reason: String)
    }


    /*interface GetSignUpV3Callback {
        fun onSuccess(selectableSignInUserResponse: SignInUserResponse?)
        fun onFailed(reason: String)
    }*/

    interface ExistsNicknameCallback {
        fun onExists()
        fun onDoesNotExists()
        fun onFailed(reason: String)
    }

    interface ModifyProfileImageCallback {
        fun onSuccess()
        fun onFailed(reason: String)
    }

    interface GetMyProfileCallback{
        fun onSuccess(userProfileResponse: UserProfileResponse)
        fun onBlackUser(userProfileResponse: UserProfileResponse)
        fun onFailed(reason: String)
    }

    interface CheckAutoLoginCallback{
        fun onSuccess(autoLoginResponse: AutoLoginResponse)
        fun onBlackUser(autoLoginResponse: AutoLoginResponse)
        fun onFailed(reason: String)
    }

    interface ModifyProfileUserInformationCallback {
        fun onSuccess(userProfileResponse: UserProfileResponse)
        fun onDepartmentError()
        fun onFailed(reason: String)
        fun onDuplicatedName()
    }

    interface RemoveOrganizationCallback {
        fun onSuccess()
        fun onFailed(reason: String)
    }

    interface SendPushTokenCallback {
        fun onSuccess()
        fun onFailed(reason: String)
    }

    interface SetCharacterCallback {
        fun onSuccess(response: UserProfileResponse)
        fun onFailed(reason: String)
    }

    interface WithdrawUserCallback {
        fun onSuccess()
        fun onFailed(reason: String)
    }

    interface NoticeCallback {
        fun onSuccess(noticeResponse: NoticeResponse)
        fun onFailed(reason: String)
    }

    fun authenticateUser(signInUserRequest: SignInUserRequest, authenticateUserCallback: AuthenticateUserCallback)

    fun existsNickname(nickname: String, existsNicknameCallback: ExistsNicknameCallback)

    fun signUp(profileFile: File?, signUpUserRequest: SignUpUserRequest, signUpCallback: SignUpCallback)

    //fun signUpV3(profileFile: File?, signUpUserRequest: SignUpUserRequest, signUpV3Callback: SignUpV3Callback)
    fun signUpV3(profileFile: File?, signUpUserRequest: SignUpUserRequest, getSignUpV3Callback: SignUpV3Callback)

    //fun signUpV3Beta(signUpUserRequest: SignUpUserRequest, getSignUpV3BetaCallback: SignUpV3BetaCallback)

    fun modifyProfileImage(index : Int,modifyProfileImageCallback: ModifyProfileImageCallback)

    fun getMyProfile(getMyProfileCallback: GetMyProfileCallback)

    fun checkAutoLogin(checkAutoLoginCallback: CheckAutoLoginCallback)

    fun modifyProfileUserInformation(profileFile: File?, modifyUserRequest: ModifyUserRequest,modifyProfileUserInformationCallback: ModifyProfileUserInformationCallback)

    fun removeOrganizationFromProfile(removeOrganizationCallback: RemoveOrganizationCallback)

    fun sendPushToken(tokenRequest: SaveTokenRequest, sendPushTokenCallback: SendPushTokenCallback)

    fun setCharacter(characterId: Int, setCharacterCallback: SetCharacterCallback)

    fun withdrawUser(withdrawUserCallback: WithdrawUserCallback)

    fun getNotice(noticeCallback: NoticeCallback)

    interface GetNothingCallback {
        fun onSuccess(response: BaseResponse<Nothing>)
        fun onFailed(reason: String)
    }

    //fun userReqLog(name: String, userId: String, device: String, deviceModel: String?, logDesc: String?, getNothingCallback: GetNothingCallback)
    //fun userReqLog(request: UserWalkLogRequest, getNothingCallback: GetNothingCallback)
}