package kr.co.bigwalk.app.campaign.ranking.info

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.adapter.GradeRecyclerAdapter
import kr.co.bigwalk.app.campaign.ranking.RankingPlusActivity
import kr.co.bigwalk.app.databinding.ActivityRankingGradeInfoBinding

class RankingGradeInfoActivity: AppCompatActivity(), BaseNavigator {
    companion object {
        const val GUIDE_CATEGORY = "category"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityRankingGradeInfoBinding = DataBindingUtil.setContentView(this, R.layout.activity_ranking_grade_info)
        val category = intent.getStringExtra(GUIDE_CATEGORY) ?: RankingPlusActivity.Category.TOTAL.categoryName
        val viewModel = RankingGradeInfoViewModel(this, category)
        binding.viewModel = viewModel

        val wordThatFitTheCategory = if (category == RankingPlusActivity.Category.TODAY.categoryName) {
            resources.getString(R.string.word_day)
        } else {
            resources.getString(R.string.a_season)
        }
        val format = resources.getString(R.string.grade_info_description)
        binding.infoDescription.text = String.format(format, wordThatFitTheCategory)

        val adapter = GradeRecyclerAdapter(category)
        binding.infoRecycler.adapter = adapter
        viewModel.gradeList.observe(this, Observer {
            adapter.items.clear()
            adapter.items.addAll(it)
            adapter.notifyItemRangeInserted(0, it.size)
        })
    }

    override fun getContext(): Activity {
        return this
    }
}