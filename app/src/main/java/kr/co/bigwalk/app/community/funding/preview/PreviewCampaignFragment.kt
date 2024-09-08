package kr.co.bigwalk.app.community.funding.preview

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.community.funding.SupportersCampaignPreviewViewModel
import kr.co.bigwalk.app.databinding.FragmentPreviewCampaignBinding
import kr.co.bigwalk.app.util.EventObserver

class PreviewCampaignFragment : Fragment() {
    private lateinit var binding: FragmentPreviewCampaignBinding
    private val previewCampaignViewModel by lazy {
        ViewModelProvider(requireActivity()).get(SupportersCampaignPreviewViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_preview_campaign, container, false)
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
            vm = previewCampaignViewModel
            deleteCampaign.setOnClickListener {
                FirebaseAnalytics.getInstance(requireContext()).logEvent("contest_exhibit_preview_btn_delete_click", Bundle())
                showDeleteCrewCampaignDialog()
            }
        }
    }

    private fun bindViewModel() {
        with(previewCampaignViewModel) {
            deleteEvent.observe(viewLifecycleOwner, EventObserver {
                activity?.finish()
            })
        }
    }

    private fun showDeleteCrewCampaignDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.dialog_delete_crew_campaign_msg))
            .setNegativeButton(R.string.delete) { dialog, _ ->
                previewCampaignViewModel.deleteCrewCampaign()
            }
            .setPositiveButton(R.string.cancel) { dialog, _ ->
            }

            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.RED)
        }
    }

    companion object {
        private const val KEY_CREW_CAMPAIGN_ID = "SEASON"
        fun newInstance(crewCampaignId: Long) =
            PreviewCampaignFragment().apply {
                arguments = Bundle().apply {
                    putLong(KEY_CREW_CAMPAIGN_ID, crewCampaignId)
                }
            }
    }
}