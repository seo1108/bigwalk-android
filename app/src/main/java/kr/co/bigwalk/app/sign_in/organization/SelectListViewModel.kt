package kr.co.bigwalk.app.sign_in.organization

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableList
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.organization.*
import kr.co.bigwalk.app.data.organization.repository.OrganizationDataSource
import kr.co.bigwalk.app.data.organization.repository.OrganizationRepository
import kr.co.bigwalk.app.exception.OrganizationException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class SelectListViewModel(private val navigator: BaseNavigator): BaseObservable() {

    var isSelectOrganization: ObservableBoolean = ObservableBoolean(true)
    var organizations : ObservableList<OrganizationItem> = ObservableArrayList()
    var departments: ObservableList<OrganizationItem> = ObservableArrayList()
    var subdepartments: ObservableList<OrganizationItem> = ObservableArrayList()

    fun requestOrganizations() {
        OrganizationRepository.getOrganizations(object : OrganizationDataSource.GetOrganizationsCallback {
            override fun onSuccess(selectableOrganizations: List<Organization>?) {
                if (selectableOrganizations.isNullOrEmpty()) {
                    showToast("선택 가능한 기업 목록이 없습니다.")
                    navigator.finish()
                    return
                }
                organizations.clear()
                organizations.addAll(selectableOrganizations)
            }

            override fun onFailed(reason: String) {
                DebugLog.e(OrganizationException(reason))
                showToast("기업 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                navigator.finish()
            }

        })
    }

    fun requestDepartments(organizationId: Long) {
        OrganizationRepository.getDepartments(organizationId, object : OrganizationDataSource.GetDepartmentsCallback {
            override fun onSuccess(selectableDepartments: List<Department>?) {
                if (selectableDepartments.isNullOrEmpty()) {
                    DebugLog.d("부서 선택할 필요 없음.")
                    return
                }
                departments.clear()
                departments.addAll(selectableDepartments)
                DebugLog.d(departments.toString())
            }

            override fun onFailed(reason: String) {
                DebugLog.e(OrganizationException(reason))
            }

        })

    }

    fun requestSubDepartments(organizationId: Long, depth: Long, parentId: Long) {
        OrganizationRepository.getSubDepartments(organizationId, depth, parentId, object : OrganizationDataSource.GetSubDepartmentsCallback {
            override fun onSuccess(selectableSubDepartments: List<Department>?) {
                if (selectableSubDepartments.isNullOrEmpty()) {
                    DebugLog.d("하위 부서 선택할 필요 없음.")
                    return
                }
                subdepartments.clear()
                subdepartments.addAll(selectableSubDepartments)
                DebugLog.d(subdepartments.toString())
            }

            override fun onFailed(reason: String) {
                DebugLog.e(OrganizationException(reason))
            }

        })

    }

    fun finishActivity() {
        //navigator.getContext()
        navigator.finish()
    }

}