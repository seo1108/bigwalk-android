package kr.co.bigwalk.app.util

import android.Manifest
import android.app.Activity
import android.view.View

fun galleryAddFile(activity: Activity, view: View) {
    if (isPermissionGranted(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
        requestPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    } else {
        saveBitmapToGallery(takeScreenShot(view))
        showToast("이미지를 성공적으로 저장하였습니다!")
    }
}