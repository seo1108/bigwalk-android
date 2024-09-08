package kr.co.bigwalk.app.data.story.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.story.dto.ResponseStory

class MyReservedStoriesPageDataSourceFactory : DataSource.Factory<Int, ResponseStory>() {

    val reservedStoriesLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, ResponseStory>>()

    override fun create(): DataSource<Int, ResponseStory> {
        val reservedStoriesPageDataSource = MyReservedStoriesPageDataSource()
        reservedStoriesLiveDataSource.postValue(reservedStoriesPageDataSource)
        return reservedStoriesPageDataSource
    }
}