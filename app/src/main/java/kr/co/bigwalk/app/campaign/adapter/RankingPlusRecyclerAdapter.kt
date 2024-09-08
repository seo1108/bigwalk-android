package kr.co.bigwalk.app.campaign.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.campaign.dto.RankData
import kr.co.bigwalk.app.databinding.ItemRecyclerRankingPlusBinding

class RankingPlusRecyclerAdapter(private val rankingReportDescription: ObservableField<String>) : PagedListAdapter<RankData, RecyclerView.ViewHolder>(DIFF_CALLBACK)  {
    private val TYPE_HEADER = 0
    private val TYPE_ITEM = 1

    override fun getItemViewType(position: Int): Int {
        if (position==TYPE_HEADER) {
            return TYPE_HEADER
        }
        return TYPE_ITEM
    }

    class RankingPlusViewHolder(private val binding: ItemRecyclerRankingPlusBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(rankData: RankData) {
            binding.rankingMyProfile.setImageBitmap(null)
            binding.ranking = rankData

            with(binding) {
                rankingItem.isSelected = rankData.mine
                rankingMyRank.isSelected = rankData.mine
                rankingMyProfileName.isSelected = rankData.mine
                rankingMyProfileDonatedStepsTitle.isSelected = rankData.mine
                rankingMyProfileDonatedSteps.isSelected = rankData.mine
            }
        }

        fun rank(rank: Int) {
            when(rank){
                1-> {
                    binding.itemRankingProfileBadge1.visibility = View.VISIBLE
                    binding.itemRankingProfileBadge2.visibility = View.GONE
                    binding.itemRankingProfileBadge3.visibility = View.GONE
                }
                2-> {
                    binding.itemRankingProfileBadge2.visibility = View.VISIBLE
                    binding.itemRankingProfileBadge1.visibility = View.GONE
                    binding.itemRankingProfileBadge3.visibility = View.GONE
                }
                3-> {
                    binding.itemRankingProfileBadge3.visibility = View.VISIBLE
                    binding.itemRankingProfileBadge1.visibility = View.GONE
                    binding.itemRankingProfileBadge2.visibility = View.GONE
                }
                else -> {
                    binding.itemRankingProfileBadge1.visibility = View.GONE
                    binding.itemRankingProfileBadge2.visibility = View.GONE
                    binding.itemRankingProfileBadge3.visibility = View.GONE
                }
            }
        }
    }

    class RankingPlusHeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        lateinit var myRankingDescription: TextView

        fun bind() {
            myRankingDescription = itemView.findViewById(R.id.my_ranking_description)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType==TYPE_HEADER){
            RankingPlusHeaderViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_header_ranking_plus, parent, false)
            )
        } else {
            RankingPlusViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_recycler_ranking_plus,
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position==TYPE_HEADER && holder is RankingPlusHeaderViewHolder) {
            holder.bind()
            holder.myRankingDescription.text = rankingReportDescription.get()
            return
        }
        holder as RankingPlusViewHolder
        holder.bind(getItem(position-1)!!)
        holder.rank(position)
    }



    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<RankData>() {
            override fun areItemsTheSame(oldItem: RankData, newItem: RankData): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: RankData, newItem: RankData): Boolean {
                return oldItem == newItem
            }

        }
    }
}