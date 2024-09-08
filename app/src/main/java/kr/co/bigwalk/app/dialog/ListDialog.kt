package kr.co.bigwalk.app.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.BottomSheetListBinding
import kr.co.bigwalk.app.report.ReportViewModel

class ListDialog(private val viewModel: ReportViewModel) : BottomSheetDialogFragment() {

    companion object {
        fun newInstance(viewModel: ReportViewModel): ListDialog {
            return ListDialog(viewModel)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: BottomSheetListBinding = DataBindingUtil.inflate(inflater, R.layout.bottom_sheet_list, container,false)
        binding.viewModel = viewModel
        binding.bottomSheetRecycler.adapter = BottomSheetRecyclerAdapter(viewModel)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener { bottomSheetDialog: DialogInterface ->
            bottomSheetDialog as BottomSheetDialog
            val frameLayout = bottomSheetDialog.findViewById<FrameLayout>(R.id.design_bottom_sheet)
            val behavior: BottomSheetBehavior<FrameLayout> = BottomSheetBehavior.from(frameLayout!!)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    if (newState == BottomSheetBehavior.STATE_HIDDEN) dismiss()
                }

                override fun onSlide(view: View, v: Float) {}
            })
            viewModel.bottomSheetBehavior = behavior
        }
        return dialog
    }
}