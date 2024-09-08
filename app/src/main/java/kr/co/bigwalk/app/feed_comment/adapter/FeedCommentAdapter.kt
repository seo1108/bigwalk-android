package kr.co.bigwalk.app.feed_comment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.blame.BlameActivity
import kr.co.bigwalk.app.blame.BlameType
import kr.co.bigwalk.app.data.feedComment.dto.FeedCommentResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerFeedCommentBinding
import kr.co.bigwalk.app.feed_comment.FeedCommentViewModel
import kr.co.bigwalk.app.util.showAlertDialog

class FeedCommentAdapter(
    private val viewModel: FeedCommentViewModel,
    private val organizationId: Long,
    private val campaignId: Long
): PagedListAdapter<FeedCommentResponse, FeedCommentViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedCommentViewHolder {
        return FeedCommentViewHolder.from(parent, viewModel, organizationId, campaignId)
    }

    override fun onBindViewHolder(holder: FeedCommentViewHolder, position: Int) {
        val data = getItem(position)
        data?.let {
            holder.bind(it)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FeedCommentResponse>() {
            override fun areItemsTheSame(oldItem: FeedCommentResponse, newItem: FeedCommentResponse): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FeedCommentResponse, newItem: FeedCommentResponse): Boolean {
                return oldItem == newItem
            }
        }
    }
}

class FeedCommentViewHolder(
    private val binding: ItemRecyclerFeedCommentBinding,
    private val viewModel: FeedCommentViewModel,
    private val organizationId: Long,
    private val campaignId: Long
): RecyclerView.ViewHolder(binding.root) {

    fun bind(data: FeedCommentResponse) {
        binding.comment = data
        binding.viewModel = this.viewModel

        binding.btnCommentDelete.setOnClickListener {
            showAlertDialog(itemView.context, R.string.dialog_delete_comment_msg) {
                viewModel.deleteComment(adapterPosition, data.id)
            }
        }
        binding.btnCommentBlame.setOnClickListener {
            itemView.context.startActivity(BlameActivity.getIntent(itemView.context, data.id, data.user.id, BlameType.FEED_COMMENT))
        }

        binding.ivCommentProfile.setOnClickListener {
            viewModel.moveToMyPage(organizationId, campaignId, data.user.id, data.user.name, data.mine, adapterPosition)
        }
    }

    companion object {
        fun from(parent: ViewGroup, vm: FeedCommentViewModel, organizationId: Long, campaignId: Long): FeedCommentViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRecyclerFeedCommentBinding.inflate(layoutInflater, parent, false)
            return FeedCommentViewHolder(binding, vm, organizationId, campaignId)
        }
    }
}