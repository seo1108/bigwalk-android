package kr.co.bigwalk.app.sign_in.organization

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivitySelectListBinding
import kr.co.bigwalk.app.sign_in.organization.adapter.SelectRecyclerAdapter

class SelectListActivity: AppCompatActivity(), BaseNavigator {

    private lateinit var binding: ActivitySelectListBinding
    var isSelectOrganization: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_list)
        val viewModel = SelectListViewModel(this)
        isSelectOrganization = intent.getBooleanExtra("Organizations", false)
        /*if (isSelectOrganization) {
            viewModel.requestOrganizations()
        } else if (intent.getLongExtra("parentId", -1L) == -1L){
            viewModel.requestDepartments(intent.getLongExtra("OrganizationId", -1L))
        } else {
            viewModel.requestSubDepartments(intent.getLongExtra("OrganizationId", -1L), intent.getLongExtra("parentId", -1L))
        }*/
        if (isSelectOrganization) {
            viewModel.requestOrganizations()
        } else {
            viewModel.requestDepartments(intent.getLongExtra("OrganizationId", -1L))
        }
        viewModel.isSelectOrganization.set(isSelectOrganization)
        binding.viewModel = viewModel
        binding.selectRecycler.adapter = SelectRecyclerAdapter(this, isSelectOrganization)
    }

    override fun getContext(): Activity {
        return this
    }

}