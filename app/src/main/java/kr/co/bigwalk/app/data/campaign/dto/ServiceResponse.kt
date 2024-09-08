package kr.co.bigwalk.app.data.campaign.dto

data class ServiceResponse(
    val type: String
) {

    fun getServiceTypeName(): String {
        if (type == ServiceType.MISSION.value) {
            return "챌린지"
        } else if (type == ServiceType.VALUE_CONSUMPTION.value) {
            return "가치소비"
        }
        return type
    }
}