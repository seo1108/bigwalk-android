package kr.co.bigwalk.app.community.funding.request

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.android.synthetic.main.activity_community.*
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.community.funding.contest.CrewRequestListViewModelFactory
import kr.co.bigwalk.app.community.funding.request.adapter.CrewRequestListAdapter
import kr.co.bigwalk.app.databinding.ActivityCrewRequestListBinding
import kr.co.bigwalk.app.dialog.CrewRequestDetailDialog
import kr.co.bigwalk.app.dialog.CrewRequestDetailDialogCallback

class CrewRequestListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCrewRequestListBinding
    private val crewRequestListViewModel by lazy {
        ViewModelProvider(
            this,
            CrewRequestListViewModelFactory(groupId)
        ).get(CrewRequestListViewModel::class.java)
    }
    private val groupId: Long by lazy { intent.getLongExtra(KEY_GROUP_ID, DEF_LONG_VALUE) }
    private val adapter by lazy {
        CrewRequestListAdapter  {
            FirebaseAnalytics.getInstance(this).logEvent("join_request_list_btn_individual_click", Bundle())
            CrewRequestDetailDialog(this@CrewRequestListActivity, it, object :
                CrewRequestDetailDialogCallback {

                override fun onApproval(requestId: Long) {
                    crewRequestListViewModel.approve(groupId, requestId)
                }

                override fun onReject(requestId: Long) {
                    crewRequestListViewModel.reject(groupId, requestId)
                }

            }).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crew_request_list)
        binding.lifecycleOwner = this

        FirebaseAnalytics.getInstance(this).logEvent("join_request_list_view", Bundle())

        setView()
        bindViewModel()
        setToolbar()
    }

    private fun setView() {
        with(binding) {
            crewRequestList.adapter = adapter
        }
    }

    private fun bindViewModel() {
        with(crewRequestListViewModel) {
            crewRequestList.observe(this@CrewRequestListActivity, Observer { list ->
                adapter.submitList(list.content)
            })
            crewRequestJoinCount.observe(this@CrewRequestListActivity, Observer { count ->
                binding.toolbarTitle.text = "크루 가입 승인(${count})"
                val intent = Intent().putExtra(KEY_REQUEST_COUNT, count)
                setResult(RESULT_OK, intent)
            })
        }
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.aos_icon_20_exit)
            title = ""
            toolbar_title.text = "크루 가입 승인(${intent.getIntExtra(KEY_REQUEST_JOIN_COUNT, 0)})"

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            else -> {
                return false
            }
        }
        return true
    }

    companion object {
        const val KEY_REQUEST_COUNT = "REQUEST_COUNT"

        private const val KEY_GROUP_ID = "GROUP_ID"
        private const val KEY_REQUEST_JOIN_COUNT = "REQUEST_JOIN_COUNT"
        fun getIntent(context: Context, groupId: Long, requestJointCount: Int) =
            Intent(context, CrewRequestListActivity::class.java).apply {
                putExtra(KEY_GROUP_ID, groupId)
                putExtra(KEY_REQUEST_JOIN_COUNT, requestJointCount)
            }
    }
}