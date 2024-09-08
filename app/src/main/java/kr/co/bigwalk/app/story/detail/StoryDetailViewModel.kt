package kr.co.bigwalk.app.story.detail

import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.ObservableField
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.story.dto.ResponseStory
import kr.co.bigwalk.app.data.story.dto.StoryContentImageResponse
import kr.co.bigwalk.app.data.story.dto.StoryContentType
import kr.co.bigwalk.app.data.story.repository.StoryDataSource
import kr.co.bigwalk.app.data.story.repository.StoryRepository
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showToast
import java.util.*

open class StoryDetailViewModel(private val fragment: BottomSheetDialogFragment, private val campaignId: Long, private val donatedSteps: Long) {

    lateinit var bottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    var story: ObservableField<ResponseStory> = ObservableField()
    var title: ObservableField<String> = ObservableField()

    init{
        story.set(ResponseStory(0, "", "", StoryContentType.IMAGES, Arrays.asList(StoryContentImageResponse(0, "")), "", false, "", null, false))
        requestStory()
    }

    private fun requestStory() {
        StoryRepository.getStory(campaignId, object : StoryDataSource.GetStoryCallback {
            override fun onSuccess(story: ResponseStory) {
                DebugLog.d("캠페인 상세: $story")
                this@StoryDetailViewModel.story.set(story)
                title.set(getShowingName(PreferenceManager.getName(), donatedSteps))
            }

            override fun onFailed(reason: String) {
                showToast("상세 포스트를  수 없습니다. 다시 시도해주세요!")
            }
        })
    }

    fun confirm() {
        fragment.dismiss()
    }

    private fun getShowingName(name: String, donatedSteps: Long): String {
        var showingName = "우리의"
        if (donatedSteps > 0) {
            showingName = name + "의"
        }
        return showingName
    }

}