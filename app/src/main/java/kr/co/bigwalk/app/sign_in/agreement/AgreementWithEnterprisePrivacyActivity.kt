package kr.co.bigwalk.app.sign_in.agreement

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_agreement_with_enterprise_privacy.*
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.organization.Organization

class AgreementWithEnterprisePrivacyActivity : AppCompatActivity(), BaseNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val organization = intent.getSerializableExtra("organization")
        if(organization is Organization){
            setContentView(R.layout.activity_agreement_with_enterprise_privacy)
            tv_content.text = getContent(organization);
        }
    }

    override fun getContext(): Activity {
        return this
    }

    private fun getContent(organization: Organization): String {
        val content = getContext().getString(R.string.agreement_with_enterprise_privacy_content)
        return content.replace("\$기업명\$", organization.name!!)
    }

    fun finishActivity(v: View) {
        finish()
    }
}