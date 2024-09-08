package kr.co.bigwalk.app.my_page

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_select_departments_list.*
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.databinding.ActivitySelectOrganizationDepartmentsListBinding
import kr.co.bigwalk.app.my_page.adapter.SelectOrganizationDepartmentListRecyclerAdapter

class SelectOrganizationDepartmentListActivity : AppCompatActivity(), BaseNavigator {
    private lateinit var binding: ActivitySelectOrganizationDepartmentsListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serializableExtra = intent.getSerializableExtra("organization")
        if(serializableExtra is Organization){
            binding = DataBindingUtil.setContentView(this, R.layout.activity_select_organization_departments_list)
//            binding.viewModel = SelectOrganizationDepartmentViewModel(this, serializableExtra, MyPageActivity.instance!!.viewModel)
//            binding.selectRecycler.adapter = SelectOrganizationDepartmentListRecyclerAdapter(this, MyPageActivity.instance!!.viewModel)
            et_search_field.addTextChangedListener (object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    (binding.selectRecycler.adapter as SelectOrganizationDepartmentListRecyclerAdapter).filter.filter(et_search_field.text)
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
        return
    }

    override fun getContext(): Activity {
        return this
    }
}
