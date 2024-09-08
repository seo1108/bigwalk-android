package kr.co.bigwalk.app.data.crowd_funding.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import kr.co.bigwalk.app.data.crowd_funding.dto.TotalMyFundingResponse

class TotalMyFundingDataSourceFactory(private val contestId: Long?) : DataSource.Factory<Int, TotalMyFundingResponse>() {

    val liveData = MutableLiveData<TotalMyFundingDataSource>()

    override fun create(): DataSource<Int, TotalMyFundingResponse> {
        val dataSource = TotalMyFundingDataSource(contestId)
        liveData.postValue(dataSource)
        return dataSource
    }

    companion object {
        fun pagedListConfig() = PagedList.Config.Builder()
            .setInitialLoadSizeHint(TotalMyFundingDataSource.PAGE_SIZE)
            .setPageSize(TotalMyFundingDataSource.PAGE_SIZE)
            .setEnablePlaceholders(true)
            .build()
    }
}