package kr.co.bigwalk.app.feed

import kr.co.bigwalk.app.data.PreferenceManager

fun isMyOrganization(organizationId: Long): Boolean {
    val prefOrganizationId = PreferenceManager.getOrganization()
    if (prefOrganizationId != -1L && prefOrganizationId == organizationId) {
        return true
    }
    return false
}

fun isPublicOrganization(organizationId: Long): Boolean {
    if (organizationId <= 0) {
        return true
    }
    return false
}

/**
 * organizationId -1 공개형 또는 내 그룹 일때 좋아요 활성
 */
fun isEnableLike(organizationId: Long) = isPublicOrganization(organizationId) || isMyOrganization(organizationId)