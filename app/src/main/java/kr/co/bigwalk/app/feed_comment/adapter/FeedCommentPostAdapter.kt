package kr.co.bigwalk.app.feed_comment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.feed.dto.Feed
import kr.co.bigwalk.app.feed.adapter.FeedChallengeRecyclerAdapter
import kr.co.bigwalk.app.feed.adapter.FeedRecyclerAdapter
import kr.co.bigwalk.app.feed_comment.FeedCommentViewModel
import kr.co.bigwalk.app.util.DebugLog

class FeedCommentPostAdapter(
    private val viewModel: FeedCommentViewModel,
    private val isEnableLike: Boolean,
    private val organizationId: Long,
    private val isMyPage: Boolean
): RecyclerView.Adapter<FeedChallengeRecyclerAdapter.FeedImagesViewHolder>() {

    private var mData = mutableListOf<Feed>()
    private var blockFeedId = -1L

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeedChallengeRecyclerAdapter.FeedImagesViewHolder {
        /*return FeedChallengeRecyclerAdapter.FeedImagesViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_feed_images,
                parent,
                false
            ), isEnableLike, organizationId, isMyPage, true
        )*/
        return FeedChallengeRecyclerAdapter.FeedImagesViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_feed_challenge,
                parent,
                false
            ), isEnableLike, organizationId, isMyPage, true
        )
    }

    override fun onBindViewHolder(
        holder: FeedChallengeRecyclerAdapter.FeedImagesViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (mData.isNotEmpty()) {
            holder.bindFeedImageHolder(viewModel, mData[position], position, payloads, blockFeedId)
        }
    }
    override fun onBindViewHolder(holder: FeedChallengeRecyclerAdapter.FeedImagesViewHolder, position: Int) {
        if (mData.isNotEmpty()) {
            holder.bindFeedImageHolder(viewModel, mData[position], position, blockFeedId = blockFeedId)
        }
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun setData(data: List<Feed>) {
        mData.clear()
        mData.addAll(data)
        notifyItemChanged(0)
    }

    fun refreshItem() {
        notifyItemChanged(0)
    }

    fun updateFeed(position: Int, feed: Feed, pagePos: Int) {
        mData.clear()
        mData.add(feed)
        DebugLog.d("like feed request updateFeed $feed")
        PreferenceManager.saveFeedInfo(feed.actionDonationHistoryId, feed.likeCount, feed.liked, feed.commentCount, feed.comment!!, false)
        notifyItemChanged(position, pagePos)
    }

    fun dimFeed(feedId: Long) {
        this.blockFeedId = feedId
        notifyItemChanged(mData.indexOfFirst { it.actionDonationHistoryId == feedId })
    }

}