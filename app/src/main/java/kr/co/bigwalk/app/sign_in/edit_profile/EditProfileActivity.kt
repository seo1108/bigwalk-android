package kr.co.bigwalk.app.sign_in.edit_profile

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import com.theartofdev.edmodo.cropper.CropImage
import gun0912.tedimagepicker.builder.TedImagePicker
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.user.ProfileType
import kr.co.bigwalk.app.data.user.dto.SignUpUserRequest
import kr.co.bigwalk.app.exception.EditProfileException
import kr.co.bigwalk.app.util.*
import kr.co.bigwalk.app.databinding.ActivityEditProfileBinding
import kr.co.bigwalk.app.ext.multiImagePicker.builder.MultiImagePicker
import kr.co.bigwalk.app.sign_in.edit_profile.EditProfileViewModel.Companion.CHARACTER_PROFILE

class EditProfileActivity : AppCompatActivity(),
    BaseNavigator {

    private lateinit var binding: ActivityEditProfileBinding
    companion object {
        var firebaseAnalytics : FirebaseAnalytics? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val request = intent.getSerializableExtra("SignUpUserRequest")
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics?.logEvent("sign_up_profile_view", Bundle())

        if (request != null && request is SignUpUserRequest) {
            val signUpUserRequest: SignUpUserRequest = request
            DebugLog.d(signUpUserRequest.toString())
            val viewModel = EditProfileViewModel(signUpUserRequest, this, supportFragmentManager)
            viewModel.profilePath.set(PreferenceManager.getCharacter())
            binding.viewModel = viewModel
        }

        checkPermission()
    }

    override fun getContext(): Activity {
        return this
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isEmpty()) return
            permissions.forEach {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // 안드로이드13 인 경우
                    if (isPermissionGranted(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                        requestPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickImageIntent ->
                            pickImageIntent.type = "image/*"
                            startActivityForResult(pickImageIntent, REQUEST_GALLERY_PERMISSION)
                        }
                    }
                } else {
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_GALLERY_PERMISSION -> {
                if (data != null && !getRealPathFromUri(data.data!!).isNullOrBlank()) {
                    CropImage.activity(data.data!!).start(this)
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    DebugLog.d("${result.uri}")
                    binding.viewModel?.profilePath?.set(result.uri.path)
                    binding.viewModel?.signUpUserRequest?.profileType = ProfileType.IMAGE.type
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                    showToast("이미지를 편집할 수 없습니다. 다시 시도해주세요!!")
                    DebugLog.e(EditProfileException(result.error.localizedMessage))
                }
            }
            CHARACTER_PROFILE -> {
                binding.viewModel?.profilePath?.set(data?.extras?.getString("ProfilePath"))
                binding.viewModel?.signUpUserRequest?.profileCharacterId = data?.extras?.getString("ProfilePath")?.toInt()
            }
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // 안드로이드13 인 경우
            if (isPermissionGranted(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                requestPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickImageIntent ->
                    startActivityForResult(pickImageIntent, REQUEST_GALLERY_PERMISSION)
                }
            }
        } else {
            if (isPermissionGranted(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickImageIntent ->
                    startActivityForResult(pickImageIntent, REQUEST_GALLERY_PERMISSION)
                }
            }
        }

    }
}