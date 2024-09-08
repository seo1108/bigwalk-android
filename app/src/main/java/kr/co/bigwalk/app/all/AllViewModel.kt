package kr.co.bigwalk.app.all

import android.content.Intent
import android.os.Bundle
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.campaign.recent.RecentCampaignActivity
import kr.co.bigwalk.app.crowd_funding.TotalMyFundingActivity
import kr.co.bigwalk.app.customer_center.CustomerCenterActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.my_page.MyPageActivity
import kr.co.bigwalk.app.my_page.MyPageViewModel.Companion.CHARACTER_REQUEST_CODE
import kr.co.bigwalk.app.notification.RealtimeNotification
import kr.co.bigwalk.app.notification.ReceivedNotificationActivity
import kr.co.bigwalk.app.report.Report2Activity
import kr.co.bigwalk.app.setting.AppSettingActivity
import kr.co.bigwalk.app.share.ShareActivity
import kr.co.bigwalk.app.sign_in.character.SelectCharacterActivity
import kr.co.bigwalk.app.util.BlackUser
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast

class AllViewModel(
    private val navigator: BaseNavigator
) : BaseObservable(){

    val profile : ObservableField<UserProfileResponse> = ObservableField()
    val isNotification: ObservableField<Boolean> = RealtimeNotification.getInstance().notification

//    init {
//        requestMyProfile()
//    }

    fun requestMyProfile(){
        UserRepository.getMyProfile(object : UserDataSource.GetMyProfileCallback{
            override fun onSuccess(userProfileResponse: UserProfileResponse) {
                profile.set(userProfileResponse)
                userProfileResponse.campaignNotiAgreement?.let {
                    PreferenceManager.saveCampaignPushSetting(
                        it
                    )
                }
                userProfileResponse.storyNotiAgreement?.let {
                    PreferenceManager.saveStoryPushSetting(
                        it
                    )
                }
                userProfileResponse.rankingNotiAgreement?.let {
                    PreferenceManager.saveRankingPushSetting(
                        it
                    )
                }
                userProfileResponse.marketingNotiAgreement?.let {
                    PreferenceManager.saveMarketingPushSetting(
                        it
                    )
                }
                userProfileResponse.profilePath?.let {
                    PreferenceManager.saveProfilePath(
                        it
                    )
                }
            }

            override fun onBlackUser(userProfileResponse: UserProfileResponse) {
                BlackUser(navigator.getContext()).showBlackUserMessageAlert(userProfileResponse)
                finish()
            }

            override fun onFailed(reason: String) {
                showToast("프로필 정보를 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(UserProfileException(reason))
            }
        })
    }

    fun finish() {
        navigator.finish()
    }

    fun moveToHome(){
        navigator.finish()
        FirebaseAnalytics.getInstance(navigator.getContext()).logEvent("all_home_button", Bundle())
    }

    fun moveUserProfileInformation(){
        val userInformationIntent = Intent(navigator.getContext(), MyPageActivity::class.java)
        navigator.getContext().startActivity(userInformationIntent)
        FirebaseAnalytics.getInstance(navigator.getContext()).logEvent("all_profile_button", Bundle())
    }

    fun moveToCampaign() {
        val intent = Intent(navigator.getContext(), CampaignActivity::class.java)
        navigator.getContext().startActivity(intent)
        FirebaseAnalytics.getInstance(navigator.getContext()).logEvent("all_campaign_button", Bundle())
    }

    fun moveToShare(){
        val intent = Intent(navigator.getContext(), ShareActivity::class.java)
        navigator.getContext().startActivity(intent)
        FirebaseAnalytics.getInstance(navigator.getContext()).logEvent("all_share_button", Bundle())
    }

    fun moveToParticipatedCampaign(){
        val intent = Intent(navigator.getContext(), RecentCampaignActivity::class.java)
        navigator.getContext().startActivity(intent)
        FirebaseAnalytics.getInstance(navigator.getContext()).logEvent("all_donated_campaign_button", Bundle())
    }

    fun moveToParticipatedFunding(){
        val intent = TotalMyFundingActivity.getIntent(navigator.getContext(), null)
        navigator.getContext().startActivity(intent)
        FirebaseAnalytics.getInstance(navigator.getContext()).logEvent("all_my_funding_button_move_click", Bundle())
    }

    fun moveToAppSettings() {
        val intent = Intent(navigator.getContext(), AppSettingActivity::class.java)
        navigator.getContext().startActivity(intent)
        FirebaseAnalytics.getInstance(navigator.getContext()).logEvent("all_app_setting_button", Bundle())
    }

    fun moveToWalkReport() {
        navigator.getContext().startActivity(Report2Activity.getIntent(navigator.getContext()))
        FirebaseAnalytics.getInstance(navigator.getContext()).logEvent("all_report_button", Bundle())
    }

    fun moveToCustomerCenter() {
        val moveToCustomerCenter = Intent(navigator.getContext(), CustomerCenterActivity::class.java)
        navigator.getContext().startActivity(moveToCustomerCenter)
        FirebaseAnalytics.getInstance(navigator.getContext()).logEvent("all_customer_center_button", Bundle())
    }

    fun moveToNotice() {
        val moveToNotice = Intent(navigator.getContext(), ReceivedNotificationActivity::class.java)
        navigator.getContext().startActivity(moveToNotice)
        FirebaseAnalytics.getInstance(navigator.getContext()).logEvent("all_notice_button", Bundle())
    }

    fun setCharacter() {
        val moveToNotice = Intent(navigator.getContext(), SelectCharacterActivity::class.java)
        moveToNotice.putExtra("AllSetCharacter", true)
        navigator.getContext().startActivityForResult(moveToNotice, CHARACTER_REQUEST_CODE)
        FirebaseAnalytics.getInstance(navigator.getContext()).logEvent("all_character_button", Bundle())
    }
}