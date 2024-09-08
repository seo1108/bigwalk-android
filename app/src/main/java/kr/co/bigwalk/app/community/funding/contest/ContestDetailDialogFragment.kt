package kr.co.bigwalk.app.community.funding.contest

import android.app.Activity
import android.content.Intent
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.community.funding.create.CreateFundingActivity
import kr.co.bigwalk.app.data.community.GroupMemberRole
import kr.co.bigwalk.app.data.funding.RequiredToCreateIds
import kr.co.bigwalk.app.databinding.FragmentDialogContestDetailBinding
import kr.co.bigwalk.app.extension.dialogFragmentResize

class ContestDetailDialogFragment : DialogFragment() {
    private lateinit var binding: FragmentDialogContestDetailBinding
    private val contestDetailViewModel by lazy {
        ViewModelProvider(
            this,
            ContestDetailViewModelFactory(requiredToCreateIds.contestId)
        ).get(ContestDetailViewModel::class.java)
    }
    private val requiredToCreateIds: RequiredToCreateIds by lazy { arguments?.getSerializable(KEY_REQUIRED_TO_CREATE_ID) as RequiredToCreateIds }
    private var myRole: GroupMemberRole = GroupMemberRole.GUEST

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_contest_detail, container, false)
        binding.lifecycleOwner = this

        val bundle = Bundle()
        bundle.putString("contest_id", requiredToCreateIds.contestId.toString())
        FirebaseAnalytics.getInstance(requireContext()).logEvent("contest_detail_view", bundle)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        contestDetailViewModel.getMyRoleFromGroup(requiredToCreateIds.groupId)
        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            vm = contestDetailViewModel
            outside.setOnClickListener {
                dismiss()
            }
            dialogContainer.setOnTouchListener { _: View, _: MotionEvent -> true }
            applyBtn.setOnClickListener {
                val bundle = Bundle()
                bundle.putLong("contest_id", requiredToCreateIds.contestId)
                FirebaseAnalytics.getInstance(requireContext()).logEvent("contest_exhibit_button_apply_click", bundle)
                if (myRole == GroupMemberRole.OWNER) {
                    startActivityForResult(CreateFundingActivity.getIntent(requireContext(), requiredToCreateIds), KEY_REQUEST_MODIFY_MODE)
                } else {
                    showApplyToContestDialog()
                }
            }
        }
    }

    private fun bindViewModel() {
        with(contestDetailViewModel) {
            myRole.observe(viewLifecycleOwner, Observer {
                this@ContestDetailDialogFragment.myRole = it
            })
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().dialogFragmentResize(this@ContestDetailDialogFragment, 0.88f, 1f)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == KEY_REQUEST_MODIFY_MODE && resultCode == Activity.RESULT_OK) {
            activity?.finish()
        }

    }

    private fun showApplyToContestDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.dialog_apply_contest_msg))
            .setPositiveButton(R.string.confirm) { dialog, _ -> }
            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }
    }

    companion object {
        const val KEY_REQUEST_MODIFY_MODE = 1
        private const val KEY_REQUIRED_TO_CREATE_ID = "REQUIRED_TO_CREATE_ID"
        fun newInstance(requiredToCreateIds: RequiredToCreateIds) =
            ContestDetailDialogFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(KEY_REQUIRED_TO_CREATE_ID, requiredToCreateIds)
                }
            }
    }

}