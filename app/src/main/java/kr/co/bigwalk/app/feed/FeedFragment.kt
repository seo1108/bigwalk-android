package kr.co.bigwalk.app.feed

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.tabs.TabLayout
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.FrameFeedBinding
import kr.co.bigwalk.app.feed.adapter.FeedCategoryViewPagerAdapter
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class FeedFragment : Fragment() {

    companion object {
        val KEY_CAMPAIGN_ID = "KEY_CAMPAIGN_ID"
        val KEY_ORGANIZATION_ID = "KEY_ORGANIZATION_ID"
        val KEY_SORT_TYPE = "KEY_SORT_TYPE"

        fun newInstance(campaignId: Long, organizationId: Long, sortType: String): FeedFragment {
            val feedFragment = FeedFragment()
            val bundle = Bundle()
            bundle.putLong(KEY_CAMPAIGN_ID, campaignId)
            bundle.putLong(KEY_ORGANIZATION_ID, organizationId)
            bundle.putString(KEY_SORT_TYPE, sortType)
            feedFragment.arguments = bundle
            return feedFragment
        }
    }

    lateinit var binding: FrameFeedBinding
    private lateinit var viewModel: FeedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate<FrameFeedBinding>(inflater, R.layout.frame_feed, container, false)
        FeedDetailActivity.firebaseAnalytics.logEvent("feed_view", null)

        val campaignId = arguments?.getLong(KEY_CAMPAIGN_ID, -1)?: -1
        val organizationId = arguments?.getLong(KEY_ORGANIZATION_ID, -1)?: -1
        val sortType = arguments?.getString(KEY_SORT_TYPE)
        if (campaignId == -1L) {
            activity?.finish()
        }
        DebugLog.d("campaignId = $campaignId ,,, organizationId = $organizationId")

        viewModel = FeedViewModel(activity as FeedDetailActivity, campaignId, organizationId, sortType!!)
        binding.viewModel = viewModel

        initViewPager()

        return binding.root
    }

    private fun initViewPager() {
        binding.feedViewPager.adapter = FeedCategoryViewPagerAdapter(childFragmentManager)
        binding.feedViewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(binding.feedTab))
        binding.feedViewPager.setSwipeEnabled(false)
        //binding.feedViewPager.offscreenPageLimit = 1
        binding.feedTab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(p0: TabLayout.Tab?) {}

            override fun onTabUnselected(p0: TabLayout.Tab?) {}

            override fun onTabSelected(p0: TabLayout.Tab?) {
                if (p0 != null) {
                    binding.feedViewPager.currentItem = p0.position
                    //(binding.feedViewPager.adapter as FeedCategoryViewPagerAdapter).getItem(p0.position).refresh
                }
            }
        })


    }


    private fun getFabAnimatorSet(fam: FloatingActionMenu): AnimatorSet {
        val set = AnimatorSet()

        val scaleOutX =
            ObjectAnimator.ofFloat(fam.menuIconView, "scaleX", 1.0f, 0.2f)
        val scaleOutY =
            ObjectAnimator.ofFloat(fam.menuIconView, "scaleY", 1.0f, 0.2f)

        val scaleInX =
            ObjectAnimator.ofFloat(fam.menuIconView, "scaleX", 0.2f, 1.0f)
        val scaleInY =
            ObjectAnimator.ofFloat(fam.menuIconView, "scaleY", 0.2f, 1.0f)

        scaleOutX.duration = 50
        scaleOutY.duration = 50

        scaleInX.duration = 150
        scaleInY.duration = 150

        scaleInX.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                fam.menuIconView.setImageResource(if (fam.isOpened) R.drawable.ui_icon_134_floating_bt else R.drawable.ui_icon_134_floating_bt)
            }
        })

        set.play(scaleOutX).with(scaleOutY)
        set.play(scaleInX).with(scaleInY).after(scaleOutX)
        set.interpolator = OvershootInterpolator(2F)

        return set
    }

}