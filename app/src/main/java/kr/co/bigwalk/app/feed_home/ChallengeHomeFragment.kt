package kr.co.bigwalk.app.feed_home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.databinding.FrameChallengeHomeBinding
import kr.co.bigwalk.app.feed_home.adapter.ChallengeHomeActiveAdapter
import kr.co.bigwalk.app.feed_home.adapter.ChallengeHomeClosedAdapter
import kr.co.bigwalk.app.feed_home.adapter.ChallengeHomeParticipatedAdapter

class ChallengeHomeFragment : Fragment() {

    companion object {
        fun newInstance(): ChallengeHomeFragment {
            return ChallengeHomeFragment()
        }
    }

    lateinit var binding: FrameChallengeHomeBinding
    //private lateinit var viewModel: ChallengeHomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.frame_challenge_home, container, false)
        // TODO ChallengeHomeFragment logEvent name 통일
        CampaignActivity.firebaseAnalytics?.logEvent("feed_home_view", null)
        var viewModel = ChallengeHomeViewModel(activity as CampaignActivity)
        viewModel.requestChallengeHome()
        binding.viewModel = viewModel

        binding.onGoingRecycler.adapter = ChallengeHomeActiveAdapter(viewModel!!, activity as CampaignActivity)
        binding.participatedRecycler.adapter = ChallengeHomeParticipatedAdapter(viewModel!!, activity as CampaignActivity)
        binding.endedRecycler.adapter = ChallengeHomeClosedAdapter(viewModel!!, activity as CampaignActivity)



        return binding.root
    }

    override fun onResume() {
        super.onResume()
    }
}