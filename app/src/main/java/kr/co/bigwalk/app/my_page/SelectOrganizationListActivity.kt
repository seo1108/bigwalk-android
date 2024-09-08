package kr.co.bigwalk.app.my_page

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivitySelectOrganizationListBinding
import kr.co.bigwalk.app.my_page.adapter.SelectOrganizationListRecyclerAdapter

class SelectOrganizationListActivity : AppCompatActivity(), BaseNavigator {

    private lateinit var binding: ActivitySelectOrganizationListBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_organization_list)
        val viewModel = SelectOrganizationViewModel(this)
        binding.selectRecycler.adapter = SelectOrganizationListRecyclerAdapter(viewModel,this)
        binding.viewModel = viewModel
    }

    override fun getContext(): Activity {
        return this
    }

}