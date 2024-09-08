package kr.co.bigwalk.app.crowd_funding.detail

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.blame.BlameActivity
import kr.co.bigwalk.app.blame.BlameType
import kr.co.bigwalk.app.crowd_funding.adapter.CommentByCrewCampaignAdapter
import kr.co.bigwalk.app.crowd_funding.detail.comment.StepFundingCommentsViewModelFactory
import kr.co.bigwalk.app.databinding.ActivityStepFundingCommentsBinding

class StepFundingCommentsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStepFundingCommentsBinding
    private val stepFundingCommentsViewModel by lazy {
        ViewModelProvider(this, StepFundingCommentsViewModelFactory(crewCampaignId)).get(StepFundingCommentsViewModel::class.java)
    }
    private val adapter by lazy {
        CommentByCrewCampaignAdapter {
            if (it.mine) {
                showDeleteCommentDialog(it.id)
            } else {
                startActivity(BlameActivity.getIntent(this, it.id, it.userId, BlameType.FUNDING_COMMENT))
            }
        }
    }
    private val crewCampaignId: Long by lazy { intent.getLongExtra(KEY_CREW_CAMPAIGN_ID, DEF_LONG_VALUE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_step_funding_comments)
        binding.lifecycleOwner = this
        FirebaseAnalytics.getInstance(this).logEvent("funding_comment_view", Bundle())

        setView()
        bindViewModel()
    }

    override fun onResume() {
        super.onResume()
        stepFundingCommentsViewModel.list.value?.dataSource?.invalidate()
    }

    private fun bindViewModel() {
        with(stepFundingCommentsViewModel) {
            stepFundingCommentsViewModel.list.observe(this@StepFundingCommentsActivity, Observer {
                adapter.submitList(it)
            })
        }
    }

    private fun setView() {
        with(binding) {
            vm = stepFundingCommentsViewModel
            rvComment.adapter = adapter
            exitButton.setOnClickListener {
                finish()
            }
            sendCommentBtn.setOnClickListener {
                FirebaseAnalytics.getInstance(this@StepFundingCommentsActivity).logEvent("funding_comment_button_upload_click", Bundle())
                stepFundingCommentsViewModel.addCommentCrewCampaign(commentInputText.text.toString())
                commentInputText.text.clear()
                val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(
                    binding.commentInputText.windowToken,
                    0
                )
            }
        }
    }

    private fun showDeleteCommentDialog(commentId: Long) {
        val dialog = AlertDialog.Builder(this)
            .setMessage(getString(R.string.dialog_delete_comment_msg))
            .setPositiveButton(R.string.delete) { _, _ ->
                stepFundingCommentsViewModel.deleteCommentCrewCampaign(commentId)
            }
            .setNegativeButton(R.string.cancel) { _, _ -> }
            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED)
        }
    }

    companion object {
        private const val KEY_CREW_CAMPAIGN_ID = "CREW_CAMPAIGN_ID"
        fun getIntent(context: Context, crewCampaignId: Long) =
            Intent(context, StepFundingCommentsActivity::class.java).apply {
                putExtra(KEY_CREW_CAMPAIGN_ID, crewCampaignId)
            }
    }
}