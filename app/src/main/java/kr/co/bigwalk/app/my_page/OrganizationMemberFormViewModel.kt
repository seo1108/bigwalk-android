package kr.co.bigwalk.app.my_page

import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.organization.Department
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.data.organization.repository.OrganizationDataSource
import kr.co.bigwalk.app.data.organization.repository.OrganizationRepository
import kr.co.bigwalk.app.data.user.dto.ModifyUserRequest
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.exception.OrganizationException
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.sign_in.agreement.AgreementWithEnterprisePrivacyActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast
import org.apache.commons.lang3.StringUtils

class OrganizationMemberFormViewModel(private val navigator: BaseNavigator) : BaseObservable() {

    val organization = ObservableField<Organization>()
    val department = ObservableField<Department>()
    val hasDepartment = ObservableBoolean(false)
    val hasEmployeeNumber = ObservableBoolean(false)
    val hasOrganizationEmail = ObservableBoolean(false)
    val employeeNumber = ObservableField<String>()
    val organizationEmail = ObservableField<String>()
    var hasAgreeWithEnterprisePrivacy = ObservableBoolean(false)
    var hasOrganization = ObservableBoolean(false)

    fun showOrganizations() {
        val intent = Intent(navigator.getContext(), SelectOrganizationListActivity::class.java)
        navigator.getContext().startActivity(intent)
    }

    fun finishActivity() {
        navigator.finish()
    }

    fun requestDepartment() {
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

    fun showDepartments() {
        val intent = Intent(navigator.getContext(), SelectOrganizationDepartmentListActivity::class.java)
        intent.putExtra("organization", organization.get())
        navigator.getContext().startActivity(intent)
    }

    fun changeOrganizationAccount() {
        if (StringUtils.isBlank(UserNameSingleton.name)) {
            showToast("유저 정보를 식별할 수 없습니다. 다시 시도해주세요!")
            navigator.finish()
            return
        }
        if (StringUtils.isBlank(organization.get()?.name)) {
            showToast("회사를 선택해주세요!")
            return
        }
        if (hasDepartment.get() && StringUtils.isBlank(department.get()?.name)) {
            showToast("부서를 선택해주세요!")
            return
        }
        if (hasEmployeeNumber.get() && StringUtils.isBlank(employeeNumber.get())) {
            showToast("사원번호를 입력해주세요!")
            return
        }
        if (hasOrganizationEmail.get() && StringUtils.isBlank(organizationEmail.get())) {
            showToast("회사 이메일을 입력해주세요!")
            return
        }
        if (!hasAgreeWithEnterprisePrivacy.get()) {
            showToast("개인정보처리방침 및 동의에 체크해주세요!")
            return
        }

        showConfirmDialog()

    }

    private fun showConfirmDialog() {
        val dialogBuilder = AlertDialog.Builder(navigator.getContext())
        dialogBuilder.setMessage("기업계정으로 전환하시겠습니까?\n입력하신 정보를 다시 한번 확인해주세요!")
            .setPositiveButton("확인") { _, _ ->
                modifyUserProfile()
            }
            .setNegativeButton("취소") {_, _ ->

            }
        dialogBuilder.show()
    }

    private fun modifyUserProfile() {
        val request = UserNameSingleton.name?.let { ModifyUserRequest(it) }
        request?.organizationId = organization.get()?.id
        if (hasDepartment.get()) request?.departmentId = department.get()?.id
        if (hasEmployeeNumber.get()) request?.organizationEmployeeNumber = employeeNumber.get()
        if (hasOrganizationEmail.get()) request?.organizationEmail = organizationEmail.get()
        request?.let {
            UserRepository.modifyProfileUserInformation(null, it, object : UserDataSource.ModifyProfileUserInformationCallback {
                override fun onSuccess(userProfileResponse: UserProfileResponse) {
                    userProfileResponse.megaOrganization?.let { organization ->
                        PreferenceManager.saveOrganization(organization.id?:-1L)
                        organization.name?.let { PreferenceManager.saveOrganizationName(it) }
                        userProfileResponse.megaDepartment?.let { department ->
                            department.name?.let { PreferenceManager.saveDepartmentName(it) }
                        }
                    }
                    if (userProfileResponse.profilePath.isNullOrBlank() || userProfileResponse.profilePath == "0")
                        PreferenceManager.saveCharacter("1")
                    else PreferenceManager.saveCharacter(userProfileResponse.profilePath!!)

                    showToast("기업 계정으로 전환하였습니다!")
                    navigator.finish()
                }

                override fun onDepartmentError() {
                    showToast("선택하신 부서는 해당 기업의 부서가 아닙니다. 다시 선택해주세요!")
                    navigator.finish()
                }

                override fun onFailed(reason: String) {
                    showToast("기업계정으로 전환할 수 없습니다. 다시 시도해주세요!")
                    DebugLog.e(UserProfileException(reason))
                    navigator.finish()
                }

                override fun onDuplicatedName() {
                    showToast("이미 존재하는 이름입니다!")
                }
            })
        }
    }

    fun agreeWithService() {
        if (hasAgreeWithEnterprisePrivacy.get()) hasAgreeWithEnterprisePrivacy.set(false) else hasAgreeWithEnterprisePrivacy.set(true)
    }

    fun showAgreementWithEnterprisePrivacy() {
        val intent = Intent(navigator.getContext(), AgreementWithEnterprisePrivacyActivity::class.java)
        intent.putExtra("organization", organization.get())
        navigator.getContext().startActivity(intent)
    }
}