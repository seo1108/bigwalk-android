package kr.co.bigwalk.app.community.info

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.theartofdev.edmodo.cropper.CropImage
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.community.ModifyCrewRequest
import kr.co.bigwalk.app.databinding.ActivityModifyCrewIntroBinding
import kr.co.bigwalk.app.exception.EditProfileException
import kr.co.bigwalk.app.util.*
import java.io.File

class ModifyCrewIntroActivity : AppCompatActivity() {
    private lateinit var binding: ActivityModifyCrewIntroBinding
    private val modifyCrewIntroViewModel by lazy {
        ViewModelProvider(this, ModifyCrewIntroViewModelFactory(crewId)).get(ModifyCrewIntroViewModel::class.java)
    }
    private val crewId: Long by lazy { intent.getLongExtra(KEY_CREW_ID, DEF_LONG_VALUE) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_modify_crew_intro)
        binding.lifecycleOwner = this

        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            vm = modifyCrewIntroViewModel
            crewImage.clipToOutline = true
            
            exitButton.setOnClickListener {
                finish()
            }
            
            imageUpload.setOnClickListener {
                if (isPermissionGranted(this@ModifyCrewIntroActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    requestPermission(this@ModifyCrewIntroActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickImageIntent ->
                        startActivityForResult(pickImageIntent, REQUEST_GALLERY_PERMISSION)
                    }
                }
            }
            modifyComplete.setOnClickListener {
                modifyCrewIntroViewModel.modifyCrew(
                    ModifyCrewRequest(
                        name = crewTitle.contentView.text.toString(),
                        description = crewIntroduce.contentView.text.toString(),
                        mainImage = viewToFile(crewImage)
                    )
                )
            }
        }
    }
    
    private fun viewToFile(view: View): File {
        val bitmap = takeScreenShot(view)
        
        return screenshotBitmapToFile(bitmap)
    }
    
    private fun bindViewModel() {
        with(modifyCrewIntroViewModel) {
            successMessage.observe(this@ModifyCrewIntroActivity, Observer { resId ->
                showToast(resId)
                finish()
            })
            failureMessage.observe(this@ModifyCrewIntroActivity, Observer { msg ->
                showToast(msg)
            })
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isEmpty()) return
            permissions.forEach {
                if (it == Manifest.permission.READ_EXTERNAL_STORAGE) {
                    if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickImageIntent ->
                            pickImageIntent.type = "image/*"
                            startActivityForResult(pickImageIntent, REQUEST_GALLERY_PERMISSION)
                        }
                    } else {
                        showToast("갤러리 이용을 위해 권한 허가가 필요합니다.")
                        return
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GALLERY_PERMISSION -> {
                if (data != null && !getRealPathFromUri(data.data!!).isNullOrBlank()) {
                    CropImage.activity(data.data!!)
                        .setAspectRatio(1, 1)
                        .start(this)
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    DebugLog.d("${result.uri}")
                    binding.crewImage.setImageURI(result.uri)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    showToast("이미지를 편집할 수 없습니다. 다시 시도해주세요!!")
                    DebugLog.e(EditProfileException(result.error.localizedMessage))
                }
            }
        }
    }

    companion object {
        private const val KEY_CREW_ID = "CREW_ID"
        fun getIntent(context: Context, crewId: Long) =
            Intent(context, ModifyCrewIntroActivity::class.java)
                .putExtra(KEY_CREW_ID, crewId)
    }
}