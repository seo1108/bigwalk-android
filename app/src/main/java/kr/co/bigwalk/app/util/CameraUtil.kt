package kr.co.bigwalk.app.util

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Rational
import android.util.Size
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_mission_certification.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraUtil(private val activity: AppCompatActivity) {

    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    private val REQUEST_CODE_PERMISSIONS = 10
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    private var imageCapture: ImageCapture? = null
    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraInfo: CameraInfo
    private lateinit var cameraController: CameraControl

    private var isBack = true
    private var callback: TakePhotoResultCallback? = null

    interface TakePhotoResultCallback {
        fun onTakePhotoResult(uri: Uri)
    }

    fun start(callback: TakePhotoResultCallback) {
        this.callback = callback
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(activity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }


        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera(isBack: Boolean = true) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(activity)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                    .setTargetResolution(Size(640, 640))
                    .build()
                    .also {
                        it.setSurfaceProvider(activity.viewFinder.createSurfaceProvider())
                    }
            imageCapture = ImageCapture.Builder().build()
            // Select back camera as a default
            val cameraSelector = if (isBack) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                val camera = cameraProvider.bindToLifecycle(activity, cameraSelector, preview, imageCapture)

                cameraController = camera.cameraControl
                cameraInfo = camera.cameraInfo

            } catch (exc: Exception) {
                DebugLog.e(exc)
            }
        }, ContextCompat.getMainExecutor(activity))
    }

    fun takePhoto() {
        val imageCapture = imageCapture ?: return
        imageCapture.setCropAspectRatio(Rational(1, 1))

        // Create time-stamped output file to hold the image
        val photoFile = File(outputDirectory, SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis()) + ".jpg")

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // Set up image capture listener, which is triggered after photo has
        // been taken
        imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(activity), object : ImageCapture.OnImageSavedCallback {
            override fun onError(exc: ImageCaptureException) {
                DebugLog.e(exc)
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(photoFile)
                callback?.onTakePhotoResult(savedUri)
            }
        })
    }

    fun changeCameraMode() {
        if (isBack) {
            startCamera(false)
        } else {
            startCamera()
        }
        isBack = !isBack
    }
    
    fun replaceFlashState() {
        cameraController.enableTorch(cameraInfo.torchState.value == TorchState.OFF)
    }
    
    fun isFlash(): Boolean {
        return when (cameraInfo.torchState.value) {
            TorchState.ON -> {
                true
            }
            TorchState.OFF -> {
                false
            }
            else -> {
                false
            }
        }
    }

    private fun getOutputDirectory(): File {
        val mediaDir = activity.externalMediaDirs.firstOrNull()?.let {
            File(it, "campaign").apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else activity.filesDir
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
    }

    fun shutdown() {
        cameraExecutor.shutdown()
    }

    fun onRequestPermissionsResult(
            requestCode: Int, permissions: Array<String>, grantResults:
            IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(activity, "카메라 권한 허용 실패", Toast.LENGTH_SHORT).show()
                activity.finish()
            }
        }
    }
}