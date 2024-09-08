package kr.co.bigwalk.app.feed.modify

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.net.toUri
import androidx.viewpager2.widget.ViewPager2
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseActivity
import kr.co.bigwalk.app.campaign.donation.additional_service.MissionCertificationAdapter
import kr.co.bigwalk.app.campaign.donation.additional_service.MissionDonationData
import kr.co.bigwalk.app.community.MyCommunityListActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.databinding.ActivityModifyFeedBinding
import kr.co.bigwalk.app.extension.inputViewTouchEvent
import kr.co.bigwalk.app.feed.like.FeedLikeActivity
import kr.co.bigwalk.app.util.EventObserver
import kr.co.bigwalk.app.util.OnSingleClickListener
import kr.co.bigwalk.app.util.showToast

class ModifyFeedActivity : BaseActivity<ActivityModifyFeedBinding>(R.layout.activity_modify_feed) {

    private val modifyFeedViewModel by viewModels<ModifyFeedViewModel>()
    private val pagerAdapter: MissionCertificationAdapter by lazy { MissionCertificationAdapter() }
    private val imagePathList: List<String>? by lazy { intent.getStringArrayListExtra(KEY_IMAGE_LIST)?.toList() }
    private val feedId: Long by lazy { intent.getLongExtra(KEY_FEED_ID, DEF_LONG_VALUE) }
    private val feedComment: String by lazy { intent.getStringExtra(KEY_COMMENT).orEmpty() }

    private val viewpagerCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            binding.indicatorMissionCertification.selectDot(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbar()
        setView()
        bindViewModel()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.aos_icon_20_exit)
            title = "정보 수정"
        }
    }

    private fun setView() {
        with(binding) {
            viewpagerMissionCertification.adapter = pagerAdapter
            viewpagerMissionCertification.registerOnPageChangeCallback(viewpagerCallback)
            setDataViewPager(imagePathList?.map { it.toUri() }.orEmpty(), null)
            contentView.setText(feedComment)
            contentView.setOnTouchListener { v, event -> v.inputViewTouchEvent(event) }
            btnModify.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(view: View) {
                    contentView.text?.forEach {
                        if (Character.getType(it) == Character.SURROGATE.toInt()) {
                            showToast("현재 이모티콘은 지원하지 않습니다.")
                            return
                        }
                    }
                    modifyFeedViewModel.modifyFeed(feedId, contentView.text.toString())
                }
            })

        }
    }

    private fun bindViewModel() {
        with(modifyFeedViewModel) {
            successEvent.observe(this@ModifyFeedActivity, EventObserver {
                val intent = this@ModifyFeedActivity.intent
                intent.putExtra("comment", PreferenceManager.getFeedComment())
                setResult(Activity.RESULT_OK, intent)
                finish()
            })
        }
    }

    private fun setDataViewPager(uri: List<Uri>, missionData: MissionDonationData?) {
        pagerAdapter.setItems(uri, missionData)
        binding.indicatorMissionCertification.createDotPanel(uri.size)
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
        private const val KEY_IMAGE_LIST = "IMAGE_LIST"
        private const val KEY_COMMENT = "COMMENT"
        private const val KEY_FEED_ID = "FEED_ID"
        fun getIntent(context: Context, list: ArrayList<String>, comment: String, feedId: Long) =
            Intent(context, ModifyFeedActivity::class.java).apply {
                putExtra(KEY_IMAGE_LIST, list)
                putExtra(KEY_COMMENT, comment)
                putExtra(KEY_FEED_ID, feedId)
            }
    }
}