package kr.co.bigwalk.app.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import com.google.firebase.dynamiclinks.ktx.*
import com.google.firebase.ktx.Firebase
import kr.co.bigwalk.app.BuildConfig

interface DeepLinkCallback {
    fun onCreated(url: String)
}

class DeepLinkCreator(val context: Context?) {
    companion object {
        const val SEGMENT_CAMPAIGN = "campaign"
        const val GROUP = "group"
        const val SPACE = "space"
        const val CREW_CAMPAIGN = "crew-campaign"
        const val KEY_ID = "id"
        const val KEY = "key"
        private const val DOMAIN_HOST = "https://bigwalk.page.link"
        private const val APP_STORE_ID = "1495283883"
        private const val AND_PACKAGE_NAME = BuildConfig.APPLICATION_ID
        private const val IOS_BUNDLE_ID = "kr.co.bigwalk.app"
        private const val IOS_MINIMUM_CAMPAIGN = "1.4.0"
        private const val IOS_MINIMUM_GROUP = "1.12.0"
        private const val IOS_MINIMUM_CREW_CAMPAIGN = "1.15.4"
        private const val AND_MINIMUM_CAMPAIGN = 85
        private const val AND_MINIMUM_GROUP = 116
        private const val AND_MINIMUM_CREW_CAMPAIGN = 137
    }
    
    fun getCampaignDeepLink(campaignId: Long, campaignName: String, imagePath: String, callback: DeepLinkCallback) {
        val shortLinkTask = Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse("https://bigwalk.co.kr/$SEGMENT_CAMPAIGN?$KEY_ID=$campaignId")
            domainUriPrefix = DOMAIN_HOST
            androidParameters(AND_PACKAGE_NAME) {
                minimumVersion = AND_MINIMUM_CAMPAIGN
            }
            iosParameters(IOS_BUNDLE_ID) {
                appStoreId = APP_STORE_ID
                minimumVersion = IOS_MINIMUM_CAMPAIGN
            }
            googleAnalyticsParameters {
                source = campaignName
                medium = "$campaignId"
                campaign = "campaign"
            }
            socialMetaTagParameters {
                title = campaignName
                description = "기부에 동참해주세요"
                imageUrl = Uri.parse(imagePath)
            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            callback.onCreated(shortLink.toString())
        }.addOnFailureListener {
            // Error
            // ...
        }
    }
    
    fun getGroupDeepLink(groupId: Long, key: String, groupName: String, imagePath: String, callback: DeepLinkCallback) {
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse("https://bigwalk.co.kr/group/$groupId/invitation?key=$key")
            domainUriPrefix = DOMAIN_HOST
            androidParameters(AND_PACKAGE_NAME) {
                minimumVersion = AND_MINIMUM_GROUP
            }
            iosParameters(IOS_BUNDLE_ID) {
                appStoreId = APP_STORE_ID
                minimumVersion = IOS_MINIMUM_GROUP
            }
            socialMetaTagParameters {
                title = "$groupName 그룹에 초대되셨습니다."
                description = "클릭하여 초대를 수락해주세요!"
                imageUrl = Uri.parse(imagePath)
            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            callback.onCreated(shortLink.toString())
        }.addOnFailureListener {
            DebugLog.d(it.localizedMessage)
        }
    }

    fun getCrewCampaignDeepLink(crewCampaignId: Long, crewCampaignTitle: String, imagePath: String, callback: DeepLinkCallback) {
        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse("https://bigwalk.co.kr/crew-campaign?id=$crewCampaignId")
            domainUriPrefix = DOMAIN_HOST
            androidParameters(AND_PACKAGE_NAME) {
                minimumVersion = AND_MINIMUM_CREW_CAMPAIGN
            }
            iosParameters(IOS_BUNDLE_ID) {
                appStoreId = APP_STORE_ID
                minimumVersion = IOS_MINIMUM_CREW_CAMPAIGN
            }
            socialMetaTagParameters {
                title = "[$crewCampaignTitle] 크루 캠페인을 같이 응원해주세요!"
                imageUrl = Uri.parse(imagePath)
            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            callback.onCreated(shortLink.toString())
        }.addOnFailureListener {
            DebugLog.d(it.localizedMessage)
        }
    }
    
    fun copyToClipboard(text: String) {
        val clipboard: ClipboardManager = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text)
        clipboard.setPrimaryClip(clip)
    }
}