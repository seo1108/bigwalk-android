package kr.co.bigwalk.app.feed_home

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityChallengeActiveListBinding
import kr.co.bigwalk.app.feed_home.adapter.ChallengeActiveListAdapter

class ChallengeActiveListActivity : AppCompatActivity(), BaseNavigator {

    private lateinit var binding: ActivityChallengeActiveListBinding
    private var viewModel: ChallengeHomeViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_challenge_active_list)
        viewModel = ChallengeHomeViewModel(this)
        viewModel!!.requestChallengeActiveList()
        binding.viewModel = viewModel

        binding.challengeRecycler.adapter = ChallengeActiveListAdapter(viewModel!!, this)
    }

    override fun getContext(): Activity {
        return this
    }
}