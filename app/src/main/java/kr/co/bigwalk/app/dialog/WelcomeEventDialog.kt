package kr.co.bigwalk.app.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.mission.dto.MissionsResponse
import kr.co.bigwalk.app.databinding.DialogWelcomeEventBinding

class WelcomeEventDialog(
    private val activity: Activity,
    private val welcomeMission: MissionsResponse
): Dialog(activity, R.style.fullscreen_dialog) {
    lateinit var binding: DialogWelcomeEventBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.dialog_welcome_event,null,false)
        binding.viewModel = WelcomeEventViewModel(this, activity, welcomeMission)
        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)

    }
}