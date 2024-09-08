package kr.co.bigwalk.app.community

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.FragmentGroupReportBinding

class GroupReportFragment : Fragment() {
    private lateinit var binding: FragmentGroupReportBinding
    private val groupReportViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CommunityInfoViewModel::class.java)
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_group_report, container, false)
        binding.lifecycleOwner = this
        FirebaseAnalytics.getInstance(requireActivity()).logEvent("group_report_view", null)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setView()
        bindViewModel()
    }
    
    private fun setView() {
        with(binding) {
            viewModel = groupReportViewModel
            reportPager.adapter = object : FragmentStateAdapter(
                childFragmentManager,
                lifecycle
            ) {
                override fun getItemCount(): Int = 2
                
                override fun createFragment(position: Int): Fragment =
                    when (position) {
                        0 -> ReportCardFragment1.newInstance()
                        1 -> ReportCardFragment2.newInstance()
                        else -> error("Invalid position")
                    }
            }
            reportPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    reportPagerIndicator.selectDot(position)
                }
            })
            reportPagerIndicator.createDotPanel(
                2,
                R.drawable.bg_indicator_feed_selector
            )
        }
    }
    
    private fun bindViewModel() {
        with(groupReportViewModel) {
        }
    }
    
    companion object {
        fun newInstance() = GroupReportFragment()
    }
}