package kr.co.bigwalk.app.data.organization.space

data class SpaceUserRequest(
    val organizationId: Long,
    val departmentId: Long
) {
    var value1: String = ""
    var value2: String = ""
    var value3: String = ""
    var value4: String = ""
    var value5: String = ""

    fun toSpaceUserRequest(content1: String, content2: String, content3: String, content4: String, content5: String) {
        value1 = content1
        value2 = content2
        value3 = content3
        value4 = content4
        value5 = content5
    }
}