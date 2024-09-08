package kr.co.bigwalk.app.util

import android.content.Context
import androidx.appcompat.app.AlertDialog
import kr.co.bigwalk.app.R

fun showAlertDialog(context: Context, msg: String, cancelable: Boolean = true, positiveCallback: () -> Unit) {
    val dialog = AlertDialog.Builder(context)
        .setMessage(msg)
        .setCancelable(cancelable)
        .setPositiveButton(R.string.confirm) { _, _ ->
            positiveCallback()
        }
        .create()

    dialog.run {
        show()
    }
}

fun showAlertDialog(context: Context, msg: Int, positiveBtnMsg: Int? = null, positiveCallback: () -> Unit) {
    val dialog = AlertDialog.Builder(context)
        .setMessage(msg)
        .setPositiveButton(positiveBtnMsg ?: R.string.confirm) { _, _ ->
            positiveCallback()
        }
        .setNegativeButton(R.string.cancel) { _, _ -> }
        .create()

    dialog.run {
        show()
    }
}