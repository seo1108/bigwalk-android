package kr.co.bigwalk.app.customer_center

import android.content.Intent
import android.net.Uri
import androidx.databinding.BaseObservable
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R

class CustomerCenterViewModel(val navigator: BaseNavigator): BaseObservable() {

    fun finishActivity() {
        navigator.finish()
    }

    fun moveToFrequentQuestions() {
        navigator.getContext().startActivity(
            Intent(
                Intent.ACTION_VIEW,
            Uri.parse(navigator.getContext().getString(R.string.frequent_questions_url)))
        )
    }

    fun moveToQna() {
        navigator.getContext().startActivity(
            Intent(
                Intent.ACTION_VIEW,
            Uri.parse(navigator.getContext().getString(R.string.inquiry_google_form_url)))
        )
    }

    fun moveToApplyingForCampaignOpening() {
        navigator.getContext().startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(navigator.getContext().getString(R.string.apply_for_campaign_opening_url)))
        )
    }

    fun moveToAbusingPolicy() {
        navigator.getContext().startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(navigator.getContext().getString(R.string.abusing_policy_url))
            }
        )
    }
}