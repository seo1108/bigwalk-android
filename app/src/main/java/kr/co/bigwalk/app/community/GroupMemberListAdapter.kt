package kr.co.bigwalk.app.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.community.GroupMemberResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerGroupMemberListBinding

class GroupMemberListAdapter(
    private val itemLongClick: (GroupMemberResponse) -> Boolean
) : ListAdapter<GroupMemberResponse, GroupMemberListViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupMemberListViewHolder =
        GroupMemberListViewHolder(parent, itemLongClick)
    
    override fun onBindViewHolder(holder: GroupMemberListViewHolder, position: Int) {
        holder.bind(currentList[position], position)
    }
    
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GroupMemberResponse>() {
            override fun areItemsTheSame(oldItem: GroupMemberResponse, newItem: GroupMemberResponse): Boolean =
                oldItem.userId == newItem.userId
            
            override fun areContentsTheSame(oldItem: GroupMemberResponse, newItem: GroupMemberResponse): Boolean =
                oldItem == newItem
        }
    }
}

class GroupMemberListViewHolder(
    parent: ViewGroup,
    private val itemLongClick: (GroupMemberResponse) -> Boolean,
    private val binding: ItemRecyclerGroupMemberListBinding =
        ItemRecyclerGroupMemberListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(response: GroupMemberResponse, position: Int) {
        with(binding) {
            data = response
            root.setOnLongClickListener {
                itemLongClick(response)
            }
            when (position) {
                0 -> rankBadge.setImageResource(R.drawable.ico_badge_rank_1)
                1 -> rankBadge.setImageResource(R.drawable.ico_badge_rank_2)
                2 -> rankBadge.setImageResource(R.drawable.ico_badge_rank_3)
                else -> rankBadge.setImageResource(R.color.transparent)
            }
        }
    }
}