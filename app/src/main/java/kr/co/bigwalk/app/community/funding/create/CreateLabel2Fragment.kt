package kr.co.bigwalk.app.community.funding.create

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.theartofdev.edmodo.cropper.CropImage
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.community.funding.SupportersCampaignPreviewActivity
import kr.co.bigwalk.app.data.community.GroupMemberRole
import kr.co.bigwalk.app.data.funding.dto.RegisterChallengeOfSupportersRequest
import kr.co.bigwalk.app.databinding.FragmentCreateLabel2Binding
import kr.co.bigwalk.app.exception.EditProfileException
import kr.co.bigwalk.app.util.*
import java.io.File

class CreateLabel2Fragment : Fragment() {
    private lateinit var binding: FragmentCreateLabel2Binding
    private val createFundingViewModel: CreateFundingViewModel by lazy {
        ViewModelProvider(requireActivity()).get(CreateFundingViewModel::class.java)
    }

    private lateinit var clickImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_label_2, container, false)
        binding.lifecycleOwner = this
        FirebaseAnalytics.getInstance(requireContext()).logEvent("contest_exhibit_making_contents_view", Bundle())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
        setView()
        bindViewModel()
    }

    private fun setToolbar() {
        with(binding) {
            exitButton.setOnClickListener {
                activity?.onBackPressed()
            }
        }
    }

    private fun setView() {
        with(binding) {
            vm = createFundingViewModel
            contentTypeRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                DebugLog.d(checkedId.toString())
                challengeForm.isVisible = checkedId == R.id.content_challenge
                nothingForm.root.isVisible = checkedId == R.id.content_nothing
                tooltipRunning.isVisible = checkedId == R.id.content_running
                tooltipSpace.isVisible = checkedId == R.id.content_space
            }
            guide1Container.contentContainer.setOnClickListener {
                createFundingViewModel.setGuide1Visible()
            }
            guide2Container.contentContainer.setOnClickListener {
                createFundingViewModel.setGuide2Visible()
            }
            /* image click */
            challengeImage.setOnClickListener {
                checkPermission()
                clickImageView = it as ImageView
                DebugLog.d(binding.challengeImage.toString())
                DebugLog.d(clickImageView.toString())
            }
            guide1Container.image.setOnClickListener {
                checkPermission()
                clickImageView = it as ImageView
            }
            guide1Container.image2.setOnClickListener {
                checkPermission()
                clickImageView = it as ImageView
            }
            guide2Container.image.setOnClickListener {
                checkPermission()
                clickImageView = it as ImageView
            }
            guide2Container.image2.setOnClickListener {
                checkPermission()
                clickImageView = it as ImageView
            }
            guide1Container.contentDelete.setOnClickListener {
                guide1Container.apply {
                    introduce1.contentView.setText("")
                    introduce2.contentView.setText("")
                    image.setImageBitmap(null)
                    image.tag = null
                    image2.setImageBitmap(null)
                    image2.tag = null
                }
            }
            guide2Container.contentDelete.setOnClickListener {
                guide2Container.apply {
                    introduce1.contentView.setText("")
                    introduce2.contentView.setText("")
                    image.setImageBitmap(null)
                    image.tag = null
                    image2.setImageBitmap(null)
                    image2.tag = null
                }
            }
            saveButton.setOnClickListener {
                FirebaseAnalytics.getInstance(requireContext()).logEvent("contest_exhibit_making_button_save_click", Bundle())
                createFundingViewModel.registerOrModifyChallengeOfCrewCampaign(
                    RegisterChallengeOfSupportersRequest(
                        title = title.contentView.text.toString(),
                        content = content.contentView.text.toString(),
                        mainImage = viewToFile(challengeImage),
                        firstHowToImage = viewToFile(guide1Container.image),
                        secondHowToImage = viewToFile(guide1Container.image2),
                        firstHowToDescription = guide1Container.introduce1.contentView.text.toString(),
                        secondHowToDescription = guide1Container.introduce2.contentView.text.toString(),
                        firstInvalidImage = viewToFile(guide2Container.image),
                        secondInvalidImage = viewToFile(guide2Container.image2),
                        firstInvalidDescription = guide2Container.introduce1.contentView.text.toString(),
                        secondInvalidDescription = guide2Container.introduce2.contentView.text.toString()
                    )
                )
            }
            nothingForm.comentBox.setOnClickListener {
                startActivity(
                    Intent(Intent.ACTION_VIEW).apply {
                        data = Uri.parse(requireContext().getString(R.string.apply_content_url))
                    }
                )
            }
            nothingForm.saveButton.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(view: View) {
                    createFundingViewModel.moveToPreview()
                }
            })
        }
    }

    private fun bindViewModel() {
        with(createFundingViewModel) {
            myRole.observe(viewLifecycleOwner, Observer { role ->
                if (role == GroupMemberRole.MEMBER) {
                    showPermissionToModifyDialog()
                }
            })
            toastMsg.observe(viewLifecycleOwner, EventObserver {
                showToast(it)
            })
            successRegister.observe(viewLifecycleOwner, Observer { item ->
                if (item.hasSequence) {
                    showSuccessRegisterDialog(item)
                } else {
                    showSuccessRegisterDialog()
                }
            })
            failureEvent.observe(viewLifecycleOwner, EventObserver { errorMsg ->
                showToast(errorMsg)
            })
        }
    }

    private fun viewToFile(view: View): File? {
        if (view.width <= 0 || view.tag == null) return null

        val bitmap = takeScreenShotWithoutBg(view)
        return screenshotBitmapToFile(bitmap)
    }

    private fun checkPermission() {
        if (isPermissionGranted(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            requestPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
        } else {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickImageIntent ->
                startActivityForResult(pickImageIntent, REQUEST_GALLERY_PERMISSION)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isEmpty()) return
            permissions.forEach {
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_GALLERY_PERMISSION -> {
                if (data != null && !getRealPathFromUri(data.data!!).isNullOrBlank()) {
                    CropImage.activity(data.data!!)
                        .setAspectRatio(clickImageView.width, clickImageView.height)
                        .start(requireContext(), this)
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    DebugLog.d("${result.uri}")
                    clickImageView.setImageURI(result.uri)
                    clickImageView.tag = result.uri
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    showToast("이미지를 편집할 수 없습니다. 다시 시도해주세요!!")
                    DebugLog.e(EditProfileException(result.error.localizedMessage))
                }
            }
        }
    }

    private fun showSuccessRegisterDialog(item: RegisterOrModifyMoveItem? = null) {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.complete_save_crew_campaign))
            .setPositiveButton(R.string.confirm) { dialog, _ ->
                if (item != null) {
                    startActivity(
                        SupportersCampaignPreviewActivity.getIntent(
                            requireContext(),
                            item.groupId,
                            item.crewCampaignId,
                            item.hasActionMission
                        )
                    )
                    activity?.setResult(Activity.RESULT_OK)
                    activity?.finish()
                } else {
                    activity?.finish()
                }
            }
            .setCancelable(false)
            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }
    }

    private fun showPermissionToModifyDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.content_manager_entry_msg))
            .setPositiveButton(R.string.confirm) { dialog, _ -> }
            .create()

        dialog.run {
            show()
            getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.BLUE)
        }
    }

    companion object {
        fun newInstance() = CreateLabel2Fragment()
    }
}