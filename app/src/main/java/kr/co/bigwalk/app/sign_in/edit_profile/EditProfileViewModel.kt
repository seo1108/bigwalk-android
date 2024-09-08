package kr.co.bigwalk.app.sign_in.edit_profile

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.net.toFile
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentManager
import com.kennyc.bottomsheet.BottomSheetListener
import com.kennyc.bottomsheet.BottomSheetMenuDialogFragment
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.user.ProfileType
import kr.co.bigwalk.app.data.user.dto.SignInUserResponse
import kr.co.bigwalk.app.data.user.dto.SignUpUserRequest
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.exception.AlreadyExistsException
import kr.co.bigwalk.app.exception.SignUpException
import kr.co.bigwalk.app.sign_in.organization.OrganizationFormActivity
import kr.co.bigwalk.app.util.*
import kr.co.bigwalk.app.walk.WalkActivity
import org.apache.commons.lang3.StringUtils
import java.io.File


class EditProfileViewModel(val signUpUserRequest: SignUpUserRequest, private val navigator: BaseNavigator, private val supportFragmentManager: FragmentManager) : BaseObservable() {

    var profilePath: ObservableField<String> = ObservableField("${signUpUserRequest.characterId}")
    var duplicatedMessage: ObservableField<String?> = ObservableField("")
    var confirmNickname: ObservableBoolean = ObservableBoolean(false)
    var nickname: ObservableField<String?> = ObservableField (
        if (signUpUserRequest.name.isNullOrBlank())
            ""
        else {
            existsNickname(signUpUserRequest.name!!)
            signUpUserRequest.name
        }
    )
    var corporateMember: ObservableBoolean = ObservableBoolean(false)
    var focusChangeListener = View.OnFocusChangeListener { view, hasFocus ->
        run {
            val editText = view as EditText
            if (!hasFocus) existsNickname(editText.text.toString())
        }
    }
    val editorActionListener = TextView.OnEditorActionListener { view, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            view.clearFocus()
        }
        return@OnEditorActionListener false
    }
    var file: File? = null

    companion object {
        val CHARACTER_PROFILE = 2300
    }

    init {
        var uploadFile: Uri?
        uploadFile = Uri.fromFile(File(profilePath.get()!!))
        DebugLog.d("프로필 경로 : " + uploadFile.toFile().absolutePath)
    }

    fun agreeWithOrganizationMember() {
        corporateMember.set(!corporateMember.get())
    }

    fun finishActivity() {
        navigator.finish()
    }

    private fun existsNickname(nickname: String) {
        validateNickname(nickname)
        if(nickname.length !in 2..10){
            confirmNickname.set(false)
            return
        }
        UserRepository.existsNickname(nickname, object: UserDataSource.ExistsNicknameCallback {
            override fun onExists() {
                showDebugToast("중복되는 닉네임이 존재합니다.")
                duplicatedMessage.set("중복되는 닉네임이 존재합니다.")
                confirmNickname.set(false)
            }

            override fun onDoesNotExists() {
                showDebugToast("사용가능한 닉네임 입니다.")
                duplicatedMessage.set("")
                confirmNickname.set(true)
                signUpUserRequest.name = nickname
            }

            override fun onFailed(reason: String) {
                showDebugToast("닉네임 중복여부를 확인할 수 없습니다. 다시 시도해주세요.")
                duplicatedMessage.set("닉네임 중복여부를 확인할 수 없습니다. 다시 시도해주세요.")
                confirmNickname.set(false)
                DebugLog.e(AlreadyExistsException(reason))
            }

        })
    }

    fun modifyProfileImage() {
        EditProfileActivity.firebaseAnalytics?.logEvent("sign_up_profile_button", Bundle())

        val sheet = BottomSheetMenuDialogFragment.Builder(navigator.getContext())
        sheet.setSheet(R.menu.bottom_sheet_menu)
        sheet.setListener(object : BottomSheetListener {
            override fun onSheetItemSelected(
                cbottomSheet: BottomSheetMenuDialogFragment,
                item: MenuItem?,
                `object`: Any?
            ) {
                when (item?.itemId) {
                    R.id.select_from_album -> {
                        if (isPermissionGranted(navigator.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            requestPermission(navigator.getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                        } else {
                            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also {
                                    pickImageIntent -> navigator.getContext().startActivityForResult(pickImageIntent, REQUEST_GALLERY_PERMISSION)
                            }
                        }
                    }
                    R.id.change_to_default_profile -> {
                        profilePath.set("0")
                        signUpUserRequest.profileType = ProfileType.DEFAULT.type
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

    fun signUp(nickname: String) {
        validateNickname(nickname)
        signUpUserRequest.name = nickname
        if (confirmNickname.get()) {
            if (corporateMember.get()) {
                val intent = Intent(navigator.getContext(), OrganizationFormActivity::class.java)
                intent.putExtra("ProfileFile", getUploadFile())
                intent.putExtra("SignUpUserRequest", signUpUserRequest)
                navigator.getContext().startActivity(intent)
            } else {
                signUp()
            }
        }

    }

    private fun validateNickname(nickname: String?) {
        if (nicknameValidator(nickname)) {
            duplicatedMessage.set("")
            confirmNickname.set(true)
        } else {
            duplicatedMessage.set("닉네임은 최소 2자 이상 최대 10자 이하 입니다.")
            confirmNickname.set(false)
        }
    }

    private fun getUploadFile(): File? {
        var uploadFile: File? = null
        when (profilePath.get()) {
            "0" -> signUpUserRequest.profileCharacterId = 0
            "1" -> signUpUserRequest.profileCharacterId = 1
            "2" -> signUpUserRequest.profileCharacterId = 2
            "3" -> signUpUserRequest.profileCharacterId = 3
            "4" -> signUpUserRequest.profileCharacterId = 4
            "5" -> signUpUserRequest.profileCharacterId = 5
            else -> {
                var fileUri: Uri? = null
                if (profilePath.get()!!.contains("http")) {// 프로필 사진 수정없이 기본정보 수정하는 경우
                    if (file != null) {
                        navigator.getContext().let { fileUri = Uri.fromFile(file) }
                    }
                } else {// 프로필 사진 수정한 경우
                    if (!StringUtils.isBlank(profilePath.get())) fileUri = Uri.fromFile(File(profilePath.get()!!))
                }
                // 임시로 올리 캐릭터를 처리할 때 이렇게 했으나 추후 업로드 부분 로직 확인해야 함
                if (fileUri?.scheme == "file" && profilePath.get() != "5") uploadFile = compressFile(navigator.getContext(), fileUri!!)
            }
        }
        return uploadFile
    }

    private fun signUp() {
        UserRepository.signUp(getUploadFile(), signUpUserRequest, object: UserDataSource.SignUpCallback {
            override fun onSuccess(signInUser: SignInUserResponse?) {
                showDebugToast("가입 완료!! $signUpUserRequest \n업로드 파일 있음:${getUploadFile()?.exists()}")
                PreferenceManager.saveAccessToken(signInUser?.accessToken)

                if (signUpUserRequest.characterId != null) {
                    PreferenceManager.saveCharacter("${signUpUserRequest.characterId}")
                } else {
                    PreferenceManager.saveCharacter("1")
                }
                signUpUserRequest.name?.let { PreferenceManager.saveName(it) }

                val bundle = Bundle()
                bundle.putString("provider", signUpUserRequest.serviceProvider.name)
                EditProfileActivity.firebaseAnalytics?.logEvent("sign_up", bundle)

                val intent = Intent(navigator.getContext(), WalkActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                navigator.getContext().startActivity(intent)
                navigator.finish()
                return
            }

            override fun onExistsNickname() {
                duplicatedMessage.set("중복되는 닉네임이 존재합니다.")
                confirmNickname.set(false)
                return
            }

            override fun onDepartmentError() {
                // 일반 유저는 필요없음
            }

            override fun onFailed(reason: String) {
                showToast("회원가입을 진행할 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(SignUpException(reason))
                return
            }

        })
    }

}