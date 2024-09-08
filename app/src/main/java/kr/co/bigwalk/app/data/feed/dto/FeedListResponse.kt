package kr.co.bigwalk.app.data.feed.dto

import kr.co.bigwalk.app.data.organization.Department

data class FeedListResponse(
    val feeds: List<Feed>,
    val totalCount: Int,
    val donationId: Long
)