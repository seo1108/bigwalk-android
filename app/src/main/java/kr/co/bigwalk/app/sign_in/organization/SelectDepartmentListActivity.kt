package kr.co.bigwalk.app.sign_in.organization

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.activity_select_departments_list.*
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.organization.Department
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.databinding.ActivitySelectDepartmentsListBinding
import kr.co.bigwalk.app.sign_in.organization.adapter.SelectDepartmentListRecyclerAdapter

class SelectDepartmentListActivity : AppCompatActivity(), BaseNavigator {

    private lateinit var binding: ActivitySelectDepartmentsListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serializableExtra = intent.getSerializableExtra("organization")

        if(serializableExtra is Organization){
            binding = DataBindingUtil.setContentView(this, R.layout.activity_select_departments_list)

            binding.viewModel = SelectDepartmentViewModel(this, serializableExtra, null, intent.getStringExtra("depth")!!, intent.getStringExtra("departmentTitle")!!)
            binding.selectDepartmentRecycler.adapter = SelectDepartmentListRecyclerAdapter(this)

            et_search_field.addTextChangedListener (object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    (binding.selectDepartmentRecycler.adapter as SelectDepartmentListRecyclerAdapter).filter.filter(et_search_field.text)
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        } else if (serializableExtra is Department && "1" != intent.getStringExtra("depth")) {
            val organizationExtra = intent.getSerializableExtra("organizationInfo")
            binding = DataBindingUtil.setContentView(this, R.layout.activity_select_departments_list)

            binding.viewModel = SelectDepartmentViewModel(this,
                organizationExtra as Organization, serializableExtra, intent.getStringExtra("depth")!!, intent.getStringExtra("departmentTitle")!!)
            binding.selectDepartmentRecycler.adapter = SelectDepartmentListRecyclerAdapter(this)

            et_search_field.addTextChangedListener (object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    (binding.selectDepartmentRecycler.adapter as SelectDepartmentListRecyclerAdapter).filter.filter(et_search_field.text)
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