package kr.co.bigwalk.app.crowd_funding.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.FragmentCrewCampaignChallengeInfoBinding
import kr.co.bigwalk.app.util.EventObserver
import kr.co.bigwalk.app.util.showToast

class CrewCampaignChallengeInfoFragment : Fragment() {
    private lateinit var binding: FragmentCrewCampaignChallengeInfoBinding
    private val crewCampaignDetailViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CrewCampaignDetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_crew_campaign_challenge_info, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            vm = crewCampaignDetailViewModel
        }
    }

    private fun bindViewModel() {
        with(crewCampaignDetailViewModel) {
            toastMessage.observe(viewLifecycleOwner, EventObserver { message ->
                showToast(message)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    companion object {
        private const val KEY_CREW_CAMPAIGN_ID = "CREW_CAMPAIGN_ID"
        fun newInstance(crewCampaignId: Long) =
            CrewCampaignChallengeInfoFragment().apply {
                arguments = Bundle().apply {
                    putLong(KEY_CREW_CAMPAIGN_ID, crewCampaignId)
                }
            }
    }
}