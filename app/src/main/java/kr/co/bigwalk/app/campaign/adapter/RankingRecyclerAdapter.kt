package kr.co.bigwalk.app.campaign.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.campaign.dto.RankingResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerRankingBinding

class RankingRecyclerAdapter : PagedListAdapter<RankingResponse, RankingRecyclerAdapter.RankingViewHolder>(DIFF_CALLBACK) {

    class RankingViewHolder(private val binding: ItemRecyclerRankingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rankingResponse: RankingResponse) {
            binding.rankingMyProfile.setImageBitmap(null)
            binding.ranking = rankingResponse
        }

        fun rank(rank: Int) {
            when(rank){
                1->binding.itemRankingProfileBadge1.visibility = View.VISIBLE
                2->binding.itemRankingProfileBadge2.visibility = View.VISIBLE
                3->binding.itemRankingProfileBadge3.visibility = View.VISIBLE
                else -> {
                    binding.itemRankingProfileBadge1.visibility = View.GONE
                    binding.itemRankingProfileBadge2.visibility = View.GONE
                    binding.itemRankingProfileBadge3.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RankingViewHolder {
        return RankingViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_ranking,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RankingViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
        holder.rank(position + 1)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RankingResponse>() {
            override fun areItemsTheSame(oldItem: RankingResponse, newItem: RankingResponse): Boolean {
                return oldItem.userId == newItem.userId
            }

            override fun areContentsTheSame(oldItem: RankingResponse, newItem: RankingResponse): Boolean {
                return oldItem == newItem
            }

        }
    }

}