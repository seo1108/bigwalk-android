package kr.co.bigwalk.app.story

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ShareCompat
import androidx.databinding.BaseObservable
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.campaign.detail.CampaignDetailActivity
import kr.co.bigwalk.app.data.story.dto.CampaignReservationRequest
import kr.co.bigwalk.app.data.story.dto.ResponseOpenStory
import kr.co.bigwalk.app.data.story.dto.ResponseStory
import kr.co.bigwalk.app.data.story.dto.StoryContentType
import kr.co.bigwalk.app.data.story.repository.MyReservedStoriesPageDataSourceFactory
import kr.co.bigwalk.app.data.story.repository.StoriesPageDataSourceFactory
import kr.co.bigwalk.app.data.story.repository.StoryDataSource
import kr.co.bigwalk.app.data.story.repository.StoryRepository
import kr.co.bigwalk.app.exception.StoryException
import kr.co.bigwalk.app.story.reserved.MyReservedStoryActivity
import kr.co.bigwalk.app.util.*

class StoryViewModel(val navigator: BaseNavigator) : BaseObservable() {

    private var pageSize = 20
    private val storyDataSourceFactory = StoriesPageDataSourceFactory()
    private val config = PagedList.Config.Builder().setPageSize(pageSize).setEnablePlaceholders(true).build()
    val stories: LiveData<PagedList<ResponseStory>> = LivePagedListBuilder(storyDataSourceFactory, config).build()
    val reservedStoriesCount: ObservableField<ResponseOpenStory> = ObservableField()

    private val myReservedDataSourceFactory = MyReservedStoriesPageDataSourceFactory()
    val myReservedStories: LiveData<PagedList<ResponseStory>> = LivePagedListBuilder(myReservedDataSourceFactory, config).build()

    fun share(story: ResponseStory) {
        if (story.storyContentType == StoryContentType.VIDEO) {
            ShareCompat.IntentBuilder.from(navigator.getContext())
                .setType("text/*")
                .setText(story.videoPath)
                .setSubject(navigator.getContext().getString(R.string.app_name))
                .startChooser()
        } else {
            Glide.with(navigator.getContext())
                .asBitmap()
                .load(story.storyContentImages[0].url)
                .into(object : CustomTarget<Bitmap?>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap?>?
                    ) {
                        val file = screenshotBitmapToFile(resource)
                        val uri: Uri = getUriFromFile(file)!!

                        ShareCompat.IntentBuilder.from(navigator.getContext())
                            .setType("image/*")
                            .setText(story.title)
                            .setSubject(navigator.getContext().getString(R.string.app_name))
                            .setStream(uri)
                            .startChooser()
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        //do nothing
                    }
                })
        }
        val bundle = Bundle()
        bundle.putString("story_id", story.id.toString())
        CampaignActivity.firebaseAnalytics?.logEvent("story_share", bundle)
    }

    fun moveToCampaign(story: ResponseStory) {
        story.campaign?.id?.let {
            val intent = Intent(navigator.getContext(), CampaignDetailActivity::class.java)
            intent.putExtra("campaign_id", it)
            navigator.getContext().startActivity(intent)
        }
    }

    fun finishActivity() {
        navigator.finish()
    }

    fun reserveCampaign(campaignId: Long, storyId: Long, reservation: Boolean) {
        StoryRepository.reserveCampaign(
            CampaignReservationRequest(
                campaignId,
                storyId,
                reservation
            ),
            object : StoryDataSource.ReserveCampaignCallback {
                override fun onSuccess() {
                    if (reservation) {
                        showToast("캠페인 예약에 성공하였습니다.")
                        showDebugToast("캠페인 예약에 성공하였습니다.")
                    } else {
                        showToast("캠페인 예약을 취소하였습니다.")
                        showDebugToast("캠페인 예약을 취소하였습니다.")
                    }
                    stories.value?.dataSource?.invalidate()
                    myReservedStories.value?.dataSource?.invalidate()
                    getMyReservedStoriesCount()
                }

                override fun onFailed(reason: String) {
                    showToast("캠페인 예약을 진행할 수 없습니다. 다시 시도해주세요!")
                    showDebugToast("캠페인 예약을 진행할 수 없습니다. 다시 시도해주세요!")
                    DebugLog.e(StoryException(reason))
                }
            })
    }

    fun moveToMyReservedStories() {
        if (reservedStoriesCount.get()?.reservationCount != null && reservedStoriesCount.get()?.reservationCount!! > 0) {
            val intent = Intent(navigator.getContext(), MyReservedStoryActivity::class.java)
            navigator.getContext().startActivity(intent)
        } else {
            showToast("예약한 포스트가 없습니다.")
        }
    }

    fun getMyReservedStoriesCount() {
        StoryRepository.getMyReservedStoriesCount(object :StoryDataSource.GetMyReservedStoriesCountCallback {
            override fun onSuccess(responseOpenStory: ResponseOpenStory) {
                reservedStoriesCount.set(responseOpenStory)
            }

            override fun onFailed(reason: String) {
                showToast("예약한 포스트 개수를 불러올 수 없습니다. 다시 시도해주세요!")
                reservedStoriesCount.set(ResponseOpenStory(0))
            }

        })
    }

}