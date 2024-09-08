package kr.co.bigwalk.app.dialog

import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.DialogUserProfileBinding
import kr.co.bigwalk.app.util.showToast

class CharacterSelectViewModel(private val characterSelectDialog: CharacterSelectDialog,
                               private val binding: DialogUserProfileBinding,
                               private val profilePath: ObservableField<String>) : BaseObservable() {
    var selectedImage: String = "0"
    fun closeDialog() {
        characterSelectDialog.dismiss()
    }

    fun clickImage(view: View) {
        when (view.id) {
            R.id.img_character_01 -> {
                selectedImage = "1"
                binding.hoverImgCharacter01.visibility = View.VISIBLE
                binding.hoverImgCharacter02.visibility = View.INVISIBLE
            }
            R.id.img_character_02 -> {
                selectedImage = "2"
                binding.hoverImgCharacter01.visibility = View.INVISIBLE
                binding.hoverImgCharacter02.visibility = View.VISIBLE
            }
        }
    }

    fun clickConfirm() {
        profilePath.set(selectedImage)
        if (profilePath.get() == "0") {
            showToast("캐릭터를 반드시 선택해 주세요!")
            return
        }
        closeDialog()
    }

}