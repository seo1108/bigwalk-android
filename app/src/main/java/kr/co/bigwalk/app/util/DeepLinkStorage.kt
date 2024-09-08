package kr.co.bigwalk.app.util

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.extension.ifLet

interface DeepLinkStorageCallback {
    fun onLoad(deepLinkData: DeepLinkData)
}

data class DeepLinkData(val deepLinkType: DeepLinkType, val campaignId: Long, val groupId: Long, val crewCampaignId: Long, val data: String? = null) {
    companion object {
        const val ID_NULL = -1L
    }
}

class DeepLinkStorage(val activity: Activity) {

    fun save(intent: Intent) {
        FirebaseDynamicLinks.getInstance()
            .getDynamicLink(intent)
            .addOnSuccessListener(activity,
                OnSuccessListener { pendingDynamicLinkData ->
                    if (pendingDynamicLinkData == null) {
                        DebugLog.d("No have dynamic link")
                        return@OnSuccessListener
                    }
                    pendingDynamicLinkData.link?.let { deepLink ->
                        DebugLog.d("deepLink: $deepLink")
                        saveDeepLink(deepLink)
                    }
                })
            .addOnFailureListener(activity) { e -> DebugLog.e(e) }
    }

    fun loadAndClear(callback: DeepLinkStorageCallback) {
        val deepLinkData = PreferenceManager.getDeepLinkData()
        if (deepLinkData.deepLinkType != DeepLinkType.NONE)
            callback.onLoad(deepLinkData)
        PreferenceManager.clearDeepLink()
    }

    private fun saveDeepLink(deepLink: Uri) {
        var id = deepLink.getQueryParameter(DeepLinkCreator.KEY_ID)?.toLongOrNull()
        var camId = deepLink.getQueryParameter(DeepLinkCreator.SEGMENT_CAMPAIGN)?.toLongOrNull()

        try {
            if (deepLink.pathSegments[0] == "space" && deepLink.pathSegments[1] == "group") {

                if (id != null && camId != null) {
                    PreferenceManager.saveDeepLink(DeepLinkType.SPACE_GROUP, camId, id, null)
                } else if (id == null && camId != null) {
                    PreferenceManager.saveDeepLink(
                        DeepLinkType.CAMPAIGN_DETAIL,
                        camId,
                        DeepLinkData.ID_NULL,
                        null
                    )
                } else if (id != null && camId == null) {
                    PreferenceManager.saveDeepLink(
                        DeepLinkType.GROUP_JOIN,
                        null,
                        id,
                        null,
                        deepLink.getQueryParameter(DeepLinkCreator.KEY).orEmpty()
                    )
                }
                /*ifLet(deepLink.getQueryParameter(DeepLinkCreator.KEY_ID)?.toLongOrNull(), deepLink.getQueryParameter(DeepLinkCreator.SEGMENT_CAMPAIGN)?.toLongOrNull()) { (groupId, campaignId) ->
                    if (groupId != null && campaignId != null) {
                        PreferenceManager.saveDeepLink(DeepLinkType.SPACE_GROUP, campaignId, groupId, null)
                    } else if (groupId == null && campaignId != null) {
                        PreferenceManager.saveDeepLink(DeepLinkType.CAMPAIGN_DETAIL, campaignId, DeepLinkData.ID_NULL, null)
                    } else if (groupId != null && campaignId == null) {
                        PreferenceManager.saveDeepLink(DeepLinkType.GROUP_JOIN, null, groupId, null, deepLink.getQueryParameter(DeepLinkCreator.KEY).orEmpty())
                    } else {

                    }

                }*/
            } else {
                if (id == null && camId == null) {
                    // 크루 초대
                    // https://bigwalk.co.kr/group/283/invitation?key=BIGWALKK
                    var key = deepLink.getQueryParameter(DeepLinkCreator.KEY)?.toString()
                    PreferenceManager.saveDeepLink(
                        DeepLinkType.CREW_JOIN,
                        null,
                        deepLink.pathSegments[1].toLong(),
                        null,
                        key
                    )
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().log("DeepLinkStorage error : ${e.stackTrace}")
        }

        /*when (deepLink.pathSegments.firstOrNull()) {
            DeepLinkCreator.GROUP -> { // SAMPLE: https://bigwalk.co.kr/group/{GROUP_ID}/invitation?key={KEY_VALUE}
                deepLink.pathSegments.elementAtOrNull(deepLink.pathSegments.indexOf(DeepLinkCreator.GROUP)+1)?.toLongOrNull()?.let { groupId ->
                    PreferenceManager.saveDeepLink(DeepLinkType.GROUP_JOIN, null, groupId, null, deepLink.getQueryParameter(DeepLinkCreator.KEY).orEmpty())
                }
            }
            DeepLinkCreator.SPACE -> { // SAMPLE: https://bigwalk.co.kr/space/group?id={GROUP_ID}&campaign={CAMPAIGN_ID}
                if (deepLink.pathSegments.elementAtOrNull(deepLink.pathSegments.indexOf(DeepLinkCreator.SPACE)+1) == DeepLinkCreator.GROUP) {
                    ifLet(deepLink.getQueryParameter(DeepLinkCreator.KEY_ID)?.toLongOrNull(), deepLink.getQueryParameter(DeepLinkCreator.SEGMENT_CAMPAIGN)?.toLongOrNull()) { (groupId, campaignId) ->
                        PreferenceManager.saveDeepLink(DeepLinkType.SPACE_GROUP, campaignId, groupId, null)
                    }
                }
            }
            DeepLinkCreator.SEGMENT_CAMPAIGN -> { // SAMPLE: https://bigwalk.co.kr/campaign?id={CAMPAIGN_ID}
                deepLink.getQueryParameter(DeepLinkCreator.KEY_ID)?.toLongOrNull()?.let { campaignId ->
                    PreferenceManager.saveDeepLink(DeepLinkType.CAMPAIGN_DETAIL, campaignId, DeepLinkData.ID_NULL, null)
                }
            }
            DeepLinkCreator.CREW_CAMPAIGN -> { // SAMPLE: https://bigwalk.co.kr/crew-campaign?id={CREW_CAMPAIGN_ID}
                deepLink.getQueryParameter(DeepLinkCreator.KEY_ID)?.toLongOrNull()?.let { crewCampaignId ->
                    PreferenceManager.saveDeepLink(DeepLinkType.CREW_CAMPAIGN, null, null, crewCampaignId)
                }
            }
            else -> {}
        }*/
    }
}