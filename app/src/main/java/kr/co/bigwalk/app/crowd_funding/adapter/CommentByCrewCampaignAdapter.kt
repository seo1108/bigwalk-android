package kr.co.bigwalk.app.crowd_funding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.data.crowd_funding.dto.ContentResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerCommentCrewCampaignBinding

class CommentByCrewCampaignAdapter(
    private val itemLongClick: (ContentResponse) -> Unit
) : PagedListAdapter<ContentResponse, CommentByCrewCampaignViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentByCrewCampaignViewHolder =
        CommentByCrewCampaignViewHolder(parent, itemLongClick)

    override fun onBindViewHolder(holder: CommentByCrewCampaignViewHolder, position: Int) {
        val item: ContentResponse? = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ContentResponse>() {
            override fun areItemsTheSame(oldItem: ContentResponse, newItem: ContentResponse): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: ContentResponse, newItem: ContentResponse): Boolean =
                oldItem == newItem
        }
    }
}

class CommentByCrewCampaignViewHolder(
    parent: ViewGroup,
    private val itemClick: (ContentResponse) -> Unit,
    private val binding: ItemRecyclerCommentCrewCampaignBinding =
        ItemRecyclerCommentCrewCampaignBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(response: ContentResponse) {
        with(binding) {
            this.comment = response
            btnCommentDelete.setOnClickListener {
                itemClick(response)
            }
            btnCommentBlame.setOnClickListener {
                itemClick(response)
            }
        }
    }
}