package kr.co.bigwalk.app.campaign.adapter

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignActivity.Companion.firebaseAnalytics
import kr.co.bigwalk.app.campaign.category.CampaignByCategoryViewModel
import kr.co.bigwalk.app.campaign.detail.CampaignDetailActivity
import kr.co.bigwalk.app.data.campaign.dto.ResponseCampaign
import kr.co.bigwalk.app.databinding.ItemRecyclerCampaignByCategoryBinding
import kr.co.bigwalk.app.util.DebugLog
import java.util.*

class CampaignCategoryRecyclerAdapter(private val viewModel: CampaignByCategoryViewModel) : PagedListAdapter<ResponseCampaign, CampaignCategoryRecyclerAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(private val binding: ItemRecyclerCampaignByCategoryBinding, private val viewModel: CampaignByCategoryViewModel) : RecyclerView.ViewHolder(binding.root) {
        fun bind(campaign: ResponseCampaign) {
            binding.campaign = campaign
            binding.itemByCategoryThumbnail.clipToOutline = true
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, CampaignDetailActivity::class.java)
                DebugLog.d("캠페인 아이디 : " + campaign.id)
                intent.putExtra("campaign_id", campaign.id)
                itemView.context.startActivity(intent)
            }
            binding.itemByCategoryDonateButton.setOnClickListener {
                firebaseAnalytics?.logEvent("category_campaign_donate ${campaign.id}", Bundle())
                viewModel.donate(campaign)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_campaign_by_category,
                parent,
                false
            ),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ResponseCampaign>() {
            override fun areItemsTheSame(oldItem: ResponseCampaign, newItem: ResponseCampaign): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ResponseCampaign, newItem: ResponseCampaign): Boolean {
                return oldItem == newItem
            }
        }
    }
}