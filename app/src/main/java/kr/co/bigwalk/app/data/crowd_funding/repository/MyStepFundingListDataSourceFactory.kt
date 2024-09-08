package kr.co.bigwalk.app.data.crowd_funding.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import kr.co.bigwalk.app.data.crowd_funding.dto.MyFundingByContestResponse

class MyStepFundingListDataSourceFactory(private val contestId: Long) : DataSource.Factory<Int, MyFundingByContestResponse>() {

    val liveData = MutableLiveData<MyStepFundingListDataSource>()

    override fun create(): DataSource<Int, MyFundingByContestResponse> {
        val dataSource = MyStepFundingListDataSource(contestId)
        this.liveData.postValue(dataSource)
        return dataSource
    }

    companion object {
        fun pagedListConfig() = PagedList.Config.Builder()
            .setInitialLoadSizeHint(MyStepFundingListDataSource.PAGE_SIZE)
            .setPageSize(MyStepFundingListDataSource.PAGE_SIZE)
            .setEnablePlaceholders(true)
            .build()
    }
}