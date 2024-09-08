package kr.co.bigwalk.app.feed_home

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.databinding.FrameFeedHomeBinding
import kr.co.bigwalk.app.extension.getDeviceWidth
import kr.co.bigwalk.app.feed.FeedDetailActivity
import kr.co.bigwalk.app.feed_home.adapter.FeedHomeRecyclerAdapter
import kr.co.bigwalk.app.util.DebugLog

class FeedHomeFragment: Fragment() {

    companion object {
        fun newInstance(): FeedHomeFragment {
            return FeedHomeFragment()
        }
    }

    lateinit var binding: FrameFeedHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frame_feed_home, container, false)
        // TODO FeedHomeFragment에 logEvent name 통일
        CampaignActivity.firebaseAnalytics?.logEvent("feed_home_view", null)
        val viewModel = FeedHomeViewModel()
        val feedHomeAdapter = FeedHomeRecyclerAdapter(viewModel)
        viewModel.missionCampaignsResponse.observe(viewLifecycleOwner, Observer { pagedList ->
            DebugLog.d("aaaa pagedList=$pagedList")
            feedHomeAdapter.submitList(pagedList)
        })

        binding.rvFeedHome.apply {
            setHasFixedSize(true)
            post {
                val margin = (getDeviceWidth() / 2) - (binding.layoutItem.measuredWidth / 2)
                addItemDecoration(FeedHomeItemDecoration(margin))
            }

//            addItemDecoration(FeedHomeItemDecoration(25.dpToPx()))
            adapter = feedHomeAdapter
        }
        LinearSnapHelper().attachToRecyclerView(binding.rvFeedHome)
        binding.feedHomeLatestFeed.setOnClickListener {
            val campaignId = PreferenceManager.getLastMissionCampaignId()
            val organizationId = PreferenceManager.getLastMissionOrganizationId()
            val intent = Intent(activity, FeedDetailActivity::class.java)
            intent.putExtra(FeedDetailActivity.KEY_CAMPAIGN_ID, campaignId)
            intent.putExtra(FeedDetailActivity.KEY_ORGANIZATION_ID, organizationId)
            startActivity(intent)
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        binding.tvFeedHomeLatestTitle.text = PreferenceManager.getLastMissisonTitle()
        binding.tvFeedHomeLatestDate.text = PreferenceManager.getLastMissionEndDate()

        binding.rvFeedHome.apply {
            view?.postDelayed({
                if (adapter?.itemCount ?: 0 <= 0) {
                    binding.notReadyMission.visibility = View.VISIBLE
                    binding.ivEmptyCharacter.visibility = View.VISIBLE
                    binding.rvFeedHome.visibility = View.GONE
                    binding.feedHomeLatestFeed.visibility = View.INVISIBLE
                }else {
                    binding.notReadyMission.visibility = View.INVISIBLE
                    binding.ivEmptyCharacter.visibility = View.GONE
                    binding.rvFeedHome.visibility = View.VISIBLE
                    binding.feedHomeLatestFeed.visibility = View.VISIBLE
                }
            },1000)
        }
    }

}

class FeedHomeItemDecoration(private val spaceSide: Int ): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view)

        if (state.itemCount == 1) {
            outRect.left = spaceSide
            outRect.right = spaceSide
            return
        }else if (position == 0) {
            outRect.left = spaceSide
            return
        }else if (position == state.itemCount - 1) {
            outRect.right = spaceSide
            return
        }

    }
}