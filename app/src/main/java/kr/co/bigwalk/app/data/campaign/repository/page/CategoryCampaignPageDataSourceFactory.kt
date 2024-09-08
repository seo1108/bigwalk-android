package kr.co.bigwalk.app.data.campaign.repository.page

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.campaign.dto.ResponseCampaign

class CategoryCampaignPageDataSourceFactory(private val categoryId: Long) : DataSource.Factory<Int, ResponseCampaign>() {

    private val campaignsLiveDataSource = MutableLiveData<PageKeyedDataSource<Int, ResponseCampaign>>()

    override fun create(): DataSource<Int, ResponseCampaign> {
        val campaignsPageDataSource = CategoryCampaignPageDataSource(categoryId)
        campaignsLiveDataSource.postValue(campaignsPageDataSource)
        return campaignsPageDataSource
    }
    
    fun invalidateDataSource() {
        campaignsLiveDataSource.value?.invalidate()
    }
}