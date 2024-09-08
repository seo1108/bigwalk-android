package kr.co.bigwalk.app.sign_in

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import com.google.firebase.analytics.FirebaseAnalytics
//import com.kakao.auth.Session
import com.nhn.android.naverlogin.OAuthLogin
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.databinding.ActivitySigninBinding
import kr.co.bigwalk.app.sign_in.facebook.FacebookSignInCallback
import kr.co.bigwalk.app.util.isPermissionGranted
import kr.co.bigwalk.app.walk.WalkActivity
import kr.co.bigwalk.app.walk.alarm.StepManager
import kr.co.bigwalk.app.walk.sensor.StepCallback
import kr.co.bigwalk.app.walk.sensor.WalkSensor
import kr.co.bigwalk.app.walk.sensor.WalkServiceManager


class SignInActivity : AppCompatActivity(), BaseNavigator, StepCallback {

    //private var kakaoSessionCallback: SessionCallback = SessionCallback(this)
    private val facebookCallbackManager: CallbackManager = CallbackManager.Factory.create()
    private val oAuthLogin: OAuthLogin = OAuthLogin.getInstance()
    private lateinit var viewModel: SignInViewModel
    companion object {
        var firebaseAnalytics: FirebaseAnalytics? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivitySigninBinding = DataBindingUtil.setContentView(this, R.layout.activity_signin)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics?.setCurrentScreen(this, "sign_up_view", null)
        firebaseAnalytics?.logEvent("sign_up_view", Bundle())

        LoginManager.getInstance().registerCallback(facebookCallbackManager, FacebookSignInCallback(this))
        //Session.getCurrentSession().addCallback(kakaoSessionCallback)
        oAuthLogin.init(this, getString(R.string.clientId), getString(R.string.clientSecret), getString(R.string.clientName))

        viewModel = SignInViewModel(this)
        binding.viewModel = viewModel

        if (!isPermissionGranted(this, Manifest.permission.ACTIVITY_RECOGNITION)) {
            WalkSensor.stepCallback = this
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) return
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        //Session.getCurrentSession().removeCallback(kakaoSessionCallback)
        LoginManager.getInstance().unregisterCallback(facebookCallbackManager)

        if (!PreferenceManager.getAccessToken().isNullOrBlank()) {
            StepManager.uploadAllWalksAndClear() {
                startWalkService(this, PreferenceManager.getDailyStep(), PreferenceManager.getDonableStep())
            }
        }
    }

    override fun getContext(): Activity {
        return this
    }

    override fun onStepChanged(dailyStep: Int, donableStep: Int) {
        viewModel.onStepChanged()
    }

    private fun moveToHomeActivity() {
        val intent = Intent(this@SignInActivity, WalkActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun startWalkService(context: Context, dailyStep: Int, donableStep: Int) {
        WalkServiceManager(context).syncStepToForeground(dailyStep, donableStep)
    }
}
