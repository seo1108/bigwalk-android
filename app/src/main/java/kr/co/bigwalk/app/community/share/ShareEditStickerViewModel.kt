package kr.co.bigwalk.app.community.share

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.community.repository.CommunityRepository
import kr.co.bigwalk.app.util.DebugLog

class ShareEditStickerViewModel : ViewModel() {
    val stickerItem = MutableLiveData<List<StickerItem>>()

    fun getGroupShareContents(groupId: Long) {
        CommunityRepository.getGroupShareContents(
            groupId = groupId,
            successCallback = { response ->
                stickerItem.value = response?.stickers?.map {
                    it.toStickerItem(PreferenceManager.getStickerIds(groupId))
                }
                PreferenceManager.saveStickerIds(
                    response?.stickers?.map { it.id } as ArrayList<Int>,
                    groupId
                )
                DebugLog.d(response.toString())
            },
            failCallback = {
                DebugLog.d(it)
            }
        )
    }
}