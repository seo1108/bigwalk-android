package kr.co.bigwalk.app.sign_up

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseActivity
import kr.co.bigwalk.app.data.user.dto.SignUpUserRequest
import kr.co.bigwalk.app.databinding.ActivitySignUpBinding
import kr.co.bigwalk.app.sign_up.agreement.AgreementDialogFragment
import kr.co.bigwalk.app.walk.WalkActivity

class SignUpActivity : BaseActivity<ActivitySignUpBinding>(R.layout.activity_sign_up) {

    private val signUpViewModel by viewModels<SignUpViewModel>()
    private val signUpAuth: SignUpUserRequest by lazy { intent.getSerializableExtra(KEY_SIGN_UP_AUTH) as SignUpUserRequest }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportFragmentManager.beginTransaction()
            .replace(R.id.sign_up_container, SelectCharacterFragment.newInstance())
            .commit()

        setToolbar()
        setView()
        bindViewModel()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.aos_icon_20_arrow)
            title = "회원가입"
        }
    }

    private fun setView() {
        with(binding) {
            vm = signUpViewModel
            nextButton.setOnClickListener {
                showNextFragment()
            }
        }
    }

    private fun bindViewModel() {
        with(signUpViewModel) {
            signUpViewModel.fetchSocialAccountInfo(signUpAuth)
            createProgressValue.observe(this@SignUpActivity, Observer {
                ObjectAnimator.ofInt(binding.progress, "progress", it)
                    .setDuration(300)
                    .start()
            })
            registerSuccessEvent.observe(this@SignUpActivity, kr.co.bigwalk.app.util.EventObserver { crewId ->
                val intent = Intent(this@SignUpActivity, WalkActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
//                finish()
            })
        }
    }

    private fun showNextFragment() {
        var nextFragment: Fragment = SelectCharacterFragment.newInstance()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.sign_up_container)
        val transaction = supportFragmentManager.beginTransaction()
        if (currentFragment != null) {
            when (currentFragment) {
                is SelectCharacterFragment -> {
                    nextFragment = SignUpInputInfoFragment.newInstance()
                    FirebaseAnalytics.getInstance(this).logEvent("sign_up_profile_character_button", Bundle())
                }
                is SignUpInputInfoFragment -> {
                    nextFragment = SelectDetailInfoFragment.newInstance()
                    FirebaseAnalytics.getInstance(this).logEvent("sign_up_Basic_info_btn_next_btn", Bundle())
                }
                is SelectDetailInfoFragment -> {
                    FirebaseAnalytics.getInstance(this).logEvent("sign_up_detail_info_btn_start_click", Bundle())
                    val dialog = AgreementDialogFragment.newInstance()
                    dialog.show(supportFragmentManager, dialog::class.java.simpleName)
                    dialog.setFragmentResultListener(AgreementDialogFragment.SUBMIT) { _: String, bundle: Bundle ->
                        signUpViewModel.requestSignUp(signUpAuth)
                    }
                    return
                }
            }
            transaction
                .setCustomAnimations(R.anim.anim_horizon_enter, R.anim.anim_horizon_exit2, R.anim.anim_horizon_enter2, R.anim.anim_horizon_exit)
                .hide(currentFragment)
        }
        transaction
            .setCustomAnimations(R.anim.anim_horizon_enter, R.anim.anim_horizon_exit2, R.anim.anim_horizon_enter2, R.anim.anim_horizon_exit)
            .add(R.id.sign_up_container, nextFragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val currentFragment = supportFragmentManager.findFragmentById(R.id.sign_up_container)
        if (currentFragment != null) {
            when (currentFragment) {
                is SelectCharacterFragment -> {
                    signUpViewModel.setFunctionAndViewForScreen(0)
                    supportActionBar?.title = getString(R.string.character_selection)
                }
                is SignUpInputInfoFragment -> {
                    signUpViewModel.setFunctionAndViewForScreen(1)
                    supportActionBar?.title = getString(R.string.enter_basic_information)
                }
            }
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
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
        private const val KEY_SIGN_UP_AUTH = "SIGN_UP_AUTH"
        fun getIntent(context: Context, signUpUserRequest: SignUpUserRequest) =
            Intent(context, SignUpActivity::class.java).apply {
                putExtra(KEY_SIGN_UP_AUTH, signUpUserRequest)
            }
    }
}