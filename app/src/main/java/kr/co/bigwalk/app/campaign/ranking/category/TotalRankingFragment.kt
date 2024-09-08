package kr.co.bigwalk.app.campaign.ranking.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.adapter.RankingPlusRecyclerAdapter
import kr.co.bigwalk.app.campaign.adapter.RankingReportAdapter
import kr.co.bigwalk.app.campaign.ranking.RankingPlusActivity
import kr.co.bigwalk.app.campaign.ranking.report.MyInfluenceReportFragment
import kr.co.bigwalk.app.campaign.ranking.report.MyRankingFragment
import kr.co.bigwalk.app.campaign.ranking.report.TotalInfluenceReportFragment
import kr.co.bigwalk.app.data.campaign.dto.RankData
import kr.co.bigwalk.app.databinding.FrameTotalRankingBinding

class TotalRankingFragment : Fragment() {
    companion object {
        fun newInstance() = TotalRankingFragment()
    }

    private lateinit var binding: FrameTotalRankingBinding
    private lateinit var adapter: RankingPlusRecyclerAdapter
    private lateinit var viewModel: TotalRankingViewModel
    private lateinit var myRankingFragment: MyRankingFragment
    private lateinit var totalInfluenceReportFragment: TotalInfluenceReportFragment
    private lateinit var myInfluenceReportFragment: MyInfluenceReportFragment

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            binding.reportPagerIndicator.selectDot(position)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        viewModel = TotalRankingViewModel(activity as RankingPlusActivity)
        childFragmentManager.fragmentFactory = FragmentFactoryImpl(viewModel)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        FirebaseAnalytics.getInstance(requireActivity()).logEvent("ranking_all_view", Bundle())
        binding = DataBindingUtil.inflate(inflater, R.layout.frame_total_ranking, container, false)
        myRankingFragment = childFragmentManager.fragmentFactory.instantiate(
            activity!!.classLoader,
            MyRankingFragment::class.java.name
        ) as MyRankingFragment
        totalInfluenceReportFragment = childFragmentManager.fragmentFactory.instantiate(
            activity!!.classLoader,
            TotalInfluenceReportFragment::class.java.name
        ) as TotalInfluenceReportFragment
        myInfluenceReportFragment = childFragmentManager.fragmentFactory.instantiate(
            activity!!.classLoader,
            MyInfluenceReportFragment::class.java.name
        ) as MyInfluenceReportFragment
        binding.viewModel = viewModel

        updateView()
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.invalidateDataSource()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            binding.reportPager.unregisterOnPageChangeCallback(onPageChangeCallback)
            viewModel.manageTotalRankNumber(viewModel.currentSeason)
        } catch (e: Exception) {

        }
    }

    private fun updateView() {
        adapter = RankingPlusRecyclerAdapter(viewModel.rankingReportDescription)
        binding.rankingRecycler.adapter = adapter
        viewModel.ranking.observe(this, Observer<PagedList<RankData>> { pagedList ->
            adapter.submitList(pagedList)
        })

        val reportAdapter = RankingReportAdapter(this)
        binding.reportPager.adapter = reportAdapter
        binding.reportPager.isSaveEnabled = false

        var prevAreOnSeason: Boolean? = null
        viewModel.areOnSeason.observe(this, Observer { areOnSeason ->
            if (prevAreOnSeason == null || prevAreOnSeason != areOnSeason) {
                reportAdapter.clearFragment()
                if (areOnSeason) {
                    reportAdapter.addFragment(myRankingFragment)
                    binding.reportPagerIndicator.visibility = View.GONE
                    binding.reportPager.unregisterOnPageChangeCallback(onPageChangeCallback)
                    binding.influenceReportTitle.visibility = View.GONE
                } else {
                    reportAdapter.addFragment(totalInfluenceReportFragment)
                    reportAdapter.addFragment(myInfluenceReportFragment)
                    binding.reportPagerIndicator.visibility = View.VISIBLE
                    binding.reportPagerIndicator.createDotPanel(
                        2,
                        R.drawable.bg_indicator_feed_selector
                    )
                    binding.reportPager.registerOnPageChangeCallback(onPageChangeCallback)
                    binding.influenceReportTitle.visibility = View.VISIBLE
                }
            }
            prevAreOnSeason = areOnSeason
        })

        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val isCollapsed = Math.abs(verticalOffset) - appBarLayout.totalScrollRange == 0
            if (viewModel.areOnSeason.value == true) {
                if (isCollapsed) {
                    binding.myRankingShare.visibility = View.GONE
                    binding.myRankingQuickDonate.visibility = View.VISIBLE
                } else {
                    if (!binding.myRankingShare.isShown) {
                        binding.myRankingShare.visibility = View.VISIBLE
                        binding.myRankingQuickDonate.visibility = View.GONE
                    }
                }
            } else {
                binding.reportPagerIndicator.visibility =
                    if (isCollapsed) View.GONE else View.VISIBLE
            }
        })
    }
}