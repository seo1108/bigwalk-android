package kr.co.bigwalk.app.data.feedHome.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.campaign.dto.ResponseCampaign
import kr.co.bigwalk.app.data.feedHome.dto.ChallengeYearPagingResponse

class ChallengeYearPageDataSourceFactory(private val type: String, private val year: String) : DataSource.Factory<Int, ChallengeYearPagingResponse>() {

    private val challengeListDataSource = MutableLiveData<PageKeyedDataSource<Int, ChallengeYearPagingResponse>>()

    override fun create(): DataSource<Int, ChallengeYearPagingResponse> {
        val challengeYearPageDataSource = ChallengeYearPageDataSource(type, year)
        challengeListDataSource.postValue(challengeYearPageDataSource)
        return challengeYearPageDataSource
    }

    fun invalidateDataSource() {
        challengeListDataSource.value?.invalidate()
    }
}