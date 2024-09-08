package kr.co.bigwalk.app.util

import android.content.Context
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.data.organization.repository.OrganizationDataSource
import kr.co.bigwalk.app.data.organization.repository.OrganizationRepository
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.exception.OrganizationException

class BlackUser(val context: Context) {

    fun showBlackUserMessageAlert(userProfileResponse: UserProfileResponse) {
        userProfileResponse.megaOrganization.let { organization ->
            organization.id ?: return@let
            OrganizationRepository.getOrganization(organization.id!!, object : OrganizationDataSource.GetOrganizationCallback {
                override fun onSuccess(organization: Organization?) {
                    organization?.let { organization ->
                        val clientInfo = "${organization.name}\n${organization.personInChargeName}\n${organization.personInChargePhoneNumber}"
                        showLongToast("${context.getString(R.string.noti_black_user)}${clientInfo}")
                        return
                    }
                    showLongToast("${context.getString(R.string.noti_black_user)}help@bigwalk.co.kr")
                }

                override fun onFailed(reason: String) {
                    DebugLog.e(OrganizationException(reason))
                    showLongToast("${context.getString(R.string.noti_black_user)}help@bigwalk.co.kr")
                }
            })
            return
        }
        showLongToast("${context.getString(R.string.noti_black_user)}help@bigwalk.co.kr")
    }
}