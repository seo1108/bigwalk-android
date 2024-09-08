package kr.co.bigwalk.app.feed_notification.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.data.feedNotification.dto.FeedNotificationResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerFeedNotificationBinding
import kr.co.bigwalk.app.feed_notification.FeedNotificationViewModel

class FeedNotificationAdapter(
    private val viewModel: FeedNotificationViewModel
): PagedListAdapter<FeedNotificationResponse, FeedNotificationViewHolder>(
    DIFF_CALLBACK
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedNotificationViewHolder {
        return FeedNotificationViewHolder.from(parent, viewModel)
    }

    override fun onBindViewHolder(holder: FeedNotificationViewHolder, position: Int) {
        val data = getItem(position)
        data?.let {
            holder.bind(it)
        }

    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FeedNotificationResponse>() {
            override fun areItemsTheSame(oldItem: FeedNotificationResponse, newItem: FeedNotificationResponse): Boolean {
                return oldItem.createdTime == newItem.createdTime
            }

            override fun areContentsTheSame(oldItem: FeedNotificationResponse, newItem: FeedNotificationResponse): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class FeedNotificationViewHolder(
    private val binding: ItemRecyclerFeedNotificationBinding,
    private val viewModel: FeedNotificationViewModel
): RecyclerView.ViewHolder(binding.root) {

    fun bind(feedNotificationResponse: FeedNotificationResponse) {
        binding.data = feedNotificationResponse

        itemView.setOnClickListener {
            viewModel.presentComment(feedNotificationResponse.actionDonationHistoryId)
        }
    }

    companion object {
        fun from(parent: ViewGroup, viewModel: FeedNotificationViewModel): FeedNotificationViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRecyclerFeedNotificationBinding.inflate(layoutInflater, parent, false)
            return FeedNotificationViewHolder(binding, viewModel)
        }
    }
}