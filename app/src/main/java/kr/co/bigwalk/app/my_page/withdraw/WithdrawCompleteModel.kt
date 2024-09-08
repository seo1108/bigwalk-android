package kr.co.bigwalk.app.my_page.withdraw

import android.app.Activity
import android.content.Intent
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.exception.WithdrawUserException
import kr.co.bigwalk.app.sign_in.SignInActivity
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast
import kr.co.bigwalk.app.walk.sensor.WalkService

class WithdrawCompleteModel(
    private val activity: Activity?,
    private val navigator: BaseNavigator
) : BaseObservable(){

    fun finishActivity() {
        val intent = Intent(activity, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        navigator.getContext().startActivity(intent)
    }
}