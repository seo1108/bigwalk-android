package kr.co.bigwalk.app.my_page

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import com.kennyc.bottomsheet.BottomSheetListener
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment
import com.theartofdev.edmodo.cropper.CropImage
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.community.dto.create.CrewConcernTagResponse
import kr.co.bigwalk.app.data.user.Gender
import kr.co.bigwalk.app.data.user.dto.MyProfileResponse
import kr.co.bigwalk.app.data.user.dto.UserConcernTagResponse
import kr.co.bigwalk.app.databinding.ActivityMyPageModifyBinding
import kr.co.bigwalk.app.exception.EditProfileException
import kr.co.bigwalk.app.profile.detail.SelectAreaActivity
import kr.co.bigwalk.app.profile.detail.SelectInterestActivity
import kr.co.bigwalk.app.sign_in.SelfCertificationActivity
import kr.co.bigwalk.app.sign_in.edit_profile.EditProfileActivity
import kr.co.bigwalk.app.sign_in.edit_profile.EditProfileViewModel
import kr.co.bigwalk.app.sign_in.organization.CorporateMemberDeliveryInputForm
import kr.co.bigwalk.app.sign_in.organization.OrganizationFormActivity
import kr.co.bigwalk.app.sign_in.organization.OrganizationViewModel
import kr.co.bigwalk.app.util.*

class MyPageModifyActivity : BaseActivity<ActivityMyPageModifyBinding>(R.layout.activity_my_page_modify) {
    private val modifyMyPageViewModel by viewModels<ModifyMyPageViewModel>()

    private val intentProfileData: MyProfileResponse? by lazy { intent.getSerializableExtra(DATA_PROFILE) as? MyProfileResponse }
    private var selectArea = Pair<String, String>("", "")
    private var selectTagList: List<UserConcernTagResponse>? = null
    private var saveBtnEnable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        modifyMyPageViewModel.downloadProfileImage()
        selectArea = Pair(intentProfileData?.firstDepthRegion.orEmpty(), intentProfileData?.secondDepthRegion.orEmpty())
        setToolbar()
        setView()
        bindViewModel()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.aos_icon_20_arrow)
            title = "프로필 수정"
        }
    }

    private fun setView() {
        with(binding) {
            vm = modifyMyPageViewModel
            profileImageContainer.setOnClickListener {
                modifyProfileImage()
            }
            editProfileOrganizationMemberTitle.setOnClickListener {
                val intent = Intent(this@MyPageModifyActivity, OrganizationFormActivity::class.java)
                intent.putExtra("CameFrom", "RegisterOrganization")
                startActivityForResult(intent, KEY_REQ_ORGANIZATION)
            }
            inputNickname.run {
                requestFocus()
                setOnFocusChangeListener { v, hasFocus ->
                    if (!hasFocus) {
                        modifyMyPageViewModel.existsNickname(inputNickname.text.toString())
                    }
                    btnNicknameClear.isVisible = hasFocus
                }
            }
            inputNickname.addTextChangedListener {
                nicknameErrorMsg.visibility = View.GONE
                saveBtnEnable = false
                invalidateOptionsMenu()
            }
            inputPhoneNumber.setOnClickListener {
                if (intentProfileData?.phoneNumber.isNullOrEmpty().not()) return@setOnClickListener
                startActivityForResult(SelfCertificationActivity.getIntent(this@MyPageModifyActivity), KEY_REQ_PHONE_AUTH)
            }
            inputEmail.addTextChangedListener {
                modifyMyPageViewModel.checkEmail(inputEmail.text.toString())
            }
            inputBirth.addTextChangedListener {
                modifyMyPageViewModel.checkBirth(inputBirth.text.toString())
            }
            genderRadioGroup.setOnCheckedChangeListener { group, checkedId ->
                when (checkedId) {
                    R.id.btn_male -> {
                        modifyMyPageViewModel.setGender(Gender.MAN)
                    }
                    R.id.btn_female -> {
                        modifyMyPageViewModel.setGender(Gender.WOMAN)
                    }
                }
            }
            inputCorporateGroup.root.setOnClickListener {
                val intent = Intent(this@MyPageModifyActivity, OrganizationFormActivity::class.java)
                intent.putExtra("CameFrom", "ModProfile")
                startActivityForResult(intent, KEY_REQ_ORGANIZATION)
            }
            changeGeneralMember.setOnClickListener {
                showAlertDialog(this@MyPageModifyActivity, R.string.dialog_change_general_member_msg) {
                    modifyMyPageViewModel.afterSelectOrganization(null)
                }
            }
            formArea.setOnClickListener {
                showSelectAreaDialog()
            }
            formInterest.setOnClickListener {
                showSelectInterestDialog()
            }

        }
    }

    private fun showSelectAreaDialog() {
        startActivityForResult(SelectAreaActivity.getIntent(this, selectArea.first, selectArea.second), REQ_CODE_AREA)
    }

    private fun showSelectInterestDialog() {
        startActivityForResult(SelectInterestActivity.getIntent(this, selectTagList?.map {
            CrewConcernTagResponse(
                id = it.id.toInt(),
                name = it.name,
                characteristic = "",
                selected = false
            )
        }.orEmpty()), REQ_CODE_INTEREST)
    }

    private fun bindViewModel() {
        with(modifyMyPageViewModel) {
            initData(intentProfileData)
            selectConcernList.observe(this@MyPageModifyActivity, Observer { list ->
                selectTagList = list
                binding.interestList.removeAllViews()
                list.forEach { tagList ->
                    val view = LayoutInflater.from(this@MyPageModifyActivity)
                        .inflate(R.layout.view_crew_create_tag, binding.interestList, false)

                    val tagView: TextView = view.findViewById(R.id.tag)
                    tagView.text = tagList.name


                    binding.interestList.addView(view)
                }
            })
            nextBtnEnable.observe(this@MyPageModifyActivity, Observer {
                saveBtnEnable = it
                invalidateOptionsMenu()
            })
            successEvent.observe(this@MyPageModifyActivity, Observer {
                finish()
            })
        }
    }

    private fun modifyProfileImage() {
        EditProfileActivity.firebaseAnalytics?.logEvent("sign_up_profile_button", Bundle())

        val sheet = BottomSheetMenuDialogFragment.Builder(this)
        sheet.setSheet(R.menu.bottom_sheet_menu)
        sheet.setListener(object : BottomSheetListener {
            override fun onSheetItemSelected(
                cbottomSheet: BottomSheetMenuDialogFragment,
                item: MenuItem?,
                `object`: Any?
            ) {
                when (item?.itemId) {
                    R.id.select_from_album -> {
                        if (isPermissionGranted(this@MyPageModifyActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            requestPermission(this@MyPageModifyActivity, Manifest.permission.READ_EXTERNAL_STORAGE)
                        } else {
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickImageIntent ->
                                startActivityForResult(pickImageIntent, REQUEST_GALLERY_PERMISSION)
                            }
                        }
                    }
                    R.id.change_to_default_profile -> {
                        modifyMyPageViewModel.setCharacter(PreferenceManager.getCharacter())
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
        }).show(supportFragmentManager)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isEmpty()) return
            permissions.forEach {
                /*if (it == Manifest.permission.READ_EXTERNAL_STORAGE) {
                    if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickImageIntent ->
                            pickImageIntent.type = "image/*"
                            startActivityForResult(pickImageIntent, REQUEST_GALLERY_PERMISSION)
                        }
                    } else {
                        showToast("갤러리 이용을 위해 권한 허가가 필요합니다.")
                        return
                    }
                }*/

                 */
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
                    modifyMyPageViewModel.setCharacter(result.uri.path ?: PreferenceManager.getCharacter())
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
                    DebugLog.d("KEY_REQ_ORGANIZATION")
                    if (data?.getSerializableExtra(OrganizationViewModel.KEY_CORPORATE_MEMBER_FORM) != null) {
                        val test = data.getSerializableExtra(OrganizationViewModel.KEY_CORPORATE_MEMBER_FORM) as CorporateMemberDeliveryInputForm
                        modifyMyPageViewModel.afterSelectOrganization(test)
                        DebugLog.d("KEY_REQ_ORGANIZATION $test")
                    }
                }
            }
            KEY_REQ_PHONE_AUTH -> {
                if (resultCode == Activity.RESULT_OK) {
                    binding.btnPhoneNumberAuth.visibility = View.VISIBLE
                    binding.inputPhoneNumber.setText(data?.getStringExtra(SelfCertificationActivity.KEY_PHONE_NUMBER).orEmpty())
                }
            }
            REQ_CODE_AREA -> {
                if (resultCode == Activity.RESULT_OK) {
                    val area = data?.getSerializableExtra(SelectAreaActivity.KEY_SELECT_AREA) as Pair<String, String>
                    selectArea = area
                    modifyMyPageViewModel.area.value = area
                }
            }
            REQ_CODE_INTEREST -> {
                if (resultCode == Activity.RESULT_OK) {
                    val list = data?.getSerializableExtra(SelectInterestActivity.KEY_SELECT_TAG_LIST) as List<CrewConcernTagResponse>?
                    modifyMyPageViewModel.selectConcernList.value = list?.map {
                        it.toUserConcernTagResponse()
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_page_modify_menu, menu)

        menu?.apply {
            val item = this.findItem(R.id.modify_profile)
            val s = SpannableString(item.title)
            s.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MyPageModifyActivity, R.color.sub2)), 0, s.length, 0)
            item.title = s
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val colorId = if (saveBtnEnable) R.color.pacific_blue else R.color.sub2
        menu?.apply {
            val item = this.findItem(R.id.modify_profile)
            val s = SpannableString(item.title)
            s.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MyPageModifyActivity, colorId)), 0, s.length, 0)
            item.title = s
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.modify_profile -> {
                if (saveBtnEnable) modifyMyPageViewModel.modifyMyProfile() else showToast("모든 정보를 올바르게 입력해주세요")
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm: InputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    companion object {
        private const val KEY_REQ_ORGANIZATION = 100
        private const val KEY_REQ_PHONE_AUTH = 101
        private const val REQ_CODE_AREA = 102
        private const val REQ_CODE_INTEREST = 103
        private const val DATA_PROFILE = "DATA_PROFILE"
        fun getIntent(context: Context, myProfileResponse: MyProfileResponse) =
            Intent(context, MyPageModifyActivity::class.java).apply {
                putExtra(DATA_PROFILE, myProfileResponse)
            }
    }
}
//class MyPageModifyActivity: AppCompatActivity(), BaseNavigator {
//    private lateinit var binding: ActivityUserProfileInformationModifyBinding
//    private lateinit var viewModel: MyPageViewModel
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile_information_modify)
//        viewModel = MyPageViewModel(this, supportFragmentManager, this)
//        binding.viewModel = viewModel
//        viewModel.downloadProfileImage()
//
//        binding.userNickname.addTextChangedListener {
//            viewModel.name.set(binding.userNickname.text.toString())
//        }
//        binding.userCorporateEmail.addTextChangedListener {
//            viewModel.organizationEmail.set(binding.userCorporateEmail.text.toString())
//        }
//        binding.userEmployeeNumber.addTextChangedListener{
//            viewModel.organizationEmployeeNumber.set(binding.userEmployeeNumber.text.toString())
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        DebugLog.d(OrganizationSingleton.organization.toString())
//        if (OrganizationSingleton.organization != null) {
//            viewModel.organization.set(OrganizationSingleton.organization)
//            viewModel.organizationName.set(OrganizationSingleton.organization!!.name)
//            viewModel.getDepartment()
//        }
//        if (DepartmentSingleton.department != null) {
//            viewModel.department.set(DepartmentSingleton.department)
//            viewModel.departmentName.set(DepartmentSingleton.department!!.name)
//        }
//        when (OrganizationSingleton.organization?.organizationIdType) {
//            OrganizationIdType.EMAIL -> {
//                viewModel.hasOrganizationEmail.set(true)
//                viewModel.hasEmployeeNumber.set(false)
//                viewModel.isSpaceType.set(false)
//            }
//            OrganizationIdType.EMPLOYEE_NUMBER -> {
//                viewModel.hasOrganizationEmail.set(false)
//                viewModel.hasEmployeeNumber.set(true)
//                viewModel.isSpaceType.set(false)
//            }
//            OrganizationIdType.EMAIL_EMPLOYEE_NUMBER_BOTH -> {
//                viewModel.hasOrganizationEmail.set(true)
//                viewModel.hasEmployeeNumber.set(true)
//                viewModel.isSpaceType.set(false)
//            }
//            else -> {
//                viewModel.hasOrganizationEmail.set(false)
//                viewModel.hasEmployeeNumber.set(false)
//            }
//        }
//    }
//
//    override fun getContext(): Activity {
//        return this
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        OrganizationSingleton.organization = null
//        DepartmentSingleton.department = null
//        UserNameSingleton.name = null
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        if (requestCode == REQUEST_PERMISSIONS) {
//            if (grantResults.isEmpty()) return
//            permissions.forEach {
//                if (it == Manifest.permission.READ_EXTERNAL_STORAGE) {
//                    if (grantResults.first() == PackageManager.PERMISSION_GRANTED) {
//                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { pickImageIntent ->
//                            pickImageIntent.type = "image/*"
//                            startActivityForResult(pickImageIntent, REQUEST_GALLERY_PERMISSION)
//                        }
//                    } else {
//                        showToast("갤러리 이용을 위해 권한 허가가 필요합니다.")
//                        return
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        when (requestCode) {
//            REQUEST_GALLERY_PERMISSION -> {
//                if (data != null && !getRealPathFromUri(data.data!!).isNullOrBlank()) {
//                    CropImage.activity(data.data!!).start(this)
//                }
//            }
//            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
//                val result = CropImage.getActivityResult(data)
//                if (resultCode == Activity.RESULT_OK) {
//                    DebugLog.d("${result.uri}")
//                    binding.viewModel?.profilePath?.set(result.uri.path)
//                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
//                    showToast("이미지를 편집할 수 없습니다. 다시 시도해주세요!!")
//                    DebugLog.e(EditProfileException(result.error.localizedMessage))
//                }
//            }
//            MyPageViewModel.CHARACTER_REQUEST_CODE -> {
//                viewModel.profilePath.set(data?.getSerializableExtra("CharacterId").toString())
//            }
//        }
//    }
//}