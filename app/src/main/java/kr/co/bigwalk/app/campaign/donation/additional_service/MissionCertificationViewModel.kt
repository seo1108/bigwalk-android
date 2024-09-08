package kr.co.bigwalk.app.campaign.donation.additional_service

import android.app.Activity
import android.content.Intent
import android.media.MediaActionSound
import android.net.Uri
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.campaign.dto.DonationUploadResponse
import kr.co.bigwalk.app.data.campaign.repository.CampaignDataSource
import kr.co.bigwalk.app.data.campaign.repository.CampaignRepository
import kr.co.bigwalk.app.feed.FeedDetailActivity
import kr.co.bigwalk.app.feed.category.FeedByCategoryFragment
import kr.co.bigwalk.app.util.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MissionCertificationViewModel(
    private val activity: Activity,
    val cameraUtil: CameraUtil,
    val missionDonationData: MissionDonationData,
    var uriList: List<Uri>,
    val fromCampaign: Boolean
) : CameraUtil.TakePhotoResultCallback {

    val userName: ObservableField<String> = ObservableField(PreferenceManager.getName())
    val userOrganizationName: ObservableField<String> = ObservableField(PreferenceManager.getOrganizationName())
    val userDepartmentName: ObservableField<String> = ObservableField(PreferenceManager.getDepartmentName())
    val certificatedDate: ObservableField<String> = ObservableField("")
    val isCameraMode: MutableLiveData<Boolean> = MutableLiveData(true)
    val loading: ObservableField<Boolean> = ObservableField(false)
    val isAgreementChecked : ObservableField<Boolean> = ObservableField(false)
    val comment = MutableLiveData<String>()

    init {
        cameraUtil.start(this)
        if (uriList.isNotEmpty()) {
            setPhoto(uriList[0])
        }
    }

    fun getUserInfo(): String {
        return "${userName.get()}\n${userOrganizationName.get()}\n${userDepartmentName.get()}"
    }

    private fun getCurrentDate(): String {
        val calToday = Calendar.getInstance()
        val today = Date(calToday.timeInMillis)
        val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.KOREA)
        return dateFormat.format(today)
    }

    fun finishActivity() {
        activity.finish()
    }

    fun takePhoto() {
        val sound = MediaActionSound();
        sound.play(MediaActionSound.SHUTTER_CLICK)
        cameraUtil.takePhoto()
    }

    fun retryCamera() {
        isCameraMode.value?.let { mode ->
            isCameraMode.value = !mode
            isAgreementChecked.set(false)
        }
    }

    fun changeCameraMode() {
        cameraUtil.changeCameraMode()
    }

    fun galleryAddFile() {
        galleryAddFile(activity, activity.findViewById<ConstraintLayout>(R.id.mission_screenshot_view))
        FirebaseAnalytics.getInstance(activity).logEvent("mission_share", Bundle().apply {
            putString("mission_id", missionDonationData.actionMission.id.toString())
        })
    }

    private fun getScreenshotImageFile(): File {
        val view = activity.findViewById<ConstraintLayout>(R.id.mission_screenshot_view)
        val bitmap = takeScreenShot(view)
        return screenshotBitmapToFile(bitmap)
    }

    fun share() {
        val uri: Uri = getUriFromFile(getScreenshotImageFile())!!
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "image/*"
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri)
        activity.startActivity(Intent.createChooser(sharingIntent, "챌린지 공유"))
    }

    fun uploadCertificationPhoto() {
        if (loading.get()!!) return
        loading.set(true)

        if (uriList.isEmpty()) return

        val files = (activity as MissionCertificationActivity).getImagesFile(uriList)
        if (files.isEmpty()) return

        FirebaseAnalytics.getInstance(activity).logEvent("feed_upload_btn_upload_click", Bundle())
        CampaignRepository
            .donateMission(
                missionDonationData,
                files[0],
                files.getOrNull(1),
                files.getOrNull(2),
                comment.value.orEmpty(),
                object : CampaignDataSource.DonateMissionCallback {
                    override fun onSuccess(donationUploadResponse: DonationUploadResponse) {
                        showToast("업로드 성공!")
                        PreferenceManager.saveLastMission(
                            missionDonationData.actionMission.title,
                            donationUploadResponse.endDate,
                            missionDonationData.campaignId,
                            donationUploadResponse.organizationId
                        )
                        FeedByCategoryFragment.feedChanged.value = 1
                        loading.set(false)

                        PreferenceManager.saveFeedUpload(true, donationUploadResponse.missionId)

                        finishActivity()
                        if (fromCampaign) {
                            //CampaignActivity.transitionTab(1)
                            /*val intent = Intent(activity, FeedDetailActivity::class.java)
                            intent.putExtra(
                                FeedDetailActivity.KEY_CAMPAIGN_ID,
                                missionDonationData.campaignId
                            )
                            intent.putExtra(
                                FeedDetailActivity.KEY_ORGANIZATION_ID,
                                donationUploadResponse.organizationId
                            )
                            activity.startActivity(intent)*/


                        }

                    }

                    override fun onNotFound() {
                        showToast("미션을 찾을 수 없습니다")
                        loading.set(false)
                    }

                    override fun onFailed(reason: String) {
                        showToast("업로드에 실패하였습니다. (error:$reason)")
                        loading.set(false)
                    }

                })


    }

    override fun onTakePhotoResult(uri: Uri) {
        uriList = listOf(uri)
        setPhoto(uri)
    }

    private fun setPhoto(uri: Uri) {
        (activity as MissionCertificationActivity).setDataViewPager(listOf(uri), missionDonationData)
        isCameraMode.value = false
//        certificatedDate.set(getCurrentDate())
    }

    fun showTermsOfFeed() {
        activity.startActivity(Intent(Intent.ACTION_VIEW,
            Uri.parse(activity.getString(R.string.terms_of_feed_url))))
    }

    fun setCheckedState(isChecked: Boolean) {
        isAgreementChecked.set(isChecked)
    }
}