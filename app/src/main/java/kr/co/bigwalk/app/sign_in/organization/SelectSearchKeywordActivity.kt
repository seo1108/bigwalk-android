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
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.databinding.ActivitySearchKeywordListBinding
import kr.co.bigwalk.app.sign_in.organization.adapter.SelectSearchKeywordAdapter

class SelectSearchKeywordActivity : AppCompatActivity(), BaseNavigator {

    private lateinit var binding: ActivitySearchKeywordListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serializableExtra = intent.getSerializableExtra("organization")

        if (serializableExtra is Organization) {
            binding = DataBindingUtil.setContentView(this, R.layout.activity_search_keyword_list)

            binding.viewModel = SelectSearchKeywordViewModel(this, serializableExtra)
            binding.selectSearchKeywordRecycler.adapter = SelectSearchKeywordAdapter(this)

            et_search_field.addTextChangedListener (object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    (binding.selectSearchKeywordRecycler.adapter as SelectSearchKeywordAdapter).filter.filter(et_search_field.text)
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }

    override fun getContext(): Activity {
        return this
    }
}