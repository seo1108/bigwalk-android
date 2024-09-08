package kr.co.bigwalk.app.data.story.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.story.dto.ResponseStory

class StoriesPageDataSourceFactory : DataSource.Factory<Int, ResponseStory>() {

    val storiesLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, ResponseStory>>()

    override fun create(): DataSource<Int, ResponseStory> {
        val storiesPageDataSource = StoryPageDataSource()
        storiesLiveDataSource.postValue(storiesPageDataSource)
        return storiesPageDataSource
    }
}