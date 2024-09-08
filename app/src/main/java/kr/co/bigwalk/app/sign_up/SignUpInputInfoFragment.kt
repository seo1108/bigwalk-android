package kr.co.bigwalk.app.sign_up

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.activityViewModels
import com.google.firebase.analytics.FirebaseAnalytics
import com.kennyc.bottomsheet.BottomSheetListener
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment
import com.theartofdev.edmodo.cropper.CropImage
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseFragment
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.user.Gender
import kr.co.bigwalk.app.data.user.ProfileType
import kr.co.bigwalk.app.databinding.FragmentSignUpInputInfoBinding
import kr.co.bigwalk.app.exception.EditProfileException
import kr.co.bigwalk.app.sign_in.SelfCertificationActivity
import kr.co.bigwalk.app.sign_in.edit_profile.EditProfileActivity
import kr.co.bigwalk.app.sign_in.edit_profile.EditProfileViewModel
import kr.co.bigwalk.app.sign_in.organization.CorporateMemberDeliveryInputForm
import kr.co.bigwalk.app.sign_in.organization.OrganizationFormActivity
import kr.co.bigwalk.app.sign_in.organization.OrganizationViewModel
import kr.co.bigwalk.app.util.*

class SignUpInputInfoFragment : BaseFragment<FragmentSignUpInputInfoBinding>(R.layout.fragment_sign_up_input_info) {

    private val signUpViewModel by activityViewModels<SignUpViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseAnalytics.getInstance(requireContext()).logEvent("sign_up_Basic_info_view", Bundle())

        setView()
        bindViewModel()
    }

    private fun setView() {
        with(binding) {
            vm = signUpViewModel
            profileImageContainer.setOnClickListener {
                modifyProfileImage()
            }
            editProfileOrganizationMemberTitle.setOnClickListener {
                val intent = Intent(requireContext(), OrganizationFormActivity::class.java)
                startActivityForResult(intent, KEY_REQ_ORGANIZATION)
            }
            inputNickname.run {
                requestFocus()
                setOnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus) {
                        signUpViewModel.existsNickname(inputNickname.text.toString())
                    }
                    btnNicknameClear.isVisible = hasFocus
                }
            }
            inputNickname.addTextChangedListener {
                signUpViewModel.changeNicknameType(inputNickname.text.toString())
            }
            inputPhoneNumber.setOnClickListener {
                startActivityForResult(SelfCertificationActivity.getIntent(requireContext()), KEY_REQ_PHONE_AUTH)
            }
            inputEmail.addTextChangedListener {
                signUpViewModel.checkEmail(inputEmail.text.toString())
            }
            inputBirth.addTextChangedListener {
                signUpViewModel.checkBirth(inputBirth.text.toString())
            }
            genderRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                when(checkedId) {
                    R.id.btn_male -> {
                        signUpViewModel.setGender(Gender.MAN)
                    }
                    R.id.btn_female -> {
                        signUpViewModel.setGender(Gender.WOMAN)
                    }
                }
            }
            inputCorporateGroup.root.setOnClickListener {
                val intent = Intent(requireContext(), OrganizationFormActivity::class.java)
                startActivityForResult(intent, KEY_REQ_ORGANIZATION)
            }
            changeGeneralMember.setOnClickListener {
                showAlertDialog(requireContext(), R.string.dialog_change_general_member_msg) {
                    signUpViewModel.afterSelectOrganization(null)
                }
            }
        }
    }


    private fun bindViewModel() {

    }

    private fun modifyProfileImage() {
        EditProfileActivity.firebaseAnalytics?.logEvent("sign_up_profile_button", Bundle())

        val sheet = BottomSheetMenuDialogFragment.Builder(requireContext())
        sheet.setSheet(R.menu.bottom_sheet_menu)
        sheet.setListener(object : BottomSheetListener {
            override fun onSheetItemSelected(
                cbottomSheet: BottomSheetMenuDialogFragment,
                item: MenuItem?,
                `object`: Any?
            ) {
                when (item?.itemId) {
                    R.id.select_from_album -> {
                        if (isPermissionGranted(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            requestPermission(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        } else {
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickImageIntent ->
                                startActivityForResult(pickImageIntent, REQUEST_GALLERY_PERMISSION)
                            }
                        }
                    }
                    R.id.change_to_default_profile -> {
                        signUpViewModel.setCharacter(PreferenceManager.getCharacter(), ProfileType.CHARACTER)
                    }
                }

            }

            override fun onSheetDismissed(
                bottomSheet: BottomSheetMenuDialogFragment,
                `object`: Any?,
                dismissEvent: Int
            ) {
                //nothing
            }

            override fun onSheetShown(bottomSheet: BottomSheetMenuDialogFragment, `object`: Any?) {
                //nothing
            }
        }).show(parentFragmentManager)
    }

    override fun onResume() {
        super.onResume()
        signUpViewModel.setFunctionAndViewForScreen(1)
        (activity as SignUpActivity).supportActionBar?.title = getString(R.string.enter_basic_information)
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
                    CropImage.activity(data.data!!).start(requireContext(), this)
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    DebugLog.d("${result.uri}")
                    signUpViewModel.setCharacter(result.uri.path ?: PreferenceManager.getCharacter(), ProfileType.IMAGE)
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    showToast("이미지를 편집할 수 없습니다. 다시 시도해주세요!!")
                    DebugLog.e(EditProfileException(result.error.localizedMessage))
                }
            }
            EditProfileViewModel.CHARACTER_PROFILE -> {
                //뷰모델에 프로필 전달
//                binding.viewModel?.profilePath?.set(data?.extras?.getString("ProfilePath"))
//                binding.viewModel?.signUpUserRequest?.profileCharacterId = data?.extras?.getString("ProfilePath")?.toInt()
            }
            KEY_REQ_ORGANIZATION -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data?.getSerializableExtra(OrganizationViewModel.KEY_CORPORATE_MEMBER_FORM) != null) {
                        val test = data.getSerializableExtra(OrganizationViewModel.KEY_CORPORATE_MEMBER_FORM) as CorporateMemberDeliveryInputForm
                        signUpViewModel.afterSelectOrganization(test)
                    }
                }
            }
            KEY_REQ_PHONE_AUTH -> {
                if (resultCode == Activity.RESULT_OK) {
                    binding.btnPhoneNumberAuth.visibility = View.VISIBLE
                    binding.inputPhoneNumber.setText(data?.getStringExtra(SelfCertificationActivity.KEY_PHONE_NUMBER).orEmpty())
                }
            }
        }
    }

    companion object {
        private const val KEY_REQ_ORGANIZATION = 100
        private const val KEY_REQ_PHONE_AUTH = 101
        fun newInstance() =
            SignUpInputInfoFragment()
    }
}