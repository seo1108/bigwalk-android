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
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.campaign.dto.ValueConsumptionCommerceResponse
import kr.co.bigwalk.app.databinding.BottomSheetValueConsumptionCommerceBinding

class DonationValueConsumptionCommerceFragment(private val donationData: DonationData, private val commerce: ValueConsumptionCommerceResponse) : BottomSheetDialogFragment() {

    private lateinit var behavior : BottomSheetBehavior<FrameLayout>
    private lateinit var viewModel: DonationValueConsumptionCommerceViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.let {
            viewModel = DonationValueConsumptionCommerceViewModel(it, donationData, commerce)

            val bundle = Bundle()
            bundle.putString("campaign_id", donationData.campaignId.toString())
            FirebaseAnalytics.getInstance(it.applicationContext).logEvent("value_consumption_commerce_view", bundle)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: BottomSheetValueConsumptionCommerceBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_value_consumption_commerce, container,false)
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
            viewModel.bottomSheetBehavior = behavior
        }
        return dialog
    }

}