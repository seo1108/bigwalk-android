package kr.co.bigwalk.app.util

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val REQUEST_PERMISSIONS = 9876
const val REQUEST_GALLERY_PERMISSION = 7654

fun isPermissionGranted(context: Context, permission: String): Boolean {
    return ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
}

fun requestPermission(activity: Activity, permission: String) {
    ActivityCompat.requestPermissions(activity, listOf(permission).toTypedArray(), REQUEST_PERMISSIONS)
}
