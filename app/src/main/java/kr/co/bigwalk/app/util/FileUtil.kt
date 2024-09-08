package kr.co.bigwalk.app.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import com.theartofdev.edmodo.cropper.CropImage
import id.zelory.compressor.Compressor
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.BuildConfig
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.exception.DownloadException
import kr.co.bigwalk.app.infra.social.repository.InfraDataSource
import kr.co.bigwalk.app.infra.social.repository.InfraRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


val tempCacheDir: String = "${BigwalkApplication.context?.cacheDir}"
val tempDownloadedFilePath: String = "$tempCacheDir/temp_file"
val saveFilePath: String = "${BigwalkApplication.context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)}"

fun getRealPathFromUri(uri: Uri): String? {
    val cursor = BigwalkApplication.context?.contentResolver!!
        .query(uri, arrayOf(MediaStore.Images.Media.DATA), null, null, null)
    var realPath: String? = null
    cursor?.let {
        val index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        realPath = cursor.getString(index)
        cursor.close()
    }
    return realPath
}

fun getUriFromFile(file: File): Uri? {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
        DebugLog.d(file.path)
        return BigwalkApplication.context?.let {
            FileProvider.getUriForFile(
                it,
                "${BuildConfig.APPLICATION_ID}.fileProvider",
                file
            )
        }
    }
    return Uri.fromFile(file)
}

fun downloadAndCompressFile(path: String, activity: Activity, canCrop: Boolean): File? {
    var compressedFile: File? = null
    InfraRepository.downloadSocialProfile(
        path,
        object : InfraDataSource.DownloadSocialProfileCallback {
            override fun onSuccess() {
                val downloadFile = File(tempDownloadedFilePath)
                if (downloadFile.exists()) {
                    DebugLog.d("social profile download success!!")
                    when (downloadFile.length()) {
                        in 0..1000000 -> compressedFile = downloadFile
                        else -> {
                            compressedFile =
                                compressFile(BigwalkApplication.context!!, downloadFile.toUri())
                            if (compressedFile!!.length() > 1000000) {
                                showToast("이미지는 최대 1MB 이어야 합니다.")
                                return
                            }
                            DebugLog.d("social profile compress success!!")
                        }
                    }
                    BigwalkApplication.context?.dismissLoadingAnimation()
                    if (canCrop) CropImage.activity(compressedFile?.toUri()).start(activity)
                }
            }

            override fun onFailed(reason: String) {
                DebugLog.e(DownloadException(reason))
            }

        })
    return compressedFile
}

/**
 * 간헐적으로 발생하는
 * Compressor 라이브러리 오류 해결을 위한 추가 코드
 */
@Throws(IOException::class)
fun compressFile(context: Context, uri: Uri): File {
    val matrix = Matrix()
    val exif = ExifInterface(uri.toFile().absolutePath)
    when (exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0)) {
        6 -> {
            matrix.postRotate(90F)
        }
        3 -> {
            matrix.postRotate(180F)
        }
        8 -> {
            matrix.postRotate(270F)
        }
    }
    return Compressor(context).compressToFile(uri.toFile())
}

fun createEmptyPart(name: String): MultipartBody.Part {
    val filePath = BigwalkApplication.context?.filesDir?.path.toString() + "/empty"
    val file = File(filePath)
    file.createNewFile()
    val requestFile = file.asRequestBody("image".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(name, file.name, requestFile)
}

@NonNull
fun prepareFilePart(file: File, name: String): MultipartBody.Part {
    BigwalkApplication.context?.let {
        val requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(name, file.name, requestFile)
    }
    return createEmptyPart(name)
}

fun takeScreenShot(view: View): Bitmap {
    view.setBackgroundColor(ContextCompat.getColor(BigwalkApplication.context!!, R.color.white))
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}

fun takeScreenShotWithoutBg(view: View): Bitmap {
    val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}

fun screenshotBitmapToFile(bitmap: Bitmap): File {
    val file = File("$tempCacheDir/screenshot${Calendar.getInstance().timeInMillis}.jpg")
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()
    return file
}

fun saveBitmapToGallery(bitmap: Bitmap) {
    val fileName = "${saveFilePath}/bigwalk-${SimpleDateFormat("yyyyMMdd-HHmmss", Locale.KOREA).format(Date())}.jpg"
    val file = File(fileName)
    val outputStream = FileOutputStream(file)
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
    outputStream.flush()
    outputStream.close()

    MediaStore.Images.Media.insertImage(
        BigwalkApplication.context?.contentResolver,
        bitmap,
        fileName,
        "bigwalk screenshot image"
    )
}
