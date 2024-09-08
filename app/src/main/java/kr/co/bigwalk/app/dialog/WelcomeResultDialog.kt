package kr.co.bigwalk.app.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.DialogWelcomeResultBinding
import kr.co.bigwalk.app.extension.valueToCommaString

class WelcomeResultDialog(
    private val activity: Activity,
    private val reward: String
    ): Dialog(activity, R.style.fullscreen_dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: DialogWelcomeResultBinding = DataBindingUtil.inflate(
            LayoutInflater.from(activity), R.layout.dialog_welcome_result,null,false)
        setContentView(binding.root)
        setCancelable(false)
        setCanceledOnTouchOutside(false)

        Glide.with(activity).load(R.drawable.aos_icon_event_result_step_check)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource is GifDrawable) {
                        resource.setLoopCount(1)
                        resource.registerAnimationCallback(object :
                            Animatable2Compat.AnimationCallback() {
                            override fun onAnimationEnd(drawable: Drawable?) {
                                super.onAnimationEnd(drawable)
                                Handler().postDelayed({
                                    dismiss()
                                    activity.finish()
                                }, 1000)
                            }
                        })
                    }
                    return false
                }
            })
            .into(binding.resultStepCheckIcon)
        binding.resultMsg.text = String.format(activity.resources.getString(R.string.welcome_event_result_msg), reward.toInt().valueToCommaString())
    }
}