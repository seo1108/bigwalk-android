package kr.co.bigwalk.app.crowd_funding.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.FragmentCrewCampaignInfoBinding

class CrewCampaignInfoFragment : Fragment() {
    private lateinit var binding: FragmentCrewCampaignInfoBinding
    private val crewCampaignDetailViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CrewCampaignDetailViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_crew_campaign_info, container, false)
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

        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    companion object {
        private const val KEY_CREW_CAMPAIGN_ID = "SEASON"
        fun newInstance(crewCampaignId: Long) =
            CrewCampaignInfoFragment().apply {
                arguments = Bundle().apply {
                    putLong(KEY_CREW_CAMPAIGN_ID, crewCampaignId)
                }
            }
    }
}