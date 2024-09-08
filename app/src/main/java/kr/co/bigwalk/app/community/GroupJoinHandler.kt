package kr.co.bigwalk.app.community

import androidx.lifecycle.MutableLiveData
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.CodeType
import kr.co.bigwalk.app.data.ResultCodeState
import kr.co.bigwalk.app.data.community.repository.CommunityRepository

class GroupJoinHandler {
    val inviteResult = MutableLiveData<ResultCodeState>()

    fun checkPossibleJoinGroup(invitedGroupId: Long, key: String?) {
        if (invitedGroupId >= 0) {
            key?.let {
                CommunityRepository.joinGroupDry(
                    groupId = invitedGroupId,
                    successCallback = { response ->
                        response ?: return@joinGroupDry
                        inviteResult.postValue(ResultCodeState(
                            true,
                            CodeType.SUCCESS_CODE.code,
                            BigwalkApplication.context?.let { context ->
                                String.format(context.getString(R.string.suggest_group_join), response.name)
                            }
                        ))
                    }, failCallback = {
                        inviteResult.postValue(it)
                    }
                )
            }
        }
    }
}