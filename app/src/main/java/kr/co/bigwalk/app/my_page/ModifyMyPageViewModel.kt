package kr.co.bigwalk.app.my_page

import android.net.Uri
import android.util.Patterns.EMAIL_ADDRESS
import androidx.lifecycle.*
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.organization.Department
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.data.organization.OrganizationRequirement
import kr.co.bigwalk.app.data.organization.repository.OrganizationDataSource
import kr.co.bigwalk.app.data.organization.repository.OrganizationRepository
import kr.co.bigwalk.app.data.user.Gender
import kr.co.bigwalk.app.data.user.dto.ModifyProfileRequest
import kr.co.bigwalk.app.data.user.dto.MyProfileResponse
import kr.co.bigwalk.app.data.user.dto.UserConcernTagResponse
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.exception.AlreadyExistsException
import kr.co.bigwalk.app.exception.OrganizationException
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.sign_in.organization.CorporateMemberDeliveryInputForm
import kr.co.bigwalk.app.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.util.regex.Pattern

class ModifyMyPageViewModel : ViewModel() {
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

    private val _successEvent = MutableLiveData<Event<Unit>>()
    val successEvent: LiveData<Event<Unit>> get() = _successEvent


    val initProfileData = MutableLiveData<MyProfileResponse?>()
    var file: File? = null

    /**
     * 기본 정보 입력 필드
     */
    val profilePath = MutableLiveData<String>()
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
    val selectArea: LiveData<String> = Transformations.map(area) {
        if (it.second.isNotEmpty()) it.first + " " + it.second else ""
    }
    val selectConcernList = MutableLiveData<List<UserConcernTagResponse>>()
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



    init {
        nextBtnEnable.addSource(phoneNumber) { validationCheck() }
        nextBtnEnable.addSource(nicknameType) { validationCheck() }
        nextBtnEnable.addSource(userEmailType) { validationCheck() }
        nextBtnEnable.addSource(userBirthType) { validationCheck() }
        nextBtnEnable.addSource(userGender) { validationCheck() }
        nextBtnEnable.addSource(organization) { validationCheck() }
        nextBtnEnable.addSource(selectArea) { validationCheck() }
        nextBtnEnable.addSource(selectConcernList) { validationCheck() }
    }

    fun initData(intentProfileData: MyProfileResponse?) {
        initProfileData.value = intentProfileData
        intentProfileData?.let {
            profilePath.value = it.profilePath
            phoneNumber.value = it.phoneNumber.orEmpty()
            nicknameType.value = NicknameType(it.name, "", true)
            userEmailType.value = EmailType(it.email.orEmpty(), !it.email.isNullOrEmpty())
            userBirthType.value = BirthType(it.getBirthday(), it.getBirthday().isNotEmpty())
            userGender.value = it.gender ?: Gender.UNKNOWN
            organization.value = CorporateMemberDeliveryInputForm(
                organization = Organization(
                    id = it.organization?.id,
                    name = it.organization?.name
                ),
                department = Department(
                    id = it.department?.id ?: DEF_LONG_VALUE,
                    name = it.department?.name.orEmpty()
                ),
                organizationEmployeeNumber = it.organizationEmployeeNumber,
                organizationEmail = it.organizationEmail,


                depth1 = Department(
                    id = it.depth1?.id ?: DEF_LONG_VALUE,
                    name = it.depth1?.name.orEmpty()
                ),
                depth2 = Department(
                    id = it.depth2?.id ?: DEF_LONG_VALUE,
                    name = it.depth2?.name.orEmpty()
                ),
                depth3 = Department(
                    id = it.depth3?.id ?: DEF_LONG_VALUE,
                    name = it.depth3?.name.orEmpty()
                ),
                depth4 = Department(
                    id = it.depth4?.id ?: DEF_LONG_VALUE,
                    name = it.depth4?.name.orEmpty()
                ),
                /*depth1Title = it.depth1Title,
                depth2Title = it.depth2Title,
                depth3Title = it.depth3Title,
                depth4Title = it.depth4Title,
                depth1Hint = it.depth1Hint,
                depth2Hint = it.depth2Hint,
                depth3Hint = it.depth3Hint,
                depth4Hint = it.depth4Hint,*/
                searchContent = it.searchContent,
                visitDay = it.visitDay,
                certNo = it.certNo,
                familyRelations = it.familyRelations,
                age = it.age,
                nickname = it.nickname,
                identification = it.identification,
                address = it.address,
                attendanceNo = it.attendanceNo,
                studentId = it.studentId,
                number = it.number,
                input = it.input,
                organizationEmployeeName = it.organizationEmployeeName,
                instagramAccount = it.instagramAccount
                /*depth1Title = organization.value!!.depth1Title,
                depth2Title = organization.value!!.depth2Title,
                depth3Title = organization.value!!.depth3Title,
                depth4Title = organization.value!!.depth4Title,
                depth1Hint = organization.value!!.depth1Hint,
                depth2Hint = organization.value!!.depth2Hint,
                depth3Hint = organization.value!!.depth3Hint,
                depth4Hint = organization.value!!.depth4Hint,
                searchContent = organization.value!!.searchContent,
                visitDay = organization.value!!.visitDay,
                certNo = organization.value!!.certNo,
                familyRelations = organization.value!!.familyRelations,
                age = organization.value!!.age,
                nickname = organization.value!!.nickname,
                identification = organization.value!!.identification,
                address = organization.value!!.address,
                attendanceNo = organization.value!!.attendanceNo,
                studentId = organization.value!!.studentId,
                number = organization.value!!.number,
                input = organization.value!!.input,
                organizationEmployeeName = organization.value!!.organizationEmployeeName,
                instagramAccount = organization.value!!.instagramAccount,*/
            )
            area.value = if (!it.secondDepthRegion.isNullOrEmpty()) Pair(it.firstDepthRegion.orEmpty(), it.secondDepthRegion) else Pair("","")
            selectConcernList.value = it.userConcerns.orEmpty()
        }

        organization.value?.organization!!.id?.let { requestOrganizationRequirements(it) }
    }

    private fun validationCheck() {
        nextBtnEnable.value =
            !phoneNumber.value.isNullOrEmpty()
                && nicknameType.value?.isAvailable == true
                && userEmailType.value?.isAvailable == true
                && userBirthType.value?.isAvailable == true
                && userGender.value != Gender.UNKNOWN
                && !selectArea.value.isNullOrEmpty()
                //&& !selectConcernList.value.isNullOrEmpty()

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

    fun afterSelectOrganization(corporateInfo: CorporateMemberDeliveryInputForm?) {
        //organization.value?.organization!!.id?.let { requestOrganizationRequirements(it) }
        organization.value = corporateInfo
        
        if (corporateInfo == null) {
            removeOrganization()
        } else {
            organization.value?.organization?.id.let { requestOrganizationRequirements(it!!) }
        }
    }

    private fun removeOrganization() {
        UserRepository.removeOrganizationFromProfile(object : UserDataSource.RemoveOrganizationCallback {
            override fun onSuccess() {
                PreferenceManager.clearOrganization()
                PreferenceManager.clearDepartmentName()
                showToast("일반계정으로 전환하였습니다!")
                _successEvent.value = Event(Unit)
            }

            override fun onFailed(reason: String) {
                showToast("일반계정으로 전환할 수 없습니다. 다시 시도해주세요!")
                DebugLog.e(UserProfileException(reason))
            }

        })
    }

    fun modifyMyProfile() {
        RemoteApiManager.getUserApi().modifyMyProfile(
            ModifyProfileRequest(
                name = nicknameType.value?.text,
                phoneNumber = phoneNumber.value.orEmpty(),
                gender = userGender.value,
                birthday = userBirthType.value?.text.orEmpty(),
                email = userEmailType.value?.text.orEmpty(),
                profileImage = getUploadFile(),
                characterId = if (profilePath.value!!.length > 1) null else PreferenceManager.getCharacter().toInt(),
                organizationId = organization.value?.organization?.id,
                departmentId = organization.value?.department?.id,
                organizationEmployeeNumber = organization.value?.organizationEmployeeNumber,
                organizationEmail = organization.value?.organizationEmail,
                organizationEtc = organization.value?.organizationEmail,
                firstDepthRegion = area.value?.first,
                secondDepthRegion = area.value?.second,
                userTagIds = selectConcernList.value?.map { it.id }
            ).toMultipartBody()
        ).enqueue(object : Callback<BaseResponse<Nothing>> {
            override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                when (response.body()?.result) {
                    Result.SUCCESS -> {
                        showToast("프로필을 변경하였습니다")
                        _successEvent.value = Event(Unit)
                        PreferenceManager.saveOrganization(organization.value?.organization?.id ?: -1L)
//                        PreferenceManager.saveCharacter("${userProfileResponse.characterId}")
                    }
                    Result.FAIL -> {
                        response.body()?.message?.let { showToast(it) }
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                showToast(t.localizedMessage)
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
                if (!initProfileData.value?.name.equals(nickname)) {
                    showDebugToast("debug : 중복되는 닉네임이 존재합니다.")
                    nicknameType.value = NicknameType(nickname, "중복되는 닉네임이 존재합니다.", false)
                    return
                } else {
                    nicknameType.value = NicknameType(nickname, "", true)
                }
            }

            override fun onDoesNotExists() {
                nicknameType.value = NicknameType(nickname, "사용가능한 닉네임 입니다.", true)
//                signUpUserRequest.name = nickname
            }

            override fun onFailed(reason: String) {
                showDebugToast("debug : 닉네임 중복여부를 확인할 수 없습니다. 다시 시도해주세요.")
                nicknameType.value = NicknameType(nickname, "닉네임 중복여부를 확인할 수 없습니다. 다시 시도해주세요.", false)
                DebugLog.e(AlreadyExistsException(reason))
            }

        })
    }

    private fun getUploadFile(): File? {
        var uploadFile: File? = null
        when (profilePath.value) {
            "0", "1", "2", "3", "4", "5" -> {}
            else -> {
                var fileUri: Uri? = null
                if (profilePath.value!!.contains("http")) {// 프로필 사진 수정없이 기본정보 수정하는 경우
//                    uploadFile = File(Uri.parse(profilePath.value).path.orEmpty())
                } else {// 프로필 사진 수정한 경우
                    fileUri = Uri.fromFile(File(profilePath.value!!))
                }
                if (fileUri?.scheme == "file") uploadFile = compressFile(BigwalkApplication.context!!, fileUri)
            }
        }
        return uploadFile
    }

    fun setCharacter(characterId: String) {
        profilePath.value = characterId
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