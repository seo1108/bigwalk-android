package kr.co.bigwalk.app.my_page

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentManager
import com.kennyc.bottomsheet.BottomSheetListener
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.mission.WelcomeMissionStatus
import kr.co.bigwalk.app.data.organization.Department
import kr.co.bigwalk.app.data.organization.Group
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.data.organization.OrganizationIdType
import kr.co.bigwalk.app.data.organization.repository.OrganizationDataSource
import kr.co.bigwalk.app.data.organization.repository.OrganizationRepository
import kr.co.bigwalk.app.data.organization.space.SpaceOrganizationResponse
import kr.co.bigwalk.app.data.organization.space.SpaceUserRequest
import kr.co.bigwalk.app.data.user.dto.ModifyUserRequest
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.data.user.repository.UserRepository.existsNickname
import kr.co.bigwalk.app.exception.OrganizationException
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.my_page.withdraw.WithdrawActivity
import kr.co.bigwalk.app.sign_in.SignInActivity
import kr.co.bigwalk.app.sign_in.character.SelectCharacterActivity
import kr.co.bigwalk.app.util.*
import kr.co.bigwalk.app.walk.alarm.AlarmBroadcastReceiver
import kr.co.bigwalk.app.walk.sensor.WalkService
import kr.co.bigwalk.app.walk.sensor.WalkServiceManager
import org.apache.commons.lang3.StringUtils
import java.io.File

class MyPageViewModel(
    private val activity: Activity?,
    private val supportFragmentManager: FragmentManager?,
    private val navigator: BaseNavigator
) : BaseObservable() {
    lateinit var profile: UserProfileResponse

    val profilePath = ObservableField<String>("")
    val name = ObservableField<String>("")
    val organizationEmail = ObservableField<String>("")
    val organizationEmployeeNumber = ObservableField<String>("")
    val organizationName = ObservableField<String>("")
    val departmentName = ObservableField<String>("")

    var organization = ObservableField<Organization>()
    var department = ObservableField<Department>()
    var group = ObservableField<Group>()
    var value1 = ObservableField<String>()
    var value2 = ObservableField<String>()
    var value3 = ObservableField<String>()
    var value4 = ObservableField<String>()
    var value5 = ObservableField<String>()
    var spaceOrganization = ObservableField<SpaceOrganizationResponse>()

    val isCorporateUser: ObservableField<Boolean> = ObservableField(false)
    val hasDepartment: ObservableField<Boolean> = ObservableField(false)
    val hasOrganizationEmail: ObservableField<Boolean> = ObservableField(false)
    val hasEmployeeNumber: ObservableField<Boolean> = ObservableField(false)
    val confirmNickname: ObservableField<Boolean> = ObservableField(false)
    val isSpaceType: ObservableField<Boolean> = ObservableField(false)
    var file: File? = null

    init {
        getMyProfile()
    }


    var focusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
        run {
            val editText = view as EditText
            if (!hasFocus) {
                if (!StringUtils.isBlank(editText.text) && editText.text.count() >= 2
                    && editText.text.count() <= 10) {
                    checkDuplicatedNickname(editText.text.toString())
                } else {
                    showToast("닉네임은 2~10자로 적어주세요.")
                }
            }
        }
    }

    private fun checkDuplicatedNickname(nidkname: String) {
        existsNickname(
            nidkname,
            object : UserDataSource.ExistsNicknameCallback {
                override fun onExists() {
                    confirmNickname.set(false)
                    if (!name.get().equals(nidkname)) {
                        showToast("중복되는 닉네임입니다.")
                        return
                    }
                }

                override fun onDoesNotExists() {
                    confirmNickname.set(true)
                    showToast("사용가능한 닉네임입니다.")
                }

                override fun onFailed(reason: String) {
                    confirmNickname.set(false)
                    showToast("닉네임 변경에 실패했습니다. 다시 시도해주세요")
                }
            })
    }

    fun downloadProfileImage() {
        if (profilePath.get()?.contains("http")!!) {
//            showToast(profilePath.toString())
            file = activity?.let { downloadAndCompressFile(profilePath.get()!!, activity, false) }
        }
    }

    fun getMyProfile() {
        UserRepository.getMyProfile(object : UserDataSource.GetMyProfileCallback {
            override fun onSuccess(userProfileResponse: UserProfileResponse) {
                profile = userProfileResponse
                profilePath.set(profile.profilePath)
                name.set(profile.name)
                if (StringUtils.isBlank(profile.megaOrganization?.name)) {
                    isCorporateUser.set(false)
                } else {
                    isCorporateUser.set(true)
                    OrganizationSingleton.organization = profile.megaOrganization
                    organization.set(profile.megaOrganization)
                    organizationName.set(organization.get()?.name)
                    organizationName.get()?.let { PreferenceManager.saveOrganizationName(it) }
                    if (!profile.megaGroup?.name.isNullOrEmpty()) {
                        group.set(profile.megaGroup)
                        value1.set(profile.megaGroup?.value1)
                        value2.set(profile.megaGroup?.value2)
                        value3.set(profile.megaGroup?.value3)
                        value4.set(profile.megaGroup?.value4)
                        value5.set(profile.megaGroup?.value5)
                    }
                    if (StringUtils.isBlank(profile.megaDepartment?.name)) {
                        hasDepartment.set(false)
                    } else {
                        hasDepartment.set(true)
                        department.set(profile.megaDepartment)
                        departmentName.set(department.get()?.name)
                        departmentName.get()?.let { PreferenceManager.saveDepartmentName(it) }
                    }
                    if (StringUtils.isBlank(profile.megaOrganizationEmail)) {
                        hasOrganizationEmail.set(false)
                    } else {
                        hasOrganizationEmail.set(true)
                        organizationEmail.set(profile.megaOrganizationEmail)
                    }
                    if (StringUtils.isBlank(profile.megaOrganizationEmployeeNumber)) {
                        hasEmployeeNumber.set(false)
                    } else {
                        hasEmployeeNumber.set(true)
                        organizationEmployeeNumber.set(profile.megaOrganizationEmployeeNumber)
                    }
                    if (profile.megaGroup?.organizationIdType?.id == OrganizationIdType.SPACE.id) {
                        isSpaceType.set(true)
                        hasOrganizationEmail.set(false)
                        hasEmployeeNumber.set(false)
                        getSpaceGroup()
                    } else {
                        isSpaceType.set(false)
                    }
                }
                DebugLog.d(userProfileResponse.toString())
            }

            override fun onBlackUser(userProfileResponse: UserProfileResponse) {
                BlackUser(navigator.getContext()).showBlackUserMessageAlert(userProfileResponse)
            }

            override fun onFailed(reason: String) {
                showToast("프로필 정보를 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(UserProfileException(reason))
                finishActivity()
            }
        })
    }

    fun getDepartment() {
        OrganizationSingleton.organization?.id?.let {
            OrganizationRepository.getDepartments(it, object : OrganizationDataSource.GetDepartmentsCallback {
                override fun onSuccess(selectableDepartments: List<Department>?) {
                    if (selectableDepartments.isNullOrEmpty()) hasDepartment.set(false) else hasDepartment.set(true)
                }

                override fun onFailed(reason: String) {
                    DebugLog.e(OrganizationException(reason))
                }

            })
        }
    }

    private fun getSpaceGroup() {
        group.get()?.id?.let { id ->
            OrganizationRepository.getSpaceOrganization(id, object : OrganizationDataSource.GetSpaceOrganizationCallback {
                override fun onSuccess(spaceOrganizationResponse: SpaceOrganizationResponse?) {
                    spaceOrganization.set(spaceOrganizationResponse)
                }

                override fun onFailed(reason: String) {
                    DebugLog.e(OrganizationException(reason))
                }
            })
        }
    }

    fun finishActivity() {
        activity?.finish()
    }


    fun logout() {
        showToast("로그아웃 되었습니다.")
        PreferenceManager.saveAccessToken(null)
        PreferenceManager.saveDailyStep(0)
        PreferenceManager.saveDonableStep(0)
        PreferenceManager.saveLastMissisonTitle(null)
        PreferenceManager.saveTutorial(false)

        // 웰컴 미션 초기화
        PreferenceManager.saveWelcomeMission1(-1)
        PreferenceManager.saveWelcomeMission1Max(0)
        PreferenceManager.saveWelcomeMission1Completed(false)
        PreferenceManager.saveWelcomeMission1ClearConfirmed(false)
        PreferenceManager.saveWelcomeMission2(-1)
        PreferenceManager.saveWelcomeMission2Max(0)
        PreferenceManager.saveWelcomeMission2Completed(false)
        PreferenceManager.saveWelcomeMission2ClearConfirmed(false)
        PreferenceManager.saveWelcomeMissionStatus(WelcomeMissionStatus.NONE.type)
        AlarmBroadcastReceiver.cancelMissionAlarmManager(navigator.getContext())

        activity?.let {
            startWalkService(it, 0, 0)
        }
        WalkService.stopService(navigator.getContext())
        val intent = Intent(activity, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        navigator.getContext().startActivity(intent)
    }

    fun withdraw() {
        val intent = Intent(activity, WithdrawActivity::class.java)
        navigator.getContext().startActivity(intent)
    }

    fun moveModifyUserInformationPage() {
        val intent = Intent(activity, MyPageModifyActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        navigator.getContext().startActivity(intent)
    }

    fun modifyProfile() {
        val sheet = BottomSheetMenuDialogFragment.Builder(activity)
        sheet.setSheet(R.menu.bottom_sheet_menu)
        sheet.setListener(object : BottomSheetListener {
            override fun onSheetItemSelected(
                cbottomSheet: BottomSheetMenuDialogFragment,
                item: MenuItem?,
                `object`: Any?
            ) {
                when (item?.itemId) {
                    R.id.select_from_album -> {
                        if (isPermissionGranted(navigator.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            requestPermission(navigator.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        } else {
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
                                    pickImageIntent -> navigator.getContext().startActivityForResult(pickImageIntent, REQUEST_GALLERY_PERMISSION)
                            }
                        }
                    }
                    R.id.change_to_default_profile -> {
                        profilePath.set("0")
                    }
                }

            }

            override fun onSheetDismissed(
                bottomSheet: BottomSheetMenuDialogFragment,
                `object`: Any?,
                dismissEvent: Int
            ) {
                //nothing
            }

            override fun onSheetShown(bottomSheet: BottomSheetMenuDialogFragment, `object`: Any?) {
                //nothing
            }
        }).show(supportFragmentManager!!)
    }

    fun moveToOrganizationList() {
        if (StringUtils.isBlank(name.get())) {
            showToast("닉네임이 없어 유저 정보를 식별할 수 없습니다. 다시 시도해주세요!")
            navigator.finish()
            return
        }
        UserNameSingleton.name = name.get()
        val intent = Intent(activity, OrganizationMemberFormActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        navigator.getContext().startActivity(intent)
    }

    fun showOrganizations() {
        if (isSpaceType.get() == false) {
            val intent = Intent(activity, SelectOrganizationListActivity::class.java)
            navigator.getContext().startActivity(intent)
        }

    }

    fun showDepartments() {
        val intent = Intent(activity, SelectOrganizationDepartmentListActivity::class.java)
        intent.putExtra("organization", OrganizationSingleton.organization)
        navigator.getContext().startActivity(intent)
    }

    private fun validateForm(): Boolean {
        if (StringUtils.isBlank(name.get()) || name.get()!!.count() < 2 || name.get()!!.count() > 10) {
            showToast("닉네임은 2~10자 사이로 입력해주세요!")
            return false
        }
        if (isCorporateUser.get()!! && StringUtils.isBlank(organizationName.get())) {
            showToast("회사를 선택해주세요!")
            return false
        }
        if (hasDepartment.get()!! && StringUtils.isBlank(departmentName.get())) {
            showToast("부서를 선택해주세요!")
            return false
        }
        if (hasEmployeeNumber.get()!! && StringUtils.isBlank(organizationEmployeeNumber.get())) {
            showToast("사원번호를 입력해주세요!")
            return false
        }
        if (hasOrganizationEmail.get()!! && StringUtils.isBlank(organizationEmail.get())) {
            showToast("회사 이메일을 입력해주세요!")
            return false
        }
        if (isSpaceType.get() == true) {
            spaceOrganization.get()?.let { spaceOrganization ->
                if (spaceOrganization.options.size > 0 && value1.get().isNullOrEmpty() && spaceOrganization.options[0].isNecessary) {
                    showToast(spaceOrganization.options[0].placeholder)
                    return false
                }
                if (spaceOrganization.options.size > 1 && value2.get().isNullOrEmpty() && spaceOrganization.options[1].isNecessary) {
                    showToast(spaceOrganization.options[1].placeholder)
                    return false
                }
                if (spaceOrganization.options.size > 2 && value3.get().isNullOrEmpty() && spaceOrganization.options[2].isNecessary) {
                    showToast(spaceOrganization.options[2].placeholder)
                    return false
                }
                if (spaceOrganization.options.size > 3 && value4.get().isNullOrEmpty() && spaceOrganization.options[3].isNecessary) {
                    showToast(spaceOrganization.options[3].placeholder)
                    return false
                }
                if (spaceOrganization.options.size > 4 && value5.get().isNullOrEmpty() && spaceOrganization.options[4].isNecessary) {
                    showToast(spaceOrganization.options[4].placeholder)
                    return false
                }
            }
        }
        return true
    }

    fun requestModify() {
        if (!validateForm()) return
        val modifyUserRequest = toModifyRequest()
        var uploadFile: File? = null
        when (profilePath.get()) {
            "0" -> modifyUserRequest?.characterId = 0
            "1" -> modifyUserRequest?.characterId = 1
            "2" -> modifyUserRequest?.characterId = 2
            "3" -> modifyUserRequest?.characterId = 3
            "4" -> modifyUserRequest?.characterId = 4
            "5" -> modifyUserRequest?.characterId = 5
            else -> {
                var fileUri: Uri? = null
                if (profilePath.get()!!.contains("http")) {// 프로필 사진 수정없이 기본정보 수정하는 경우
                    if (file != null) {
                        activity?.let { fileUri = Uri.fromFile(file) }
                    }
                } else {// 프로필 사진 수정한 경우
                    fileUri = Uri.fromFile(File(profilePath.get()!!))
                }
                if (fileUri?.scheme == "file") uploadFile = compressFile(navigator.getContext(), fileUri!!)
            }

        }

        if (modifyUserRequest == null) return
        UserRepository.modifyProfileUserInformation(
            uploadFile,
            modifyUserRequest,
            object : UserDataSource.ModifyProfileUserInformationCallback {
                override fun onSuccess(userProfileResponse: UserProfileResponse) {
                    if (isSpaceType.get() == true) {
                        modifySpaceGroup(userProfileResponse)
                    } else {
                        showToast("프로필변경에 성공하였습니다.")
                        profile = userProfileResponse
                        name.set(userProfileResponse.name)
                        organizationEmail.set(userProfileResponse.megaOrganizationEmail)
                        organizationEmployeeNumber.set(userProfileResponse.megaOrganizationEmployeeNumber)
                        organization.set(userProfileResponse.megaOrganization)
                        department.set(userProfileResponse.megaDepartment)
                        group.set(userProfileResponse.megaGroup)
                        profilePath.set(userProfileResponse.profilePath)

                        PreferenceManager.saveOrganization(userProfileResponse.megaOrganization?.id ?: -1L)
                        PreferenceManager.saveCharacter("${userProfileResponse.characterId}")

                        navigator.finish()
                    }
                }

                override fun onDepartmentError() {
                    showToast("선택하신 부서는 해당 기업의 부서가 아닙니다. 다시 선택해주세요!")
                }

                override fun onFailed(reason: String) {
                    DebugLog.e(UserProfileException(reason))
                    showToast("프로필변경에 실패하였습니다.")
                }

                override fun onDuplicatedName() {
                    showToast("이미 존재하는 이름입니다!")
                }
            })
    }


    private fun modifySpaceGroup(userProfileResponse: UserProfileResponse) {
        val request = SpaceUserRequest(
            organizationId = spaceOrganization.get()?.id ?: -1,
            departmentId = 0
        )
        request.toSpaceUserRequest(
            value1.get().orEmpty(),
            value2.get().orEmpty(),
            value3.get().orEmpty(),
            value4.get().orEmpty(),
            value5.get().orEmpty()
        )
        OrganizationRepository
            .modifyProfileByOrganization(request, object : OrganizationDataSource.ModifyProfileByOrganizationCallback {
                override fun onSuccess() {
                    showToast("프로필변경에 성공하였습니다.")
                    profile = userProfileResponse
                    name.set(userProfileResponse.name)
                    organizationEmail.set(userProfileResponse.megaOrganizationEmail)
                    organizationEmployeeNumber.set(userProfileResponse.megaOrganizationEmployeeNumber)
                    organization.set(userProfileResponse.megaOrganization)
                    department.set(userProfileResponse.megaDepartment)
                    group.set(userProfileResponse.megaGroup)
                    profilePath.set(userProfileResponse.profilePath)

                    PreferenceManager.saveOrganization(userProfileResponse.megaOrganization?.id ?: -1L)
                    PreferenceManager.saveCharacter("${userProfileResponse.characterId}")

                    navigator.finish()
                }

                override fun onFailed(reason: String) {
                    DebugLog.e(OrganizationException(reason))
                }

            })
    }

    private fun toModifyRequest(): ModifyUserRequest? {
        val request = name.get()?.let { ModifyUserRequest(it) }
        if (isCorporateUser.get()!!) {request?.organizationId = organization.get()?.id}
        if (hasDepartment.get()!!) {request?.departmentId = department.get()?.id}
        if (hasOrganizationEmail.get()!!) {request?.organizationEmail = organizationEmail.get()}
        if (hasEmployeeNumber.get()!!) {request?.organizationEmployeeNumber = organizationEmployeeNumber.get()}
        return request
    }

    fun removeOrganizationAccount() {
        showRemoveOrganizationDialog()
    }

    private fun showRemoveOrganizationDialog() {
        val dialogBuilder = AlertDialog.Builder(navigator.getContext())
        dialogBuilder.setMessage("일반 계정으로 전환하시겠습니까?\n개인정보 수정에서 기업 계정으로 재전환이 가능합니다.")
            .setPositiveButton("확인") { _, _ ->
                removeOrganization()
            }
            .setNegativeButton("취소") {_, _ ->

            }
        dialogBuilder.show()
    }

    private fun removeOrganization() {
        UserRepository.removeOrganizationFromProfile(object : UserDataSource.RemoveOrganizationCallback {
            override fun onSuccess() {
                PreferenceManager.clearOrganization()
                PreferenceManager.clearDepartmentName()
                showToast("일반계정으로 전환하였습니다!")
                navigator.finish()
            }

            override fun onFailed(reason: String) {
                showToast("일반계정으로 전환할 수 없습니다. 다시 시도해주세요!")
                DebugLog.e(UserProfileException(reason))
                navigator.finish()
            }

        })
    }

    private fun startWalkService(context: Context, dailyStep: Int, donableStep: Int) {
        WalkServiceManager(context).syncStepToForeground(dailyStep, donableStep)
    }

    companion object {
        const val CHARACTER_REQUEST_CODE = 4250
    }

}

object UserNameSingleton {
    var name: String? = null
}

object OrganizationSingleton {
    var organization: Organization? = null
}

object DepartmentSingleton {
    var department: Department? = null
}