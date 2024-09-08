package kr.co.bigwalk.app.story.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.story.dto.ResponseStory
import kr.co.bigwalk.app.data.story.dto.StoryContentType
import kr.co.bigwalk.app.databinding.ItemRecyclerStoryImagesBinding
import kr.co.bigwalk.app.databinding.ItemRecyclerStoryVideoBinding
import kr.co.bigwalk.app.exception.YouTubePlayerException
import kr.co.bigwalk.app.story.StoryViewModel
import kr.co.bigwalk.app.util.DebugLog
import org.apache.commons.lang3.StringUtils

class StoryRecyclerAdapter(private val lifecycle: Lifecycle, private val viewModel: StoryViewModel)
    : PagedListAdapter<ResponseStory, StoryRecyclerAdapter.BaseViewHolder<*>>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {

        return when (viewType) {
            TYPE_VIDEO -> StoryVideoViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_recycler_story_video,
                    parent,
                    false
                ), lifecycle, viewModel
            )
            TYPE_IMAGE -> StoryImagesViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_recycler_story_images,
                    parent,
                    false
                )
                , viewModel
            )
            else -> StoryImagesViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_recycler_story_images,
                    parent,
                    false
                )
                , viewModel
            )
        }
    }

    abstract class BaseViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: T)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is StoryImagesViewHolder -> getItem(position)?.let { holder.bind(it) }
            is StoryVideoViewHolder -> getItem(position)?.let { holder.bind(it) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)?.storyContentType) {
            StoryContentType.IMAGES -> TYPE_IMAGE
            StoryContentType.VIDEO -> TYPE_VIDEO
            else -> TYPE_IMAGE
        }
    }

    class StoryImagesViewHolder(
        private val binding: ItemRecyclerStoryImagesBinding,
        private val viewModel: StoryViewModel
    ) : BaseViewHolder<ResponseStory>(binding.root) {
        override fun bind(item: ResponseStory) {
            binding.story = item

            val adapter = StoryViewPagerRecyclerAdapter()
            binding.viewpagerStory.adapter = adapter
            binding.viewpagerStory.registerOnPageChangeCallback(viewpagerCallback)
            adapter.setItems(item.storyContentImages)
            binding.indicatorStory.createDotPanel(item.storyContentImages.size, R.drawable.bg_indicator_feed_selector)

            binding.itemStoryDescription.setOnClickListener {
                binding.itemStoryDescription.maxLines = Integer.MAX_VALUE
                binding.itemStoryDescription.ellipsize = null
            }
            binding.viewModel = viewModel
            binding.itemStoryReservation.setOnClickListener {
                item.campaign?.id?.let { campaignId -> viewModel.reserveCampaign(campaignId, item.id, !item.reserved) }
            }
        }

        private val viewpagerCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.indicatorStory.selectDot(position)
            }
        }

    }

    class StoryVideoViewHolder(
        private val binding: ItemRecyclerStoryVideoBinding,
        private val lifecycle: Lifecycle,
        private val viewModel: StoryViewModel
    ) : BaseViewHolder<ResponseStory>(binding.root) {

        private val youTubePlayerListener: AbstractYouTubePlayerListener =
            object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    if (StringUtils.isBlank(binding.story?.videoPath)) return
                    DebugLog.d(binding.story?.videoPath?.let { parseVideoIdFromUrl(it) })
                    binding.story?.videoPath?.let { youTubePlayer.cueVideo(parseVideoIdFromUrl(it), 0f) }
                }

                override fun onError(
                    youTubePlayer: YouTubePlayer,
                    error: PlayerConstants.PlayerError
                ) {
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

            }

        override fun bind(item: ResponseStory) {
            binding.story = item
            binding.itemStoryDescription.setOnClickListener {
                binding.itemStoryDescription.maxLines = Integer.MAX_VALUE
                binding.itemStoryDescription.ellipsize = null
            }
            binding.viewModel = viewModel
            binding.itemYouTubePlayerView.addYouTubePlayerListener(youTubePlayerListener)
            lifecycle.addObserver(binding.itemYouTubePlayerView)

            binding.itemStoryReservation.setOnClickListener {
                item.campaign?.id?.let { campaignId -> viewModel.reserveCampaign(campaignId, item.id, !item.reserved) }
            }
        }

        private fun parseVideoIdFromUrl(url: String): String {
            return url.split("=")[1]
        }
    }

    companion object {
        const val TYPE_IMAGE = 0
        const val TYPE_VIDEO = 1

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ResponseStory>() {
            override fun areItemsTheSame(oldItem: ResponseStory, newItem: ResponseStory): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ResponseStory, newItem: ResponseStory): Boolean {
                return oldItem == newItem
            }

        }
    }

}