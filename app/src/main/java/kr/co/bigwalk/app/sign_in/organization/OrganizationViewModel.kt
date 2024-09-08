package kr.co.bigwalk.app.sign_in.organization

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.ranking.RankingPlusActivity
import kr.co.bigwalk.app.crowd_funding.detail.CrewCampaignDetailActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.organization.*
import kr.co.bigwalk.app.data.organization.dto.CertNoRequest
import kr.co.bigwalk.app.data.organization.dto.OrganizationRequest
import kr.co.bigwalk.app.data.organization.repository.OrganizationDataSource
import kr.co.bigwalk.app.data.organization.repository.OrganizationRepository
import kr.co.bigwalk.app.data.user.dto.MyProfileResponse
import kr.co.bigwalk.app.data.user.dto.SignUpUserRequest
import kr.co.bigwalk.app.exception.OrganizationException
import kr.co.bigwalk.app.sign_in.agreement.AgreementWithEnterprisePrivacyActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.getCompleteWord
import kr.co.bigwalk.app.util.showToast
import java.io.File
import java.io.Serializable
import java.text.DecimalFormat
import java.util.*

class OrganizationViewModel: BaseObservable() {

    //var navigator: BaseNavigator? = null
    var navigator: OrganizationNavigator? = null
    var signUpUserRequest: SignUpUserRequest? = null
    var profileFile: File? = null
    var organization : ObservableField<Organization> = ObservableField()
    var canChooseDepartment: ObservableBoolean = ObservableBoolean(false)
    var department: ObservableField<Department> = ObservableField()
    var canInputEmail: ObservableBoolean = ObservableBoolean(false)
    var canInputEmployeeNumber: ObservableBoolean = ObservableBoolean(false)
    var hasAgreeWithEnterprisePrivacy = ObservableBoolean(false)
    var hasOrganization = ObservableBoolean(false)
    var isModify = ObservableBoolean(false)
    var isDeepLink = ObservableBoolean(false)
    var isGoCampaign = ObservableBoolean(false)

    var subDepartment1: ObservableField<Department> = ObservableField()
    var subDepartment2: ObservableField<Department> = ObservableField()
    var subDepartment3: ObservableField<Department> = ObservableField()
    var subDepartment4: ObservableField<Department> = ObservableField()
    var canChooseDepth1Department: ObservableBoolean = ObservableBoolean(false)
    var canChooseDepth2Department: ObservableBoolean = ObservableBoolean(false)
    var canChooseDepth3Department: ObservableBoolean = ObservableBoolean(false)
    var canChooseDepth4Department: ObservableBoolean = ObservableBoolean(false)
    var organizationRequirement: ObservableField<OrganizationRequirement> = ObservableField()
    var depth1type: ObservableField<String> = ObservableField()
    var depth2type: ObservableField<String> = ObservableField()
    var depth3type: ObservableField<String> = ObservableField()
    var depth4type: ObservableField<String> = ObservableField()

    var familyRelation: ObservableField<String> = ObservableField("")
    var age: ObservableField<String> = ObservableField("")
    var nickname: ObservableField<String> = ObservableField("")
    var name: ObservableField<String> = ObservableField("")
    var id: ObservableField<String> = ObservableField("")
    var instaAccount: ObservableField<String> = ObservableField("")
    var joinNumber: ObservableField<String> = ObservableField("")
    var studentId: ObservableField<String> = ObservableField("")
    var address: ObservableField<String> = ObservableField("")
    var organizationEmployeeNumber: ObservableField<String> = ObservableField("")
    var organizationEmail: ObservableField<String> = ObservableField("")
    var visitdate: ObservableField<String> = ObservableField("")
    var inputValue: ObservableField<String> = ObservableField("")
    var searchValue: ObservableField<String> = ObservableField("")
    var authNum: ObservableField<String> = ObservableField("")
    var number: ObservableField<String> = ObservableField("")
    var isAuthCodeValid: ObservableBoolean = ObservableBoolean(false)

    companion object {
        const val SELECT_ORGANIZATION_REQUEST_CODE = 6654
        const val SELECT_DEPARTMENT_REQUEST_CODE = 4432
        const val SELECT_SUBDEPARTMENT1_REQUEST_CODE = 4433
        const val SELECT_SUBDEPARTMENT2_REQUEST_CODE = 4434
        const val SELECT_SUBDEPARTMENT3_REQUEST_CODE = 4435
        const val SELECT_SUBDEPARTMENT4_REQUEST_CODE = 4436
        const val SELECT_SEARCH_REQUEST_CODE = 5344
        const val KEY_CORPORATE_MEMBER_FORM = "CORPORATE_MEMBER_FORM"

        var originOrganizationId = 0L
        var campaignId = 0L
        var originOrganization: MyProfileResponse? = null
    }

    fun showOrganizations() {
        val intent = Intent(navigator?.getContext(), SelectListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("Organizations", true)
        navigator?.getContext()?.startActivityForResult(intent, SELECT_ORGANIZATION_REQUEST_CODE)
    }

    fun afterSelectOrganization(selectedOrganization: Organization) {//
        /*showOrganizationIdTypes(selectedOrganization.organizationIdType!!)
        requestDepartments(selectedOrganization.id!!)
        signUpUserRequest?.organizationId = selectedOrganization.id
        organization.set(selectedOrganization)*/

        // observable field 초기화
        initObservableData()

        signUpUserRequest?.organizationId = selectedOrganization.id
        organization.set(selectedOrganization)
        requestOrganizationRequirements(selectedOrganization.id!!)
    }

    fun showDepartments() {
        val intent = Intent(navigator?.getContext(), SelectDepartmentListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("organization", organization.get())
        navigator?.getContext()?.startActivityForResult(intent, SELECT_DEPARTMENT_REQUEST_CODE)
    }

    fun afterSelectDepartment(selectedDepartment: Department) {
        signUpUserRequest?.departmentId = selectedDepartment.id
        department.set(selectedDepartment)
    }

    fun showSubDepartments1() {
        val intent = Intent(navigator?.getContext(), SelectDepartmentListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("organization", organization.get())
        intent.putExtra("depth", "1")
        intent.putExtra("departmentTitle", organizationRequirement.get()!!.depth1+"선택")
        navigator?.getContext()?.startActivityForResult(intent, SELECT_SUBDEPARTMENT1_REQUEST_CODE)
    }

    fun showSubDepartments2() {
        if (subDepartment1!!.get()?.name.isNullOrBlank()) {
            depth1type.get()?.let { showToast(it) }
            return
        }

        val intent = Intent(navigator?.getContext(), SelectDepartmentListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("organization", subDepartment1.get())
        intent.putExtra("organizationInfo", organization.get())
        intent.putExtra("depth", "2")
        intent.putExtra("departmentTitle", organizationRequirement.get()!!.depth2+"선택")
        navigator?.getContext()?.startActivityForResult(intent, SELECT_SUBDEPARTMENT2_REQUEST_CODE)
    }

    fun showSubDepartments3() {
        if (subDepartment1!!.get()?.name.isNullOrBlank()) {
            depth1type.get()?.let { showToast(it) }
            return
        }

        if (subDepartment2!!.get()?.name.isNullOrBlank()) {
            depth2type.get()?.let { showToast(it) }
            return
        }

        val intent = Intent(navigator?.getContext(), SelectDepartmentListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("organization", subDepartment2.get())
        intent.putExtra("organizationInfo", organization.get())
        intent.putExtra("depth", "3")
        intent.putExtra("departmentTitle", organizationRequirement.get()!!.depth3+"선택")
        navigator?.getContext()?.startActivityForResult(intent, SELECT_SUBDEPARTMENT3_REQUEST_CODE)
    }

    fun showSubDepartments4() {
        if (subDepartment1!!.get()?.name.isNullOrBlank()) {
            depth1type.get()?.let { showToast(it) }
            return
        }

        if (subDepartment2!!.get()?.name.isNullOrBlank()) {
            depth2type.get()?.let { showToast(it) }
            return
        }

        if (subDepartment3!!.get()?.name.isNullOrBlank()) {
            depth3type.get()?.let { showToast(it) }
            return
        }



        val intent = Intent(navigator?.getContext(), SelectDepartmentListActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("organization", subDepartment3.get())
        intent.putExtra("organizationInfo", organization.get())
        intent.putExtra("depth", "4")
        intent.putExtra("departmentTitle", organizationRequirement.get()!!.depth4+"선택")
        navigator?.getContext()?.startActivityForResult(intent, SELECT_SUBDEPARTMENT4_REQUEST_CODE)
    }

    fun afterSelectSubDepartment(selectedSubDepartment: Department, depth: Long) {
        /*signUpUserRequest?.departmentId = selectedSubDepartment.id
        department.set(selectedDepartment)*/

        when (depth) {
            1L -> {
                signUpUserRequest?.departmentId = selectedSubDepartment.id
                subDepartment1.set(selectedSubDepartment)
                subDepartment2.set(null)
                subDepartment3.set(null)
                subDepartment4.set(null)
            }
            2L -> {
                subDepartment2.set(selectedSubDepartment)
                subDepartment3.set(null)
                subDepartment4.set(null)
            }
            3L -> {
                subDepartment3.set(selectedSubDepartment)
                subDepartment4.set(null)
            }
            4L -> {
                //parentId3.set(selectedSubDepartment.id!!)
                subDepartment4.set(selectedSubDepartment)
            }
        }
    }

    fun showKeyword() {
        val intent = Intent(navigator?.getContext(), SelectSearchKeywordActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("organization", organization.get())
        navigator?.getContext()?.startActivityForResult(intent, SELECT_SEARCH_REQUEST_CODE)
    }

    fun afterSelectSearchKeyword(selectedContent: Content) {
        /*signUpUserRequest?.departmentId = selectedSubDepartment.id
        department.set(selectedDepartment)*/
        searchValue.set(selectedContent.content)
    }

    fun finishActivity() {
        navigator?.finish()
    }

    fun organizationSignUp() {
        DebugLog.d("mypagemodiyfyactivity organizationSignUp")
        if (organizationRequirement.get()!!.isCertNo == true) {
            if (isModify.get()) {
                DebugLog.d("mypagemodiyfyactivity organizationSignUp 1")
                isAuthCodeValid.set(true)
                signUpUserRequest?.certNo = authNum.get()!!

                saveOrganizationInfo()
                signUpCommon()
                //navigator?
            } else {
                requestVerifyAuthNum(organization.get()!!.id)
            }
        } else if (organizationRequirement.get()!!.isCertNo == false && isGoCampaign.get()!!) {
            saveOrganizationInfo()
            /*navigator!!.getContext().startActivity(CrewCampaignDetailActivity.getIntent(navigator!!.getContext(), campaignId))
            finishActivity()*/
            navigator?.goCampaignDetail()
        } else {
            saveOrganizationInfo()
            signUpCommon()
        }
/*        if (organization.get() == null) {
            showToast("회사를 선택해주세요")
            return
        }*/

//        UserRepository.signUp(profileFile, signUpUserRequest!!, object : UserDataSource.SignUpCallback {
//            override fun onSuccess(signInUser: SignInUserResponse?) {
//                showDebugToast("가입 완료!!\n업로드 파일 있음:${profileFile?.exists()}")
//                DebugLog.d("$signUpUserRequest 사원번호: ${signUpUserRequest?.organizationEmployeeNumber} " +
//                        "이메일: ${signUpUserRequest?.organizationEmail} " +
//                        "기업 id: ${signUpUserRequest?.organizationId} " +
//                        "부서 id: ${signUpUserRequest?.departmentId} " +
//                        "업로드 파일: $profileFile")
//                PreferenceManager.saveAccessToken(signInUser?.accessToken)
//                PreferenceManager.saveOrganization(signUpUserRequest?.organizationId?:-1L)
//                if (signUpUserRequest?.characterId != null) {
//                    PreferenceManager.saveCharacter("${signUpUserRequest?.characterId}")
//                } else {
//                    PreferenceManager.saveCharacter("1")
//                }
//
//                val intent = Intent(navigator?.getContext(), WalkActivity::class.java)
//                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                navigator?.getContext()?.startActivity(intent)
//                navigator?.finish()
//                return
//            }
//
//            override fun onExistsNickname() {
//                showToast("중복되는 닉네임이 존재합니다. 닉네임을 다시 입력해주세요!")
//                return
//            }
//
//            override fun onDepartmentError() {
//                showToast("선택하신 부서는 해당 기업의 부서가 아닙니다. 부서를 재선택해주세요!")
//                return
//            }
//
//            override fun onFailed(reason: String) {
//                showToast("회원가입을 진행할 수 없습니다. 다시 시도해주세요.")
//                DebugLog.e(SignUpException(reason))
//                return
//            }
//
//        })
    }

    private fun signUpCommon() {
        if (!validateOrganizationIdTypes()) return
        //validateSignUpUserRequest()
        val intent = navigator?.getContext()?.intent
        intent?.putExtra(KEY_CORPORATE_MEMBER_FORM, CorporateMemberDeliveryInputForm(
            organization = organization.get(),
            department = subDepartment1.get(),
            organizationEmployeeNumber = organizationEmployeeNumber.get(),
            organizationEmail = organizationEmail.get(),
            depth1 = subDepartment1.get(),
            depth2 = subDepartment2.get(),
            depth3 = subDepartment3.get(),
            depth4 = subDepartment3.get(),
            /*depth1Title = organizationRequirement.get()!!.depth1+"명",
            depth2Title = organizationRequirement.get()!!.depth2+"명",
            depth3Title = organizationRequirement.get()!!.depth3+"명",
            depth4Title = organizationRequirement.get()!!.depth4+"명",
            depth1Hint = depth1type.get(),
            depth2Hint = depth2type.get(),
            depth3Hint = depth3type.get(),
            depth4Hint = depth4type.get(),*/
            searchContent = searchValue!!.get(),
            visitDay = visitdate.get(),
            certNo = authNum.get(),
            familyRelations = familyRelation.get(),
            age = age.get(),
            nickname = nickname.get(),
            identification = id.get(),
            address = address.get(),
            attendanceNo = joinNumber.get(),
            studentId = studentId.get(),
            number = number.get(),
            input = inputValue.get(),
            organizationEmployeeName = name.get(),
            instagramAccount = instaAccount.get()
        ))
        navigator?.getContext()?.setResult(-1, intent)
        navigator?.finish()
    }

    private fun validateOrganizationIdTypes(): Boolean {
        // https://gist.github.com/susemi99/a45ca534cc109271f34e6c992f69f048
        if (organization!!.get()?.name.isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_select_corporate))
            return false
        } else {
            signUpUserRequest?.organizationId = organization!!.get()?.id
        }

        if (canChooseDepth1Department.get() && subDepartment1!!.get()?.name.isNullOrBlank()) {
            depth1type.get()?.let { showToast(it) }
            return false
        } else if (canChooseDepth1Department.get()) {
            signUpUserRequest?.depth1 = subDepartment1!!.get()?.id
        }

        if (canChooseDepth2Department.get() && subDepartment2!!.get()?.name.isNullOrBlank()) {
            depth2type.get()?.let { showToast(it) }
            return false
        } else if (canChooseDepth2Department.get()) {
            signUpUserRequest?.depth2 = subDepartment2!!.get()?.id
        }

        if (canChooseDepth3Department.get() && subDepartment3!!.get()?.name.isNullOrBlank()) {
            depth3type.get()?.let { showToast(it) }
            return false
        } else if (canChooseDepth3Department.get()) {
            signUpUserRequest?.depth3 = subDepartment3!!.get()?.id
        }

        if (canChooseDepth4Department.get() && subDepartment4!!.get()?.name.isNullOrBlank()) {
            depth4type.get()?.let { showToast(it) }
            return false
        } else if (canChooseDepth4Department.get()) {
            signUpUserRequest?.depth4 = subDepartment4!!.get()?.id
        }

        if (organizationRequirement.get()!!.isSearchContent == true && searchValue!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_search_value))
            return false
        } else if (organizationRequirement.get()!!.isSearchContent == true) {
            signUpUserRequest?.searchContent = subDepartment2!!.get()?.name
        }

        if (organizationRequirement.get()!!.isVisitDay == true && visitdate!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_visit_date))
            return false
        } else if (organizationRequirement.get()!!.isVisitDay == true) {
            signUpUserRequest?.visitDay = visitdate!!.get()
        }

        if (organizationRequirement.get()!!.isFamilyRelations == true && familyRelation!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_family))
            return false
        } else if (organizationRequirement.get()!!.isFamilyRelations == true) {
            signUpUserRequest?.familyRelations = familyRelation!!.get()
        }

        if (organizationRequirement.get()!!.isAge == true && age!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_age))
            return false
        } else if (organizationRequirement.get()!!.isAge == true) {
            signUpUserRequest?.age = age!!.get()
        }

        if (organizationRequirement.get()!!.isNickname == true && nickname!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_employee_nickname))
            return false
        } else if (organizationRequirement.get()!!.isNickname == true) {
            signUpUserRequest?.nickname = nickname!!.get()
        }

        if (organizationRequirement.get()!!.isNumber == true && number!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_number))
            return false
        } else if (organizationRequirement.get()!!.isNumber == true) {
            signUpUserRequest?.number = number!!.get()
        }

        if (organizationRequirement.get()!!.isOrganizationEmployeeNumber == true && organizationEmployeeNumber!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_employee_number))
            return false
        } else if (organizationRequirement.get()!!.isOrganizationEmployeeNumber == true) {
            signUpUserRequest?.organizationEmployeeNumber = organizationEmployeeNumber.get()
        }

        if (organizationRequirement.get()!!.isIdentification == true && id!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_employee_id))
            return false
        } else if (organizationRequirement.get()!!.isIdentification == true) {
            signUpUserRequest?.identification = id!!.get()
        }

        if (organizationRequirement.get()!!.isName == true && name!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_name))
            return false
        } else if (organizationRequirement.get()!!.isName == true) {
            signUpUserRequest?.organizationEmployeeName = name!!.get()
        }

        if (organizationRequirement.get()!!.isOrganizationEmail == true && organizationEmail!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_employee_email))
            return false
        } else if (organizationRequirement.get()!!.isOrganizationEmail == true) {
            signUpUserRequest?.organizationEmail = organizationEmail.get()
        }

        if (organizationRequirement.get()!!.isOrganizationEmail == true && !Patterns.EMAIL_ADDRESS.matcher(organizationEmail.get().orEmpty()).matches()) {
            showToast("이메일 형식에 맞게 입력해주세요")
            return false
        } else if (organizationRequirement.get()!!.isOrganizationEmail == true) {
            signUpUserRequest?.organizationEmail = organizationEmail.get()
        }

        if (organizationRequirement.get()!!.isInstagramAccount == true && instaAccount!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_insta_account))
            return false
        } else if (organizationRequirement.get()!!.isInstagramAccount == true) {
            signUpUserRequest?.instagramAccount = instaAccount!!.get()
        }

        if (organizationRequirement.get()!!.isInput == true && inputValue!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_input_value))
            return false
        } else if (organizationRequirement.get()!!.isInput == true) {
            signUpUserRequest?.input = inputValue!!.get()
        }

        if (organizationRequirement.get()!!.isAddress == true && address!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_address))
            return false
        } else if (organizationRequirement.get()!!.isAddress == true) {
            signUpUserRequest?.address = address!!.get()
        }

        if (organizationRequirement.get()!!.isAttendanceNo == true && joinNumber!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_join_number))
            return false
        } else if (organizationRequirement.get()!!.isAttendanceNo == true) {
            signUpUserRequest?.attendanceNo = joinNumber!!.get()
        }

        if (organizationRequirement.get()!!.isStudentId == true && studentId!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_student_id))
            return false
        } else if (organizationRequirement.get()!!.isAttendanceNo == true) {
            signUpUserRequest?.studentId = studentId!!.get()
        }

        if (!isModify.get() && organizationRequirement.get()!!.isCertNo == true && authNum!!.get().isNullOrBlank()) {
            showToast(navigator!!.getContext().getString(R.string.please_input_employee_auth_num))
            return false
        }

        if (!hasOrganization.get() && !hasAgreeWithEnterprisePrivacy.get()) {
            showToast("개인정보처리방침 및 동의에 체크해주세요!")
            return false
        }

        return true

        /*if (canInputEmployeeNumber.get() && organizationEmployeeNumber.get().isNullOrBlank()) {
            showToast("사원번호를 입력해주세요")
            return false
        } else {
            signUpUserRequest?.organizationEmployeeNumber = organizationEmployeeNumber.get()
        }
        if (canInputEmail.get() && organizationEmail.get().isNullOrBlank()) {
            showToast("이메일을 입력해주세요")
            return false
        } else {
            signUpUserRequest?.organizationEmail = organizationEmail.get()
        }
        if (canInputEmail.get() && !Patterns.EMAIL_ADDRESS.matcher(organizationEmail.get().orEmpty()).matches()) {
            showToast("이메일 형식에 맞게 입력해주세요")
            return false
        } else {
            signUpUserRequest?.organizationEmail = organizationEmail.get()
        }
        if (!hasAgreeWithEnterprisePrivacy.get()) {
            showToast("개인정보처리방침 및 동의에 체크해주세요!")
            return false
        }
        return true*/
    }

    private fun validateSignUpUserRequest() {
        if (!canChooseDepartment.get()) {
            signUpUserRequest?.departmentId = null
        }
        if (!canInputEmail.get()) {
            signUpUserRequest?.organizationEmail = null
        }
        if (!canInputEmployeeNumber.get()) {
            signUpUserRequest?.organizationEmployeeNumber = null
        }
    }

    private fun showOrganizationIdTypes(organizationIdType: OrganizationIdType) {
        when (organizationIdType) {
            OrganizationIdType.EMAIL -> {
                canInputEmail.set(true)
                canInputEmployeeNumber.set(false)
            }
            OrganizationIdType.EMPLOYEE_NUMBER -> {
                canInputEmail.set(false)
                canInputEmployeeNumber.set(true)
            }
            OrganizationIdType.EMAIL_EMPLOYEE_NUMBER_BOTH -> {
                canInputEmail.set(true)
                canInputEmployeeNumber.set(true)
            }
            OrganizationIdType.NONE -> {
                canInputEmail.set(false)
                canInputEmployeeNumber.set(false)
            }
        }
    }

    private fun requestOrganizationRequirements(organizationId: Long) {
        // 최초 선택한 기업id와 현재 선택된 기업id가 같으면, 편집모드 (인증코드와 개인정보처리방침 동의할 필요 없음)
        if (originOrganizationId == organizationId) isModify.set(true)

        OrganizationRepository.getOrganizationRequirement(organizationId, object: OrganizationDataSource.GetOrganizationRequirementCallback {
            override fun onSuccess(selectableOrganizationsRequirement: OrganizationRequirement?) {
                if (selectableOrganizationsRequirement != null) {
                    //canChooseDepartment.set(false)
                    when (selectableOrganizationsRequirement.depth1) {
                        "" -> canChooseDepth1Department.set(false)
                        else -> {
                            canChooseDepth1Department.set(true)
                            depth1type.set(getCompleteWord(selectableOrganizationsRequirement.depth1!!,"을","를") + " 선택해주세요")
                        }
                    }
                    when (selectableOrganizationsRequirement.depth2) {
                        "" -> canChooseDepth2Department.set(false)
                        else -> {
                            canChooseDepth2Department.set(true)
                            depth2type.set(getCompleteWord(selectableOrganizationsRequirement.depth2!!,"을","를") + " 선택해주세요")
                        }
                    }
                    when (selectableOrganizationsRequirement.depth3) {
                        "" -> canChooseDepth3Department.set(false)
                        else -> {
                            canChooseDepth3Department.set(true)
                            depth3type.set(getCompleteWord(selectableOrganizationsRequirement.depth3!!,"을","를") + " 선택해주세요")
                        }
                    }
                    when (selectableOrganizationsRequirement.depth4) {
                        "" -> canChooseDepth4Department.set(false)
                        else -> {
                            canChooseDepth4Department.set(true)
                            depth4type.set(getCompleteWord(selectableOrganizationsRequirement.depth4!!,"을","를") + " 선택해주세요")
                        }
                    }
                    organizationRequirement.set(selectableOrganizationsRequirement)
                    DebugLog.d("[OrganizationRequirement] : $selectableOrganizationsRequirement")
                    //requestSubDepartments(organizationId, 1, 0)
                    return
                }
                canChooseDepartment.set(true)


            }

            override fun onFailed(reason: String) {
                DebugLog.e(OrganizationException(reason))
            }

        })
    }

    private fun requestDepartments(organizationId: Long) {
        OrganizationRepository.getDepartments(organizationId, object : OrganizationDataSource.GetDepartmentsCallback {
            override fun onSuccess(selectableDepartments: List<Department>?) {
                if (selectableDepartments.isNullOrEmpty()) {
                    canChooseDepartment.set(false)
                    DebugLog.d("부서 선택할 필요 없음.")
                    return
                }
                canChooseDepartment.set(true)
            }

            override fun onFailed(reason: String) {
                DebugLog.e(OrganizationException(reason))
            }

        })

    }

    private fun requestSubDepartments(organizationId: Long, depth: Long, parentId: Long) {
        Log.d("OrganizationSubDepartment", "12345")
        OrganizationRepository.getSubDepartments(organizationId, depth, parentId, object : OrganizationDataSource.GetSubDepartmentsCallback {
            override fun onSuccess(selectableSubDepartments: List<Department>?) {
                if (selectableSubDepartments.isNullOrEmpty()) {
                    canChooseDepartment.set(false)

                    return
                }
                canChooseDepartment.set(true)
                DebugLog.d("[OrganizationSubDepartment] : $selectableSubDepartments")
            }

            override fun onFailed(reason: String) {
                DebugLog.e(OrganizationException(reason))
            }
        })
    }

    private fun requestVerifyAuthNum(organizationId: Long?) {
        val request: CertNoRequest = CertNoRequest(authNum.get()!!)
        OrganizationRepository.verifyAuthNum(organizationId!!, request, object : OrganizationDataSource.VerifyAuthNumCallback {
            override fun onSuccess(isValid: Boolean) {
                DebugLog.d("[requestVerifyAuthNum] : $isValid")
                if (!validateOrganizationIdTypes()) return
                if (isValid) {
                    isAuthCodeValid.set(true)
                    signUpUserRequest?.certNo = authNum.get()!!

                    saveOrganizationInfo()

                    if (isGoCampaign.get()!!) {
                        navigator?.goCampaignDetail()
                        /*navigator?.getContext()?.startActivity(CrewCampaignDetailActivity.getIntent(navigator?.getContext()?, campaignId))
                        finishActivity()*/
                        /*val intent = Intent(navigator?.getContext(), CrewCampaignDetailActivity::class.java)
                        intent.putExtra("CREW_CAMPAIGN_ID", campaignId)
                        navigator?.getContext()?.startActivity(intent)
                        finishActivity()*/
                    } else {
                        signUpCommon()
                    }

                    //return
                }
            }

            override fun onFailed(reason: String) {
                if (!validateOrganizationIdTypes()) return

                showToast("인증번호가 유호하지 않습니다")
                DebugLog.e(OrganizationException(reason))

            }
        })
    }

    private fun saveOrganizationInfo() {
        val request: OrganizationRequest = OrganizationRequest(
            organization.get()!!.id.toString(),
            subDepartment1.get()?.id,
            subDepartment2.get()?.id,
            subDepartment3.get()?.id,
            subDepartment4.get()?.id,
            searchValue?.get(),
            visitdate?.get(),
            organizationEmail?.get(),
            authNum?.get(),
            familyRelation?.get(),
            age?.get(),
            nickname?.get(),
            id?.get(),
            instaAccount?.get(),
            address?.get(),
            joinNumber?.get(),
            studentId?.get(),
            number?.get(),
            inputValue?.get(),
            organizationEmployeeNumber?.get(),
            name?.get()
        )


        OrganizationRepository.saveOrganizationInfo(request, object : OrganizationDataSource.SaveOrganizationInfoCallback {
            override fun onSuccess() {
                //showToast("정보가 저장되었습니다.")
                organization.get()!!.id?.let { PreferenceManager.saveOrganization(it) }
            }

            override fun onFailed(reason: String) {
                DebugLog.e(OrganizationException(reason))
            }
        })
    }

    fun agreeWithService() {
        if (hasAgreeWithEnterprisePrivacy.get()) hasAgreeWithEnterprisePrivacy.set(false) else hasAgreeWithEnterprisePrivacy.set(true)
    }

    fun showAgreementWithEnterprisePrivacy() {
        val intent = Intent(navigator?.getContext(), AgreementWithEnterprisePrivacyActivity::class.java)
        intent.putExtra("organization", organization.get())
        navigator?.getContext()?.startActivity(intent)
    }

    fun showDatePicker() {
        Log.d("showDatePicker", "showDatePickershowDatePicker")
        val cal = Calendar.getInstance()
        DatePickerDialog(navigator!!.getContext(),{_, year, month, date -> //datepickerdialog 띄우기
            visitdate.set("${String.format("%04d",year)}.${String.format("%02d",month+1)}.${String.format("%02d",date)}")
        }, cal.get(Calendar.YEAR),cal.get(Calendar.MARCH),cal.get(Calendar.DATE)).show()
    }

    fun setUserOrganizationData(profile : MyProfileResponse) {
        originOrganizationId = profile.organization!!.id

        isModify.set(true)
        hasOrganization.set(true)
        Log.d("setUserOrganizationData", profile.toString())
        OrganizationRepository.getOrganizationRequirement(profile.organization!!.id, object: OrganizationDataSource.GetOrganizationRequirementCallback {
            override fun onSuccess(selectableOrganizationsRequirement: OrganizationRequirement?) {
                if (selectableOrganizationsRequirement != null) {
                    var org = Organization(profile.organization!!.id, profile.organization!!.name)

                    organization.set(org)
                    when (selectableOrganizationsRequirement.depth1) {
                        "" -> canChooseDepth1Department.set(false)
                        else -> {
                            canChooseDepth1Department.set(true)
                            depth1type.set(getCompleteWord(selectableOrganizationsRequirement.depth1!!,"을","를") + " 선택해주세요")
                            profile.depth1?.let {
                                try {
                                    var sub = Department(profile.depth1!!.id, profile.depth1!!.name)
                                    subDepartment1.set(sub)
                                } catch (e: Exception ) {
                                    subDepartment1.set(null)
                                }
                            }

                        }
                    }
                    when (selectableOrganizationsRequirement.depth2) {
                        "" -> canChooseDepth2Department.set(false)
                        else -> {
                            canChooseDepth2Department.set(true)
                            depth2type.set(getCompleteWord(selectableOrganizationsRequirement.depth2!!,"을","를") + " 선택해주세요")
                            profile.depth2?.let {
                                try {
                                    var sub = Department(profile.depth2!!.id, profile.depth2!!.name)
                                    subDepartment2.set(sub)
                                } catch (e: Exception ) {
                                    subDepartment2.set(null)
                                }
                            }
                        }
                    }
                    when (selectableOrganizationsRequirement.depth3) {
                        "" -> canChooseDepth3Department.set(false)
                        else -> {
                            canChooseDepth3Department.set(true)
                            depth3type.set(getCompleteWord(selectableOrganizationsRequirement.depth3!!,"을","를") + " 선택해주세요")
                            profile.depth3?.let {
                                try {
                                    var sub = Department(profile.depth3!!.id, profile.depth3!!.name)
                                    subDepartment3.set(sub)
                                } catch (e: Exception ) {
                                    subDepartment3.set(null)
                                }
                            }


                        }
                    }
                    when (selectableOrganizationsRequirement.depth4) {
                        "" -> canChooseDepth4Department.set(false)
                        else -> {
                            canChooseDepth4Department.set(true)
                            depth4type.set(getCompleteWord(selectableOrganizationsRequirement.depth4!!,"을","를") + " 선택해주세요")
                            profile.depth4?.let {
                                try {
                                    var sub = Department(profile.depth4!!.id, profile.depth4!!.name)
                                    subDepartment4.set(sub)
                                } catch (e: Exception ) {
                                    subDepartment4.set(null)
                                }
                            }
                        }
                    }
                    organizationRequirement.set(selectableOrganizationsRequirement)

                    profile.searchContent?.let {
                        searchValue.set(profile.searchContent)
                    }

                    profile.visitDay?.let {
                        visitdate.set(profile.visitDay)
                    }

                    profile.familyRelations?.let {
                        familyRelation.set(profile.familyRelations)
                    }

                    profile.age?.let {
                        age.set(profile.age)
                    }

                    profile.nickname?.let {
                        nickname.set(profile.nickname)
                    }

                    profile.number?.let {
                        number.set(profile.number)
                    }

                    profile.organizationEmployeeNumber?.let {
                        organizationEmployeeNumber.set(profile.organizationEmployeeNumber)
                    }

                    profile.identification?.let {
                        id.set(profile.identification)
                    }

                    profile.organizationEmployeeName?.let {
                        name.set(profile.organizationEmployeeName)
                    }

                    profile.organizationEmail?.let {
                        organizationEmail.set(profile.organizationEmail)
                    }

                    profile.instagramAccount?.let {
                        instaAccount.set(profile.instagramAccount)
                    }

                    profile.input?.let {
                        inputValue.set(profile.input)
                    }

                    profile.address?.let {
                        address.set(profile.address)
                    }

                    profile.attendanceNo?.let {
                        joinNumber.set(profile.attendanceNo)
                    }

                    profile.studentId?.let {
                        studentId.set(profile.studentId)
                    }

                    profile.certNo?.let {
                        authNum.set(profile.certNo)
                    }


                    DebugLog.d("[OrganizationRequirement] : $selectableOrganizationsRequirement")

                    //requestSubDepartments(organizationId, 1, 0)
                    return
                }
                canChooseDepartment.set(true)




            }

            override fun onFailed(reason: String) {
                DebugLog.e(OrganizationException(reason))
            }

        })

    }

    private fun initObservableData() {
        organization.set(null)
        canChooseDepartment.set(false)
        department.set(null)
        canInputEmail.set(false)
        canInputEmployeeNumber.set(false)
        hasAgreeWithEnterprisePrivacy.set(false)
        //hasOrganization.set(false)
        isModify.set(false)
        subDepartment1.set(null)
        subDepartment2.set(null)
        subDepartment3.set(null)
        subDepartment4.set(null)
        canChooseDepth1Department.set(false)
        canChooseDepth2Department.set(false)
        canChooseDepth3Department.set(false)
        canChooseDepth4Department.set(false)
        organizationRequirement.set(null)
        depth1type.set("")
        depth2type.set("")
        depth3type.set("")
        depth4type.set("")
        familyRelation.set("")
        age.set("")
        nickname.set("")
        name.set("")
        id.set("")
        instaAccount.set("")
        joinNumber.set("")
        studentId.set("")
        address.set("")
        organizationEmployeeNumber.set("")
        organizationEmail.set("")
        visitdate.set("")
        inputValue.set("")
        searchValue.set("")
        authNum.set("")
        number.set("")
        isAuthCodeValid.set(false)
    }


    fun setSpaceGroupOrganization(organizationId: Long) {
        isDeepLink.set(true)

        OrganizationRepository.getOrganizations(object : OrganizationDataSource.GetOrganizationsCallback {
            override fun onSuccess(selectableOrganizations: List<Organization>?) {
                selectableOrganizations!!.forEach { org ->
                    if (org.id == organizationId) {

                        afterSelectOrganization(org)

                    }
                }
            }

            override fun onFailed(reason: String) {
                DebugLog.e(OrganizationException(reason))
                showToast("기업 정보를 불러올 수 없습니다. 다시 시도해주세요.")
                navigator!!.finish()
            }

        })

    }

    fun setWhereToGo(camId: Long) {
        Log.d("TTTTTTTTTTTTTTT", "" + camId)
        isGoCampaign.set(true)
        campaignId = camId
    }

    /*fun rollbackObservableData() {
        organization.set(originOrganization.)
        canChooseDepartment.set(false)
        department.set(null)
        canInputEmail.set(false)
        canInputEmployeeNumber.set(false)
        hasAgreeWithEnterprisePrivacy.set(false)
        hasOrganization.set(false)
        isModify.set(false)
        subDepartment1.set(null)
        subDepartment2.set(null)
        subDepartment3.set(null)
        subDepartment4.set(null)
        canChooseDepth1Department.set(false)
        canChooseDepth2Department.set(false)
        canChooseDepth3Department.set(false)
        canChooseDepth4Department.set(false)
        organizationRequirement.set(null)
        depth1type.set("")
        depth2type.set("")
        depth3type.set("")
        depth4type.set("")
        familyRelation.set("")
        age.set("")
        nickname.set("")
        name.set("")
        id.set("")
        instaAccount.set("")
        joinNumber.set("")
        studentId.set("")
        address.set("")
        organizationEmployeeNumber.set("")
        organizationEmail.set("")
        visitdate.set("")
        inputValue.set("")
        searchValue.set("")
        authNum.set("")
        number.set("")
        isAuthCodeValid.set(false)
    }*/
}

data class CorporateMemberDeliveryInputForm(
    val organization: Organization?,
    val department: Department?,
    val organizationEmployeeNumber: String?,
    val organizationEmail: String?,
    val depth1: Department?,
    val depth2: Department?,
    val depth3: Department?,
    val depth4: Department?,
    /*val depth1Title: String?,
    val depth2Title: String?,
    val depth3Title: String?,
    val depth4Title: String?,
    val depth1Hint: String?,
    val depth2Hint: String?,
    val depth3Hint: String?,
    val depth4Hint: String?,*/
    val searchContent: String?,
    val visitDay: String?,
    val certNo: String?,
    val familyRelations: String?,
    val age: String?,
    val nickname: String?,
    val identification: String?,
    val address: String?,
    val attendanceNo: String?,
    val studentId: String?,
    val number: String?,
    val input: String?,
    val organizationEmployeeName: String?,
    val instagramAccount: String?

): Serializable