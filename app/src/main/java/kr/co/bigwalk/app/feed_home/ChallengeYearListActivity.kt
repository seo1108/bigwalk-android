package kr.co.bigwalk.app.feed_home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityChallengeYearListBinding
import kr.co.bigwalk.app.feed_home.adapter.ChallengeYearPagingAdapter

class ChallengeYearListActivity : AppCompatActivity(), BaseNavigator {

    private lateinit var binding: ActivityChallengeYearListBinding
    private var viewModel: ChallengeHomeViewModel? = null

    private var page = 0
    private var size = 30
    private var type : String = ""
    private var year : String = ""

    companion object {
        var isBlur : Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        type = intent.getStringExtra("type")!!
        year = intent.getStringExtra("year")!!

        binding = DataBindingUtil.setContentView(this, R.layout.activity_challenge_year_list)

        viewModel = ChallengeHomeViewModel(this)
        viewModel!!.requestChallengeByYear(type, year, page, size)

        binding.viewModel = viewModel

        if (type == "participated") {
            binding.title = year +"년 " + resources.getString(R.string.participated_challenge)
            isBlur = false

        } else {
            binding.title = year +"년 " + resources.getString(R.string.ended_challenge)
            isBlur = true
        }

        binding.scrollContainer.viewTreeObserver.addOnScrollChangedListener {
            if (binding.scrollContainer.getChildAt(0).bottom
                <= binding.scrollContainer.height + binding.scrollContainer.scrollY
            ) {
                // Scroll Bottom
                if (viewModel!!.isLoadFinish.get() == false) {
                    //viewModel!!.observablePage.set(page)
                    page++
                    viewModel!!.requestChallengeByYear(type, year, page, size)
                }
            }
        }

        binding.challengeRecycler.adapter = ChallengeYearPagingAdapter(isBlur, viewModel!!,this)
    }

    override fun getContext(): Activity {
        return this
    }
}