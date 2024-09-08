package kr.co.bigwalk.app.campaign.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignActivity
import kr.co.bigwalk.app.campaign.CampaignViewModel
import kr.co.bigwalk.app.campaign.detail.CampaignDetailActivity
import kr.co.bigwalk.app.data.campaign.dto.ResponseCampaign
import kr.co.bigwalk.app.databinding.ItemRecyclerMyCampaignBinding

class ParticipatedCampaignAdapter(private val viewModel: CampaignViewModel) : RecyclerView.Adapter<ParticipatedCampaignAdapter.ViewHolder>() {

    private var participatedCampaigns = listOf<ResponseCampaign>()

    class ViewHolder(private val binding: ItemRecyclerMyCampaignBinding
                     , val viewModel: CampaignViewModel) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(campaign: ResponseCampaign) {
            binding.campaign = campaign
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, CampaignDetailActivity::class.java)
                intent.putExtra("campaign_id", campaign.id)
                itemView.context.startActivity(intent)
            }
            binding.itemMyCampaignDonateButton.setOnClickListener {
                CampaignActivity.firebaseAnalytics?.logEvent("participated_campaign_donate ${campaign.id}", Bundle())
                viewModel.donate(campaign)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_my_campaign,
                parent,
                false
            ),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(participatedCampaigns[position])
    }

    override fun getItemCount(): Int {
        return participatedCampaigns.size
    }

    fun setItems(items: ObservableList<ResponseCampaign>) {
        this.participatedCampaigns = items
        notifyDataSetChanged()
    }
    
    fun replaceItem(id: Long, today: Long, total: Long) {
        val participatedCampaigns = this.participatedCampaigns
        val findIndex = participatedCampaigns.indexOfFirst { it.id == id }
        if (findIndex >= 0) {
            participatedCampaigns[findIndex].apply {
                maxDonationPerDay = total
                my.todayDonatedSteps = today
            }
            this.participatedCampaigns = participatedCampaigns
            notifyItemChanged(findIndex)
        }
    }

}