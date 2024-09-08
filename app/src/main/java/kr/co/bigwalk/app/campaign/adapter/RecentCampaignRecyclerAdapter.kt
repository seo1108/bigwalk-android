package kr.co.bigwalk.app.campaign.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.detail.CampaignDetailActivity
import kr.co.bigwalk.app.campaign.recent.RecentCampaignViewModel
import kr.co.bigwalk.app.data.campaign.dto.ResponseCampaign
import kr.co.bigwalk.app.databinding.ItemRecyclerRecentCampaignBinding
import java.util.*

class RecentCampaignRecyclerAdapter(private val viewModel: RecentCampaignViewModel) :
    RecyclerView.Adapter<RecentCampaignRecyclerAdapter.RecentCampaignViewHolder>() {

    private var recentCampaigns = listOf<ResponseCampaign>()

    class RecentCampaignViewHolder(
        private val binding: ItemRecyclerRecentCampaignBinding,
        private val viewModel: RecentCampaignViewModel
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(recentCampaign: ResponseCampaign) {
            binding.campaign = recentCampaign
            if (Date() > recentCampaign.endDate) binding.notifyEndedView.visibility = View.VISIBLE
            else binding.notifyEndedView.visibility = View.GONE
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, CampaignDetailActivity::class.java)
                intent.putExtra("campaign_id", recentCampaign.id)
                itemView.context.startActivity(intent)
            }
            binding.itemRecentCampaignDonateButton.setOnClickListener {
                viewModel.donateStep(recentCampaign)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentCampaignViewHolder {
        return RecentCampaignViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_recent_campaign,
                parent,
                false
            ),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: RecentCampaignViewHolder, position: Int) {
        holder.bind(recentCampaigns[position])
    }

    override fun getItemCount(): Int {
        return recentCampaigns.size
    }

    fun setItems(items: ObservableList<ResponseCampaign>) {
        this.recentCampaigns = items
        notifyDataSetChanged()
    }
    
    fun replaceItem(id: Long, today: Long, total: Long) {
        val recentCampaigns = this.recentCampaigns
        val findIndex = recentCampaigns.indexOfFirst { it.id == id }
        if (findIndex >= 0) {
            recentCampaigns[findIndex].apply {
                maxDonationPerDay = total
                my.todayDonatedSteps = today
            }
            this.recentCampaigns = recentCampaigns
            notifyItemChanged(findIndex)
        }
    }

}