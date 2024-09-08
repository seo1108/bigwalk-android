package kr.co.bigwalk.app.community.funding

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.DEF_INT_VALUE
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.community.adapter.ContestListAdapter
import kr.co.bigwalk.app.community.funding.contest.ContestDetailDialogFragment
import kr.co.bigwalk.app.data.funding.RequiredToCreateIds
import kr.co.bigwalk.app.data.funding.dto.ContestResponse
import kr.co.bigwalk.app.databinding.ActivityContestBinding

class ContestActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContestBinding
    private val contestViewModel: ContestViewModel by lazy {
        ViewModelProvider(this).get(ContestViewModel::class.java)
    }
    private val evenContestListAdapter: ContestListAdapter by lazy {
        ContestListAdapter {
            showContestDetailDialog(it)
            FirebaseAnalytics.getInstance(this).logEvent("contest_exhibit_button_card_click", Bundle())
        }
    }
    private val oddContestListAdapter: ContestListAdapter by lazy {
        ContestListAdapter {
            showContestDetailDialog(it)
            FirebaseAnalytics.getInstance(this).logEvent("contest_exhibit_button_card_click", Bundle())
        }
    }
    private val groupId: Long by lazy { intent.getLongExtra(KEY_GROUP_ID, DEF_LONG_VALUE) }
    private val sequence: Int by lazy { intent.getIntExtra(KEY_FRAME_SEQUENCE, DEF_INT_VALUE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contest)
        binding.lifecycleOwner = this
        FirebaseAnalytics.getInstance(this).logEvent("contest_exhibit_view", Bundle())

        this.window?.apply {
            this.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            exitButton.setOnClickListener {
                finish()
            }
            evenContestList.adapter = evenContestListAdapter
            oddContestList.adapter = oddContestListAdapter
        }
    }

    private fun showContestDetailDialog(contestResponse: ContestResponse) {
        if (contestResponse.id < 0) {
            showReadyToContestDialog()
            return
        }
        val dialogFragment = ContestDetailDialogFragment.newInstance(RequiredToCreateIds(groupId, contestResponse.id, sequence))
        dialogFragment.show(supportFragmentManager, dialogFragment.tag)
    }

    private fun bindViewModel() {
        with(contestViewModel) {
            evenContestList.observe(this@ContestActivity, Observer {
                evenContestListAdapter.submitList(it)
            })
            oddContestList.observe(this@ContestActivity, Observer {
                oddContestListAdapter.submitList(it)
            })
        }
    }

    private fun showReadyToContestDialog() {
        val dialog = AlertDialog.Builder(this)
            .setMessage(getString(R.string.dialog_ready_to_contest_msg))
            .setPositiveButton(R.string.confirm) { dialog, _ -> }
            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }
    }

    companion object {
        private const val KEY_GROUP_ID = "GROUP_ID"
        private const val KEY_FRAME_SEQUENCE = "FRAME_SEQUENCE"
        fun getIntent(context: Context, groupId: Long, sequence: Int) =
            Intent(context, ContestActivity::class.java).apply {
                putExtra(KEY_GROUP_ID, groupId)
                putExtra(KEY_FRAME_SEQUENCE, sequence)
            }
    }
}