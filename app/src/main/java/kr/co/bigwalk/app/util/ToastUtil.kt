package kr.co.bigwalk.app.util

import android.widget.Toast
import kr.co.bigwalk.app.BuildConfig
import kr.co.bigwalk.app.BigwalkApplication

fun showToast(message: String) {
    Toast.makeText(BigwalkApplication.context, message, Toast.LENGTH_SHORT).show()
}

fun showToast(strResId: Int) {
    Toast.makeText(BigwalkApplication.context, BigwalkApplication.context?.getString(strResId), Toast.LENGTH_SHORT).show()
}

fun showLongToast(message: String) {
    Toast.makeText(BigwalkApplication.context, message, Toast.LENGTH_LONG).show()
}

fun showDebugToast(message: String) {
    when (BuildConfig.FLAVOR) {
        "local", "local_server", "dev" -> Toast.makeText(BigwalkApplication.context, message, Toast.LENGTH_SHORT).show()
    }
}