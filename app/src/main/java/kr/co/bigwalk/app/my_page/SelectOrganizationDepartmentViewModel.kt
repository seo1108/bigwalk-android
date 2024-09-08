package kr.co.bigwalk.app.my_page

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.organization.Department
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.data.organization.repository.OrganizationDataSource
import kr.co.bigwalk.app.data.organization.repository.OrganizationRepository
import kr.co.bigwalk.app.exception.OrganizationException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class SelectOrganizationDepartmentViewModel(private val navigator: BaseNavigator, organization: Organization, private val viewModel: MyPageViewModel) : BaseObservable(){

    var departments : ObservableArrayList<Department> = ObservableArrayList()

    init {
        requestDepartments(organization = organization)
    }

    private fun requestDepartments(organization: Organization) {
        OrganizationRepository.getDepartments(organization.id!!, object : OrganizationDataSource.GetDepartmentsCallback {
            override fun onSuccess(selectableDepartments: List<Department>?) {
                viewModel.organization.set(organization)
                viewModel.organizationName.set(organization.name)
                if (selectableDepartments?.size == 0) {
                    DebugLog.d("부서를 입력받지 않는 기업.")
                    viewModel.department.set(null)
                    viewModel.departmentName.set("")
                    navigator.finish()
                    return
                }
                departments.clear()
                departments.addAll(selectableDepartments!!)
            }

            override fun onFailed(reason: String) {
                navigator.finish()
                showToast("부서 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(OrganizationException(reason))
            }

        })
    }

    fun finishActivity() {
        navigator.finish()
    }


}