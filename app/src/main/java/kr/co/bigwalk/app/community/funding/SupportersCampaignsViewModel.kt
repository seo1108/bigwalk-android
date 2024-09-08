package kr.co.bigwalk.app.community.funding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.community.GroupMemberRole
import kr.co.bigwalk.app.data.community.dto.SupportersCampaignsResponse
import kr.co.bigwalk.app.data.community.repository.CommunityRepository
import kr.co.bigwalk.app.data.funding.repository.FundingRepository
import kr.co.bigwalk.app.util.DebugLog

class SupportersCampaignsViewModel(private val groupId: Long) : ViewModel() {
    private val _myRole = MutableLiveData<GroupMemberRole>()
    val myRole: LiveData<GroupMemberRole> get() = _myRole

    private val _supportersCampaigns = MutableLiveData<Map<Int, SupportersCampaignsResponse>>()
    val supportersCampaigns: LiveData<Map<Int, SupportersCampaignsResponse>> get() = _supportersCampaigns
    private val _clickCampaignId = MutableLiveData<Pair<Long, Boolean>>()
    val clickCampaignId: LiveData<Pair<Long, Boolean>> get() = _clickCampaignId
    private val _clickSequence = MutableLiveData<Int>()
    val clickSequence: LiveData<Int> get() = _clickSequence

    val tooltipVisible = MutableLiveData<Boolean>()

    init {
        getMyRoleFromGroup()
    }

    private fun getMyRoleFromGroup() {
        CommunityRepository.getMyRoleFromGroup(
            groupId = groupId,
            successCallback = { response ->
                if (response != null) {
                    _myRole.value = response.myRole
                }
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    fun getMyLabelList() {
        FundingRepository.getMyLabelList(
            groupId = groupId,
            successCallback = { response ->
                if (response != null) {
                    setSupportersCampaignSequence(response.list)
                }
                DebugLog.d(response.toString())
            },
            failCallback = {
                DebugLog.d(it)
            }
        )
    }

    private fun setSupportersCampaignSequence(list: List<SupportersCampaignsResponse>) {
        val tempMap = mutableMapOf<Int, SupportersCampaignsResponse>()
        list.forEach { listInfo ->
            tempMap[listInfo.sequence] = listInfo
        }
        _supportersCampaigns.value = tempMap
    }

    fun moveToDetailOrCompetition(sequence: Int) {
        val data = supportersCampaigns.value?.get(sequence)
        if (data != null) {
            _clickCampaignId.value = Pair(data.id, data.hasActionMission)
        } else {
            _clickSequence.value = sequence
        }
    }

    fun setTooltipVisible() {
        if (PreferenceManager.haveSeenCrewCampaignGuideTooltip(groupId).not() && PreferenceManager.haveSeenCrewCampaignGuide()) {
            PreferenceManager.saveCrewCampaignGuideTooltip(true, groupId)
            tooltipVisible.value = true
        } else {
            tooltipVisible.value = false
        }
    }

}