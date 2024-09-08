package kr.co.bigwalk.app.crowd_funding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.funding.dto.CrewCampaignRankingResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerFundingRankingBinding

class CrewCampaignFundingRankingAdapter : PagedListAdapter<CrewCampaignRankingResponse, CrewCampaignFundingRankingViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewCampaignFundingRankingViewHolder =
        CrewCampaignFundingRankingViewHolder(parent)

    override fun onBindViewHolder(holder: CrewCampaignFundingRankingViewHolder, position: Int) {
        val item: CrewCampaignRankingResponse? = getItem(position)
        if (item != null) {
            holder.bind(item, position)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CrewCampaignRankingResponse>() {
            override fun areItemsTheSame(oldItem: CrewCampaignRankingResponse, newItem: CrewCampaignRankingResponse): Boolean =
                oldItem.userId == newItem.userId

            override fun areContentsTheSame(oldItem: CrewCampaignRankingResponse, newItem: CrewCampaignRankingResponse): Boolean =
                oldItem == newItem
        }
    }
}

class CrewCampaignFundingRankingViewHolder(
    parent: ViewGroup,
    private val binding: ItemRecyclerFundingRankingBinding =
        ItemRecyclerFundingRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(response: CrewCampaignRankingResponse, position: Int) {
        with(binding) {
            this.data = response
            when (position) {
                0 -> rankBadge.setImageResource(R.drawable.ico_badge_rank_1)
                1 -> rankBadge.setImageResource(R.drawable.ico_badge_rank_2)
                2 -> rankBadge.setImageResource(R.drawable.ico_badge_rank_3)
                else -> rankBadge.setImageResource(R.color.transparent)
            }
        }
    }
}