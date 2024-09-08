package kr.co.bigwalk.app.community.share

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.media.MediaActionSound
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import gun0912.tedimagepicker.builder.TedImagePicker
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.FragmentShareMyInfluenceBinding
import kr.co.bigwalk.app.util.*
import java.io.ByteArrayOutputStream

class ShareMyInfluenceFragment : Fragment() {
    private lateinit var binding: FragmentShareMyInfluenceBinding
    private val groupShareViewModel: GroupShareViewModel by lazy {
        ViewModelProvider(requireActivity()).get(GroupShareViewModel::class.java)
    }
    private val cameraUtil by lazy { CameraUtil(requireActivity() as AppCompatActivity) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_share_my_influence, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setView()
    }

    private fun setView() {
        with(binding) {
            vm = groupShareViewModel
            btnGetPicture.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(view: View) {
                    val sound = MediaActionSound()
                    sound.play(MediaActionSound.SHUTTER_CLICK)
                    cameraUtil.takePhoto()
                }
            })
            btnMode.setOnClickListener {
                cameraUtil.changeCameraMode()
            }
            btnFlash.setOnClickListener {
                cameraUtil.replaceFlashState()
                btnFlash.setImageResource(if (cameraUtil.isFlash()) R.drawable.ic_camera_flash_on else R.drawable.ic_camera_flash_off)
            }
            btnSelectPhoto.setOnClickListener {
                if (isPermissionGranted(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    requestPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                } else {
                    TedImagePicker.with(requireContext()).start { uri ->
                        startEditActivity(uri)
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.viewFinder.visibility = View.VISIBLE
        binding.btnFlash.setImageResource(R.drawable.ic_camera_flash_off)
        cameraUtil.start(object : CameraUtil.TakePhotoResultCallback {
            override fun onTakePhotoResult(uri: Uri) {
                startEditActivity(uri)
            }
        })
    }

    private fun startEditActivity(uri: Uri) {
        binding.picture.setImageURI(uri)
        binding.viewFinder.visibility = View.INVISIBLE
        startActivityForResult(
            ShareEditStickerActivity.getIntent(requireContext(), viewToByteArray(binding.imageContainer), groupShareViewModel.groupId),
            ShareEditStickerActivity.KEY_SHARE
        )
    }

    private fun viewToByteArray(view: View): ByteArray {
        val bitmap = takeScreenShot(view)
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)

        return stream.toByteArray()
    }

    override fun onPause() {
        super.onPause()
        cameraUtil.shutdown()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        cameraUtil.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            ShareEditStickerActivity.KEY_SHARE -> {
                if (resultCode == Activity.RESULT_OK) activity?.finish()
            }
        }
    }

    companion object {
        fun newInstance() = ShareMyInfluenceFragment()
    }
}