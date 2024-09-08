package kr.co.bigwalk.app.extension

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieAnimationView
import java.util.*

fun ImageView.setImageByTime(@DrawableRes dayImgId: Int, @DrawableRes nightImgId: Int) {
    this.setImageResource(
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 6..17 -> {
                dayImgId
            }
            else -> {
                nightImgId
            }
        }
    )
}

fun LottieAnimationView.setAnimationByTime(@RawRes dayImgId: Int, @RawRes nightImgId: Int) {
    this.setAnimation(
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 6..17 -> {
                dayImgId
            }
            else -> {
                nightImgId
            }
        }
    )
}

fun LottieAnimationView.setAnimationByNight(@RawRes nightImgId: Int) {
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 6..17 -> {
            }
            else -> {
                this.setAnimation(nightImgId)
            }
        }
}

fun View.setBackgroundColorByTime(@ColorRes dayColorId: Int, nightColorId: Int) {
    this.backgroundTintList =
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 6..17 -> {
                ContextCompat.getColorStateList(this.context, dayColorId)
            }
            else -> {
                ContextCompat.getColorStateList(this.context, nightColorId)
            }
        }
}

fun TextView.setTextColorByTime(@ColorRes dayColorId: Int, nightColorId: Int) {
    this.setTextColor(
        when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 6..17 -> {
                ContextCompat.getColorStateList(this.context, dayColorId)
            }
            else -> {
                ContextCompat.getColorStateList(this.context, nightColorId)
            }
        })
}