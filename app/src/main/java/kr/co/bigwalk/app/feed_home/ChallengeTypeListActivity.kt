package kr.co.bigwalk.app.feed_home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityChallengeTypeListBinding
import kr.co.bigwalk.app.feed_home.adapter.ChallengeFirstYearAdapter
import kr.co.bigwalk.app.feed_home.adapter.ChallengeSecondYearAdapter
import kr.co.bigwalk.app.feed_home.adapter.ChallengeThirdYearAdapter

class ChallengeTypeListActivity : AppCompatActivity(), BaseNavigator {

    private lateinit var binding: ActivityChallengeTypeListBinding
    private var viewModel: ChallengeHomeViewModel? = null

    companion object {
        var isBlur : Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_challenge_type_list)
        viewModel = ChallengeHomeViewModel(this)
        viewModel!!.requestChallengeTypeList(intent.getStringExtra("type")!!)

        binding.viewModel = viewModel

        if (intent.getStringExtra("type")!! == "participated") {
            binding.title = resources.getString(R.string.participated_challenge)
            isBlur = false
        } else {
            binding.title = resources.getString(R.string.ended_challenge)
            isBlur = true
        }

        binding.firstYearTitle.setOnClickListener {
            if (intent.getStringExtra("type")!! == "participated") {
                val intent = Intent(this, ChallengeYearListActivity::class.java)
                intent.putExtra("type", "participated")
                intent.putExtra("year", viewModel!!.firstYear.get())
                startActivity(intent)
            } else {
                val intent = Intent(this, ChallengeYearListActivity::class.java)
                intent.putExtra("type", "closed")
                intent.putExtra("year", viewModel!!.firstYear.get())
                startActivity(intent)
            }
        }

        binding.secondYearTitle.setOnClickListener {
            if (intent.getStringExtra("type")!! == "participated") {
                val intent = Intent(this, ChallengeYearListActivity::class.java)
                intent.putExtra("type", "participated")
                intent.putExtra("year", viewModel!!.secondYear.get())
                startActivity(intent)
            } else {
                val intent = Intent(this, ChallengeYearListActivity::class.java)
                intent.putExtra("type", "closed")
                intent.putExtra("year", viewModel!!.secondYear.get())
                startActivity(intent)
            }
        }

        binding.thirdYearTitle.setOnClickListener {
            if (intent.getStringExtra("type")!! == "participated") {
                val intent = Intent(this, ChallengeYearListActivity::class.java)
                intent.putExtra("type", "participated")
                intent.putExtra("year", viewModel!!.thirdYear.get())
                startActivity(intent)
            } else {
                val intent = Intent(this, ChallengeYearListActivity::class.java)
                intent.putExtra("type", "closed")
                intent.putExtra("year", viewModel!!.thirdYear.get())
                startActivity(intent)
            }
        }

        binding.firstYearRecycler.adapter = ChallengeFirstYearAdapter(isBlur, viewModel!!, this)
        binding.secondYearRecycler.adapter = ChallengeSecondYearAdapter(isBlur, viewModel!!, this)
        binding.thirdYearRecycler.adapter = ChallengeThirdYearAdapter(isBlur, viewModel!!, this)
    }

    override fun getContext(): Activity {
        return this
    }
}