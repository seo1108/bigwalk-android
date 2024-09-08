package kr.co.bigwalk.app.blame.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.data.blame.dto.UserBlockResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerUserBlockBinding

class UserBlockListAdapter(
    private val itemClick: (UserBlockResponse) -> Unit
) : PagedListAdapter<UserBlockResponse, UserBlockListViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserBlockListViewHolder =
        UserBlockListViewHolder(parent, itemClick)

    override fun onBindViewHolder(holder: UserBlockListViewHolder, position: Int) {
        val item: UserBlockResponse? = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserBlockResponse>() {
            override fun areItemsTheSame(oldItem: UserBlockResponse, newItem: UserBlockResponse): Boolean =
                oldItem.userId == newItem.userId

            override fun areContentsTheSame(oldItem: UserBlockResponse, newItem: UserBlockResponse): Boolean =
                oldItem == newItem
        }
    }
}

class UserBlockListViewHolder(
    parent: ViewGroup,
    private val itemClick: (UserBlockResponse) -> Unit,
    private val binding: ItemRecyclerUserBlockBinding =
        ItemRecyclerUserBlockBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(response: UserBlockResponse) {
        with(binding) {
            this.response = response
            btnUnblock.setOnClickListener {
                itemClick(response)
            }
        }
    }
}