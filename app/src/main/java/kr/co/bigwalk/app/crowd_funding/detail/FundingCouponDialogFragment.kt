package kr.co.bigwalk.app.crowd_funding.detail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.crowd_funding.detail.funding.StepFundingActivity
import kr.co.bigwalk.app.databinding.FragmentDialogFundingCouponBinding
import kr.co.bigwalk.app.extension.dialogFragmentResize

class FundingCouponDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentDialogFundingCouponBinding
    private val crewTitle: String by lazy { arguments?.getString(KEY_CREW_TITLE).orEmpty() }
    private val progressItem: SendProgressItem by lazy { arguments?.getSerializable(KEY_PROGRESS_ITEM) as SendProgressItem }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_funding_coupon, container, false)
        binding.lifecycleOwner = this
        FirebaseAnalytics.getInstance(requireContext()).logEvent("funding_campaign_membership_reward_view", Bundle())
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setView()
    }

    private fun setView() {
        with(binding) {
            outside.setOnClickListener {
                dismiss()
            }
            dialogContainer.setOnTouchListener { _: View, _: MotionEvent -> true }
            couponExplain.text = getString(R.string.funding_coupon_explain, crewTitle)
            btn.setOnClickListener {
                FirebaseAnalytics.getInstance(requireContext()).logEvent("funding_campaign_btn_member_reward_click", Bundle())
                startActivity(StepFundingActivity.getIntent(requireContext(), progressItem))
                dismiss()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().dialogFragmentResize(this@FundingCouponDialogFragment, 0.88f, 1f)
    }

    companion object {
        private const val KEY_CREW_TITLE = "CREW_TITLE"
        private const val KEY_PROGRESS_ITEM = "PROGRESS_ITEM"
        fun newInstance(crewTitle: String, item: SendProgressItem) =
            FundingCouponDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(KEY_CREW_TITLE, crewTitle)
                    putSerializable(KEY_PROGRESS_ITEM, item)
                }
            }
    }
}