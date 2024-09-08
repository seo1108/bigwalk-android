package kr.co.bigwalk.app.sign_in.agreement

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.databinding.ObservableBoolean
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager

class AgreementViewModel(private val navigator: AgreementNavigator) {

    var agreeWithAllTerms = ObservableBoolean(false)
    var agreeWithService = ObservableBoolean(false)
    var agreeWithPrivacy = ObservableBoolean(false)
    var agreeWithOver14 = ObservableBoolean(false)
    var agreeWithMarketing = ObservableBoolean(false)

    fun finishActivity() {
        navigator.finish()
    }

    fun agreeWithAllTerms() {
        if (agreeWithAllTerms.get()) {
            agreeWithAllTerms.set(false)
            agreeWithService.set(false)
            agreeWithPrivacy.set(false)
            agreeWithOver14.set(false)
            agreeWithMarketing.set(false)
            PreferenceManager.saveMarketingPushSetting(false)
            val bundle = Bundle()
            bundle.putString("agreement", "N")
            AgreementActivity.firebaseAnalytics?.logEvent("agreement_all", bundle)
        } else {
            agreeWithAllTerms.set(true)
            agreeWithService.set(true)
            agreeWithPrivacy.set(true)
            agreeWithOver14.set(true)
            agreeWithMarketing.set(true)
            PreferenceManager.saveMarketingPushSetting(true)
            val bundle = Bundle()
            bundle.putString("agreement", "Y")
            AgreementActivity.firebaseAnalytics?.logEvent("agreement_all", bundle)
        }
    }

    fun agreeWithService() {
        if (agreeWithService.get()) agreeWithService.set(false) else agreeWithService.set(true)
    }

    fun agreeWithPrivacy() {
        if (agreeWithPrivacy.get()) agreeWithPrivacy.set(false) else agreeWithPrivacy.set(true)
    }

    fun agreeWithOver14() {
        if (agreeWithOver14.get()) agreeWithOver14.set(false) else agreeWithOver14.set(true)
    }

    fun agreeWithMarketing() {
        if (agreeWithMarketing.get()){
            agreeWithMarketing.set(false)
            PreferenceManager.saveMarketingPushSetting(false)
            val bundle = Bundle()
            bundle.putString("agreement", "N")
            AgreementActivity.firebaseAnalytics?.logEvent("agreement_marketing_button", bundle)
        } else {
            agreeWithMarketing.set(true)
            PreferenceManager.saveMarketingPushSetting(true)
            val bundle = Bundle()
            bundle.putString("agreement", "Y")
            AgreementActivity.firebaseAnalytics?.logEvent("agreement_marketing_button", bundle)
        }
    }

    fun moveToNext() {
        if (agreeWithService.get() && agreeWithPrivacy.get() && agreeWithOver14.get()) {
            navigator.moveToNext(agreeWithMarketing.get())
        }
    }

    fun showTermsOfService() {
        navigator.getContext().startActivity(Intent(Intent.ACTION_VIEW,
            Uri.parse(navigator.getContext().getString(R.string.terms_of_service_url))))
    }

    fun showTermsOfPrivacy() {
        navigator.getContext().startActivity(Intent(Intent.ACTION_VIEW,
            Uri.parse(navigator.getContext().getString(R.string.terms_of_privacy_url))))
    }

    fun showTermsOfMarketing() {
        navigator.getContext().startActivity(Intent(Intent.ACTION_VIEW,
            Uri.parse(navigator.getContext().getString(R.string.terms_of_marketing_url))))
    }
}