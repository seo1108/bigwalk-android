package kr.co.bigwalk.app.campaign

import androidx.lifecycle.MutableLiveData

object CampaignDonateDataManager {
    val updateCampaignDonateData = MutableLiveData<Triple<Long, Long, Long>>()
}