package kr.co.bigwalk.app.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.community.CrewRequestResponse
import kr.co.bigwalk.app.databinding.DialogCrewRequestDetailBinding

interface CrewRequestDetailDialogCallback {
    fun onApproval(requestId: Long)
    fun onReject(requestId: Long)
}

class CrewRequestDetailDialog(private val activity: Activity, val data: CrewRequestResponse, val callback: CrewRequestDetailDialogCallback): Dialog(activity, R.style.fullscreen_dialog) {
    lateinit var binding: DialogCrewRequestDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.dialog_crew_request_detail,null,false)
        setContentView(binding.root)

        FirebaseAnalytics.getInstance(activity).logEvent("join_request_detail_view", Bundle())

        binding.data = data
        binding.outside.setOnClickListener { dismiss() }
        binding.approvalButton.setOnClickListener {
            FirebaseAnalytics.getInstance(activity).logEvent("join_request_button_permission_click", Bundle())
            dismiss()
            callback.onApproval(data.id)
        }
        binding.rejectButton.setOnClickListener {
            FirebaseAnalytics.getInstance(activity).logEvent("join_request_button_rejection_click", Bundle())
            dismiss()
            callback.onReject(data.id)
        }

        binding.dialogContainer.setOnTouchListener { _: View, _: MotionEvent -> true }
    }
}