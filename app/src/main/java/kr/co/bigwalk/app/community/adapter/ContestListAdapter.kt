package kr.co.bigwalk.app.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.data.funding.dto.ContestResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerContestBinding

class ContestListAdapter(
    private val itemClick: (ContestResponse) -> Unit
) : ListAdapter<ContestResponse, ContestListViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContestListViewHolder =
        ContestListViewHolder(parent, itemClick)
    
    override fun onBindViewHolder(holder: ContestListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ContestResponse>() {
            override fun areItemsTheSame(oldItem: ContestResponse, newItem: ContestResponse): Boolean =
                oldItem.id == newItem.id
            
            override fun areContentsTheSame(oldItem: ContestResponse, newItem: ContestResponse): Boolean =
                oldItem == newItem
        }
    }
}

class ContestListViewHolder(
    parent: ViewGroup,
    private val itemClick: (ContestResponse) -> Unit,
    private val binding: ItemRecyclerContestBinding =
        ItemRecyclerContestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(response: ContestResponse) {
        with(binding) {
            this.response = response
            root.setOnClickListener {
                itemClick(response)
            }
        }
    }
}