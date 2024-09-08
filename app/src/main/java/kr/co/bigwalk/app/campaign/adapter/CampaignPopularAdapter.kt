package kr.co.bigwalk.app.campaign.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.campaign.CampaignViewModel
import kr.co.bigwalk.app.data.campaign.dto.ResponseCampaign
import kr.co.bigwalk.app.databinding.ItemRecyclerCampaignBinding
import java.util.*

class CampaignPopularAdapter(private val viewModel: CampaignViewModel): RecyclerView.Adapter<CampaignPopularAdapter.ViewHolder>() {

    private var popularCampaigns = listOf<ResponseCampaign>()

    class ViewHolder(val binding: ItemRecyclerCampaignBinding, val viewModel: CampaignViewModel): RecyclerView.ViewHolder(binding.root) {
        fun bind(campaignsResponse: ResponseCampaign) {
            if (null != campaignsResponse.endDate && Date() > campaignsResponse.endDate) binding.campaignPopularNotifyEndedView.visibility = View.VISIBLE
            else binding.campaignPopularNotifyEndedView.visibility = View.GONE
            binding.campaign = campaignsResponse
            itemView.setOnClickListener {
                CampaignActivity.firebaseAnalytics?.logEvent("campaign_button_recommend_campaign_click", Bundle())
                if (campaignsResponse.isConsumption()) {
                    viewModel.moveToConsumption(campaignsResponse.id)
                }else {
                    viewModel.moveToCampaign(campaignsResponse.id)
                }
            }
            binding.donationButton.setOnClickListener {
                CampaignActivity.firebaseAnalytics?.logEvent("popular_campaign_donate", Bundle())
                viewModel.donate(campaignsResponse)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder (
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_campaign,
                parent,
                false
            ),
            viewModel
        )
    }

    override fun getItemCount(): Int {
        return popularCampaigns.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(popularCampaigns[position])
    }

    fun setItems(items: ObservableList<ResponseCampaign>) {
        this.popularCampaigns = items
        notifyDataSetChanged()
    }
    
    fun replaceItem(id: Long, today: Long, total: Long) {
        val popularCampaigns = this.popularCampaigns
        val findIndex = popularCampaigns.indexOfFirst { it.id == id }
        if (findIndex >= 0) {
            popularCampaigns[findIndex].apply {
                maxDonationPerDay = total
                my.todayDonatedSteps = today
            }
            this.popularCampaigns = popularCampaigns
            notifyItemChanged(findIndex)
        }
    }
}