package kr.co.bigwalk.app.crowd_funding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.crowd_funding.detail.funding.FundingCardItem
import kr.co.bigwalk.app.databinding.ItemRecyclerFundingMissionBinding

class StepFundingMissionAdapter : ListAdapter<FundingCardItem, StepFundingMissionViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StepFundingMissionViewHolder =
        StepFundingMissionViewHolder(parent)
    
    override fun onBindViewHolder(holder: StepFundingMissionViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FundingCardItem>() {
            override fun areItemsTheSame(oldItem: FundingCardItem, newItem: FundingCardItem): Boolean =
                oldItem == newItem
            
            override fun areContentsTheSame(oldItem: FundingCardItem, newItem: FundingCardItem): Boolean =
                oldItem == newItem
        }
    }
}

class StepFundingMissionViewHolder(
    parent: ViewGroup,
    private val binding: ItemRecyclerFundingMissionBinding =
        ItemRecyclerFundingMissionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(response: FundingCardItem) {
        with(binding) {
            this.response = response
        }
    }
}
