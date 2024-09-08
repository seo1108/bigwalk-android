package kr.co.bigwalk.app.my_page

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.organization.OrganizationIdType
import kr.co.bigwalk.app.databinding.ActivityOrganizationMemberFormBinding
import kr.co.bigwalk.app.util.DebugLog

class OrganizationMemberFormActivity: AppCompatActivity(), BaseNavigator {

    private lateinit var binding: ActivityOrganizationMemberFormBinding
    private lateinit var viewModel: OrganizationMemberFormViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_organization_member_form)
        viewModel = OrganizationMemberFormViewModel(this)
        binding.viewModel = viewModel
    }

    override fun onResume() {
        super.onResume()
        DebugLog.d(OrganizationSingleton.organization.toString())
        if (OrganizationSingleton.organization != null) {
            viewModel.hasOrganization.set(true)
            if (viewModel.organization.get()?.name ?: "" != OrganizationSingleton.organization?.name ?: "") {
                DepartmentSingleton.department = null
                viewModel.department.set(null)
            }
            viewModel.organization.set(OrganizationSingleton.organization)
            viewModel.requestDepartment()
        }
        if (DepartmentSingleton.department != null) {
            viewModel.department.set(DepartmentSingleton.department)
        }
        when (OrganizationSingleton.organization?.organizationIdType) {
            OrganizationIdType.EMAIL -> {
                viewModel.hasOrganizationEmail.set(true)
                viewModel.hasEmployeeNumber.set(false)
            }
            OrganizationIdType.EMPLOYEE_NUMBER -> {
                viewModel.hasOrganizationEmail.set(false)
                viewModel.hasEmployeeNumber.set(true)
            }
            OrganizationIdType.EMAIL_EMPLOYEE_NUMBER_BOTH -> {
                viewModel.hasOrganizationEmail.set(true)
                viewModel.hasEmployeeNumber.set(true)
            }
            else -> {
                viewModel.hasOrganizationEmail.set(false)
                viewModel.hasEmployeeNumber.set(false)
            }
        }
    }

    override fun getContext(): Activity {
        return this
    }

    override fun onDestroy() {
        super.onDestroy()
        OrganizationSingleton.organization = null
        DepartmentSingleton.department = null
    }
}