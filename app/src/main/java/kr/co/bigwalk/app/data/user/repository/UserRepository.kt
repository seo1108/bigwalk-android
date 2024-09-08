package kr.co.bigwalk.app.data.user.repository

import kr.co.bigwalk.app.BuildConfig
import kr.co.bigwalk.app.data.api.UserWalkLogRequest
import kr.co.bigwalk.app.data.user.dto.*
import java.io.File

object UserRepository : UserDataSource {

    private val userDataSource: UserDataSource

    init {
        @Suppress("ConstantConditionIf")
        userDataSource = if (BuildConfig.FLAVOR == "local") UserLocalDataSource else UserRemoteDataSource
    }

    override fun authenticateUser(signInUserRequest: SignInUserRequest, authenticateUserCallback: UserDataSource.AuthenticateUserCallback) {
        userDataSource.authenticateUser(signInUserRequest, authenticateUserCallback)
    }

    override fun signUp(profileFile: File?, signUpUserRequest: SignUpUserRequest, signUpCallback: UserDataSource.SignUpCallback) {
        userDataSource.signUp(profileFile, signUpUserRequest, signUpCallback)
    }

    override fun signUpV3(profileFile: File?, signUpUserRequest: SignUpUserRequest, signUpV3Callback: UserDataSource.SignUpV3Callback) {
        userDataSource.signUpV3(profileFile, signUpUserRequest, signUpV3Callback)
    }

    override fun existsNickname(nickname: String, existsNicknameCallback: UserDataSource.ExistsNicknameCallback) {
        userDataSource.existsNickname(nickname, existsNicknameCallback)
    }

    override fun modifyProfileImage(index: Int,modifyProfileImageCallback:UserDataSource.ModifyProfileImageCallback) {
        userDataSource.modifyProfileImage(index,modifyProfileImageCallback)
    }

    override fun getMyProfile(getMyProfileCallback: UserDataSource.GetMyProfileCallback) {
        userDataSource.getMyProfile(getMyProfileCallback)
    }

    override fun checkAutoLogin(checkAutoLoginCallback: UserDataSource.CheckAutoLoginCallback) {
        userDataSource.checkAutoLogin(checkAutoLoginCallback)
    }

    override fun modifyProfileUserInformation(profileFile: File?, modifyUserRequest: ModifyUserRequest, modifyProfileUserInformationCallback: UserDataSource.ModifyProfileUserInformationCallback) {
        userDataSource.modifyProfileUserInformation(profileFile, modifyUserRequest,modifyProfileUserInformationCallback)
    }

    override fun removeOrganizationFromProfile(removeOrganizationCallback: UserDataSource.RemoveOrganizationCallback) {
        userDataSource.removeOrganizationFromProfile(removeOrganizationCallback)
    }

    override fun sendPushToken(tokenRequest: SaveTokenRequest, sendPushTokenCallback: UserDataSource.SendPushTokenCallback) {
        userDataSource.sendPushToken(tokenRequest, sendPushTokenCallback)
    }

    override fun setCharacter(characterId: Int, setCharacterCallback: UserDataSource.SetCharacterCallback) {
        userDataSource.setCharacter(characterId, setCharacterCallback)
    }

    override fun withdrawUser(withdrawUserCallback: UserDataSource.WithdrawUserCallback) {
        userDataSource.withdrawUser(withdrawUserCallback)
    }

    override fun getNotice(noticeCallback: UserDataSource.NoticeCallback) {
        userDataSource.getNotice(noticeCallback)
    }

    /*override fun userReqLog(
        name: String,
        userId: String,
        device: String,
        deviceModel: String?,
        logDesc: String?,
        getNothingCallback: UserDataSource.GetNothingCallback
    ) {
        userDataSource.userReqLog(name, userId, device, deviceModel, logDesc, getNothingCallback)
    }*/

    /*override fun userReqLog(
        request: UserWalkLogRequest,
        getNothingCallback: UserDataSource.GetNothingCallback
    ) {
        userDataSource.userReqLog(request, getNothingCallback)
    }*/
}