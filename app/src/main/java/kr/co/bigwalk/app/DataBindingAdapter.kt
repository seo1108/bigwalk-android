package kr.co.bigwalk.app

import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.Gravity
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.synnapps.carouselview.CarouselView
import kr.co.bigwalk.app.campaign.adapter.*
import kr.co.bigwalk.app.campaign.category.CampaignByCategoryFragment
import kr.co.bigwalk.app.campaign.ranking.RankingPlusActivity
import kr.co.bigwalk.app.community.info.RankChangeStatus
import kr.co.bigwalk.app.data.ImageResource
import kr.co.bigwalk.app.data.NetworkState
import kr.co.bigwalk.app.data.campaign.GradeIcon
import kr.co.bigwalk.app.data.campaign.dto.CampaignContentResponse
import kr.co.bigwalk.app.data.campaign.dto.Category
import kr.co.bigwalk.app.data.campaign.dto.ResponseCampaign
import kr.co.bigwalk.app.data.community.PropensityType
import kr.co.bigwalk.app.data.feed.dto.ChallengeImageItem
import kr.co.bigwalk.app.data.feed.dto.Feed
import kr.co.bigwalk.app.data.feedHome.dto.ChallengeInfoResponse
import kr.co.bigwalk.app.data.feed.dto.FeedCategory
import kr.co.bigwalk.app.data.feedHome.dto.MissionCampaignResponse
import kr.co.bigwalk.app.data.feedNotification.dto.FeedNotificationResponse
import kr.co.bigwalk.app.data.notification.NotiTypeId
import kr.co.bigwalk.app.data.organization.*
import kr.co.bigwalk.app.data.story.dto.StoryContentImageResponse
import kr.co.bigwalk.app.dialog.BottomSheetRecyclerAdapter
import kr.co.bigwalk.app.exception.YouTubePlayerException
import kr.co.bigwalk.app.extension.*
import kr.co.bigwalk.app.feed.adapter.ChallengeImageAdapter
import kr.co.bigwalk.app.feed.adapter.FeedCategoryViewPagerAdapter
import kr.co.bigwalk.app.feed.adapter.FeedChallengeRecyclerAdapter
import kr.co.bigwalk.app.feed.category.FeedByCategoryFragment
import kr.co.bigwalk.app.feed_home.adapter.*
import kr.co.bigwalk.app.my_page.adapter.SelectOrganizationDepartmentListRecyclerAdapter
import kr.co.bigwalk.app.my_page.adapter.SelectOrganizationListRecyclerAdapter
import kr.co.bigwalk.app.sign_in.organization.adapter.SelectDepartmentListRecyclerAdapter
import kr.co.bigwalk.app.sign_in.organization.adapter.SelectRecyclerAdapter
import kr.co.bigwalk.app.sign_in.organization.adapter.SelectSearchKeywordAdapter
import kr.co.bigwalk.app.util.DebugLog
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


@BindingAdapter("loadImage")
fun loadImage(view: ImageView, url: String?) {
    DebugLog.d("loadImage 프로필 로드 경로 $url")
    if (url.isNullOrEmpty()) {
        DebugLog.d("loadImage null 프로필 로드 경로 $url")
        view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.img_default_48))
    } else {
        DebugLog.d("loadImage not null 프로필 로드 경로 $url")
        if (url == "0" || url == "1" || url == "2" || url == "3" || url == "4" || url == "5") {   //캐릭터 이미지
            when (url) {
                "1" -> {
                    Glide.with(view.context)
                        .asBitmap()
                        .load(R.drawable.character_profile_color_01)
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.circleCropTransform())
                        .into(view)
                }
                "2" -> {
                    Glide.with(view.context)
                        .asBitmap()
                        .load(R.drawable.character_profile_color_02)
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.circleCropTransform())
                        .into(view)
                }
                "3" -> {
                    Glide.with(view.context)
                        .asBitmap()
                        .load(R.drawable.character_profile_color_03)
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.circleCropTransform())
                        .into(view)
                }
                "4" -> {
                    Glide.with(view.context)
                        .asBitmap()
                        .load(R.drawable.character_profile_color_04)
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.circleCropTransform())
                        .into(view)
                }
                "5" -> {
                    Glide.with(view.context)
                        .asBitmap()
                        .load(R.drawable.character_profile_color_05)
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.circleCropTransform())
                        .into(view)
                }
                else -> Glide.with(view.context)
                    .asBitmap()
                    .load(R.drawable.img_default_48)
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.circleCropTransform())
                    .into(view)
            }
        } else { //일반 사진이나 프로필
            if (MimeTypeMap.getFileExtensionFromUrl(url).contains(".gif")) {
                Glide.with(view.context)
                    .asGif()
                    .load(url)
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.img_default_88)
                    .error(R.drawable.img_default_88)
                    .into(view)
            } else {
                Glide.with(view.context)
                    .asBitmap()
                    .load(url)
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.img_default_48)
                    .error(R.drawable.img_default_48)
                    .into(view)
            }
        }
    }
}

@BindingAdapter("loadImageWhite")
fun loadImageWhite(view: ImageView, url: String?) {
    DebugLog.d("loadImageWhite 프로필 로드 경로 $url")
    if (url.isNullOrEmpty()) {
        view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.img_default_48))
    } else {
        if (url == "0" || url == "1" || url == "2" || url == "3" || url == "4" || url == "5") {   //캐릭터 이미지
            when (url) {
                "1" -> {
                    Glide.with(view.context)
                        .asBitmap()
                        .load(R.drawable.img_character_01)
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.circleCropTransform())
                        .into(view)
                }
                "2" -> {
                    Glide.with(view.context)
                        .asBitmap()
                        .load(R.drawable.img_character_02)
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.circleCropTransform())
                        .into(view)
                }
                "3" -> {
                    Glide.with(view.context)
                        .asBitmap()
                        .load(R.drawable.img_character_03)
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.circleCropTransform())
                        .into(view)
                }
                "4" -> {
                    Glide.with(view.context)
                        .asBitmap()
                        .load(R.drawable.img_character_04)
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.circleCropTransform())
                        .into(view)
                }
                "5" -> {
                    Glide.with(view.context)
                        .asBitmap()
                        .load(R.drawable.img_character_05)
                        .apply(RequestOptions.centerCropTransform())
                        .apply(RequestOptions.circleCropTransform())
                        .into(view)
                }
                else -> Glide.with(view.context)
                    .asBitmap()
                    .load(R.drawable.img_default_48)
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.circleCropTransform())
                    .into(view)
            }
        } else { //일반 사진이나 프로필
            if (MimeTypeMap.getFileExtensionFromUrl(url).contains(".gif")) {
                Glide.with(view.context)
                    .asGif()
                    .load(url)
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.img_default_88)
                    .error(R.drawable.img_default_88)
                    .into(view)
            } else {
                Glide.with(view.context)
                    .asBitmap()
                    .load(url)
                    .apply(RequestOptions.centerCropTransform())
                    .apply(RequestOptions.circleCropTransform())
                    .placeholder(R.drawable.img_default_48)
                    .error(R.drawable.img_default_48)
                    .into(view)
            }
        }
    }
}

@BindingAdapter("loadFullImage")
fun loadFullImage(view: ImageView, url: String?) {
    DebugLog.d("loadFullImage 프로필 로드 경로 $url")
    if (url.isNullOrEmpty()) {
        view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.img_default_312))
        return
    } else {
        Glide.with(view.context)
            .asBitmap()
            .load(url)
            .placeholder(R.drawable.img_default_312)
            .error(R.drawable.img_default_312)
            .override(Target.SIZE_ORIGINAL)
            .into(view)
    }
}

@BindingAdapter("loadFullImage")
fun loadFullImage(view: ImageView, resId: Int?) {
    DebugLog.d("loadFullImage 프로필 로드 경로 $resId")
    if (resId == null) {
        view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.img_default_312))
        return
    } else {
        Glide.with(view.context)
            .asBitmap()
            .load(resId)
            .placeholder(R.drawable.img_default_312)
            .error(R.drawable.img_default_312)
            .override(Target.SIZE_ORIGINAL)
            .into(view)
    }
}

@BindingAdapter("loadFullImage")
fun loadFullImage(view: ImageView, uri: Uri?) {
    DebugLog.d("loadFullImage 프로필 로드 경로 $uri")
    if (uri == null) {
        view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.img_default_312))
        return
    } else {
        Glide.with(view.context)
            .asBitmap()
            .load(uri)
            .placeholder(R.drawable.img_default_312)
            .error(R.drawable.img_default_312)
            .override(Target.SIZE_ORIGINAL)
            .into(view)
    }
}

@BindingAdapter("loadFullImageUrl")
fun loadFullImageUrl(view: ImageView, url: String?) {
    DebugLog.d("loadFullImage 프로필 로드 경로 $url")
    if (url.isNullOrEmpty()) {
        view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.img_default_312))
        return
    } else {
        Glide.with(view.context)
            .load(url)
            .placeholder(R.drawable.img_default_312)
            .error(R.drawable.img_default_312)
            .override(Target.SIZE_ORIGINAL)
            .into(view)
    }
}

@BindingAdapter("loadCampaignCreateImage")
fun loadCampaignCreateImage(view: ImageView, url: String?) {
    DebugLog.d("loadCampaignCreateImage 프로필 로드 경로 $url")
    if (url.isNullOrEmpty()) {
        return
    } else {
        Glide.with(view.context)
            .load(url)
            .placeholder(R.drawable.radius_25_placeholder)
            .error(R.drawable.radius_25_placeholder)
            .override(Target.SIZE_ORIGINAL)
            .into(view)
    }
}

@BindingAdapter("loadGif", "startGif")
fun loadGif(view: ImageView, resId: Int?, start: Boolean) {
    if (resId == null) {
        view.setImageDrawable(ContextCompat.getDrawable(view.context, R.drawable.img_default_312))
        return
    } else {
        if (start.not()) return
        Glide.with(view.context)
            .load(resId)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource is GifDrawable) {
                        resource.setLoopCount(2)
                        resource.registerAnimationCallback(object : Animatable2Compat.AnimationCallback() {
                            override fun onAnimationEnd(drawable: Drawable?) {
                                super.onAnimationEnd(drawable)
                            }
                        })
                    }
                    return false
                }
            })
            .into(view)
    }
}

@BindingAdapter("clearFocusAndDispatch")
fun clearOnFocusAndDispatch(view: EditText, listener: View.OnFocusChangeListener?) {
    view.onFocusChangeListener = listener
}

@BindingAdapter("editorActionListener")
fun editorActionListener(view: EditText, listener: TextView.OnEditorActionListener?) {
    view.setOnEditorActionListener(listener)
}

@BindingAdapter("bindItem")
fun bindSelectableItem(recyclerView: RecyclerView, organizationItems: ObservableList<OrganizationItem>) {
    if (organizationItems.isNullOrEmpty()) return
    val adapter = recyclerView.adapter as SelectRecyclerAdapter
    adapter.setItems(organizationItems)
    adapter.notifyDataSetChanged()
}


@BindingAdapter("bindOrganizationItem")
fun bindOrganizationItem(recyclerView: RecyclerView, organizations: ObservableList<Organization>) {
    if (organizations.isNullOrEmpty()) return
    val adapter = recyclerView.adapter as SelectOrganizationListRecyclerAdapter
    adapter.setItems(organizations)
    adapter.notifyDataSetChanged()
}


@BindingAdapter("bindItem")
fun bindDepartmentItem(recyclerView: RecyclerView, departments: ObservableArrayList<Department>) {
    if (departments.isNullOrEmpty()) return
    var adapter = recyclerView.adapter
    when {
        recyclerView.adapter is SelectOrganizationDepartmentListRecyclerAdapter -> {
            adapter = recyclerView.adapter as SelectOrganizationDepartmentListRecyclerAdapter
            adapter.setItems(departments)
            adapter.notifyDataSetChanged()
        }
        recyclerView.adapter is SelectDepartmentListRecyclerAdapter -> {
            adapter = recyclerView.adapter as SelectDepartmentListRecyclerAdapter
            adapter.setItems(departments)
            adapter.notifyDataSetChanged()
        }
    }
}

@BindingAdapter("bindItem")
fun bindSearchItem(recyclerView: RecyclerView, contentItems: ObservableList<Content>) {
    if (contentItems.isNullOrEmpty()) return
    val adapter = recyclerView.adapter as SelectSearchKeywordAdapter
    adapter.setItems(contentItems)
    adapter.notifyDataSetChanged()
}

@BindingAdapter("bindItem")
fun bindChallengeHomeItem(recyclerView: RecyclerView, items: ObservableList<ChallengeInfoResponse>) {
    if (items.isNullOrEmpty()) return
    var adapter = recyclerView.adapter
    when (recyclerView.adapter) {
        is ChallengeHomeActiveAdapter -> {
            adapter = recyclerView.adapter as ChallengeHomeActiveAdapter
            adapter.setItems(items)
            adapter.notifyDataSetChanged()
        }
        is ChallengeHomeParticipatedAdapter -> {
            adapter = recyclerView.adapter as ChallengeHomeParticipatedAdapter
            adapter.setItems(items)
            adapter.notifyDataSetChanged()
        }
        is ChallengeHomeClosedAdapter -> {
            adapter = recyclerView.adapter as ChallengeHomeClosedAdapter
            adapter.setItems(items)
            adapter.notifyDataSetChanged()
        }
        is ChallengeActiveListAdapter -> {
            adapter = recyclerView.adapter as ChallengeActiveListAdapter
            adapter.setItems(items)
            adapter.notifyDataSetChanged()
        }
        is ChallengeFirstYearAdapter -> {
            adapter = recyclerView.adapter as ChallengeFirstYearAdapter
            adapter.setItems(items)
            adapter.notifyDataSetChanged()
        }
        is ChallengeSecondYearAdapter -> {
            adapter = recyclerView.adapter as ChallengeSecondYearAdapter
            adapter.setItems(items)
            adapter.notifyDataSetChanged()
        }
        is ChallengeThirdYearAdapter -> {
            adapter = recyclerView.adapter as ChallengeThirdYearAdapter
            adapter.setItems(items)
            adapter.notifyDataSetChanged()
        }
        is ChallengeYearPagingAdapter -> {
            adapter = recyclerView.adapter as ChallengeYearPagingAdapter
            adapter.setItems(items)
            adapter.notifyDataSetChanged()
        }
    }
}

@BindingAdapter("bindImageItem")
fun bindChallengeImageItem(recyclerView: RecyclerView, items: ObservableList<ChallengeImageItem>) {
    val adapter = recyclerView.adapter as ChallengeImageAdapter
    adapter.setItems(items)
    adapter.notifyDataSetChanged()
}

@BindingAdapter("bindFeedItem")
fun bindFeedItem(recyclerView: RecyclerView, items: ObservableList<Feed>) {
    val adapter = recyclerView.adapter as FeedChallengeRecyclerAdapter
    //adapter.addItems(items)
    adapter.setItems(items)
    adapter.notifyDataSetChanged()
}

@BindingAdapter("onNavigationItemSelected")
fun onNavigationItemSelected(
    view: BottomNavigationView,
    listener: BottomNavigationView.OnNavigationItemSelectedListener
) {
    view.setOnNavigationItemSelectedListener(listener)
}

@BindingAdapter("bindItem")
fun bindCampaignContentItem(
    recyclerView: RecyclerView,
    contents: ObservableList<CampaignContentResponse>
) {
    if (contents.isNullOrEmpty()) return
    val adapter = recyclerView.adapter as CampaignContentRecyclerAdapter
    adapter.setItems(contents)
}

@BindingAdapter("youtubePlayerListener")
fun addYoutubePlayerListener(view: YouTubePlayerView, listener: AbstractYouTubePlayerListener) {
    view.addYouTubePlayerListener(listener)
}

@BindingAdapter("setVideoId")
fun setVideoId(view: YouTubePlayerView, videoPath: String) {
    view.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
        override fun onReady(youTubePlayer: YouTubePlayer) {
            youTubePlayer.cueVideo(videoPath.substring(videoPath.lastIndexOf("=") + 1), 0f)
        }

        override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
            super.onError(youTubePlayer, error)
            DebugLog.e(YouTubePlayerException(error.name))
        }

        override fun onStateChange(
            youTubePlayer: YouTubePlayer,
            state: PlayerConstants.PlayerState
        ) {
            super.onStateChange(youTubePlayer, state)
            DebugLog.d(state.name)
        }

    })
}

@BindingAdapter("loadContentsImages")
fun loadContentsImages(view: CarouselView, contents: List<ImageResource>) {
    view.setImageListener { position, imageView ->
        Glide.with(view.context)
            .asBitmap()
            .load(contents[position].imagePath)
            .placeholder(R.drawable.img_default_88)
            .into(imageView!!)
    }
    view.pageCount = contents.size
    view.indicatorGravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
}

@BindingAdapter("loadStoryImages")
fun loadStoryImages(view: CarouselView, contents: List<StoryContentImageResponse>) {
    view.setImageListener { position, imageView ->
        Glide.with(view.context)
            .asBitmap()
            .load(contents[position].url)
            .centerCrop()
            .placeholder(R.drawable.img_default_88)
            .into(imageView!!)
    }
    view.pageCount = contents.size
    view.indicatorGravity = Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM
}

@BindingAdapter("loadStoryViewPagerImages")
fun loadStoryViewPagerImages(view: ImageView, url: String?) {
    url?.let {
        Glide.with(view.context)
            .asBitmap()
            .load(url)
            .centerCrop()
            .placeholder(R.drawable.img_default_88)
            .into(view)
    }
}

@BindingAdapter("seekBarChangeListener")
fun setOnSeekBarChangeListener(view: SeekBar, listener: SeekBar.OnSeekBarChangeListener) {
    view.setOnSeekBarChangeListener(listener)
}

/* barData:
*  axisValue: x좌표 이름
*  average: 평균 */
@BindingAdapter("axisValue", "barData", "average")
fun setBarChartData(barChart: BarChart, axisValue: ObservableList<String>, barData: BarData?, average: Long) {
    val xAxis = barChart.xAxis
    xAxis.position = XAxis.XAxisPosition.BOTTOM
    xAxis.valueFormatter = IndexAxisValueFormatter(axisValue)
    val rightAxis = barChart.axisRight
    barChart.axisLeft.setDrawZeroLine(false)
    barChart.axisLeft.setDrawGridLines(false)
    barChart.axisLeft.setDrawLabels(false)
    barChart.axisLeft.setDrawAxisLine(false)
    barChart.axisRight.isEnabled = false
    xAxis.setDrawGridLines(false)
    rightAxis.setDrawGridLines(false)
    barChart.description.isEnabled = false
    barChart.isDragEnabled = false
    barChart.isScaleXEnabled = false
    barChart.isScaleYEnabled = false
    barChart.data = barData?.apply { barWidth = 0.35f }
//    barChart.setFitBars(true)
    barChart.axisLeft.axisMinimum = 0f // 바와 하단 라인 사이 간격 제거
    barChart.legend.isEnabled = false // 차트 제목 제거

    // 평균 선 및 값
    if (average > 0L) { // 평균 선을 보여줄 필요가 없는 차트에서는 xml에서 0 입력
        val averageLine = LimitLine(average.toFloat())
        averageLine.lineWidth = 1f
        averageLine.lineColor = Color.parseColor("#999999")
        averageLine.enableDashedLine(16f, 16f, 4f)
        averageLine.textColor = Color.parseColor("#222222")
        averageLine.textSize = 12f
        averageLine.isEnabled = true
        barChart.axisLeft.removeAllLimitLines()
        barChart.axisLeft.addLimitLine(averageLine)
    }

    barChart.setNoDataText("") // 데이터 없을 경우 기본 문구 제거
    barChart.animateY(800)
    barData?.let { barChart.groupBars(xAxis.axisMinimum, 0.26f, 0.02f) }
    barChart.invalidate()
}

@BindingAdapter("switchListener")
fun setOnSwitchChangeListener(view: CompoundButton, listener: CompoundButton.OnCheckedChangeListener) {
    view.setOnCheckedChangeListener(listener)
}

@BindingAdapter("loadBottomSheetList")
fun loadBottomSheetList(recyclerView: RecyclerView, items: ObservableList<String>) {
    if (items.isNullOrEmpty()) return
    val adapter = recyclerView.adapter as BottomSheetRecyclerAdapter
    adapter.setItems(items)
}

@BindingAdapter("loadDrawable")
fun loadDrawable(imageView: ImageView, drawable: Int) {
    imageView.setImageResource(drawable)
}

@BindingAdapter("loadPopularCampaigns")
fun loadPopularCampaigns(recyclerView: RecyclerView, popularCampaigns: ObservableList<ResponseCampaign>) {
    if (popularCampaigns.isNullOrEmpty()) return
    val adapter = recyclerView.adapter as CampaignPopularAdapter
    adapter.setItems(popularCampaigns)
}

@BindingAdapter("loadParticipatedCampaigns")
fun loadParticipatedCampaigns(recyclerView: RecyclerView, participatedCampaigns: ObservableList<ResponseCampaign>) {
    if (participatedCampaigns.isNullOrEmpty()) return
    val adapter = recyclerView.adapter as ParticipatedCampaignAdapter
    adapter.setItems(participatedCampaigns)
}

@BindingAdapter("loadRecentCampaigns")
fun loadRecentCampaigns(recyclerView: RecyclerView, recentCampaigns: ObservableList<ResponseCampaign>) {
    if (recentCampaigns.isNullOrEmpty()) return
    val adapter = recyclerView.adapter as RecentCampaignRecyclerAdapter
    adapter.setItems(recentCampaigns)
}

@BindingAdapter("addTabs")
fun addTabs(tabLayout: TabLayout, categories: ObservableList<Category>) {
    for (category in categories) {
        tabLayout.addTab(tabLayout.newTab().setText(category.name))
    }
    tabLayout.setMargin(0, 0, 8.dpToPx(), 0)
}

@BindingAdapter("addFeedTabs")
fun addFeedTabs(tabLayout: TabLayout, categories: ObservableList<FeedCategory>) {
    for (category in categories) {
        tabLayout.addTab(tabLayout.newTab().setText(category.name))
    }
    tabLayout.setMargin(0, 0, 8.dpToPx(), 0)
}

@BindingAdapter("app:tabMargin")
fun tabMargin(tabLayout: TabLayout, tabSpace: Int) {
    tabLayout.setMargin(0, 0, tabSpace.dpToPx(), 0)
}

@BindingAdapter("setViewPager")
fun setViewPager(viewPager: ViewPager, fragments: ObservableList<CampaignByCategoryFragment>) {
    val adapter = viewPager.adapter as CampaignCategoryViewPagerAdapter
    adapter.setItems(fragments)
}

@BindingAdapter("setFeedViewPager")
fun setFeedViewPager(viewPager: ViewPager, fragments: ObservableList<FeedByCategoryFragment>) {
    val adapter = viewPager.adapter as FeedCategoryViewPagerAdapter
    adapter.setItems(fragments)
}

@BindingAdapter("optionHighLight")
fun setTextViewBold(textView: TextView, isBold: Boolean) {
    if (isBold) {
        TextViewCompat.setTextAppearance(textView, R.style.NanumSquareB20MainFont)
    } else {
        TextViewCompat.setTextAppearance(textView, R.style.NanumSquareR20MainFont)
    }
}

@BindingAdapter("displayCommentDate")
fun setDisplayCommentDate(textView: TextView, date: String?) {
    if (date.isNullOrEmpty()) return
    try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
        val date = format.parse(date)
        date?.let {
            val cal = Calendar.getInstance()
            cal.time = it
            val now = Calendar.getInstance()
            now.time = Date()
            val diffSec: Long = (now.timeInMillis - cal.timeInMillis) / 1000
            val diffDays = diffSec.toInt() / (24 * 60 * 60) //일자수 차이
            when (diffDays) {
                0 -> {
                    if (cal[Calendar.DATE] != now[Calendar.DATE]) {
                        textView.text = "1일 전"
                    } else {
                        val format = SimpleDateFormat("a HH:mm", Locale.KOREA)
                        textView.text = format.format(cal.time)
                    }
                }
                in 1..7 -> {
                    textView.text = "$diffDays" + "일 전"
                }
                in 8..100 -> {
                    textView.text = "${diffDays / 7}주 전"
                }
                else -> {
                    val format = SimpleDateFormat("yyyy.MM", Locale.KOREA)
                    textView.text = format.format(cal.time)
                }
            }
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
}


@BindingAdapter("setNotificationIcon")
fun setNotificationIcon(imageView: ImageView, notiTypeId: Int) {
    when (notiTypeId) {
        NotiTypeId.FEED_LIKE.id -> {
            imageView.setImageDrawable(ContextCompat.getDrawable(imageView.context, R.drawable.aos_icon_16_alret_like))
        }
        NotiTypeId.FEED_COMMENT.id -> {
            imageView.setImageDrawable(ContextCompat.getDrawable(imageView.context, R.drawable.aos_icon_16_alret_comment))
        }
        else -> {
        }
    }
}

@BindingAdapter("setNotificationTitle")
fun setNotificationTitle(textView: TextView, data: FeedNotificationResponse) {
    when (data.notiType) {
        NotiTypeId.FEED_LIKE.id -> {
            textView.text = "[${data.createdBy}]님이\n회원님의 챌린지를 좋아합니다."
        }
        NotiTypeId.FEED_COMMENT.id -> {
            textView.text = "[${data.createdBy}]님이\n회원님의 챌린지에 댓글을 남겼습니다."
        }
        else -> {
        }
    }
}

@BindingAdapter("setMissionDate")
fun setMissionDate(textView: TextView, data: MissionCampaignResponse) {
    try {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
        val startDate = format.parse(data.startDate)
        val endDate = format.parse(data.endDate)
        ifLet(startDate, endDate) { (start, end) ->
            val startCal = Calendar.getInstance()
            startCal.time = start
            val endCal = Calendar.getInstance()
            endCal.time = end
            textView.text = startCal.toDisplayString() + "~" + endCal.toDisplayString()
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
}

@BindingAdapter("setMissionEndDate")
fun setMissionEndDate(textView: TextView, endDate: String?) {
    if (endDate.isNullOrEmpty()) return
    textView.text = getDisplayEndDate(endDate)
}

@BindingAdapter("app:onThrottleFirstClick", "app:onThrottleFirstClickState", requireAll = false)
fun onThrottleFirstClick(
    view: View,
    onClickListener: View.OnClickListener,
    state: NetworkState?
) {
    view.setOnClickListener { v ->
        if (state != NetworkState.LOADING) {
            onClickListener.onClick(v)
        }
    }
}

@BindingAdapter("setTitle")
fun setTitle(textView: TextView, title: String?) {
    if (!title.isNullOrEmpty()) {
        textView.text = title
    }
}

@BindingAdapter("loadGradeIcon")
fun loadGradeIcon(view: ImageView, iconData: GradeIcon?) {
    DebugLog.d("loadGradeIcon 프로필 로드 경로 ${iconData?.category} ${iconData?.level}")
    val resource = if (iconData?.category == RankingPlusActivity.Category.TOTAL.categoryName) {
        R.array.ranking_grade_img
    } else {
        R.array.ranking_today_grade_img
    }
    val images: TypedArray = view.context.resources.obtainTypedArray(resource)
    val index = iconData?.level?.toInt()?.minus(1)
    Glide.with(view.context)
        .asBitmap()
        .load(images.getResourceId(index ?: 0, 0))
        .into(view)
    images.recycle()
}

@BindingAdapter("visibleIf")
fun visibleIf(view: View, value: Boolean) {
    view.isVisible = value
}

@BindingAdapter("invisibleIf")
fun invisibleIf(view: View, value: Boolean) {
    view.isInvisible = value
}

@BindingAdapter("goneIf")
fun goneIf(view: View, value: Boolean) {
    view.isGone = value
}

@BindingAdapter("loadGroupRankChangeImage")
fun loadGroupRankChangeImage(view: ImageView, status: RankChangeStatus) {
    when (status) {
        RankChangeStatus.UP -> view.setImageResource(R.drawable.aos_icon_ranking_up)
        RankChangeStatus.DOWN -> view.setImageResource(R.drawable.aos_icon_ranking_down)
        RankChangeStatus.NONE -> view.setImageResource(R.color.transparent)
    }
}

@BindingAdapter("clearTextOnClick")
fun clearTextOnClick(view: View, value: EditText) {
    view.setOnClickListener {
        value.text.clear()
    }
}

@BindingAdapter("setVisibleToFocus")
fun setVisibleToFocus(view: View, value: EditText) {
    value.setOnFocusChangeListener { v, hasFocus ->
        view.isVisible = hasFocus
    }
}

@BindingAdapter("clipToOutline")
fun clipToOutline(view: View, value: Boolean) {
    view.clipToOutline = value
}

@BindingAdapter("setBackgroundColor")
fun setBackgroundColor(view: View, value: Int) {
    view.backgroundTintList = ColorStateList.valueOf(value)
}

@BindingAdapter("setTextChangedListener")
fun setTextChangedListener(view: EditText, value: Boolean) {
    view.addTextChangedListener {
        val replaceText = view.text.toString().replace("\n\n", "\n")
        if (view.text.toString() == replaceText) return@addTextChangedListener
        view.setText(replaceText)
        view.setSelection(view.length())
    }
}

@BindingAdapter("setTextChangedListener")
fun setTextChangedListener(view: EditText, maxEnterCount: Int) {
    val inputEnterKey = "\n".repeat(maxEnterCount + 1)
    val replaceEnterKey = "\n".repeat(maxEnterCount)
    view.addTextChangedListener {
        val replaceText = view.text.toString().replace(inputEnterKey, replaceEnterKey)
        if (view.text.toString() == replaceText) return@addTextChangedListener
        view.setText(replaceText)
        view.setSelection(view.length())
    }
}

@BindingAdapter("setTextChangedListener", "min")
fun setTextChangedListener(view: TextView, inputView: EditText, min: Int) {
    inputView.addTextChangedListener {
        view.text = inputView.text.length.toString() + "자"
        view.setTextColor(Color.parseColor(if (inputView.text.length >= min) "#4379e7" else "#f15060"))
    }
}

@BindingAdapter("setBackgroundColorByPropensity")
fun setBackgroundColorByPropensity(view: View, value: PropensityType) {
    view.backgroundTintList = ColorStateList.valueOf(
        when (value) {
            PropensityType.HEALTH -> Color.parseColor("#fff5ef")
            PropensityType.ENVIRONMENT -> Color.parseColor("#e7f6ff")
            PropensityType.RUNNING -> Color.parseColor("#f4fbf2")
            PropensityType.NONE -> Color.parseColor("#fff5ef")
        }
    )
}