package kr.co.bigwalk.app.setting

import android.widget.CompoundButton
import androidx.databinding.ObservableBoolean
import com.google.firebase.messaging.FirebaseMessaging
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.notification.NotiType
import kr.co.bigwalk.app.data.notification.UserSetNotificationAgreementRequest
import kr.co.bigwalk.app.data.notification.repository.NotificationDataSource
import kr.co.bigwalk.app.data.notification.repository.NotificationRepository
import kr.co.bigwalk.app.util.showToast


class AppSettingViewModel(private val navigator: BaseNavigator) {

    var isCampaignPushEnabled = ObservableBoolean(PreferenceManager.getCampaignPushSetting())
    var isStoryPushEnabled = ObservableBoolean(PreferenceManager.getStoryPushSetting())
    var isRankingPushEnabled = ObservableBoolean(PreferenceManager.getRankingPushSetting())
    var isWalkPushEnabled = ObservableBoolean(PreferenceManager.getWalkPushSetting())
    var isMarketingPushEnabled = ObservableBoolean(PreferenceManager.getMarketingPushSetting())

    var campaignSwitchListener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                if (isChecked) {
                    isCampaignPushEnabled.set(true)
                    PreferenceManager.saveCampaignPushSetting(true)
                    FirebaseMessaging.getInstance().subscribeToTopic("campaign")
                    saveNotificationSetting(NotiType.CAMPAIGN, true)
                } else {
                    isCampaignPushEnabled.set(false)
                    PreferenceManager.saveCampaignPushSetting(false)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("campaign")
                    saveNotificationSetting(NotiType.CAMPAIGN, false)
                }
            }
        }

    var storySwitchListener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                if (isChecked) {
                    isStoryPushEnabled.set(true)
                    PreferenceManager.saveStoryPushSetting(true)
                    FirebaseMessaging.getInstance().subscribeToTopic("story")
                    saveNotificationSetting(NotiType.STORY, true)
                } else {
                    isStoryPushEnabled.set(false)
                    PreferenceManager.saveStoryPushSetting(false)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("story")
                    saveNotificationSetting(NotiType.STORY, false)
                }
            }
        }

    var rankingSwitchListener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                if (isChecked) {
                    isRankingPushEnabled.set(true)
                    PreferenceManager.saveRankingPushSetting(true)
                    FirebaseMessaging.getInstance().subscribeToTopic("ranking")
                    saveNotificationSetting(NotiType.RANKING, true)
                } else {
                    isRankingPushEnabled.set(false)
                    PreferenceManager.saveRankingPushSetting(false)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("ranking")
                    saveNotificationSetting(NotiType.RANKING, false)
                }
            }
        }

    var walkSwitchListener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                if (isChecked) {
                    isWalkPushEnabled.set(true)
                    PreferenceManager.saveWalkPushSetting(true)
                    FirebaseMessaging.getInstance().subscribeToTopic("walk")
                    saveNotificationSetting(NotiType.WALK, true)
                } else {
                    isWalkPushEnabled.set(false)
                    PreferenceManager.saveWalkPushSetting(false)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("walk")
                    saveNotificationSetting(NotiType.WALK, false)
                }
            }
        }

    var marketingSwitchListener =
        CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                if (isChecked) {
                    isMarketingPushEnabled.set(true)
                    PreferenceManager.saveMarketingPushSetting(true)
                    FirebaseMessaging.getInstance().subscribeToTopic("marketing")
                    saveNotificationSetting(NotiType.MARKETING, true)
                } else {
                    isMarketingPushEnabled.set(false)
                    PreferenceManager.saveMarketingPushSetting(false)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("marketing")
                    saveNotificationSetting(NotiType.MARKETING, false)
                }
            }
        }

    private fun saveNotificationSetting(notiType: NotiType, agreement: Boolean) {
        NotificationRepository.saveNotificationByType(
            notiType.notiType,
            UserSetNotificationAgreementRequest(agreement),
            object : NotificationDataSource.SaveNotificationByTypeCallback {
                override fun onSuccess() {
                    val message = when (notiType) {
                        NotiType.CAMPAIGN -> "캠페인 소식 알림을 " + if (agreement) "받습니다" else "받지 않습니다"
                        NotiType.STORY -> "스토리 소식 알림을 " + if (agreement) "받습니다" else "받지 않습니다"
                        NotiType.RANKING -> "랭킹 소식 알림을 " + if (agreement) "받습니다" else "받지 않습니다"
                        NotiType.WALK -> "걸음 관련 알림을 " + if (agreement) "받습니다" else "받지 않습니다"
                        NotiType.MARKETING -> "마케팅 소식 알림을 " + if (agreement) "받습니다" else "받지 않습니다"
                    }
                    showToast(message)
                }

                override fun onFailed(reason: String) {
                    showToast("알림 수신 설정을 할 수 없습니다. 다시 시도해주세요.")
                }

            })
    }

    fun finish() {
        navigator.finish()
    }

}