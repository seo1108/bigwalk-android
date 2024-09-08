package kr.co.bigwalk.app.sign_in

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseActivity
import kr.co.bigwalk.app.databinding.ActivitySelfCertificationBinding
import kr.co.bigwalk.app.util.EventObserver
import kr.co.bigwalk.app.util.OnSingleClickListener
import kr.co.bigwalk.app.util.showAlertDialog

class SelfCertificationActivity : BaseActivity<ActivitySelfCertificationBinding>(R.layout.activity_self_certification) {

    private val selfCertificationViewModel by viewModels<SelfCertificationViewModel>()

    private val mCountDown: CountDownTimer = object : CountDownTimer(179999, 500) {
        override fun onTick(millisUntilFinished: Long) {
            //update the UI with the new count
            val min = millisUntilFinished / 60000
            var sec = millisUntilFinished % 60000 / 1000
            var text = "0${min}:${sec}"
            if (sec < 10) text = "0${min}:0${sec}"
            binding.timer.text = text
        }

        override fun onFinish() {
            //countdown finish
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setToolbar()
        setView()
        bindViewModel()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.aos_icon_20_exit)
            title = "휴대폰 번호 인증"
        }
    }

    private fun setView() {
        with(binding) {
            vm = selfCertificationViewModel
            inputPhoneNumber.addTextChangedListener {
                inputCertificationCode.text?.clear()
            }
            btnSendVerificationCode.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(view: View) {
                    selfCertificationViewModel.sendCertificationCode()
                    if (binding.timer.text.isNotEmpty())
                        FirebaseAnalytics.getInstance(this@SelfCertificationActivity).logEvent("sign_up_btn_resend_number_click", Bundle())
                    else
                        FirebaseAnalytics.getInstance(this@SelfCertificationActivity).logEvent("sign_up_btn_send_number_click", Bundle())
                }
            })
            btnVerification.setOnClickListener {
                selfCertificationViewModel.certifyMobileNumber(inputCertificationCode.text.toString())
            }
        }
    }

    private fun bindViewModel() {
        with(selfCertificationViewModel) {
            isCertifyFocus.observe(this@SelfCertificationActivity, Observer {
                if (it) {
                    binding.inputCertificationCode.run {
                        isFocusableInTouchMode = true
                        requestFocus()
                    }
                    mCountDown.start()
                }
            })
            dialogMsg.observe(this@SelfCertificationActivity, EventObserver {
                showAlertDialog(this@SelfCertificationActivity, it){}
            })
            actionDialogMsg.observe(this@SelfCertificationActivity, EventObserver {
                showAlertDialog(this@SelfCertificationActivity, it, cancelable = false) {
                    val intent = intent.putExtra(KEY_PHONE_NUMBER, binding.inputPhoneNumber.text.toString())
                    setResult(RESULT_OK, intent)
                    finish()
                }
            })
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

    companion object {
        const val KEY_PHONE_NUMBER = "PHONE_NUMBER"
        fun getIntent(context: Context) =
            Intent(context, SelfCertificationActivity::class.java)
    }
}