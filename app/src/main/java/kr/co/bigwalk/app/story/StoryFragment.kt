package kr.co.bigwalk.app.story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.databinding.FrameStoryBinding
import kr.co.bigwalk.app.story.adapter.StoryRecyclerAdapter

class StoryFragment : Fragment() {

    private lateinit var viewModel: StoryViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<FrameStoryBinding>(inflater, R.layout.frame_story, container, false)
        viewModel = StoryViewModel(activity as CampaignActivity)
        CampaignActivity.firebaseAnalytics?.logEvent("story_view", null)
        binding.viewModel = viewModel
        val adapter = StoryRecyclerAdapter(lifecycle, viewModel)
        binding.campaignRecycler.adapter = adapter
        viewModel.stories.observe(this,
            Observer { pagedList -> adapter.submitList(pagedList) })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.getMyReservedStoriesCount()
        viewModel.stories.value?.dataSource?.invalidate()
    }

}