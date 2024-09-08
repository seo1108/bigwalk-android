package kr.co.bigwalk.app.data.user.repository

import kr.co.bigwalk.app.data.api.UserWalkLogRequest
import kr.co.bigwalk.app.data.user.UserProvider
import kr.co.bigwalk.app.data.user.dto.*
import java.io.File

object UserLocalDataSource : UserDataSource {

    override fun authenticateUser(signInUserRequest: SignInUserRequest, authenticateUserCallback: UserDataSource.AuthenticateUserCallback) {
        val saveUserRequest = UserNotRegisteredResponse(
            "리플로우",
            null
        )
        authenticateUserCallback.onSignUp(saveUserRequest)
    }

    override fun signUp(profileFile: File?, signUpUserRequest: SignUpUserRequest, signUpCallback: UserDataSource.SignUpCallback) {
        signUpCallback.onSuccess(SignInUserResponse("ACCESS_TOKEN_FOR_LOCAL"))
    }

    override fun signUpV3(profileFile: File?, signUpUserRequest: SignUpUserRequest, signUpV3Callback: UserDataSource.SignUpV3Callback) {
        signUpV3Callback.onSuccess(SignInUserResponse("ACCESS_TOKEN_FOR_LOCAL"))
    }

    /*override fun signUpV3Beta(profileFile: File?, signUpUserRequest: SignUpUserRequest, signUpV3BetaCallback: UserDataSource.SignUpV3BetaCallback) {
        signUpV3BetaCallback.onSuccess(SignInUserResponse("ACCESS_TOKEN_FOR_LOCAL"))
    }*/

    override fun existsNickname(nickname: String, existsNicknameCallback: UserDataSource.ExistsNicknameCallback) {
        val nicknameForTest = "배승민"
        if (nicknameForTest == nickname) existsNicknameCallback.onExists() else existsNicknameCallback.onDoesNotExists()
    }

    override fun modifyProfileImage(index: Int,modifyProfileImageCallback:UserDataSource.ModifyProfileImageCallback) {
        //noting
    }

    override fun getMyProfile(getMyProfileCallback: UserDataSource.GetMyProfileCallback) {
        getMyProfileCallback.onSuccess(
            UserProfileResponse(
                profilePath = "https://bigwalk-dev.s3.ap-northeast-2.amazonaws.com/test/img_character_01.png",
                id = -1,
                email = "bigwalkdev@bigwalk.co.kr",
                name = "크페이",
                groups = emptyList(),
                campaignNotiAgreement = true,
                storyNotiAgreement = true,
                rankingNotiAgreement = true,
                marketingNotiAgreement = false,
                characterId = 1,
                userProvider = UserProvider("KAKAO", -1),
                blocked = false
            )
        )
    }

    override fun checkAutoLogin(checkAutoLoginCallback: UserDataSource.CheckAutoLoginCallback) {
        //nothing
    }

    override fun modifyProfileUserInformation(profileFile: File?, modifyUserRequest: ModifyUserRequest, modifyProfileUserInformationCallback: UserDataSource.ModifyProfileUserInformationCallback) {
        //nothing
    }

    override fun removeOrganizationFromProfile(removeOrganizationCallback: UserDataSource.RemoveOrganizationCallback) {
        //nothing
    }

    override fun sendPushToken(tokenRequest: SaveTokenRequest, sendPushTokenCallback: UserDataSource.SendPushTokenCallback) {
        //nothing
    }

    override fun setCharacter(characterId: Int, setCharacterCallback: UserDataSource.SetCharacterCallback) {
        //nothing
    }

    override fun withdrawUser(withdrawUserCallback: UserDataSource.WithdrawUserCallback) {
        //nothing
    }

    override fun getNotice(noticeCallback: UserDataSource.NoticeCallback) {
        //noting
    }

    /*override fun userReqLog(
        name: String,
        userId: String,
        device: String,
        deviceModel: String?,
        logDesc: String?,
        getNothingCallback: UserDataSource.GetNothingCallback
    ) {
        //noting
    }*/

    /*override fun userReqLog(request: UserWalkLogRequest, getNothingCallback: UserDataSource.GetNothingCallback) {
        //noting
    }*/

}