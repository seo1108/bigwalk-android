package kr.co.bigwalk.app.data.funding

import java.io.Serializable

data class RequiredToCreateIds(
    val groupId: Long,
    val contestId: Long,
    val sequence: Int
): Serializable