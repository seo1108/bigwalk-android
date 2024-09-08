package kr.co.bigwalk.app.campaign.ranking.category

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import kr.co.bigwalk.app.campaign.ranking.RankingPlusViewModel
import kr.co.bigwalk.app.campaign.ranking.report.MyInfluenceReportFragment
import kr.co.bigwalk.app.campaign.ranking.report.MyRankingFragment
import kr.co.bigwalk.app.campaign.ranking.report.TotalInfluenceReportFragment

class FragmentFactoryImpl(private val viewModel: RankingPlusViewModel): FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            MyInfluenceReportFragment::class.java.name -> MyInfluenceReportFragment(viewModel)
            TotalInfluenceReportFragment::class.java.name -> TotalInfluenceReportFragment(viewModel)
            MyRankingFragment::class.java.name -> MyRankingFragment(viewModel)
            else -> super.instantiate(classLoader, className)
        }
        return super.instantiate(classLoader, className)
    }
}