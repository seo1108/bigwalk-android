package kr.co.bigwalk.app.sign_in.agreement

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.user.dto.SignUpUserRequest
import kr.co.bigwalk.app.databinding.ActivityAgreementBinding
import kr.co.bigwalk.app.sign_in.character.SelectCharacterActivity
import kr.co.bigwalk.app.util.DebugLog

class AgreementActivity : AppCompatActivity(), AgreementNavigator {

    private lateinit var binding : ActivityAgreementBinding
    private lateinit var viewModel : AgreementViewModel

    companion object{
        var firebaseAnalytics: FirebaseAnalytics? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics?.logEvent("agreement_view", Bundle())
        
        binding = DataBindingUtil.setContentView(this, R.layout.activity_agreement)
        viewModel = AgreementViewModel(this)
        binding.viewModel = viewModel

    }

    override fun moveToNext(agreeWithMarketing: Boolean) {
        val request = intent.getSerializableExtra("SignUpUserRequest")
        val signUpUserRequest: SignUpUserRequest = request as SignUpUserRequest
        DebugLog.d(signUpUserRequest.toString())
        signUpUserRequest.marketingAgreement = agreeWithMarketing
        val intent = Intent(this, SelectCharacterActivity::class.java)
        intent.putExtra("SignUpUserRequest", signUpUserRequest)
        startActivity(intent)
    }

    override fun getContext(): Activity {
        return this
    }
}