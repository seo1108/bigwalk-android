package kr.co.bigwalk.app.sign_up.agreement

import android.os.Bundle
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.sign_in.agreement.AgreementActivity

class AgreementDialogViewModel : ViewModel() {
    var agreeWithService = MutableLiveData<Boolean>(false)
    var agreeWithPrivacy = MutableLiveData<Boolean>(false)
    var agreeWithOver14 = MutableLiveData<Boolean>(false)
    var agreeWithMarketing = MutableLiveData<Boolean>(false)
    val isCheckedAll = MediatorLiveData<Boolean>()
    val isPass = MediatorLiveData<Boolean>()

    init {
        isCheckedAll.run {
            addSource(agreeWithService) { checkAllChecked() }
            addSource(agreeWithPrivacy) { checkAllChecked() }
            addSource(agreeWithOver14) { checkAllChecked() }
            addSource(agreeWithMarketing) { checkAllChecked() }
        }
        isPass.run {
            addSource(agreeWithService) { checkPassable() }
            addSource(agreeWithPrivacy) { checkPassable() }
            addSource(agreeWithOver14) { checkPassable() }
        }
    }

    private fun checkAllChecked() {
        isCheckedAll.value =
            agreeWithService.value == true
                && agreeWithPrivacy.value == true
                && agreeWithOver14.value == true
                && agreeWithMarketing.value == true
    }

    private fun checkPassable() {
        isPass.value =
            agreeWithService.value == true
                && agreeWithPrivacy.value == true
                && agreeWithOver14.value == true
    }

    fun agreeWithAllTerms(isChecked: Boolean) {
        PreferenceManager.saveMarketingPushSetting(isChecked)
        val bundle = Bundle()
        bundle.putString("agreement", if (isChecked) "Y" else "N")
        AgreementActivity.firebaseAnalytics?.logEvent("agreement_all", bundle)
    }

    fun agreeWithService(isChecked: Boolean) {
        agreeWithService.value = isChecked
    }

    fun agreeWithPrivacy(isChecked: Boolean) {
        agreeWithPrivacy.value = isChecked
    }

    fun agreeWithOver14(isChecked: Boolean) {
        agreeWithOver14.value = isChecked
    }

    fun agreeWithMarketing(isChecked: Boolean) {
        agreeWithMarketing.value = isChecked
        PreferenceManager.saveMarketingPushSetting(isChecked)
        val bundle = Bundle()
        bundle.putString("agreement", if (isChecked) "Y" else "N")
        AgreementActivity.firebaseAnalytics?.logEvent("agreement_marketing_button", bundle)
    }
}