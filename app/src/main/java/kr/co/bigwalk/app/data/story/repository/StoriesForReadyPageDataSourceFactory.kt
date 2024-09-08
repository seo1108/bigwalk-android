package kr.co.bigwalk.app.data.story.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.story.dto.Story

class StoriesForReadyPageDataSourceFactory : DataSource.Factory<Int, Story>() {

    private val storiesLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, Story>>()

    override fun create(): DataSource<Int, Story> {
        val storiesPageDataSource =
            StoryForReadyPageDataSource()
        storiesLiveDataSource.postValue(storiesPageDataSource)
        return storiesPageDataSource
    }
}