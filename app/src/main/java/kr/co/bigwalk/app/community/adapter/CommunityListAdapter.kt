package kr.co.bigwalk.app.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.data.community.MyCommunityResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerCommunityBinding

class CommunityListAdapter(
    private val itemClick: (MyCommunityResponse) -> Unit
) : ListAdapter<MyCommunityResponse, CommunityListViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommunityListViewHolder =
        CommunityListViewHolder(parent, itemClick)
    
    override fun onBindViewHolder(holder: CommunityListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MyCommunityResponse>() {
            override fun areItemsTheSame(oldItem: MyCommunityResponse, newItem: MyCommunityResponse): Boolean =
                oldItem.groupId == newItem.groupId
            
            override fun areContentsTheSame(oldItem: MyCommunityResponse, newItem: MyCommunityResponse): Boolean =
                oldItem == newItem
        }
    }
}

class CommunityListViewHolder(
    parent: ViewGroup,
    private val itemClick: (MyCommunityResponse) -> Unit,
    private val binding: ItemRecyclerCommunityBinding =
        ItemRecyclerCommunityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(response: MyCommunityResponse) {
        with(binding) {
            this.response = response
            image.clipToOutline = true
            root.setOnClickListener {
                itemClick(response)
            }
        }
    }
}