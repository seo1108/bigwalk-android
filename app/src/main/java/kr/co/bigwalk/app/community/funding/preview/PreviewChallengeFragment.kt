package kr.co.bigwalk.app.community.funding.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.FragmentPreviewChallengeBinding
import kr.co.bigwalk.app.util.EventObserver
import kr.co.bigwalk.app.util.showToast

class PreviewChallengeFragment : Fragment() {
    private lateinit var binding: FragmentPreviewChallengeBinding
    private val previewChallengeViewModel by lazy {
        ViewModelProvider(this, PreviewChallengeViewModelFactory(arguments?.getLong(KEY_CREW_CAMPAIGN_ID) ?: DEF_LONG_VALUE)).get(
            PreviewChallengeViewModel::class.java
        )
    }
    private val groupId: Long by lazy { arguments?.getLong(KEY_GROUP_ID) ?: DEF_LONG_VALUE }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_preview_challenge, container, false)
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
            vm = previewChallengeViewModel
            deleteCampaign.setOnClickListener {
                FirebaseAnalytics.getInstance(requireContext()).logEvent("contest_exhibit_preview_btn_delete_click", Bundle())
                previewChallengeViewModel.deleteCrewCampaign()
            }
        }
    }

    private fun bindViewModel() {
        with(previewChallengeViewModel) {
            toastMessage.observe(viewLifecycleOwner, EventObserver { message ->
                showToast(message)
            })
            deleteEvent.observe(viewLifecycleOwner, EventObserver {
                activity?.finish()
            })
        }
    }

    override fun onStart() {
        super.onStart()
        previewChallengeViewModel.requestChallengeOfCrewCampaign()
        previewChallengeViewModel.getMyRoleFromGroup(groupId)
    }

    companion object {
        private const val KEY_CREW_CAMPAIGN_ID = "CREW_CAMPAIGN_ID"
        private const val KEY_GROUP_ID = "GROUP_ID"
        fun newInstance(crewCampaignId: Long, groupId: Long) =
            PreviewChallengeFragment().apply {
                arguments = Bundle().apply {
                    putLong(KEY_CREW_CAMPAIGN_ID, crewCampaignId)
                    putLong(KEY_GROUP_ID, groupId)
                }
            }
    }
}