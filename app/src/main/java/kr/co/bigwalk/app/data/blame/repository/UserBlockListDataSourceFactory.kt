package kr.co.bigwalk.app.data.blame.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PagedList
import kr.co.bigwalk.app.data.blame.dto.UserBlockResponse

class UserBlockListDataSourceFactory() : DataSource.Factory<Int, UserBlockResponse>() {

    val liveData = MutableLiveData<UserBlockListDataSource>()

    override fun create(): DataSource<Int, UserBlockResponse> {
        val dataSource = UserBlockListDataSource()
        this.liveData.postValue(dataSource)
        return dataSource
    }

    companion object {
        fun pagedListConfig() = PagedList.Config.Builder()
            .setInitialLoadSizeHint(UserBlockListDataSource.PAGE_SIZE)
            .setPageSize(UserBlockListDataSource.PAGE_SIZE)
            .setEnablePlaceholders(true)
            .build()
    }
}