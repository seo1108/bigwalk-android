package kr.co.bigwalk.app.data.funding.repository

import kr.co.bigwalk.app.data.funding.dto.LabelCategoryListResponse
import kr.co.bigwalk.app.data.funding.dto.LabelCategoryResponse

object FundingLocalDataSource {

    fun getLabelCategoryList(
        successCallback: (LabelCategoryListResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        val list = mutableListOf<LabelCategoryResponse>()
        list.add(LabelCategoryResponse(id = 2, seq = 6, name = "아이/동물"))
        list.add(LabelCategoryResponse(id = 3, seq = 1, name = "노인"))
        list.add(LabelCategoryResponse(id = 11, seq = 3, name = "지구촌"))
        list.add(LabelCategoryResponse(id = 13, seq = 4, name = "환경"))
        list.add(LabelCategoryResponse(id = 14, seq = 5, name = "장애인"))

        successCallback(LabelCategoryListResponse(list))
    }
}