package kr.co.bigwalk.app.campaign.ranking

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.adapter.SeasonRankingAdapter
import kr.co.bigwalk.app.databinding.ActivitySeasonRankingBinding
import kotlin.math.max

class SeasonRankingActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeasonRankingBinding
//    private val seasonRankingViewModel by lazy {
//        ViewModelProvider(this).get(SeasonRankingViewModel::class.java)
//    }
    private val seasonRankingViewModel by viewModels<SeasonRankingViewModel>()
    private val adapter = SeasonRankingAdapter {
        val dialogFragment = RankerBySeasonDialogFragment.newInstance(it.seasonKey)
        dialogFragment.show(supportFragmentManager, dialogFragment.tag)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAnalytics.getInstance(this).logEvent("ranking_view", Bundle())
        binding = DataBindingUtil.setContentView(this, R.layout.activity_season_ranking)

        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            seasonList.adapter = adapter
            homeAsUp.setOnClickListener {
                finish()
            }

            var scrollDy = seasonList.paddingTop
            val maxDist = seasonList.paddingTop
            seasonList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    scrollDy -= dy
                    image.alpha = max(0.5f, (scrollDy.toFloat() / maxDist))
                }
            })
        }
    }

    private fun bindViewModel() {
        with(seasonRankingViewModel) {
            topRankerInfo.observe(this@SeasonRankingActivity, Observer {
                adapter.submitList(it)
            })
        }
    }
}