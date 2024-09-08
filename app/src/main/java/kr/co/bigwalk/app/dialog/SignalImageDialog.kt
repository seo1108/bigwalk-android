package kr.co.bigwalk.app.dialog

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.DialogSignalImageBinding

interface SignalCallback {
    fun onConfirm()
}

class SignalImageDialog(private val activity: Activity, val data: Pair<String, String>, val callback: SignalCallback): Dialog(activity, R.style.fullscreen_dialog) {
    lateinit var binding: DialogSignalImageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(activity),
            R.layout.dialog_signal_image,null,false)
        setContentView(binding.root)

        binding.outside.setOnClickListener { dismiss() }
        binding.signalConfirm.setOnClickListener {
            dismiss()
            callback.onConfirm()
        }
        binding.signalContent.text = data.first
        Glide.with(context)
            .asBitmap()
            .load(data.second)
            .apply(RequestOptions.centerCropTransform())
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.img_default_48)
            .error(R.drawable.img_default_48)
            .into(binding.signalImage)
    }
}