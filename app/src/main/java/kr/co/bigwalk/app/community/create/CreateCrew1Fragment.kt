package kr.co.bigwalk.app.community.create

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.theartofdev.edmodo.cropper.CropImage
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.FragmentCreateCrew1Binding
import kr.co.bigwalk.app.exception.EditProfileException
import kr.co.bigwalk.app.util.*
import java.io.File

class CreateCrew1Fragment : Fragment() {
    private lateinit var binding: FragmentCreateCrew1Binding
    private val createCommunityViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CreateCommunityViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_crew_1, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setView()
    }

    private fun setView() {
        with(binding) {
            vm = createCommunityViewModel
            crewImage.setOnClickListener {
                checkPermission()
            }
            crewTitle.contentView.addTextChangedListener {
                createCommunityViewModel.firstCrewTitle.value = it.toString()
            }
            crewTitle.contentView.setOnFocusChangeListener { v, hasFocus ->
                crewTitle.inputFieldDelete.isVisible = hasFocus
                if (!hasFocus) {
                    createCommunityViewModel.duplicateCheckForCrewTitle()
                }
            }
            crewIntroduce.contentView.addTextChangedListener {
                createCommunityViewModel.firstCrewSubTitle.value = it.toString()
            }
        }
    }

    private fun viewToFile(view: View): File {
        val bitmap = takeScreenShotWithoutBg(view)
        return screenshotBitmapToFile(bitmap)
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // 안드로이드13 인 경우
            if (isPermissionGranted(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)) {
                requestPermission(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickImageIntent ->
                    startActivityForResult(pickImageIntent, REQUEST_GALLERY_PERMISSION)
                }
            }
        } else {
            if (isPermissionGranted(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                requestPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
            } else {
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickImageIntent ->
                    startActivityForResult(pickImageIntent, REQUEST_GALLERY_PERMISSION)
                }
            }
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isEmpty()) return
            permissions.forEach {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // 안드로이드13 인 경우
                    if (isPermissionGranted(requireContext(), Manifest.permission.READ_MEDIA_IMAGES)) {
                        requestPermission(requireActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickImageIntent ->
                            pickImageIntent.type = "image/*"
                            startActivityForResult(pickImageIntent, REQUEST_GALLERY_PERMISSION)
                        }
                    }
                } else {
                    if (it == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        if (grantResults.first() == PackageManager.PERMISSION_GRANTED) {
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
        when (requestCode) {
            REQUEST_GALLERY_PERMISSION -> {
                if (data != null && !getRealPathFromUri(data.data!!).isNullOrBlank()) {
                    CropImage.activity(data.data!!)
                        .setAspectRatio(1, 1)
                        .start(requireContext(), this)
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    DebugLog.d("${result.uri}")
                    binding.crewImage.setImageURI(result.uri)
                    createCommunityViewModel.firstCrewImage.value = Pair(result.uri, viewToFile(binding.crewImage))
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    showToast("이미지를 편집할 수 없습니다. 다시 시도해주세요!!")
                    DebugLog.e(EditProfileException(result.error.localizedMessage))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        createCommunityViewModel.setFunctionAndViewForScreen(0)
    }

    companion object {
        fun newInstance() = CreateCrew1Fragment()
    }
}