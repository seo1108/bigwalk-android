package kr.co.bigwalk.app.campaign.donation

import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.co.bigwalk.app.data.campaign.dto.ServiceType

fun makeBottomSheetDialogFragment(donationData: DonationData): BottomSheetDialogFragment {
    donationData.service?.let { service ->
        when (service.type) {
            ServiceType.MISSION.value -> {
                return DonationStepMissionFragment(donationData)
            }
            else -> {}
        }
    }
    return DonationFragment(donationData)
}