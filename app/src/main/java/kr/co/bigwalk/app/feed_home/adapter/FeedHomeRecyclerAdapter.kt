package kr.co.bigwalk.app.feed_home.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.feedHome.dto.MissionCampaignResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerFeedHomeBinding
import kr.co.bigwalk.app.feed.FeedDetailActivity
import kr.co.bigwalk.app.feed_home.FeedHomeViewModel
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.showAlertDialog


class FeedHomeRecyclerAdapter(
    private val viewModel: FeedHomeViewModel
): PagedListAdapter<MissionCampaignResponse, FeedHomeViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedHomeViewHolder {
        return FeedHomeViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: FeedHomeViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MissionCampaignResponse>() {
            override fun areItemsTheSame(oldItem: MissionCampaignResponse, newItem: MissionCampaignResponse): Boolean {
                return oldItem.missionId == newItem.missionId
            }

            override fun areContentsTheSame(oldItem: MissionCampaignResponse, newItem: MissionCampaignResponse): Boolean {
                return oldItem == newItem
            }
        }
    }

}

class FeedHomeViewHolder(
    private val binding: ItemRecyclerFeedHomeBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(missionCampaignResponse: MissionCampaignResponse) {
        DebugLog.d("missionCampaign = ${missionCampaignResponse}")
        binding.data = missionCampaignResponse

        itemView.setOnClickListener {
            if (missionCampaignResponse.isOpen.not() && missionCampaignResponse.organizationId.toLong() != PreferenceManager.getOrganization()) {
                showAlertDialog(itemView.context, "${missionCampaignResponse.groupName.orEmpty()}기업 유저에게만 공개된 챌린지입니다. 다른 챌린지에 함께 해주세요.") {}
                return@setOnClickListener
            }
            DebugLog.d("present FeedDetail campaignId ${missionCampaignResponse.campaignId}, organizationId = ${missionCampaignResponse.organizationId}")
            val intent = Intent(itemView.context, FeedDetailActivity::class.java)
            intent.putExtra(FeedDetailActivity.KEY_CAMPAIGN_ID, missionCampaignResponse.campaignId.toLong())
            intent.putExtra(FeedDetailActivity.KEY_ORGANIZATION_ID, missionCampaignResponse.organizationId.toLong())
            itemView.context.startActivity(intent)
        }

    }

    companion object {
        fun from(parent: ViewGroup): FeedHomeViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRecyclerFeedHomeBinding.inflate(layoutInflater, parent, false)
            return FeedHomeViewHolder(binding)
        }
    }
}