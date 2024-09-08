package kr.co.bigwalk.app.campaign.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.campaign.dto.RankingResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerRankerSeasonBinding

class RankerBySeasonAdapter : ListAdapter<RankingResponse, RankerBySeasonViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankerBySeasonViewHolder =
        RankerBySeasonViewHolder(parent)

    override fun onBindViewHolder(holder: RankerBySeasonViewHolder, position: Int) {
        holder.bind(currentList[position], position)
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RankingResponse>() {
            override fun areItemsTheSame(oldItem: RankingResponse, newItem: RankingResponse): Boolean =
                oldItem.userId == newItem.userId

            override fun areContentsTheSame(oldItem: RankingResponse, newItem: RankingResponse): Boolean =
                oldItem == newItem
        }
    }
}

class RankerBySeasonViewHolder(
    parent: ViewGroup,
    private val binding: ItemRecyclerRankerSeasonBinding =
        ItemRecyclerRankerSeasonBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(response: RankingResponse, position: Int) {
        with(binding) {
            ranking = response
            when (position) {
                0 -> rankBadge.setImageResource(R.drawable.ico_badge_rank_1)
                1 -> rankBadge.setImageResource(R.drawable.ico_badge_rank_2)
                2 -> rankBadge.setImageResource(R.drawable.ico_badge_rank_3)
                else -> rankBadge.setImageResource(R.color.transparent)
            }
        }
    }
}