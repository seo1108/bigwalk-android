package kr.co.bigwalk.app.sign_in.organization

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.organization.Content
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.data.organization.repository.OrganizationDataSource
import kr.co.bigwalk.app.data.organization.repository.OrganizationRepository
import kr.co.bigwalk.app.exception.OrganizationException
import kr.co.bigwalk.app.util.DebugLog

class SelectSearchKeywordViewModel(private val navigator: BaseNavigator, private val organization: Organization): BaseObservable() {
    var contents : ObservableArrayList<Content> = ObservableArrayList()

    init {
        requestSearchKeyword(organization = organization)
    }

    private fun requestSearchKeyword(organization: Organization) {
        OrganizationRepository.getSearchKeyword(organization.id!!, object : OrganizationDataSource.GetSearchKeywordCallback {
            override fun onSuccess(selectableContents: List<Content>?) {
                if (selectableContents.isNullOrEmpty()) {
                    DebugLog.d("키워드 없음.")
                    return
                }
                contents.clear()
                contents.addAll(selectableContents)
                DebugLog.d(contents.toString())
            }

            override fun onFailed(reason: String) {
                DebugLog.e(OrganizationException(reason))
            }

        })
    }

    fun finishActivity() {
        navigator.finish()
    }
}