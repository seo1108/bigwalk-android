package kr.co.bigwalk.app.feed_comment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.PagedList
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.blame.BlameActivity
import kr.co.bigwalk.app.blame.BlameType
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.feedComment.dto.FeedCommentResponse
import kr.co.bigwalk.app.databinding.ActivityFeedCommentBinding
import kr.co.bigwalk.app.feed.FeedManager
import kr.co.bigwalk.app.feed.FeedOptionDialogFragment
import kr.co.bigwalk.app.feed.modify.ModifyFeedActivity
import kr.co.bigwalk.app.feed_comment.adapter.FeedCommentAdapter
import kr.co.bigwalk.app.feed_comment.adapter.FeedCommentPostAdapter
import kr.co.bigwalk.app.sign_in.organization.OrganizationViewModel
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.KeyboardVisibilityUtils
import kr.co.bigwalk.app.util.showAlertDialog
import kr.co.bigwalk.app.util.showToast

class FeedCommentActivity : AppCompatActivity(), BaseNavigator {

    companion object {
        const val ACTION_DONATION_HISTORY_ID = "ACTION_DONATION_HISTORY_ID"
        const val IS_ENABLE_LIKE = "IS_ENABLE_LIKE"
        const val ORGANIZATION_ID = "ORGANIZATION_ID"
        const val IS_FROM_NOTIFY = "IS_FROM_NOTIFY"


        var commentList: PagedList<FeedCommentResponse>? = null
    }

    private lateinit var binding: ActivityFeedCommentBinding
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private var viewModel: FeedCommentViewModel? = null
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private var postAdapter : FeedCommentPostAdapter? = null

    private var mCampaignId = -1L

    private var isFromNotify = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFeedCommentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        binding.lifecycleOwner = this
        binding.ivFeedCommentBack.setOnClickListener {
            finish()
        }
        val isEnableLike = intent.getBooleanExtra(IS_ENABLE_LIKE, false)
        val organizationId = intent.getLongExtra(ORGANIZATION_ID, -1)
        val actiondonationHistoryId = intent.getLongExtra(ACTION_DONATION_HISTORY_ID, -1)
        isFromNotify = intent.getBooleanExtra(IS_FROM_NOTIFY, false)

        val factory = FeedCommentViewModelFactory(this, actiondonationHistoryId)
        viewModel = ViewModelProvider(this, factory).get(FeedCommentViewModel::class.java)
        binding.viewModel = viewModel
        postAdapter = FeedCommentPostAdapter(viewModel!!, isEnableLike, organizationId, false)
        binding.rvFeedCommentPost.adapter = postAdapter

        FeedManager.updateFeed.observe(this, Observer {
            if (it.second.actionDonationHistoryId == actiondonationHistoryId) {
                postAdapter!!.updateFeed(it.first, it.second, it.third)
            }
        })

        FeedManager.removeFeed.observe(this, Observer {
            if (actiondonationHistoryId == it) {
                finish()
            }
        })

        FeedManager.dimFeed.observe(this, Observer {
            postAdapter!!.dimFeed(it)
        })

        viewModel?.clickMoreButton?.observe(this, Observer { feed ->
            val dialog = FeedOptionDialogFragment.newInstance(feed.mine, object : FeedOptionDialogFragment.OptionClickListener {
                override fun onBlameClick() {
                    startActivity(BlameActivity.getIntent(this@FeedCommentActivity, feed.actionDonationHistoryId, feed.user.id, BlameType.FEED))
                }

                override fun onModifyClick() {
                    val list = arrayListOf<String>()
                    list.add(feed.certifiedImagePath)
                    feed.certifiedImagePath2?.let { list.add(it) }
                    feed.certifiedImagePath3?.let { list.add(it) }
                    startActivityForResult(ModifyFeedActivity.getIntent(this@FeedCommentActivity, list, feed.comment.orEmpty(), feed.actionDonationHistoryId), 1)

                }

                override fun onDeleteClick() {
                    showAlertDialog(this@FeedCommentActivity, R.string.dialog_msg_feed_delete) {
                        viewModel?.requestFeedDelete(feed.actionDonationHistoryId)
                        FirebaseAnalytics.getInstance(this@FeedCommentActivity).logEvent("feed_btn_contents_delete_click", Bundle())
                    }
                }

            })
            dialog.show(supportFragmentManager, dialog.tag)
        })

        viewModel!!.hideKeyboard.observe(this, Observer {
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                binding.etFeedCommentInputEdit.windowToken,
                0
            )
        })

        viewModel!!.feedInfo.observe(this, Observer { feed ->
            feed?.let {
                postAdapter!!.setData(listOf(it))
                mCampaignId = it.campaignId
                initCommentAdapter(organizationId, it.campaignId)
            }
        })

        FeedManager.commentFeed.observe(this, Observer {
            if (it.actionDonationHistoryId == actiondonationHistoryId) {
//                postAdapter.setData(listOf(it))  // localdatasource시
                viewModel?.comments?.value?.dataSource?.invalidate()
            }
        })

        viewModel!!.feedError.observe(this, Observer {
            if (it) {
                showToast("삭제된 게시물입니다.")
                finish()
            }
        })

        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            onShowKeyboard = { keyboardHeight ->
                binding.svFeedComment.run {
                    smoothScrollTo(scrollX, binding.rvFeedComment.bottom)
                }
            })

        viewModel?.fetchFeedInfo()

    }

    override fun onResume() {
        super.onResume()
        // TODO : 댓글 -> 유저 차단 시 새로고침 하게 하기위해 (임시)
        viewModel?.comments?.value?.dataSource?.invalidate()

        viewModel?.fetchFeedInfo()
    }

    fun initCommentAdapter(organizationId: Long, campaignId: Long) {
        val commentAdapter = FeedCommentAdapter(viewModel!!, organizationId, campaignId)
        binding.rvFeedComment.adapter = commentAdapter
        binding.rvFeedComment.isNestedScrollingEnabled = false
        viewModel!!.comments.observe(this, Observer {
            commentAdapter.submitList(it)
        })
    }


    override fun onDestroy() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroy()
    }

    override fun getContext(): Activity {
        return this
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            viewModel?.fetchFeedInfo()
        }
    }

}