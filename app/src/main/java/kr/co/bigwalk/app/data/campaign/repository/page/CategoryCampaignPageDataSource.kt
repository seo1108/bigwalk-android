package kr.co.bigwalk.app.data.campaign.repository.page

import androidx.paging.PageKeyedDataSource
import kr.co.bigwalk.app.data.campaign.dto.ResponseCampaign
import kr.co.bigwalk.app.data.campaign.repository.CampaignDataSource
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.exception.CampaignException
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class CategoryCampaignPageDataSource(private val categoryId: Long) : PageKeyedDataSource<Int, ResponseCampaign>() {

    companion object {
        const val PAGE_SIZE = 20
        const val FIRST_PAGE = 0
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, ResponseCampaign>) {
        CampaignRepository.getCampaignsByCategory(categoryId, FIRST_PAGE, PAGE_SIZE,
            object : CampaignDataSource.GetCampaignsByCategoryCallback {
                override fun onSuccess(campaigns: List<ResponseCampaign>) {
                    System.out.println("카테고리 init $categoryId ${campaigns.size}")
                    callback.onResult(campaigns, null, FIRST_PAGE + 1)
                }

                override fun onFailed(reason: String) {
                    showToast("카테고리별 캠페인 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }
            }
        )
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, ResponseCampaign>) {
        CampaignRepository.getCampaignsByCategory(categoryId, params.key, PAGE_SIZE,
            object : CampaignDataSource.GetCampaignsByCategoryCallback {
                override fun onSuccess(campaigns: List<ResponseCampaign>) {
                    System.out.println("카테고리 after $categoryId ${campaigns.size}")
                    callback.onResult(
                        campaigns,
                        if (params.requestedLoadSize < PAGE_SIZE) null else params.key + 1
                    )
                }

                override fun onFailed(reason: String) {
                    showToast("카테고리별 캠페인 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }
            }
        )
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, ResponseCampaign>) {
        val keyPage = params.key
        CampaignRepository.getCampaignsByCategory(categoryId, keyPage, PAGE_SIZE,
            object : CampaignDataSource.GetCampaignsByCategoryCallback {
                override fun onSuccess(campaigns: List<ResponseCampaign>) {
                    callback.onResult(
                        campaigns,
                        if (params.key > FIRST_PAGE) params.key - 1 else null
                    )
                }

                override fun onFailed(reason: String) {
                    showToast("카테고리별 캠페인 목록을 불러올 수 없습니다. 다시 시도해주세요.")
                    DebugLog.e(CampaignException(reason))
                }
            }
        )
    }
}