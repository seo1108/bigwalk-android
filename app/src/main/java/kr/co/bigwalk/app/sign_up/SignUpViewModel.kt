package kr.co.bigwalk.app.sign_up

import android.net.Uri
import android.os.Bundle
import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.api.SocialAccountInfoResponse
import kr.co.bigwalk.app.data.community.dto.create.CrewConcernTagResponse
import kr.co.bigwalk.app.data.organization.Department
import kr.co.bigwalk.app.data.organization.OrganizationRequirement
import kr.co.bigwalk.app.data.organization.repository.OrganizationDataSource
import kr.co.bigwalk.app.data.organization.repository.OrganizationRepository
import kr.co.bigwalk.app.data.user.Gender
import kr.co.bigwalk.app.data.user.ProfileType
import kr.co.bigwalk.app.data.user.ServiceProvider
import kr.co.bigwalk.app.data.user.dto.SignInUserResponse
import kr.co.bigwalk.app.data.user.dto.SignUpUserRequest
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.exception.AlreadyExistsException
import kr.co.bigwalk.app.exception.OrganizationException
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.notification.BigwalkFcmServiceManager
import kr.co.bigwalk.app.sign_in.edit_profile.EditProfileActivity
import kr.co.bigwalk.app.sign_in.organization.CorporateMemberDeliveryInputForm
import kr.co.bigwalk.app.util.*
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import org.apache.commons.lang3.StringUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.regex.Pattern

data class SignUpRequest(
    val serviceProvider: ServiceProvider,
    val accessToken: String,
    val name: String? = "",
    val phoneNumber: String,
    val gender: Gender? = null,
    val birthday: String,
    val email: String,
    val profileImage: File? = null,
    val profileType: ProfileType? = null,
    val marketingAgreement: Boolean = false,
    val characterId: Int? = null,
    val profileCharacterId: Int? = null,
    val organizationId: Long? = null,
    val departmentId: Long? = null,
    val organizationEmployeeNumber: String? = null,
    val organizationEmail: String? = null,
    val firstDepthRegion: String? = null,
    val secondDepthRegion: String? = null,
    val userTagIds: List<Long>? = null,

    val depth1: Long? = 0,
    val depth2: Long? = 0,
    val depth3: Long? = 0,
    val depth4: Long? = 0,
    val searchContent: String? = "",
    val visitDay: String?= "",
    val certNo: String?= "",
    val familyRelations: String?= "",
    val age: String?= "",
    val nickname: String?= "",
    val identification: String?= "",
    val instagramAccount: String?= "",
    val address: String?= "",
    val attendanceNo: String?= "",
    val studentId: String?= "",
    val number: String?= "",
    val input: String?= "",
    val organizationEmployeeName: String? = ""
) {
    fun toMultipartBody(): List<MultipartBody.Part> {
        return MultipartBody.Builder().run {
            if (serviceProvider.social.isNotEmpty()) addFormDataPart("serviceProvider", serviceProvider.social)
            if (accessToken.isNotEmpty()) addFormDataPart("accessToken", accessToken)
            if (name != null) addFormDataPart("name", name)
            if (phoneNumber.isNotEmpty()) addFormDataPart("phoneNumber", phoneNumber)
            if (gender != null) addFormDataPart("gender", gender.gender)
            if (birthday.isNotEmpty()) addFormDataPart("birthday", birthday)
            if (email.isNotEmpty()) addFormDataPart("email", email)
            if (profileType != null) addFormDataPart("profileType", profileType.type)
            if (marketingAgreement) addFormDataPart("marketingAgreement", marketingAgreement.toString())
            if (characterId != null) addFormDataPart("characterId", characterId.toString())
            if (profileCharacterId != null) addFormDataPart("profileCharacterId", profileCharacterId.toString())
            if (organizationId != null) addFormDataPart("organizationId", organizationId.toString())
            if (departmentId != null) addFormDataPart("departmentId", departmentId.toString())
            if (organizationEmployeeNumber != null) addFormDataPart("organizationEmployeeNumber", organizationEmployeeNumber)
            if (organizationEmail != null) addFormDataPart("organizationEmail", organizationEmail)
            if (firstDepthRegion != null) addFormDataPart("firstDepthRegion", firstDepthRegion)
            if (secondDepthRegion != null) addFormDataPart("secondDepthRegion", secondDepthRegion)
            if (profileImage != null) {
                addFormDataPart("profileImage", profileImage.name, profileImage.asRequestBody(MultipartBody.FORM))
            }
            if (userTagIds != null) {
                userTagIds.forEach { addFormDataPart("userTagIds", it.toString()) }
            }



            if (depth1 != null) addFormDataPart("depth1", depth1.toString())
            if (depth2 != null) addFormDataPart("depth2", depth2.toString())
            if (depth3 != null) addFormDataPart("depth3", depth3.toString())
            if (depth4 != null) addFormDataPart("depth4", depth4.toString())
            if (searchContent != null) addFormDataPart("searchContent", searchContent)
            if (visitDay != null) addFormDataPart("visitDay", visitDay)
            if (certNo != null) addFormDataPart("certNo", certNo)
            if (familyRelations != null) addFormDataPart("familyRelations", familyRelations)
            if (age != null) addFormDataPart("age", age)
            if (nickname != null) addFormDataPart("nickname", nickname)
            if (identification != null) addFormDataPart("identification", identification)
            if (instagramAccount != null) addFormDataPart("instagramAccount", instagramAccount)
            if (address != null) addFormDataPart("address", address)
            if (attendanceNo != null) addFormDataPart("attendanceNo", attendanceNo)
            if (studentId != null) addFormDataPart("studentId", studentId)
            if (number != null) addFormDataPart("number", number)
            if (input != null) addFormDataPart("input", input)
            if (organizationEmployeeName != null) addFormDataPart("organizationEmployeeName", organizationEmployeeName)

            build().parts
        }
    }
}

class SignUpViewModel : ViewModel() {
    data class NicknameType(
        val text: String,
        val duplicatedMessage: String,
        val isAvailable: Boolean
    )

    data class EmailType(
        val text: String,
        val isAvailable: Boolean
    )

    data class BirthType(
        val text: String,
        val isAvailable: Boolean
    )

    private val _registerSuccessEvent = MutableLiveData<Event<Long>>()
    val registerSuccessEvent: LiveData<Event<Long>> get() = _registerSuccessEvent

    private val _screenPosition = MutableLiveData<Int>()
    val screenPosition: LiveData<Int> get() = _screenPosition

    private val _createProgressValue = MutableLiveData<Int>()
    val createProgressValue: LiveData<Int> get() = _createProgressValue

    /**
     * 기본 정보 입력 필드
     */
    var file: File? = null
    val profilePath = MutableLiveData<String>(PreferenceManager.getCharacter())
    val profileType = MutableLiveData<ProfileType>(ProfileType.CHARACTER)
    val phoneNumber = MutableLiveData<String>()
    val nicknameType = MutableLiveData<NicknameType>()
    val userEmailType = MutableLiveData<EmailType>()
    val userBirthType = MutableLiveData<BirthType>()
    val userGender = MutableLiveData<Gender>()
    val organization = MutableLiveData<CorporateMemberDeliveryInputForm?>()

    /**
     * 상세 정보 선택 필드
     */
    val area = MutableLiveData<Pair<String, String>>()
    private val selectArea: LiveData<String> = Transformations.map(area) {
        if (it.second.isNotEmpty()) it.first + " " + it.second else ""
    }
    val selectConcernList = MutableLiveData<List<CrewConcernTagResponse>>()

    val nextBtnTitle = MutableLiveData<String>()
    val nextBtnEnable = MediatorLiveData<Boolean>()

    /**
     * 기업 기본 정보 필드
     */
    private val _organizationRequirement = MutableLiveData<OrganizationRequirement>()
    val organizationRequirement: LiveData<OrganizationRequirement> get() = _organizationRequirement

    val depth1Title = MutableLiveData<String>()
    val depth2Title = MutableLiveData<String>()
    val depth3Title = MutableLiveData<String>()
    val depth4Title = MutableLiveData<String>()
    val depth1Hint = MutableLiveData<String>()
    val depth2Hint = MutableLiveData<String>()
    val depth3Hint = MutableLiveData<String>()
    val depth4Hint = MutableLiveData<String>()


    data class UserValidation(
        var isEmailAvailable: Boolean
    )

    val userValidation = MediatorLiveData<UserValidation>()

    init {
        nextBtnEnable.addSource(screenPosition) { validationCheck() }
        nextBtnEnable.addSource(phoneNumber) { validationCheck() }
        nextBtnEnable.addSource(nicknameType) { validationCheck() }
        nextBtnEnable.addSource(userEmailType) { validationCheck() }
        nextBtnEnable.addSource(userBirthType) { validationCheck() }
        nextBtnEnable.addSource(userGender) { validationCheck() }
        nextBtnEnable.addSource(organization) { validationCheck() }
        nextBtnEnable.addSource(selectArea) { validationCheck() }
        nextBtnEnable.addSource(selectConcernList) { validationCheck() }
        userValidation.value = UserValidation(
            isEmailAvailable = false
        )

    }

    fun fetchSocialAccountInfo(signUpAuth: SignUpUserRequest) {
        RemoteApiManager.getUserApi().getSocialAccountInfo(
            serviceProvider = signUpAuth.serviceProvider,
            accessToken = signUpAuth.accessToken
        ).enqueue(object : Callback<BaseResponse<SocialAccountInfoResponse>> {
            override fun onResponse(
                call: Call<BaseResponse<SocialAccountInfoResponse>>,
                response: Response<BaseResponse<SocialAccountInfoResponse>>
            ) {
                when (response.body()?.result) {
                    Result.SUCCESS -> {
                        response.body()?.data?.let { snsInfo ->
                            initData(snsInfo)
                        }
                    }
                    Result.FAIL -> {
                        response.body()?.message?.let { showToast(it) }
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse<SocialAccountInfoResponse>>, t: Throwable) {
                showToast(t.localizedMessage)
            }

        })
    }

    private fun initData(intentProfileData: SocialAccountInfoResponse) {
        intentProfileData.let {
            profilePath.value = it.profilePath.orEmpty()
            phoneNumber.value = it.phoneNumber?.replace("-", "").orEmpty()
            nicknameType.value = NicknameType(it.name.orEmpty(), "", true)
            userEmailType.value = EmailType(it.email.orEmpty(), true)
            if (!it.birthYear.isNullOrEmpty() && it.birthday.isNullOrEmpty()) userBirthType.value = BirthType(it.birthYear + it.birthday, true)
            userGender.value = it.gender ?: Gender.UNKNOWN
        }
    }

    private fun validationCheck() {
        when (screenPosition.value) {
            0 -> {
                nextBtnEnable.value = true
            }
            1 -> {
                nextBtnEnable.value =
                !phoneNumber.value.isNullOrEmpty()
                    && nicknameType.value?.isAvailable == true
                    && userEmailType.value?.isAvailable == true
                    && userBirthType.value?.isAvailable == true
                    && (userGender.value == Gender.MAN || userGender.value == Gender.WOMAN)

//                        && showOrganizationIdTypes(organization.value?.organization?.organizationIdType ?: OrganizationIdType.NONE)

            }
            2 -> {
                nextBtnEnable.value =
                    !selectArea.value.isNullOrEmpty()
                        && !selectConcernList.value.isNullOrEmpty()
            }
        }
    }

    fun checkEmail(email: String) {
        val pattern: Pattern = EMAIL_ADDRESS
        userEmailType.value = EmailType(text = email, isAvailable = pattern.matcher(email).matches())
    }

    fun checkBirth(birth: String) {
        userBirthType.value = BirthType(text = birth, isAvailable = Pattern.matches(BIRTH_PATTERN, birth))
    }

    fun setGender(gender: Gender) {
        userGender.value = gender
    }

    fun setFunctionAndViewForScreen(position: Int) {
        _screenPosition.value = position
        when (position) {
            0 -> {
                _createProgressValue.value = 111
                nextBtnTitle.value = "선택하기"
            }
            1 -> {
                _createProgressValue.value = 222
                nextBtnTitle.value = "다음"
            }
            2 -> {
                _createProgressValue.value = 333
                nextBtnTitle.value = "동의 후 시작하기"
            }
        }
    }

    fun afterSelectOrganization(corporateInfo: CorporateMemberDeliveryInputForm?) {
        organization.value = corporateInfo
        organization.value?.organization?.id.let { requestOrganizationRequirements(it!!) }
    }



    fun requestSignUp(signUpAuth: SignUpUserRequest) {
        /*val signup = SignUpRequest(
            serviceProvider = signUpAuth.serviceProvider,
            accessToken = signUpAuth.accessToken,
            name = nicknameType.value?.text,
            phoneNumber = phoneNumber.value.orEmpty(),
            gender = userGender.value,
            birthday = userBirthType.value?.text.orEmpty(),
            email = userEmailType.value?.text.orEmpty(),
            profileImage = getUploadFile(),
            profileType = profileType.value,
            marketingAgreement = true,
            characterId = PreferenceManager.getCharacter().toInt(),
            profileCharacterId = PreferenceManager.getCharacter().toInt(),
            organizationId = organization.value?.organization?.id,
            departmentId = organization.value?.department?.id,
            organizationEmployeeNumber = organization.value?.organizationEmployeeNumber,
            organizationEmail = organization.value?.organizationEmail,
            firstDepthRegion = area.value?.first,
            secondDepthRegion = area.value?.second,
            userTagIds = selectConcernList.value?.map { it.id.toLong() },

            depth1 = organization.value?.depth1?.id,
            depth2 = organization.value?.depth2?.id,
            depth3 = organization.value?.depth3?.id,
            depth4 = organization.value?.depth4?.id,
            searchContent = organization.value?.searchContent,
            visitDay = organization.value?.visitDay,
            certNo = organization.value?.certNo,
            familyRelations = organization.value?.familyRelations,
            age = organization.value?.age,
            nickname = organization.value?.nickname,
            identification = organization.value?.identification,
            address = organization.value?.address,
            attendanceNo = organization.value?.attendanceNo,
            studentId = organization.value?.studentId,
            number = organization.value?.number,
            input = organization.value?.input,
            organizationEmployeeName = organization.value?.organizationEmployeeName,
            instagramAccount = organization.value?.instagramAccount
        )


        DebugLog.d("requestSignUp : $signup")*/


        RemoteApiManager.getService().signUpV3(
            SignUpRequest(
                serviceProvider = signUpAuth.serviceProvider,
                accessToken = signUpAuth.accessToken,
                name = nicknameType.value?.text,
                phoneNumber = phoneNumber.value.orEmpty(),
                gender = userGender.value,
                birthday = userBirthType.value?.text.orEmpty(),
                email = userEmailType.value?.text.orEmpty(),
                profileImage = getUploadFile(),
                profileType = profileType.value,
                marketingAgreement = true,
                characterId = PreferenceManager.getCharacter().toInt(),
                profileCharacterId = PreferenceManager.getCharacter().toInt(),
                organizationId = organization.value?.organization?.id,
                departmentId = organization.value?.department?.id,
                organizationEmployeeNumber = organization.value?.organizationEmployeeNumber,
                organizationEmail = organization.value?.organizationEmail,
                firstDepthRegion = area.value?.first,
                secondDepthRegion = area.value?.second,
                userTagIds = selectConcernList.value?.map { it.id.toLong() },

                depth1 = organization.value?.depth1?.id,
                depth2 = organization.value?.depth2?.id,
                depth3 = organization.value?.depth3?.id,
                depth4 = organization.value?.depth4?.id,
                searchContent = organization.value?.searchContent,
                visitDay = organization.value?.visitDay,
                certNo = organization.value?.certNo,
                familyRelations = organization.value?.familyRelations,
                age = organization.value?.age,
                nickname = organization.value?.nickname,
                identification = organization.value?.identification,
                address = organization.value?.address,
                attendanceNo = organization.value?.attendanceNo,
                studentId = organization.value?.studentId,
                number = organization.value?.number,
                input = organization.value?.input,
                organizationEmployeeName = organization.value?.organizationEmployeeName,
                instagramAccount = organization.value?.instagramAccount
            ).toMultipartBody()
        ).enqueue(object : Callback<SignInUserResponse> {
            override fun onResponse(call: Call<SignInUserResponse>, response: Response<SignInUserResponse>) {
                DebugLog.d("requestSignUp : ${response.toString()}")
                DebugLog.d("requestSignUp : ${response.body()}")
                DebugLog.d("requestSignUp : ${response.body()!!.accessToken}")

                if (response.body()!!.accessToken != "") {
                    PreferenceManager.saveAccessToken(response.body()!!.accessToken)
                    nicknameType.value?.text?.let { PreferenceManager.saveName(it) }
                    BigwalkFcmServiceManager.retrieveCurrentToken()

                    val bundle = Bundle()
                    bundle.putString("provider", signUpAuth.serviceProvider.name)
                    EditProfileActivity.firebaseAnalytics?.logEvent("sign_up", bundle)
                    getUserProfile()
                }
            }

            override fun onFailure(call: Call<SignInUserResponse>, t: Throwable) {
                showToast(t.localizedMessage)
            }

        })
    }


    private fun getUserProfile() {
        UserRepository.getMyProfile(object : UserDataSource.GetMyProfileCallback {
            override fun onSuccess(userProfileResponse: UserProfileResponse) {
                PreferenceManager.saveUserId(userProfileResponse.id)
                PreferenceManager.saveName(userProfileResponse.name)
                userProfileResponse.megaOrganization?.let { organization ->
                    PreferenceManager.saveOrganization(organization.id ?: -1L)
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

//                val bundle = Bundle()
//                bundle.putString("provider", "NAVER")
//                SignInActivity.firebaseAnalytics?.logEvent("login", bundle)
                _registerSuccessEvent.value = Event(1L)
            }

            override fun onBlackUser(userProfileResponse: UserProfileResponse) {
//                BlackUser(context).showBlackUserMessageAlert(userProfileResponse)
            }

            override fun onFailed(reason: String) {
                FirebaseCrashlytics.getInstance().log("유저 프로필 불러오기 실패 : $reason")
                DebugLog.e(UserProfileException("유저 프로필 불러오기 실패: $reason"))
                showToast(reason)
            }

        })
    }

    fun existsNickname(nickname: String) {
        if (nickname.length !in 2..10) {
            if (nickname.isNotEmpty()) {
                nicknameType.value = NicknameType(nickname, "닉네임은 최소 2자 이상 최대 10자 이하 입니다.", false)
            }
            return
        }
        UserRepository.existsNickname(nickname, object : UserDataSource.ExistsNicknameCallback {
            override fun onExists() {
                showDebugToast("debug:중복되는 닉네임이 존재합니다.")
                nicknameType.value = NicknameType(nickname, "중복되는 닉네임이 존재합니다.", false)
            }

            override fun onDoesNotExists() {
                nicknameType.value = NicknameType(nickname, "사용가능한 닉네임 입니다.", true)
//                signUpUserRequest.name = nickname
            }

            override fun onFailed(reason: String) {
                showDebugToast("닉네임 중복여부를 확인할 수 없습니다. 다시 시도해주세요.")
                nicknameType.value = NicknameType(nickname, "닉네임 중복여부를 확인할 수 없습니다. 다시 시도해주세요.", false)
                DebugLog.e(AlreadyExistsException(reason))
            }

        })
    }

    private fun getUploadFile(): File? {
        var uploadFile: File? = null
        when (profilePath.value) {
            "0","1","2","3","4","5" -> {}
            else -> {
                var fileUri: Uri? = null
                if (profilePath.value!!.contains("http")) {// 프로필 사진 수정없이 기본정보 수정하는 경우
                    if (file != null) {
                        BigwalkApplication.context.let { fileUri = Uri.fromFile(file) }
                    }
                } else {// 프로필 사진 수정한 경우
                    if (!StringUtils.isBlank(profilePath.value)) fileUri = Uri.fromFile(File(profilePath.value!!))
                }
                // 임시로 올리 캐릭터를 처리할 때 이렇게 했으나 추후 업로드 부분 로직 확인해야 함
                if (fileUri?.scheme == "file" && profilePath.value != "5") uploadFile = compressFile(BigwalkApplication.context?.applicationContext!!, fileUri!!)
            }
        }
        return uploadFile
    }

    fun setCharacter(characterId: String, profileType: ProfileType) {
        profilePath.value = characterId
        this.profileType.value = profileType
    }

    fun changeNicknameType(nickname: String) {
        nicknameType.value = NicknameType(nickname, "", false)
    }

    private fun requestOrganizationRequirements(organizationId: Long) {
        OrganizationRepository.getOrganizationRequirement(organizationId, object: OrganizationDataSource.GetOrganizationRequirementCallback {
            override fun onSuccess(selectableOrganizationsRequirement: OrganizationRequirement?) {
                if (selectableOrganizationsRequirement != null) {
                    _organizationRequirement.value = selectableOrganizationsRequirement!!
                    //canChooseDepartment.set(false)
                    if (!selectableOrganizationsRequirement.depth1.isNullOrBlank()) {
                        depth1Title.value = selectableOrganizationsRequirement!!.depth1+"명"
                        depth1Hint.value = getCompleteWord(selectableOrganizationsRequirement.depth1!!,"을","를") + " 선택해주세요"
                    }

                    if (!selectableOrganizationsRequirement.depth2.isNullOrBlank()) {
                        depth2Title.value = selectableOrganizationsRequirement.depth2!!+"명"
                        depth2Hint.value = getCompleteWord(selectableOrganizationsRequirement.depth2!!,"을","를") + " 선택해주세요"
                    }

                    if (!selectableOrganizationsRequirement.depth3.isNullOrBlank()) {
                        depth3Title.value = selectableOrganizationsRequirement.depth3!!+"명"
                        depth3Hint.value = getCompleteWord(selectableOrganizationsRequirement.depth3!!,"을","를") + " 선택해주세요"
                    }

                    if (!selectableOrganizationsRequirement.depth4.isNullOrBlank()) {
                        depth4Title.value = selectableOrganizationsRequirement.depth4!!+"명"
                        depth4Hint.value = getCompleteWord(selectableOrganizationsRequirement.depth4!!,"을","를") + " 선택해주세요"
                    }

                }
            }

            override fun onFailed(reason: String) {
                DebugLog.e(OrganizationException(reason))
            }

        })
    }
}