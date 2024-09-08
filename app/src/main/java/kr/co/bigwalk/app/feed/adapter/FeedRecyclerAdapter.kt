package kr.co.bigwalk.app.feed.adapter

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.feed.dto.Feed
import kr.co.bigwalk.app.data.feed.repository.FeedDataSource
import kr.co.bigwalk.app.databinding.ItemRecyclerFeedImagesBinding
import kr.co.bigwalk.app.extension.setColorOfParticularPart
import kr.co.bigwalk.app.feed.FeedManager
import kr.co.bigwalk.app.feed.FeedPostViewModel
import kr.co.bigwalk.app.feed.category.FeedByCategoryViewModel
import kr.co.bigwalk.app.feed.isPublicOrganization
import kr.co.bigwalk.app.feed_comment.FeedCommentActivity
import kr.co.bigwalk.app.util.DebugLog

class FeedRecyclerAdapter(
    private val viewModel: FeedByCategoryViewModel,
    private val isEnableLike: Boolean,
    private val organizationId: Long,
    private val isMyPage: Boolean
): PagedListAdapter<Feed, FeedRecyclerAdapter.BaseViewHolder<*>>(DIFF_CALLBACK) {
    private var blockFeedId = -1L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {

        return FeedImagesViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_feed_images,
                parent,
                false
            ), isEnableLike, organizationId, isMyPage
        )
    }

    abstract class BaseViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: T, payloads: MutableList<Any>, viewModel: FeedPostViewModel, callback: (Int) -> Unit)
    }

    override fun onBindViewHolder(
        holder: BaseViewHolder<*>,
        position: Int,
        payloads: MutableList<Any>
    ) {
        when (holder) {
            is FeedImagesViewHolder -> getItem(position)?.let { feed ->

                holder.bindFeedImageHolder(viewModel, feed, position, payloads, blockFeedId)

            }
        }
    }


    private var items: PagedList<Feed>? = null

    fun addItems(it: PagedList<Feed>?) {
        items = it
        submitList(it)
    }
    fun dimFeed(feedId: Long) {
        this.blockFeedId = feedId
        items?.indexOfFirst { it.actionDonationHistoryId == feedId }?.let { position ->
            notifyItemChanged(position)
        }
    }


    fun updateFeed(position: Int, feed: Feed, pagePos: Int) {
        val idx = items?.snapshot()?.indexOfFirst { it.actionDonationHistoryId == feed.actionDonationHistoryId } ?: 0
        if (idx == -1 || itemCount == 0) return
        getItem(idx)?.let {
            it.likeCount = feed.likeCount
            it.liked = feed.liked
            it.commentCount = feed.commentCount
        }
        notifyItemChanged(idx, pagePos)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is FeedImagesViewHolder -> getItem(position)?.let { feed ->
                holder.bindFeedImageHolder(viewModel, feed, position, blockFeedId = blockFeedId)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return 0
    }

    class FeedImagesViewHolder(
        private val binding: ItemRecyclerFeedImagesBinding,
        private val isEnableLike: Boolean,
        private val organizationId: Long,
        private val isMyPage: Boolean,
        private val isComment: Boolean = false
    ) : BaseViewHolder<Feed>(binding.root) {

        val profile = binding.feedProfile
        val likeCount = binding.likeCount
        val commentCount = binding.commentCount
        val btnLiked = binding.btnLiked
        val btnComment = binding.btnComment
        val viewPager = binding.viewpagerFeedImages
        val indicator = binding.indicatorFeedImages
        val countText = binding.tvFeedImagesCount
        var itemSize = 0

        fun bindFeedImageHolder(
            viewModel: FeedPostViewModel,
            feed: Feed,
            position: Int,
            payloads: MutableList<Any> = mutableListOf(),
            blockFeedId: Long
        ) {

            bind(feed, payloads, viewModel) { pagePos ->
                likeFeed(viewModel, feed, position, pagePos)
            }
            binding.blockView.isVisible = feed.actionDonationHistoryId == blockFeedId

            binding.moreButton.setOnClickListener {
                viewModel.showDialogAction(feed)
            }

            likeCount.setOnClickListener {
                if (feed.likeCount > 0) {
                    val isPublic = isPublicOrganization(organizationId)
                    viewModel.moveToFeedLike(feed.actionDonationHistoryId, feed.liked, isPublic)
                }
            }

            profile.setOnClickListener {
                if (isMyPage) return@setOnClickListener
                viewModel.moveToMyPage(organizationId, feed.campaignId, feed.user.id, feed.user.name, feed.mine, adapterPosition)
            }
        }

        fun likeFeed(viewModel: FeedPostViewModel, feed: Feed, position: Int, pagePos: Int) {
            if (!isEnableLike) return
            val likeToChange = !feed.liked
            viewModel.requestLikeFeed(feed.actionDonationHistoryId, likeToChange, object : FeedDataSource.LikeFeedCallback {
                override fun onSuccess() {
                    feed.liked = likeToChange
                    if (likeToChange) { feed.likeCount+=1 } else { feed.likeCount-=1 }
                    FeedManager.updateFeed.value = Triple(adapterPosition, feed, pagePos)
                }
                override fun onFailed(reason: String) {
                    DebugLog.d("like feed request failed: $reason")
                }
            })
        }

        override fun bind(item: Feed, payloads: MutableList<Any>, viewModel: FeedPostViewModel, callback: (Int) -> Unit) {
            binding.feed = item
            likeCount.text = getDisplayLikeCount(item.likeCount, isEnableLike)
            commentCount.text = getDisplayCommentCount(item.commentCount, isEnableLike)
            btnLiked.background = getLikeButtonBackground(item.liked)
            btnLiked.visibility = if (isEnableLike) View.VISIBLE else View.GONE
            btnComment.visibility = if (isEnableLike) View.VISIBLE else View.GONE

            if (item.comment.isNullOrEmpty()) {
                binding.comment.text = item.comment
            } else {
                val splitComment = item.comment!!.split("\n")
                val firstLine = splitComment.first()
                val isMultiLine: Boolean = splitComment.size > 1
                val moreString = "... 더 보기"
                if (!item.isExpanded && firstLine.length > 20) {
                    val str = firstLine.substring(0..19)
                    val span = SpannableString(str + moreString)
                    span.setSpan(ForegroundColorSpan(Color.GRAY), str.length, str.length + moreString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                    binding.comment.text = span
                } else if (!item.isExpanded && isMultiLine) {
                    val str = firstLine
                    val span = SpannableString(str + moreString)
                    span.setSpan(ForegroundColorSpan(Color.GRAY), str.length, str.length + moreString.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
                    binding.comment.text = span
                } else {
                    binding.comment.text = item.comment
                }
            }

            val urlList = mutableListOf<String>()
            if (item.certifiedImagePath.isNotEmpty()) urlList.add(item.certifiedImagePath)
            item.certifiedImagePath2?.let {
                if (it.isNotEmpty()) {
                    urlList.add(item.certifiedImagePath2)
                }
            }
            item.certifiedImagePath3?.let {
                if (it.isNotEmpty()) {
                    urlList.add(item.certifiedImagePath3)
                }
            }
            val adapter = FeedImagesViewPagerAdapter(urlList, item.liked) {
                // 더블탭 콜백 왔을때, 좋아요 상태인 경우는 업데이트 안함
                if (!isEnableLike) return@FeedImagesViewPagerAdapter
                if (!item.liked) callback.invoke(viewPager.currentItem)
                binding.ivFeedImagesLike.isVisible = true
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.ivFeedImagesLike.isVisible = false
                },1500)
            }
            binding.btnLiked.setOnClickListener {
                callback.invoke(viewPager.currentItem)
            }
            viewPager.adapter = adapter
//            adapter.setHasStableIds(true)
            val rv = (viewPager as? RecyclerView)
            rv?.setHasFixedSize(true)
            rv?.itemAnimator = null
//            val itemAnimator = rv?.itemAnimator
//            itemAnimator?.let {
//                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
//                itemAnimator.changeDuration = 0
//                itemAnimator.endAnimations()
//            }

            btnComment.setOnClickListener {
                viewModel.pagerPos = viewPager.currentItem
                if (isComment) return@setOnClickListener
                presentComment(item)
            }
            commentCount.setOnClickListener {
                viewModel.pagerPos = viewPager.currentItem
                if (isComment) return@setOnClickListener
                presentComment(item)
            }
            binding.comment.setOnClickListener {
                item.isExpanded = true
                binding.comment.text = item.comment
            }

            viewPager.registerOnPageChangeCallback(viewpagerCallback)
            indicator.createDotPanel(urlList.size, R.drawable.bg_indicator_feed_selector)
            countText.isVisible = urlList.size > 1
            itemSize = urlList.size

            if (payloads.isNotEmpty()) {
                if (payloads[0] is Int) {
//                    adapter.setItems(urlList, payloads[0] as Int)
                    viewPager.setCurrentItem(payloads[0] as Int, false)
                }
            }

        }

        fun presentComment(item: Feed) {
            if (itemView.context == null) return
            val intent = Intent(itemView.context, FeedCommentActivity::class.java)
            intent.putExtra(FeedCommentActivity.IS_ENABLE_LIKE, isEnableLike)
            intent.putExtra(FeedCommentActivity.ORGANIZATION_ID, organizationId)
            intent.putExtra(FeedCommentActivity.ACTION_DONATION_HISTORY_ID, item.actionDonationHistoryId)
            itemView.context.startActivity(intent)
        }
        private val viewpagerCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (itemSize > 1) {
                    countText.isVisible = position == 0
                }
                binding.indicatorFeedImages.selectDot(position)
                countText.text = "${viewPager.currentItem + 1}/${itemSize}"
            }
        }

        private fun getLikeButtonBackground(liked: Boolean): Drawable? {
            return ContextCompat.getDrawable(
                itemView.context,
                if (liked) R.drawable.aos_icon_dowalk_like_fill else R.drawable.aos_icon_dowalk_like
            )
        }

        private fun getLikeCountContent(count: Long): SpannableString {
            if (count > 0) {
                val first = "${count}${itemView.context.getString(R.string.person)}"
                val second = "${itemView.context.getString(R.string.like_it)}"
                return "$first$second".setColorOfParticularPart(first, ContextCompat.getColor(itemView.context, R.color.black))
            }
            else return itemView.context.getString(R.string.try_like_feed).setColorOfParticularPart("", ContextCompat.getColor(itemView.context, R.color.black))
        }

        private fun getDisplayLikeCount(count: Long, isEnable: Boolean): String {
            return if (count > 0) {
                "${itemView.context.getString(R.string.feed_like)} ${count}개"
            }else {
                if (isEnable) itemView.context.getString(R.string.try_like_feed)
                else itemView.context.getString(R.string.feed_like)
            }
        }

        private fun getDisplayCommentCount(count: Long, isEnable: Boolean): String {
            return if (count > 0) {
                "${itemView.context.getString(R.string.feed_comment)} ${count}개"
            }else {
                if (isEnable) itemView.context.getString(R.string.try_feed_comment)
                else itemView.context.getString(R.string.feed_comment)
            }
        }
    }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Feed>() {
            override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
                return oldItem.actionDonationHistoryId == newItem.actionDonationHistoryId
            }

            override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
                return oldItem == newItem
            }
        }
    }

}