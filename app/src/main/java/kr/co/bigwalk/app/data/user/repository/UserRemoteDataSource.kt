package kr.co.bigwalk.app.data.user.repository

import androidx.annotation.NonNull
import com.google.gson.GsonBuilder
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.ErrorMessage
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.api.UserWalkLogRequest
import kr.co.bigwalk.app.data.user.dto.*
import kr.co.bigwalk.app.notification.BigwalkFcmServiceManager
import kr.co.bigwalk.app.util.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


object UserRemoteDataSource: UserDataSource {

    override fun authenticateUser(signInUserRequest: SignInUserRequest, authenticateUserCallback: UserDataSource.AuthenticateUserCallback) {
       RemoteApiManager.getService().authenticateUser(signInUserRequest).enqueue(object : Callback<Any> {

           override fun onFailure(call: Call<Any>, t: Throwable) {
               authenticateUserCallback.onFailed(t.localizedMessage)
           }

           override fun onResponse(call: Call<Any>, response: Response<Any>) {
               when (response.code()) {
                   200 -> {
                       val tokenResponse = GsonBuilder().create()
                           .fromJson(response.body().toString(), SignInUserResponse::class.java)
                       BigwalkFcmServiceManager.retrieveCurrentToken()
                       authenticateUserCallback.onSignIn(tokenResponse)
                   }
                   404 -> {
                       val userNotRegisteredResponse = GsonBuilder().create()
                           .fromJson(response.errorBody()?.string(), UserNotRegisteredResponse::class.java)
                       authenticateUserCallback.onSignUp(userNotRegisteredResponse)
                   }
                   else -> authenticateUserCallback.onFailed(ErrorMessage.of(response.errorBody().toString()).jsonString)
               }
           }

       })
    }

    override fun signUp(profileFile: File?, signUpUserRequest: SignUpUserRequest, signUpCallback: UserDataSource.SignUpCallback) {
        DebugLog.d("profileFile:"+profileFile?.name)
        val part = if (profileFile == null) createEmptyPart("profileImage") else prepareFilePart(profileFile, "profileImage")
        val map = createPartFromSignUpUserRequest(signUpUserRequest)
        RemoteApiManager.getService().signUp(part, map).enqueue(object : Callback<SignInUserResponse> {
            override fun onFailure(call: Call<SignInUserResponse>, t: Throwable) {
                signUpCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<SignInUserResponse>, response: Response<SignInUserResponse>) {
                when (response.code()) {
                    200 -> {
                        BigwalkFcmServiceManager.retrieveCurrentToken()
                        signUpCallback.onSuccess(response.body())
                    }
                    400 -> signUpCallback.onDepartmentError()
                    409 -> signUpCallback.onExistsNickname()
                    else -> signUpCallback.onFailed(response.errorBody()!!.string())
                }
            }

        })
    }

   /*override fun signUpV3Beta(profileFile: File?, signUpUserRequest: SignUpUserRequest, signUpV3Callback: UserDataSource.SignUpV3BetaCallback) {
        DebugLog.d("profileFile:"+profileFile?.name)
        val part = if (profileFile == null) createEmptyPart("profileImage") else prepareFilePart(profileFile, "profileImage")
        val map = createPartFromSignUpUserRequest(signUpUserRequest)
        RemoteApiManager.getService().signUpV3Beta(part, map).enqueue(object : Callback<SignInUserResponse> {
            override fun onFailure(call: Call<SignInUserResponse>, t: Throwable) {
                signUpV3Callback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<SignInUserResponse>, response: Response<SignInUserResponse>) {
                when (response.code()) {
                    200 -> {
                        BigwalkFcmServiceManager.retrieveCurrentToken()
                        signUpV3Callback.onSuccess(response.body())
                    }
                    //400 -> signUpV3Callback.onDepartmentError()
                    //409 -> signUpV3Callback.onExistsNickname()
                    else -> signUpV3Callback.onFailed(response.errorBody()!!.string())
                }
            }

        })
    }*/

   override fun signUpV3(profileFile: File?, signUpUserRequest: SignUpUserRequest, getSignUpV3Callback: UserDataSource.SignUpV3Callback) {
        DebugLog.d("profileFile:"+profileFile?.name)
        val part = if (profileFile == null) createEmptyPart("profileImage") else prepareFilePart(profileFile, "profileImage")
        val map = createPartFromSignUpUserRequest(signUpUserRequest)
        RemoteApiManager.getService().signUp(part, map).enqueue(object : Callback<SignInUserResponse> {
            override fun onFailure(call: Call<SignInUserResponse>, t: Throwable) {
                getSignUpV3Callback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<SignInUserResponse>, response: Response<SignInUserResponse>) {
                DebugLog.d(call.request().toString())
                DebugLog.d(call.request().body.toString())
                when (response.code()) {
                    200 -> getSignUpV3Callback.onSuccess(response.body())
                    else -> getSignUpV3Callback.onFailed(response.code().toString())
                }
            }
        })
    }

    override fun existsNickname(nickname: String, existsNicknameCallback: UserDataSource.ExistsNicknameCallback) {
        RemoteApiManager.getService().existsNickname(nickname).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                existsNicknameCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    200 -> existsNicknameCallback.onDoesNotExists()
                    409 -> existsNicknameCallback.onExists()
                    else -> existsNicknameCallback.onFailed(response.code().toString())
                }
            }

        })
    }

    override fun modifyProfileImage(index: Int,modifyProfileImageCallback: UserDataSource.ModifyProfileImageCallback) {
        RemoteApiManager.getService().modifyProfileImage(index).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                modifyProfileImageCallback.onFailed(t.localizedMessage)
            }
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    200 -> modifyProfileImageCallback.onSuccess()
                    else -> modifyProfileImageCallback.onFailed(response.code().toString())
                }
            }
        })
    }

    override fun getMyProfile(getMyProfileCallback: UserDataSource.GetMyProfileCallback) {
        RemoteApiManager.getService().getMyProfile()
            .enqueue(object : Callback<UserProfileResponse>{
                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    getMyProfileCallback.onFailed(t.localizedMessage)
                }

                override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                    when (response.code()) {
                        200->{
                            response.body()?.let {
                                if (it.checkIsBlockedUser())
                                    getMyProfileCallback.onBlackUser(it)
                                else
                                    getMyProfileCallback.onSuccess(it)
                            }
                        }
                        else->{
                            getMyProfileCallback.onFailed(response.errorBody()?.string()!!)
                        }
                    }
                }
            })
    }

    override fun checkAutoLogin(checkAutoLoginCallback: UserDataSource.CheckAutoLoginCallback) {
        RemoteApiManager.getService().checkAutoLogin()
            .enqueue(object : Callback<BaseResponse<AutoLoginResponse>> {
                override fun onFailure(call: Call<BaseResponse<AutoLoginResponse>>, t: Throwable) {
                    checkAutoLoginCallback.onFailed(t.localizedMessage)
                }

                override fun onResponse(call: Call<BaseResponse<AutoLoginResponse>>, response: Response<BaseResponse<AutoLoginResponse>>) {
                    when (response.body()?.result) {
                        Result.SUCCESS -> {
                            response.body()?.data?.let {
                                if (it.checkIsBlockedUser())
                                    checkAutoLoginCallback.onBlackUser(it)
                                else
                                    checkAutoLoginCallback.onSuccess(it)
                            }
                        }
                        Result.FAIL -> {
                            showToast(response.body()?.message.orEmpty())
                            checkAutoLoginCallback.onFailed(response.errorBody()?.string()!!)
                        }
                    }
                }
            })
    }

    override fun modifyProfileUserInformation(profileFile: File?, modifyUserRequest: ModifyUserRequest, modifyProfileUserInformationCallback: UserDataSource.ModifyProfileUserInformationCallback) {
        DebugLog.d(modifyUserRequest.toString())
        val part = if (profileFile == null) createEmptyPart("profileImage") else prepareFilePart(profileFile, "profileImage")
        val map = createPartFromModifyUserRequest(modifyUserRequest)
        RemoteApiManager.getService().modifyProfileUserInformation(part, map).enqueue(object : Callback<UserProfileResponse>{
            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                modifyProfileUserInformationCallback.onFailed(t.localizedMessage)
            }
            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                when(response.code()){
                    200 -> response.body().let{
                        modifyProfileUserInformationCallback.onSuccess(it!!)
                    }
                    400 -> modifyProfileUserInformationCallback.onDepartmentError()
                    409 -> modifyProfileUserInformationCallback.onDuplicatedName()
                    else -> modifyProfileUserInformationCallback.onFailed(response.code().toString())
                }
            }
        })
    }

    override fun removeOrganizationFromProfile(removeOrganizationCallback: UserDataSource.RemoveOrganizationCallback) {
        RemoteApiManager.getService().removeOrganizationFromProfile().enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                removeOrganizationCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    200 -> response.body().let { removeOrganizationCallback.onSuccess() }
                    else -> removeOrganizationCallback.onFailed(response.code().toString())
                }
            }

        })
    }

    override fun sendPushToken(tokenRequest: SaveTokenRequest, sendPushTokenCallback: UserDataSource.SendPushTokenCallback) {
        RemoteApiManager.getService().sendPushToken(tokenRequest).enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                sendPushTokenCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                when (response.code()) {
                    200 -> response.body().let { sendPushTokenCallback.onSuccess() }
                    else -> sendPushTokenCallback.onFailed(response.code().toString())
                }
            }

        })
    }

    override fun setCharacter(characterId: Int, setCharacterCallback: UserDataSource.SetCharacterCallback) {
        RemoteApiManager.getService().setCharacter(characterId).enqueue(object : Callback<UserProfileResponse> {
            override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                setCharacterCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<UserProfileResponse>, response: Response<UserProfileResponse>) {
                when (response.code()) {
                    200 -> response.body().let { setCharacterCallback.onSuccess(it!!) }
                    else -> setCharacterCallback.onFailed(response.code().toString())
                }
            }

        })
    }

    override fun withdrawUser(withdrawUserCallback: UserDataSource.WithdrawUserCallback) {
        RemoteApiManager.getService().withdraw().enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                withdrawUserCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.code() == 200) {
                    withdrawUserCallback.onSuccess()
                } else {
                    withdrawUserCallback.onFailed(response.errorBody()?.string()!!)
                }
            }

        })
    }

    override fun getNotice(noticeCallback: UserDataSource.NoticeCallback) {
        RemoteApiManager.getService().getNotice().enqueue(object : Callback<NoticeResponse> {
            override fun onFailure(call: Call<NoticeResponse>, t: Throwable) {
                noticeCallback.onFailed(t.localizedMessage)
            }

            override fun onResponse(call: Call<NoticeResponse>, response: Response<NoticeResponse>) {
                when (response.code()) {
                    200 -> response.body()?.let { noticeCallback.onSuccess(it) }
                    else -> noticeCallback.onFailed(response.errorBody()?.string()!!)
                }
            }
        })
    }

    /*override fun userReqLog(
        name: String,
        userId: String,
        device: String,
        deviceModel: String?,
        logDesc: String?,
        getNothingCallback: UserDataSource.GetNothingCallback
    ) {
        RemoteApiManager.getUserApi().userReqLog(name, userId, device, deviceModel, logDesc)
            .enqueue(object : Callback<BaseResponse<Nothing>> {
                override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                    getNothingCallback.onFailed(t.localizedMessage)
                }

                override fun onResponse(
                    call: Call<BaseResponse<Nothing>>,
                    response: Response<BaseResponse<Nothing>>
                ) {
                    when (response.code()) {
                        200 -> response.body()?.let { getNothingCallback.onSuccess(it) }
                        else -> getNothingCallback.onFailed(response.errorBody()?.string()!!)
                    }
                }
            })
    }*/
    /*override fun userReqLog(request: UserWalkLogRequest, getNothingCallback: UserDataSource.GetNothingCallback) {
        RemoteApiManager.getUserApi().userReqLog(request)
            .enqueue(object : Callback<BaseResponse<Nothing>> {
                override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                    getNothingCallback.onFailed(t.localizedMessage)
                }

                override fun onResponse(
                    call: Call<BaseResponse<Nothing>>,
                    response: Response<BaseResponse<Nothing>>
                ) {
                    when (response.code()) {
                        200 -> response.body()?.let { getNothingCallback.onSuccess(it) }
                        else -> getNothingCallback.onFailed(response.errorBody()?.string()!!)
                    }
                }
            })
    }*/

    @NonNull
    private fun createPartFromSignUpUserRequest(signUpUserRequest: SignUpUserRequest): HashMap<String, RequestBody> {
        val map : HashMap<String, RequestBody> = HashMap()
        map["serviceProvider"] = signUpUserRequest.serviceProvider.name.toRequestBody("text/plain".toMediaTypeOrNull())
        map["accessToken"] = signUpUserRequest.accessToken.toRequestBody("text/plain".toMediaTypeOrNull())
        map["marketingAgreement"] = signUpUserRequest.marketingAgreement.toString().toRequestBody("text/plain".toMediaTypeOrNull())
        signUpUserRequest.name?.let { map["name"] = it.toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.organizationId?.let{map["organizationId"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.departmentId?.let { map["departmentId"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.organizationEmail?.let { map["organizationEmail"] = it.toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.organizationEmployeeNumber?.let { map["organizationEmployeeNumber"] = it.toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.characterId?.let { map["characterId"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.profileType?.let { map["profileType"] = it.toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.profileCharacterId?.let { map["profileCharacterId"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }


        signUpUserRequest.depth1?.let{map["depth1"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.depth2?.let{map["depth2"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.depth3?.let{map["depth3"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.depth4?.let{map["depth4"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.searchContent?.let{map["searchContent"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.visitDay?.let{map["visitDay"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.certNo?.let{map["certNo"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.familyRelations?.let{map["familyRelations"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.age?.let{map["age"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.nickname?.let{map["nickname"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.identification?.let{map["identification"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.instagramAccount?.let{map["instagramAccount"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.address?.let{map["address"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.attendanceNo?.let{map["attendanceNo"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.studentId?.let{map["studentId"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.number?.let{map["number"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        signUpUserRequest.input?.let{map["input"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }


        return map
    }

    @NonNull
    private fun createPartFromModifyUserRequest(modifyUserRequest: ModifyUserRequest): HashMap<String, RequestBody> {
        val map : HashMap<String, RequestBody> = HashMap()
        map["name"] = modifyUserRequest.name.toRequestBody("text/plain".toMediaTypeOrNull())
        modifyUserRequest.organizationId?.let { map["organizationId"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        modifyUserRequest.departmentId?.let { map["departmentId"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        modifyUserRequest.organizationEmployeeNumber?.let { map["organizationEmployeeNumber"] = it.toRequestBody("text/plain".toMediaTypeOrNull()) }
        modifyUserRequest.organizationEmail?.let { map["organizationEmail"] = it.toRequestBody("text/plain".toMediaTypeOrNull()) }
        modifyUserRequest.characterId?.let { map["characterId"] = it.toString().toRequestBody("text/plain".toMediaTypeOrNull()) }
        return map
    }

}