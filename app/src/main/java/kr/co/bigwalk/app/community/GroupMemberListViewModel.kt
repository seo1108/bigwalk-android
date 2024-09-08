package kr.co.bigwalk.app.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import gun0912.tedimagepicker.util.ToastUtil
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.community.CrewRequestListResponse
import kr.co.bigwalk.app.data.community.DelegateOwnerRequest
import kr.co.bigwalk.app.data.community.GroupMemberResponse
import kr.co.bigwalk.app.data.community.GroupMemberRole
import kr.co.bigwalk.app.data.community.repository.CommunityRepository
import kr.co.bigwalk.app.util.*

class GroupMemberListViewModel(val groupId: Long, val requestJointCount: Int) : ViewModel() {
    private val _groupMemberList = MutableLiveData<List<GroupMemberResponse>>()
    val groupMemberList: LiveData<List<GroupMemberResponse>> get() = _groupMemberList
    private val _myInfoToGroup = MutableLiveData<GroupMemberResponse>()
    val myInfoToGroup: LiveData<GroupMemberResponse> get() = _myInfoToGroup
    private val _groupInviteDeepLink = MutableLiveData<String>()
    val groupInviteDeepLink: LiveData<String> get() = _groupInviteDeepLink

    private val _delegationComplete = MutableLiveData<Unit>()
    val delegationComplete: LiveData<Unit> get() = _delegationComplete
    private val _kickComplete = MutableLiveData<String>()
    val kickComplete: LiveData<String> get() = _kickComplete
    private val _leaveComplete = MutableLiveData<Unit>()
    val leaveComplete: LiveData<Unit> get() = _leaveComplete
    private val _invalidMember = MutableLiveData<Unit>()
    val invalidMember: LiveData<Unit> get() = _invalidMember

    private val _requestCount = MutableLiveData<Boolean>(requestJointCount > 0)
    val requestCount: LiveData<Boolean> get() = _requestCount

    init {
        fetchGroupMemberList()
    }
    
    fun fetchGroupMemberList() {
        CommunityRepository.getGroupMemberList(
            groupId = groupId,
            successCallback = { memberList ->
                _groupMemberList.value = memberList
                memberList?.find { it.mine }?.let {
                    _myInfoToGroup.value = it
                } ?: run {
                    _invalidMember.value = Unit
                }
            },
            failCallback = { reason ->
                DebugLog.d(Exception(reason).toString())
            })
    }
    
    fun delegateGroupOwner(userId: Long, role: GroupMemberRole) {
        CommunityRepository.delegateGroupOwner(
            groupId = groupId,
            request = DelegateOwnerRequest(userId, role),
            successCallback = {
                _delegationComplete.value = Unit
                fetchGroupMemberList()
            },
            failCallback = {
                ToastUtil.showToast(it)
            }
        )
    }
    
    fun kickGroupMember(member: GroupMemberResponse) {
        CommunityRepository.kickGroupMember(
            groupId = groupId,
            userId = member.userId,
            successCallback = { memberList ->
                _groupMemberList.value = memberList?.filter {
                    !it.mine
                }
                memberList?.find { it.mine }?.let {
                    _myInfoToGroup.value = it
                }
                _kickComplete.value = member.name
            },
            failCallback = {
                ToastUtil.showToast(it)
            }
        )
    }
    
    fun leaveGroup() {
        CommunityRepository.leaveGroup(
            groupId = groupId,
            successCallback = {
                _leaveComplete.value = Unit
                fetchGroupMemberList()
                PreferenceManager.saveGroupId(-1)
                PreferenceManager.saveGroupRank(-1L, groupId)
            },
            failCallback = {
                ToastUtil.showToast(it)
            }
        )
    }
    
    fun setGroupDeepLink(groupId: Long, groupName: String, groupImagePath: String) {
        CommunityRepository.getGroupInviteKey(
            groupId = groupId,
            successCallback = { inviteKey ->
                inviteKey ?: return@getGroupInviteKey
                
                DeepLinkCreator(null).getGroupDeepLink(
                    groupId = groupId,
                    key = inviteKey.key,
                    groupName = groupName,
                    imagePath = groupImagePath,
                    callback = object : DeepLinkCallback {
                        override fun onCreated(url: String) {
                            _groupInviteDeepLink.value = url
                        }
                    })
                
                DebugLog.d(inviteKey.toString())
            },
            failCallback = {
                DebugLog.d(it)
            }
        )
    }
}