package kr.co.bigwalk.app.crowd_funding.myfunding

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.crowd_funding.FundingRewardResultItem
import kr.co.bigwalk.app.crowd_funding.TotalMyFundingViewModel
import kr.co.bigwalk.app.data.crowd_funding.dto.FundingState
import kr.co.bigwalk.app.databinding.FragmentDialogMyFundingRewardBinding
import kr.co.bigwalk.app.extension.dialogFragmentResize
import kr.co.bigwalk.app.util.EventObserver

class MyFundingRewardDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentDialogMyFundingRewardBinding
    private val totalMyFundingViewModel by lazy {
        ViewModelProvider(requireActivity()).get(TotalMyFundingViewModel::class.java)
    }
    private val contestDetailViewModel by lazy {
        ViewModelProvider(this, MyFundingRewardViewModelFactory(fundingRewardResultItem)).get(MyFundingRewardViewModel::class.java)
    }
    private val fundingRewardResultItem: FundingRewardResultItem by lazy { arguments?.getSerializable(KEY_RESULT_ITEM) as FundingRewardResultItem }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_my_funding_reward, container, false)
        binding.lifecycleOwner = this
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            vm = contestDetailViewModel
            context = this@MyFundingRewardDialogFragment
            outside.setOnClickListener {
                dismiss()
            }
            dialogContainer.setOnTouchListener { _: View, _: MotionEvent -> true }
            btn.setOnClickListener {
                when (fundingRewardResultItem.fundingState) {
                    FundingState.FUNDING_FAIL, FundingState.FUNDING_FAILURE_TO_ACHIEVE, FundingState.DELETE_FUNDING -> {
                        contestDetailViewModel.refundForFundingSteps()
                    }
                    FundingState.FUNDING_SUCCESS_AND_DONATE -> {
                        contestDetailViewModel.fundForFundingSteps()
                    }
                    else -> {
                        dismiss()
                    }
                }
            }
        }
    }

    private fun bindViewModel() {
        with(contestDetailViewModel) {
            fundSuccessEvent.observe(viewLifecycleOwner, EventObserver {
                showFundDialog()
            })
            refundSuccessEvent.observe(viewLifecycleOwner, EventObserver {
                showRefundDialog(it)
            })
            failureEvent.observe(viewLifecycleOwner, EventObserver {
                showErrorDialog(it)
            })
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().dialogFragmentResize(this@MyFundingRewardDialogFragment, 0.88f, 1f)
    }

    private fun showRefundDialog(msg: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.dialog_refund_msg, msg))
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                totalMyFundingViewModel.invalidateDataSource()
                dismiss()
            }
            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }
    }

    private fun showFundDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage("걸음 적용이 완료되었습니다")
            .setPositiveButton(R.string.confirm) { _, _ ->
                activity?.finish()
            }
            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }
    }

    private fun showErrorDialog(msg: String) {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(msg)
            .setPositiveButton(R.string.confirm) { _, _ ->
                dismiss()
            }
            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }
    }

    companion object {
        private const val KEY_RESULT_ITEM = "RESULT_ITEM"
        fun newInstance(item: FundingRewardResultItem) =
            MyFundingRewardDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_RESULT_ITEM, item)
                }
            }
    }

}