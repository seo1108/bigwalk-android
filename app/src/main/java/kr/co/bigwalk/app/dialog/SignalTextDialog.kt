package kr.co.bigwalk.app.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.community.CommunityInfoViewModel
import kr.co.bigwalk.app.databinding.DialogSignalImageBinding
import kr.co.bigwalk.app.databinding.DialogSignalTextBinding

class SignalTextDialog(private val activity: Activity, val content: String): Dialog(activity, R.style.fullscreen_dialog) {
    lateinit var binding: DialogSignalTextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.dialog_signal_text,null,false)

        binding.outside.setOnClickListener { dismiss() }
        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(true)

        binding.signalConfirm.setOnClickListener { dismiss() }
        binding.signalContent.text = content
    }
}