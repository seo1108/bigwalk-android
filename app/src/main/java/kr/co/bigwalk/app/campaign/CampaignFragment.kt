package kr.co.bigwalk.app.campaign

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignActivity.Companion.firebaseAnalytics
import kr.co.bigwalk.app.campaign.adapter.CampaignCategoryViewPagerAdapter
import kr.co.bigwalk.app.campaign.adapter.CampaignPopularAdapter
import kr.co.bigwalk.app.campaign.adapter.ParticipatedCampaignAdapter
import kr.co.bigwalk.app.campaign.adapter.StoryForReadyAdapter
import kr.co.bigwalk.app.data.story.dto.Story
import kr.co.bigwalk.app.databinding.FrameCampaignBinding
import kr.co.bigwalk.app.extension.dpToPx
import kr.co.bigwalk.app.util.RecyclerDecoration

class CampaignFragment : Fragment() {

    private lateinit var binding: FrameCampaignBinding
    lateinit var viewModel: CampaignViewModel

    companion object {
        fun newInstance(): CampaignFragment {
            return CampaignFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frame_campaign, container, false)
        if (::viewModel.isInitialized) {
            binding.viewModel = viewModel
            firebaseAnalytics?.logEvent("campaign_view", null)

            binding.campaignMyCampaignRecycler.addItemDecoration(RecyclerDecoration(24, true))
            val participatedAdapter = ParticipatedCampaignAdapter(viewModel)
            binding.campaignMyCampaignRecycler.adapter = participatedAdapter

            binding.campaignPopularRecycler.addItemDecoration(PopularDecoration(18.dpToPx()))
            val popularAdapter = CampaignPopularAdapter(viewModel)
            binding.campaignPopularRecycler.adapter = popularAdapter
            LinearSnapHelper().attachToRecyclerView(binding.campaignPopularRecycler)

            binding.campaignStoryRecycler.addItemDecoration(RecyclerDecoration(24, true))
            val storyAdapter = StoryForReadyAdapter(viewModel)
            binding.campaignStoryRecycler.adapter = storyAdapter
            viewModel.storyLiveData.observe(
                this,
                Observer<PagedList<Story>> { pagedList ->
                    storyAdapter.submitList(pagedList)
                    pagedList.addWeakCallback(null, object: PagedList.Callback() {
                        override fun onChanged(position: Int, count: Int) {
                        }
                        override fun onInserted(position: Int, count: Int) {
                            if (count > 0) viewModel.storiesVisibility.set(true)
                            else viewModel.storiesVisibility.set(false)
                        }
                        override fun onRemoved(position: Int, count: Int) {}
                    })
                })
            
            CampaignDonateDataManager.updateCampaignDonateData.observe(viewLifecycleOwner, Observer {
                participatedAdapter.replaceItem(it.first, it.second, it.third)
                popularAdapter.replaceItem(it.first, it.second, it.third)
            })

            binding.campaignViewPager.adapter = CampaignCategoryViewPagerAdapter(childFragmentManager)
            binding.campaignViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.campaignCategoryTab))
            binding.campaignCategoryTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(p0: TabLayout.Tab?) {}

                override fun onTabUnselected(p0: TabLayout.Tab?) {}

                override fun onTabSelected(p0: TabLayout.Tab?) {
                    if (p0 != null) {
                        binding.campaignViewPager.currentItem = p0.position
                    }
                }

            })
        }

        return binding.root
    }

}

class PopularDecoration(private val spaceSide: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildLayoutPosition(view)
        if (state.itemCount == 1) {
            outRect.left = spaceSide
            outRect.right = spaceSide
            return
        }else if (position == 0) {
            outRect.left = spaceSide
            outRect.right = 5.dpToPx()
            return
        }else if (position == state.itemCount - 1) {
            outRect.left = 5.dpToPx()
            outRect.right = spaceSide
            return
        }else {
            outRect.left = 5.dpToPx()
            outRect.right = 5.dpToPx()
        }
    }

}