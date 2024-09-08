package kr.co.bigwalk.app.feed.adapter

import android.annotation.SuppressLint
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
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.feed.dto.Feed
import kr.co.bigwalk.app.data.feed.repository.FeedDataSource
import kr.co.bigwalk.app.databinding.ItemRecyclerFeedChallengeBinding
import kr.co.bigwalk.app.extension.setColorOfParticularPart
import kr.co.bigwalk.app.feed.FeedManager
import kr.co.bigwalk.app.feed.FeedPostViewModel
import kr.co.bigwalk.app.feed.category.FeedByCategoryViewModel
import kr.co.bigwalk.app.feed.isPublicOrganization
import kr.co.bigwalk.app.feed_comment.FeedCommentActivity
import kr.co.bigwalk.app.util.DebugLog

class FeedChallengeRecyclerAdapter(
    private val viewModel: FeedByCategoryViewModel,
    private val isEnableLike: Boolean,
    private val organizationId: Long,
    private val isMyPage: Boolean
): RecyclerView.Adapter<FeedChallengeRecyclerAdapter.BaseViewHolder<*>>() {
    private var blockFeedId = -1L

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {

        return FeedImagesViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_feed_challenge,
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

    private var items = mutableListOf<Feed>()
    //private var items : List<Feed>? = null

    /*fun addItems(it: List<Feed>?) {
        items = it!!
        submitList(it)
        //notifyDataSetChanged()
    }*/

    fun refreshFeed() {
        if (PreferenceManager.getFeedId() < 0) return
        var position: Int = -1
        run breaker@ {
            viewModel.feedList.forEachIndexed() { idx, it ->
                DebugLog.d("like feed request resume list ${PreferenceManager.addedFeedId()} ${PreferenceManager.getFeedId()} $it")
                if (it.actionDonationHistoryId == PreferenceManager.getFeedId()) {
                    position = idx
                    return@breaker
                }
            }
        }
        DebugLog.d("like feed request resume list $position")
        if (position < 0) return
        if (viewModel.feedList.isNotEmpty()) {
            if (PreferenceManager.getFeedId() == -1L) return
            viewModel.feedList[position].likeCount = PreferenceManager.getFeedLikeCount()
            viewModel.feedList[position].liked = PreferenceManager.getFeedIsLike()
            viewModel.feedList[position].commentCount = PreferenceManager.getFeedCommentCount()
            viewModel.feedList[position].comment = PreferenceManager.getFeedComment()

            if (PreferenceManager.getFeedIsDelete()) {
                viewModel.feedList.removeAt(position)
                //PreferenceManager.saveFeedInfo(-1, 0, false, 0, "", false)
                //notifyDataSetChanged()
                notifyItemRemoved(position)
            } else {
                DebugLog.d("like feed request resume notifyItemChanged")
                notifyItemChanged(position)
            }
        }

        //PreferenceManager.saveFeedInfo(-1, 0, false, 0, "", false)
    }

    fun refreshFeed(refreshPosition: Int) {
        if (refreshPosition == -1) return
        if (PreferenceManager.getFeedId() == -1L) return
        viewModel.feedList[refreshPosition].likeCount = PreferenceManager.getFeedLikeCount()
        viewModel.feedList[refreshPosition].liked = PreferenceManager.getFeedIsLike()
        viewModel.feedList[refreshPosition].commentCount = PreferenceManager.getFeedCommentCount()
        viewModel.feedList[refreshPosition].comment = PreferenceManager.getFeedComment()

        if (PreferenceManager.getFeedIsDelete()) {
            viewModel.feedList.removeAt(refreshPosition)
            //PreferenceManager.saveFeedInfo(-1, 0, false, 0, "", false)
            //notifyDataSetChanged()
            notifyItemRemoved(refreshPosition)
        } else {
            notifyItemChanged(refreshPosition)
        }

        //PreferenceManager.saveFeedInfo(-1, 0, false, 0, "", false)
    }

    fun setItems(it: MutableList<Feed>) {
        this.items = (it as MutableList<Feed>?)!!
        notifyDataSetChanged()
    }

    fun getItem(position: Int) : Feed {
        return items[position]
    }

    fun getItems() : MutableList<Feed> {
        return this.items
    }

    override fun getItemCount(): Int {
        return items!!.size
    }

    fun dimFeed(feedId: Long) {
        this.blockFeedId = feedId
        items?.indexOfFirst { it.actionDonationHistoryId == feedId }?.let { position ->
            notifyItemChanged(position)
        }
    }


    fun updateFeed(position: Int, feed: Feed, pagePos: Int) {
        //val idx = items?.snapshot()?.indexOfFirst { it.actionDonationHistoryId == feed.actionDonationHistoryId } ?: 0
        val idx = items?.indexOfFirst { it.actionDonationHistoryId == feed.actionDonationHistoryId } ?: 0
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
        private val binding: ItemRecyclerFeedChallengeBinding,
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
                //DebugLog.d("like feed request comeback4")
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



            /*binding.btnLiked.setOnClickListener {
                DebugLog.d("like feed request comeback3")
                if (!isEnableLike) return@setOnClickListener
                val likeToChange = !feed.liked
                viewModel.requestLikeFeed(feed.actionDonationHistoryId, likeToChange, object : FeedDataSource.LikeFeedCallback {
                    override fun onSuccess() {
                        DebugLog.d("like feed request : $position $feed")

                        feed.liked = likeToChange
                        var likeCount = feed.likeCount
                        if (likeToChange) {
                            feed.likeCount+=1
                        } else {
                            feed.likeCount-=1
                        }

                        if (feed.likeCount > 0) {
                            binding.likeText = "${itemView.context.getString(R.string.feed_like)} ${feed.likeCount}개"
                        } else {
                            if (isEnableLike) binding.likeText = itemView.context.getString(R.string.try_like_feed)
                            else binding.likeText = itemView.context.getString(R.string.feed_like)
                        }

                        FeedManager.updateFeed.value = Triple(adapterPosition, feed, 0)
                    }
                    override fun onFailed(reason: String) {
                        DebugLog.d("like feed request failed: $reason")
                    }
                })
            }*/


        }

        fun likeFeed(viewModel: FeedPostViewModel, feed: Feed, position: Int, pagePos: Int) {
            DebugLog.d("like feed request likeFeed")
            if (!isEnableLike) return
            val likeToChange = !feed.liked
            viewModel.requestLikeFeed(feed.actionDonationHistoryId, likeToChange, object : FeedDataSource.LikeFeedCallback {
                override fun onSuccess() {
                    DebugLog.d("like feed request : $position $feed")

                    viewModel.feedList.forEach { it ->
                        DebugLog.d("like feed requestList $it")
                    }

                    feed.liked = likeToChange
                    var likeCount = feed.likeCount
                    if (likeToChange) {
                        feed.likeCount+=1
                    } else {
                        feed.likeCount-=1
                    }

                    if (feed.likeCount > 0) {
                        binding.likeCount.text = "${itemView.context.getString(R.string.feed_like)} ${feed.likeCount}개"
                    } else {
                        if (isEnableLike) binding.likeCount.text = itemView.context.getString(R.string.try_like_feed)
                        else binding.likeCount.text = itemView.context.getString(R.string.feed_like)
                    }

                    btnLiked.background = getLikeButtonBackground(feed.liked)

                    PreferenceManager.saveFeedInfo(feed.actionDonationHistoryId, feed.likeCount, feed.liked, feed.commentCount, feed.comment, false)

                    FeedManager.updateFeed.value = Triple(adapterPosition, feed, pagePos)
                }
                override fun onFailed(reason: String) {
                    DebugLog.d("like feed request failed: $reason")
                }
            })
        }

        override fun bind(item: Feed, payloads: MutableList<Any>, viewModel: FeedPostViewModel, callback: (Int) -> Unit) {
            DebugLog.d("like feed request comeback5")
            binding.feed = item
            //likeCount.text = getDisplayLikeCount(item.likeCount, isEnableLike)
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

            if (item.likeCount > 0) {
                DebugLog.d("like feed request comeback1")
                //binding.likeText = "${itemView.context.getString(R.string.feed_like)} ${item.likeCount}개"
                binding.likeCount.text = "${itemView.context.getString(R.string.feed_like)} ${item.likeCount}개"
            } else {
                DebugLog.d("like feed request comeback2")
                if (isEnableLike) binding.likeCount.text = itemView.context.getString(R.string.try_like_feed)
                else binding.likeCount.text = itemView.context.getString(R.string.feed_like)
            }
        }

        fun presentComment(item: Feed) {
            if (itemView.context == null) return

            PreferenceManager.saveFeedInfo(item.actionDonationHistoryId, item.likeCount, item.liked, item.commentCount, item.comment, false)

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


    private val differCallback = object : DiffUtil.ItemCallback<Feed>(){
        override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return  oldItem.actionDonationHistoryId == newItem.actionDonationHistoryId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,differCallback)

}