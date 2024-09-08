package kr.co.bigwalk.app.campaign.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.data.campaign.dto.SeasonTopRankerResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerSeasonRankingBinding

class SeasonRankingAdapter(
    private val itemClick: (SeasonTopRankerResponse) -> Unit
) : ListAdapter<SeasonTopRankerResponse, SeasonRankingViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SeasonRankingViewHolder =
        SeasonRankingViewHolder(parent, itemClick)

    override fun onBindViewHolder(holder: SeasonRankingViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<SeasonTopRankerResponse>() {
            override fun areItemsTheSame(oldItem: SeasonTopRankerResponse, newItem: SeasonTopRankerResponse): Boolean =
                oldItem.name == newItem.name

            override fun areContentsTheSame(oldItem: SeasonTopRankerResponse, newItem: SeasonTopRankerResponse): Boolean =
                oldItem == newItem
        }
    }
}

class SeasonRankingViewHolder(
    parent: ViewGroup,
    private val itemClick: (SeasonTopRankerResponse) -> Unit,
    private val binding: ItemRecyclerSeasonRankingBinding =
        ItemRecyclerSeasonRankingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(response: SeasonTopRankerResponse) {
        with(binding) {
            data = response
            root.setOnClickListener {
                itemClick(response)
            }
        }
    }
}