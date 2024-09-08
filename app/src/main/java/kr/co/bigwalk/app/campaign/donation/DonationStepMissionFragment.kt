package kr.co.bigwalk.app.campaign.donation

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.databinding.BottomSheetStepMissionDonationBinding
import kr.co.bigwalk.app.util.DebugLog

class DonationStepMissionFragment(private val donationData: DonationData) : BottomSheetDialogFragment() {

    private lateinit var behavior : BottomSheetBehavior<FrameLayout>
    private lateinit var viewModel: DonationStepMissionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let { viewModel = DonationStepMissionViewModel(it, donationData, true) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: BottomSheetStepMissionDonationBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_step_mission_donation, container,false)
        binding.viewModel = viewModel
        PreferenceManager.sawMission(donationData.campaignId)

        viewModel.playAnimation.observe(viewLifecycleOwner, Observer {
            activity?.runOnUiThread {
                binding.rollingContainer.showNext()
            }
        })
        viewModel.notPlayAnimation.observe(viewLifecycleOwner, Observer {
            binding.rollingContainer.run {
                inAnimation = null
                outAnimation = null
                displayedChild = 1
            }
        })
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { bottomSheetDialog: DialogInterface ->
            bottomSheetDialog as BottomSheetDialog
            val frameLayout = bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            behavior = BottomSheetBehavior.from(frameLayout!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.setBottomSheetCallback(object : BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    try {
                        if (newState == BottomSheetBehavior.STATE_HIDDEN) dismiss()
                    } catch (e: Exception) {
                        FirebaseCrashlytics.getInstance().log("DonationStepMissionFragment bottom sheet error : ${e.stackTrace}")
                    }
                }

                override fun onSlide(view: View, v: Float) {}
            })
            viewModel.bottomSheetBehavior = behavior
        }
        return dialog
    }



}