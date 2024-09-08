package kr.co.bigwalk.app.data.community.repository

import kr.co.bigwalk.app.data.community.CrewRequestListResponse
import kr.co.bigwalk.app.data.community.CrewRequestResponse

object CommunityLocalDataSource {

    fun getCrewRequestList(
        groupId: Long,
        successCallback: (CrewRequestListResponse?) -> Unit,
        failCallback: (String) -> Unit
    ) {
        val list = mutableListOf<CrewRequestResponse>()
        list.add(CrewRequestResponse(id = 1L, userName = "진이요", answer = "답변이요", question = "질문이요", profilePath = "https://bigwalk.s3.ap-northeast-2.amazonaws.com/user/profile_image/126243-RUIxEf.jpeg"))
        list.add(CrewRequestResponse(id = 2L, userName = "진이요2", answer = "답변이요2", question = "질문이요2", profilePath = "https://bigwalk.s3.ap-northeast-2.amazonaws.com/user/profile_image/126243-RUIxEf.jpeg"))

        successCallback(CrewRequestListResponse(list))
    }
}