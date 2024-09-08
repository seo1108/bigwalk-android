package kr.co.bigwalk.app.my_page

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.data.organization.repository.OrganizationDataSource
import kr.co.bigwalk.app.data.organization.repository.OrganizationRepository
import kr.co.bigwalk.app.exception.OrganizationException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class SelectOrganizationViewModel(private val navigator: BaseNavigator) : BaseObservable(){
    var organizations : ObservableList<Organization> = ObservableArrayList<Organization>()

    init {
        requestOrganizations()
    }

    fun finishActivity(){
        navigator.finish()
    }


    private fun requestOrganizations() {
        OrganizationRepository.getOrganizations(object : OrganizationDataSource.GetOrganizationsCallback {
            override fun onSuccess(selectableOrganizations: List<Organization>?) {
                if (selectableOrganizations.isNullOrEmpty()) {
                    showToast("선택 가능한 기업 목록이 없습니다.")
                    navigator.finish()
                    return
                }
                organizations.clear()
                organizations.addAll(selectableOrganizations)
            }

            override fun onFailed(reason: String) {
                DebugLog.e(OrganizationException(reason))
                showToast("기업 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                navigator.finish()
            }

        })
    }
}