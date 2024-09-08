package kr.co.bigwalk.app.profile.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseActivity
import kr.co.bigwalk.app.community.adapter.SelectInterestCategoryAdapter
import kr.co.bigwalk.app.community.create.SelectInterestViewModel
import kr.co.bigwalk.app.data.community.dto.create.CrewConcernTagResponse
import kr.co.bigwalk.app.databinding.ActivitySelectInterestBinding
import kr.co.bigwalk.app.util.EventObserver
import java.io.Serializable

class SelectInterestActivity : BaseActivity<ActivitySelectInterestBinding>(R.layout.activity_select_interest) {

    private val selectInterestViewModel by viewModels<SelectInterestViewModel>()
    private val selectInterestCategoryAdapter by lazy { SelectInterestCategoryAdapter(selectInterestViewModel) }
    private val selectTagList: List<CrewConcernTagResponse> by lazy { intent.getSerializableExtra(KEY_SELECT_TAG_LIST) as List<CrewConcernTagResponse>  }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbar()
        setView()
        bindViewModel()
    }

    private fun setToolbar() {
        with(binding) {
            exitButton.setOnClickListener {
                onBackPressed()
            }
            saveButton.setOnClickListener {
                selectInterestViewModel.validationCheck()
            }
        }
    }

    private fun setView() {
        with(binding) {
            categoryContainer.adapter = selectInterestCategoryAdapter
        }
    }

    private fun bindViewModel() {
        with(selectInterestViewModel) {
            fetchCrewConcern2(selectTagList)
            crewConcernList.observe(this@SelectInterestActivity, Observer {
                selectInterestCategoryAdapter.submitList(it)
            })
            saveSuccess.observe(this@SelectInterestActivity, EventObserver {
                val intent = intent.putExtra(KEY_SELECT_TAG_LIST, it  as Serializable)
                setResult(RESULT_OK, intent)
                finish()
            })
        }
    }

    companion object {
        const val KEY_SELECT_TAG_LIST = "SELECT_TAG_LIST"
        fun getIntent(context: Context, selectTagList: List<CrewConcernTagResponse>) =
            Intent(context, SelectInterestActivity::class.java).apply {
                putExtra(KEY_SELECT_TAG_LIST, selectTagList as Serializable)
            }
    }
}