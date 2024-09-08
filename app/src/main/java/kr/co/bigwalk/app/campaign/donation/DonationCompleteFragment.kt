package kr.co.bigwalk.app.campaign.donation

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.BottomSheetDonationCompleteBinding

class DonationCompleteFragment(private val viewModel: DonationViewModel) : BottomSheetDialogFragment() {

    private lateinit var behavior : BottomSheetBehavior<FrameLayout>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: BottomSheetDonationCompleteBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_donation_complete, container,false)
        binding.viewModel = viewModel
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
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) if (isAdded) dismissAllowingStateLoss()
                }

                override fun onSlide(view: View, v: Float) {}
            })
            viewModel.donationCompleteBottomSheetBehavior = behavior
        }
        return dialog
    }

}