package kr.co.bigwalk.app.community.funding.create

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import com.theartofdev.edmodo.cropper.CropImage
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.funding.LabelSignUpMethod
import kr.co.bigwalk.app.data.funding.dto.RegisterLabelRequest
import kr.co.bigwalk.app.databinding.FragmentCreateLabel1Binding
import kr.co.bigwalk.app.exception.EditProfileException
import kr.co.bigwalk.app.util.*
import java.io.File

class CreateLabel1Fragment : Fragment() {
    private lateinit var binding: FragmentCreateLabel1Binding
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_create_label_1, container, false)
        binding.lifecycleOwner = this
        FirebaseAnalytics.getInstance(requireContext()).logEvent("contest_exhibit_making_view", Bundle())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setView()
        bindViewModel()
        setToolbar()
    }

    private fun setToolbar() {
        binding.exitButton.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setView() {
        with(binding) {
            vm = createFundingViewModel
            categoryContainer.categoryList.children.forEach {
                it.setOnClickListener { clickView ->
                    createFundingViewModel.categoryId = clickView.tag.toString().toInt()
                    categoryContainer.categoryList.children.forEach { childView ->
                        childView.isSelected = childView == clickView
                    }
                }
            }
            labelJoinSet.setOnCheckedChangeListener { group, checkedId ->
                DebugLog.d(checkedId.toString())
                when (checkedId) {
                    R.id.not_approve_ratio -> {
                        createFundingViewModel.setLabelSignUpMethod(LabelSignUpMethod.NOT_APPROVE)
                    }
                    R.id.approve_ratio -> {
                        createFundingViewModel.setLabelSignUpMethod(LabelSignUpMethod.APPROVE)
                    }
                    R.id.private_ratio -> {
                        createFundingViewModel.setLabelSignUpMethod(LabelSignUpMethod.PRIVATE)
                    }
                }
            }
            questionEnable.setOnCheckedChangeListener { group, checkedId ->
                createFundingViewModel.setQuestionEnable(checkedId == R.id.use_question_button)
            }
            detail1Container.contentContainer.setOnClickListener {
                createFundingViewModel.setDetail1Visible()
            }
            detail2Container.contentContainer.setOnClickListener {
                createFundingViewModel.setDetail2Visible()
            }
            detail3Container.contentContainer.setOnClickListener {
                createFundingViewModel.setDetail3Visible()
            }
            /* image click */
            crewImage.setOnClickListener {
                checkPermission()
                clickImageView = it as ImageView
            }
            detail1Container.image.setOnClickListener {
                checkPermission()
                clickImageView = it as ImageView
            }
            detail2Container.image.setOnClickListener {
                checkPermission()
                clickImageView = it as ImageView
            }
            detail3Container.image.setOnClickListener {
                checkPermission()
                clickImageView = it as ImageView
            }
            detail1Container.contentDelete.setOnClickListener {
                detail1Container.apply {
                    detailTitle.contentView.setText("")
                    detailIntro.contentView.setText("")
                    image.setImageBitmap(null)
                    image.tag = null
                }
            }
            detail2Container.contentDelete.setOnClickListener {
                detail2Container.apply {
                    detailTitle.contentView.setText("")
                    detailIntro.contentView.setText("")
                    image.setImageBitmap(null)
                    image.tag = null
                }
            }
            detail3Container.contentDelete.setOnClickListener {
                detail3Container.apply {
                    detailTitle.contentView.setText("")
                    detailIntro.contentView.setText("")
                    image.setImageBitmap(null)
                    image.tag = null
                }
            }
            nextButton.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(view: View) {
                    FirebaseAnalytics.getInstance(requireContext()).logEvent("contest_exhibit_making_button_next_click", Bundle())
                    createFundingViewModel.registerOrModifyCrewCampaign(
                        RegisterLabelRequest(
                            title = title.contentView.text.toString(),
                            competitionId = -1,
                            sequence = -1,
                            categoryId = null,
                            campaignImage = viewToFile(crewImage),
                            subTitle = subTitle.contentView.text.toString(),
                            description = description.contentView.text.toString(),
                            labelRecruitmentMethod = null,
                            question = questionInputView.contentView.text.toString(),
                            firstContentTitle = detail1Container.detailTitle.contentView.text.toString(),
                            firstContentDescription = detail1Container.detailIntro.contentView.text.toString(),
                            firstContentImage = viewToFile(detail1Container.image),
                            secondContentTitle = detail2Container.detailTitle.contentView.text.toString(),
                            secondContentDescription = detail2Container.detailIntro.contentView.text.toString(),
                            secondContentImage = viewToFile(detail2Container.image),
                            thirdContentTitle = detail3Container.detailTitle.contentView.text.toString(),
                            thirdContentDescription = detail3Container.detailIntro.contentView.text.toString(),
                            thirdContentImage = viewToFile(detail3Container.image)
                        )
                    )
                }
            })
        }
    }

    private fun bindViewModel() {
        with(createFundingViewModel) {
            previousCategoryId.observe(viewLifecycleOwner, Observer { initCategory ->
                binding.categoryContainer.categoryList.children.forEach {
                    if (it.tag.toString() == initCategory.toString()) it.performClick()
                }
            })
            toastMsg.observe(viewLifecycleOwner, EventObserver {
                showToast(it)
            })
            successEvent.observe(viewLifecycleOwner, EventObserver {
                showNextView()
            })
            failureEvent.observe(viewLifecycleOwner, EventObserver { errorMsg ->
                showToast(errorMsg)
            })
        }
    }

    private fun showNextView() {
        requireFragmentManager().beginTransaction()
            .setCustomAnimations(R.anim.anim_horizon_enter, R.anim.anim_horizon_exit2, R.anim.anim_horizon_enter2, R.anim.anim_horizon_exit)
            .replace(
                R.id.create_label_container,
                CreateLabel2Fragment.newInstance()
            )
            .addToBackStack(null)
            .commit()
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
                        .setAspectRatio(1, 1)
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

    companion object {
        fun newInstance() = CreateLabel1Fragment()
    }
}