package kr.co.bigwalk.app.feed.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerFeedLikeBinding

class FeedLikeRecyclerAdapter(
    private val isPublic: Boolean
) : PagedListAdapter<UserProfileResponse, FeedLikeRecyclerAdapter.FeedLikeViewHolder>(DIFF_CALLBACK) {

    class FeedLikeViewHolder(private val binding: ItemRecyclerFeedLikeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(user: UserProfileResponse, isPublic: Boolean) {
            binding.user = user
            binding.feedLikeDepartment.isVisible = (!isPublic && user.megaDepartment != null)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedLikeViewHolder {
        return FeedLikeViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_feed_like,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FeedLikeViewHolder, position: Int) {
        holder.bind(getItem(position)!!, isPublic)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserProfileResponse>() {
            override fun areItemsTheSame(oldItem: UserProfileResponse, newItem: UserProfileResponse): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: UserProfileResponse, newItem: UserProfileResponse): Boolean {
                return oldItem == newItem
            }

        }
    }

}