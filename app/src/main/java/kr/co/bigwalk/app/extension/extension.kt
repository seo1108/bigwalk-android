package kr.co.bigwalk.app.extension

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.google.android.material.tabs.TabLayout
import kr.co.bigwalk.app.BigwalkApplication
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

fun <T: Any> guardLet(vararg elements: T?, closure: () -> Nothing): List<T> {
    return if (elements.all { it != null }) {
        elements.filterNotNull()
    } else {
        closure()
    }
}

fun <T: Any> ifLet(vararg elements: T?, closure: (List<T>) -> Unit) {
    if (elements.all { it != null }) {
        closure(elements.filterNotNull())
    }
}

fun Int.dpToPx(): Int = (this * BigwalkApplication.context!!.resources.displayMetrics.density).toInt()

fun Float.dpToPxFloat(): Int = (this * BigwalkApplication.context!!.resources.displayMetrics.density).toInt()

fun getDeviceWidth() = BigwalkApplication.context!!.resources.displayMetrics.widthPixels
fun getDeviceHeight() = BigwalkApplication.context!!.resources.displayMetrics.heightPixels

fun TabLayout.setMargin(left: Int, top: Int, right: Int, bottom: Int) {
    for (i in 0 until this.tabCount) {
        val tab = (this.getChildAt(0) as ViewGroup).getChildAt(i)
        val p = tab.layoutParams as ViewGroup.MarginLayoutParams
        p.setMargins(left, top, right, bottom)
        tab.requestLayout()
    }
}

fun Calendar.toDisplayString(): String {
    return toDisplayString(false)
}

fun Calendar.toDisplayString(excludeWeek: Boolean): String {
    val year = this[Calendar.YEAR]
    val month = this[Calendar.MONTH] + 1
    val day = this[Calendar.DATE]
    val week = this[Calendar.DAY_OF_WEEK]
    return "$year.${month.fillzero()}.${day.fillzero()}" + if (excludeWeek) "" else "(${week.toWeek()})"
}

fun Calendar.toTagString(includeTime: Boolean = false): String {
    val year = this[Calendar.YEAR]
    val month = this[Calendar.MONTH] + 1
    val day = this[Calendar.DATE]
    val hour = this[Calendar.HOUR_OF_DAY]
    val minute = this[Calendar.MINUTE]
    return "$year${month.fillzero()}${day.fillzero()}" + if (includeTime) "${hour.fillzero()}${minute.fillzero()}" else ""
}

fun getDisplayEndDate(date: String): String {
    return getDisplayEndDate(date, false)
}

fun getDisplayEndDate(date: String, excludeWeek: Boolean): String {
    try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
        val endDate = format.parse(date)
        endDate?.let {
            val endCal = Calendar.getInstance()
            endCal.time = it
            return  "~${endCal.toDisplayString(excludeWeek)}"
        }
    }catch (e: ParseException) {
        e.printStackTrace()
    }
    return ""
}

fun getDisplayPeriod(startDateText: String, endDateText: String?): String {
    try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
        val startDate = format.parse(startDateText)
        val endDate = endDateText?.let { format.parse(endDateText) }
        var periodText: String = ""
        val cal = Calendar.getInstance()
        startDate?.let {
            cal.time = it
            periodText += cal.toDisplayString(true)
        }
        endDate?.let {
            cal.time = it
            periodText += "~${cal.toDisplayString(true)}"
        }
        return periodText
    }catch (e: ParseException) {
        e.printStackTrace()
    }
    return ""
}

fun getServerDateStringToDate(dateText: String): Date? {
    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
    return format.parse(dateText)
}

fun Calendar.formatToSimple(): String {
    val year = this[Calendar.YEAR]
    val month = this[Calendar.MONTH] + 1
    val day = this[Calendar.DATE]
    val week = this[Calendar.DAY_OF_WEEK]
    val hour = this[Calendar.HOUR_OF_DAY]
    val minute = this[Calendar.MINUTE]
    return "$year.${month.fillzero()}.${day.fillzero()}(${week.toWeek()}) ${hour.fillzero()}:${minute.fillzero()}"
}

fun Int.toWeek(): String {
    return when(this) {
        1 -> "일"
        2 -> "월"
        3 -> "화"
        4 -> "수"
        5 -> "목"
        6 -> "금"
        7 -> "토"
        else -> ""
    }
}

fun Int.fillzero(): String {
    return String.format("%02d", this)
}


@Throws(IOException::class)
fun Uri.uriToBitmap(context: Context): Bitmap =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        ImageDecoder.decodeBitmap(
            ImageDecoder.createSource(context.contentResolver, this)
        ) { decoder: ImageDecoder, _: ImageDecoder.ImageInfo?, _: ImageDecoder.Source? ->
            decoder.isMutableRequired = true
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    } else {
        BitmapDrawable(
            context.resources,
            MediaStore.Images.Media.getBitmap(context.contentResolver, this)
        ).bitmap
    }

fun getLauncherClassName(context: Context): String? {
    val intent = Intent(Intent.ACTION_MAIN)
    intent.addCategory(Intent.CATEGORY_LAUNCHER)
    intent.setPackage(context.applicationContext.packageName)
    val resolveInfoList =
        context.applicationContext.packageManager.queryIntentActivities(intent, 0)
    return if (resolveInfoList != null && resolveInfoList.size > 0) {
        resolveInfoList[0].activityInfo.name
    } else ""
}

fun Context.dialogFragmentResize(dialogFragment: DialogFragment, width: Float, height: Float) {
    val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

    if (Build.VERSION.SDK_INT < 30) {

        val display = windowManager.defaultDisplay
        val size = Point()

        display.getSize(size)

        val window = dialogFragment.dialog?.window

        val x = (size.x * width).toInt()
        val y = (size.y * height).toInt()
        window?.setLayout(x, y)

    } else {

        val rect = windowManager.currentWindowMetrics.bounds

        val window = dialogFragment.dialog?.window

        val x = (rect.width() * width).toInt()
        val y = (rect.height() * height).toInt()

        window?.setLayout(x, y)
    }
}