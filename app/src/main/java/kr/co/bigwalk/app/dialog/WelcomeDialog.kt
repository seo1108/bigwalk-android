package kr.co.bigwalk.app.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.DialogWelcomeBinding

class WelcomeDialog(private val activity: Activity): Dialog(activity, R.style.fullscreen_dialog) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: DialogWelcomeBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity), R.layout.dialog_welcome,null,false)
        binding.viewModel = WelcomeViewModel(activity, this)
        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
    }
}