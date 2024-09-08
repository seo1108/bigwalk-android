package kr.co.bigwalk.app.dialog

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import androidx.databinding.BaseObservable
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.databinding.DialogUserProfileBinding

class CharacterSelectDialog(
    private val activity: Activity,
    private val profilePath: ObservableField<String>
) : Dialog(activity){
    lateinit var binding: DialogUserProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(LayoutInflater.from(activity),R.layout.dialog_user_profile,null,false)
        setContentView(binding.root)
        binding.viewModel = CharacterSelectViewModel(this, binding, profilePath)
    }
}