package kr.co.bigwalk.app.sign_in.organization

import android.util.Log
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.organization.Department
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.data.organization.SubDepartment
import kr.co.bigwalk.app.data.organization.repository.OrganizationDataSource
import kr.co.bigwalk.app.data.organization.repository.OrganizationRepository
import kr.co.bigwalk.app.exception.OrganizationException
import kr.co.bigwalk.app.util.DebugLog

class SelectDepartmentViewModel(private val navigator: BaseNavigator, private val organization: Organization, private val department: Department?, private val depth: String, private val departmentTitle: String) : BaseObservable(){

    var departments : ObservableArrayList<Department> = ObservableArrayList()
    var subDepartments : ObservableArrayList<Department> = ObservableArrayList()
    var title : ObservableField<String> = ObservableField()

    init {
        //requestDepartments(organization = organization)
        if ("1" == depth) {
            requestSubDepartments1(organization = organization!!)
        } else {
            requestSubDepartments(department!!, depth)
        }
    }

    private fun requestDepartments(organization: Organization) {
        OrganizationRepository.getDepartments(organization.id!!, object : OrganizationDataSource.GetDepartmentsCallback {
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

    private fun requestSubDepartments(department: Department, subDepth: String) {
        var parentId = 0L
        if (subDepth == "0") {
            parentId = 0
        } else {
            parentId = department!!.id
        }

        OrganizationRepository.getSubDepartments(organization.id!!, depth.toLong(), parentId, object : OrganizationDataSource.GetSubDepartmentsCallback {
            override fun onSuccess(selectableSubDepartments: List<Department>?) {
                title.set(departmentTitle)
                if (selectableSubDepartments.isNullOrEmpty()) {
                    DebugLog.d("depth$depth 선택할 필요 없음.")
                    return
                }
                subDepartments.clear()
                subDepartments.addAll(selectableSubDepartments)
                DebugLog.d(subDepartments.toString())
            }

            override fun onFailed(reason: String) {
                DebugLog.e(OrganizationException(reason))
            }

        })
    }



    private fun requestSubDepartments1(organization: Organization) {
        OrganizationRepository.getSubDepartments(organization.id!!, 1, 0, object : OrganizationDataSource.GetSubDepartmentsCallback {
            override fun onSuccess(selectableSubDepartments: List<Department>?) {
                title.set(departmentTitle)
                if (selectableSubDepartments.isNullOrEmpty()) {
                    DebugLog.d("depth1 선택할 필요 없음.")
                    return
                }
                subDepartments.clear()
                subDepartments.addAll(selectableSubDepartments)
                DebugLog.d(subDepartments.toString())
            }

            override fun onFailed(reason: String) {
                DebugLog.e(OrganizationException(reason))
            }

        })
    }


    fun finishActivity() {
        navigator.finish()
    }


}