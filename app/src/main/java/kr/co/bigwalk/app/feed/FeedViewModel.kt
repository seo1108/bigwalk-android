package kr.co.bigwalk.app.feed

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableField
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.feed.dto.FeedCategory
import kr.co.bigwalk.app.data.feed.dto.FeedInfoResponse
import kr.co.bigwalk.app.data.organization.Department
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.data.organization.repository.OrganizationDataSource
import kr.co.bigwalk.app.data.organization.repository.OrganizationRepository
import kr.co.bigwalk.app.exception.OrganizationException
import kr.co.bigwalk.app.feed.category.FeedByCategoryFragment
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showLongToast
import kr.co.bigwalk.app.util.showToast

class FeedViewModel(
    val navigator: BaseNavigator,
    val campaignId: Long,
    val organizationId: Long,
    val sortType: String
) : BaseObservable() {

    val feedInfoLiveData: MutableLiveData<FeedInfoResponse> = MutableLiveData()
    val feedCategories: ObservableList<FeedCategory> = ObservableArrayList()
    val feedFragments: ObservableList<FeedByCategoryFragment> = ObservableArrayList()
    val isPublic: ObservableField<Boolean> = ObservableField(false)

    companion object {
        fun getFeedCategoryType(value: Int): FeedCategoryType {
            return when (value) {
                FeedCategoryType.ALL.value -> FeedCategoryType.ALL
                FeedCategoryType.ME.value -> FeedCategoryType.ME
                FeedCategoryType.DEPARTMENT.value -> FeedCategoryType.DEPARTMENT
                FeedCategoryType.MY_DEPARTMENT.value -> FeedCategoryType.MY_DEPARTMENT
                FeedCategoryType.USER.value -> FeedCategoryType.USER
                else -> FeedCategoryType.ALL
            }
        }
    }
    enum class FeedCategoryType(val value: Int) {
        ALL(0),
        ME(1),
        DEPARTMENT(2),
        MY_DEPARTMENT(3),
        USER(4);
    }

    init {
        if (isPublicOrganization(organizationId)) {
            feedCategories.add(FeedCategory(FeedCategoryType.ALL, -1, navigator.getContext().getString(R.string.all)))

            val isEnableLike = isEnableLike(organizationId)
            for (category in feedCategories) {
                feedFragments.add(FeedByCategoryFragment.newInstance(navigator, campaignId, category, isEnableLike, organizationId, sortType))
            }
            isPublic.set(true)
        }else {
            isPublic.set(false)
            requestGetOrganization { organizationName ->
                requestGetDepartment(organizationName)
            }
        }

    }

    fun finishActivity() {
        navigator.finish()
    }

    private fun requestGetOrganization(callback: (String) -> Unit) {
        OrganizationRepository.getOrganization(organizationId, object : OrganizationDataSource.GetOrganizationCallback {
            override fun onSuccess(selectableOrganizations: Organization?) {
                callback.invoke(selectableOrganizations?.name ?: navigator.getContext().getString(R.string.all))
            }

            override fun onFailed(reason: String) {
                showLongToast("기업 정보를 가져올 수 없습니다.")
                DebugLog.e(OrganizationException(reason))
            }
        })
    }

    private fun requestGetDepartment(organizationName: String) {

        OrganizationRepository.getDepartments(organizationId, object : OrganizationDataSource.GetDepartmentsCallback {
            override fun onSuccess(selectableDepartments: List<Department>?) {

                feedCategories.add(FeedCategory(FeedCategoryType.ALL, -1, organizationName))

                selectableDepartments?.map {
                    DebugLog.d("selectableDepartments = $selectableDepartments")
                    FeedCategory(FeedCategoryType.DEPARTMENT, it.id, it.name)
                }?.let {
                    feedCategories.addAll(it)
                }

                val isEnableLike = isEnableLike(organizationId)
                for (category in feedCategories) {
                    DebugLog.d("selectableDepartments 1= $category")
                    feedFragments.add(FeedByCategoryFragment.newInstance(navigator, campaignId, category, isEnableLike, organizationId, sortType))
                }

//                feedInfoLiveData.postValue(feedInfo)
            }
            override fun onFailed(reason: String) {
//                navigator.finish()
                showToast("부서 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(OrganizationException(reason))
            }

        })
    }

}