package kr.co.bigwalk.app.feed_mypage

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseActivity
import kr.co.bigwalk.app.blame.adapter.UserBlockListAdapter
import kr.co.bigwalk.app.databinding.ActivityUserBlockListBinding
import kr.co.bigwalk.app.util.showAlertDialog

class UserBlockListActivity : BaseActivity<ActivityUserBlockListBinding>(R.layout.activity_user_block_list) {
    private val userBlockListViewModel by viewModels<UserBlockListViewModel>()
    private val blockListAdapter by lazy { UserBlockListAdapter { item ->
        showAlertDialog(this, R.string.dialog_msg_unblock_user) {
            userBlockListViewModel.unblockUser(item.userId)
            FirebaseAnalytics.getInstance(this).logEvent("feed_blocklist_btn_unlock_click", Bundle())
        }
    } }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAnalytics.getInstance(this).logEvent("feed_blocklist_view", Bundle())
        setToolbar()
        setView()
        bindViewModel()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.aos_icon_20_arrow)
            title = "차단 목록"
        }
    }

    private fun setView() {
        with(binding) {
            blockListContainer.adapter = blockListAdapter
        }
    }

    private fun bindViewModel() {
        with(userBlockListViewModel) {
            emptyList.observe(this@UserBlockListActivity, Observer {
                binding.emptyView.isVisible = true
            })
            userBlockList.observe(this@UserBlockListActivity, Observer {
                blockListAdapter.submitList(it)
                binding.emptyView.isVisible = false
            })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun getIntent(context: Context) =
            Intent(context, UserBlockListActivity::class.java)
    }
}