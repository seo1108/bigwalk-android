package kr.co.bigwalk.app.share

import android.Manifest
import android.content.Intent
import android.content.res.TypedArray
import android.net.Uri
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.share.ResponseShare
import kr.co.bigwalk.app.data.share.ResponseShareCampaign
import kr.co.bigwalk.app.data.share.repository.ShareDataSource
import kr.co.bigwalk.app.data.share.repository.ShareRepository
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.extension.ifLet
import kr.co.bigwalk.app.util.*
import java.text.SimpleDateFormat
import java.util.*


class ShareViewModel(
    private val navigator: BaseNavigator
) : BaseObservable() {
    val today: ObservableField<String> = ObservableField(
        SimpleDateFormat(
            "yyyy.MM.dd(EE) HH:mm",
            Locale.KOREA
        ).format(Date())
    )
    val allShare: ObservableField<ResponseShare> = ObservableField()
    val campaignShare: ObservableField<ResponseShareCampaign> = ObservableField()
    val dailySteps = PreferenceManager.getDailyStep().toLong()
    val images: TypedArray = navigator.getContext().resources.obtainTypedArray(R.array.share_background)
    private var count = 0
    val background: ObservableField<Int> = ObservableField(images.getResourceId(count, 0))
    val profile : ObservableField<UserProfileResponse> = ObservableField()
    val deepLink: ObservableField<String> = ObservableField()

    fun getAllShareData() {
        ShareRepository.getAllShareData(object : ShareDataSource.GetAllShareDataCallback {
            override fun onSuccess(responseShare: ResponseShare) {
                allShare.set(responseShare)
            }

            override fun onFailed(reason: String) {
                showToast("공유 정보를 불러올 수 없습니다. 다시 시도해주세요!")
                navigator.finish()
            }
        })
    }

    fun getCampaignShareData(campaignId: Long, campaignName: String?, imageUrl: String?) {
        ShareRepository.getCampaignShareData(
            campaignId,
            object : ShareDataSource.GetCampaignShareDataCallback {
                override fun onSuccess(responseShareCampaign: ResponseShareCampaign) {
                    campaignShare.set(responseShareCampaign)
                }

                override fun onFailed(reason: String) {
                    showToast("공유 정보를 불러올 수 없습니다. 다시 시도해주세요!")
                    navigator.finish()
                }
            })
        ifLet(campaignName, imageUrl) { (campaignName, imageUrl) ->
            DeepLinkCreator(navigator.getContext()).getCampaignDeepLink(
                    campaignId, campaignName, imageUrl,
                    object : DeepLinkCallback {
                        override fun onCreated(url: String) {
                            deepLink.set(url)
                        }
                    })
        }
    }

    fun requestMyProfile(){
        UserRepository.getMyProfile(object : UserDataSource.GetMyProfileCallback {
            override fun onSuccess(userProfileResponse: UserProfileResponse) {
                profile.set(userProfileResponse)
//                showToast(userProfileResponse.profilePath!!)
            }

            override fun onBlackUser(userProfileResponse: UserProfileResponse) {
                BlackUser(navigator.getContext()).showBlackUserMessageAlert(userProfileResponse)
            }

            override fun onFailed(reason: String) {
                showToast("프로필 정보를 불러올 수 없습니다. 다시 시도해주세요.")
                DebugLog.e(UserProfileException(reason))
            }
        })
    }

    fun changeBackground() {
        count++
        if (count >= images.length()) {
            count = 0
        }
        background.set(images.getResourceId(count, 0))
    }

    fun shareScreenshot(channel: String) {
        val view = navigator.getContext().findViewById<ConstraintLayout>(R.id.share_screenshot_view)
        val bitmap = takeScreenShot(view)
        val savedScreenshot = screenshotBitmapToFile(bitmap)
        val uri: Uri = getUriFromFile(savedScreenshot)!!

        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "image/*"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, deepLink.get())
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
        navigator.getContext().startActivity(Intent.createChooser(sharingIntent, "캠페인 공유"))

        if (channel == CAMPAIGN) {
            ShareActivity.firebaseAnalytics?.logEvent("campaign_share", Bundle())
        } else {
            ShareActivity.firebaseAnalytics?.logEvent("all_share", Bundle())
        }
    }

    fun shareDeepLink() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/html"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, deepLink.get())
        navigator.getContext().startActivity(Intent.createChooser(sharingIntent, "캠페인 공유"))
    }

    fun galleryAddFile(channel: String) {
        if (isPermissionGranted(navigator.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            requestPermission(navigator.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            val view = navigator.getContext().findViewById<ConstraintLayout>(R.id.share_screenshot_view)
            saveBitmapToGallery(takeScreenShot(view))
            if (channel == CAMPAIGN) {
                ShareActivity.firebaseAnalytics?.logEvent("campaign_share_save_image", Bundle())
            } else {
                ShareActivity.firebaseAnalytics?.logEvent("all_share_save_image", Bundle())
            }
            showToast("이미지를 성공적으로 저장하였습니다!")
        }
    }

    fun getTodayStepsString(): String {
        val steps = if(dailySteps > 10000) {
            longValueToCommaString(dailySteps / 10000) + "만 " + dailySteps % 10000
        } else {
            longValueToCommaString(dailySteps)
        }
        return "${steps}걸음"
    }

    fun getKcalString(): String {
        return "${dailySteps / 28}Kcal"
    }

    fun finish() {
        navigator.finish()
    }

    companion object {
        const val ALL = "ALL"
        const val CAMPAIGN = "CAMPAIGN"
    }

}