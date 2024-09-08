package kr.co.bigwalk.app.notification

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.detail.CampaignDetailActivity
import kr.co.bigwalk.app.community.funding.SupportersCampaignPreviewActivity
import kr.co.bigwalk.app.crowd_funding.detail.CrewCampaignDetailActivity
import kr.co.bigwalk.app.data.notification.NotiTypeId
import kr.co.bigwalk.app.data.notification.Notification
import kr.co.bigwalk.app.databinding.ItemRecyclerReceivedNotificationBinding
import kr.co.bigwalk.app.extension.extractUrl
import kr.co.bigwalk.app.extension.ifLet
import kr.co.bigwalk.app.util.showAlertDialog
import java.text.SimpleDateFormat
import java.util.*

class ReceivedNotificationAdapter : PagedListAdapter<Notification, ReceivedNotificationAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(private val binding: ItemRecyclerReceivedNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(notification: Notification) {
            binding.notification = notification
            itemView.setOnClickListener {
                when (notification.notiType) {
                    NotiTypeId.CAMPAIGN.id, NotiTypeId.STORY.id -> {
                        val data = notification.getDataMap()["campaign"]
                        moveToCampaignDetail(data?.toLong())
                    }
                    NotiTypeId.CREW_CAMPAIGN_AUDIT.id -> {
                        val type = notification.getDataMap()["type"]
                        val data = notification.getDataMap()["crewCampaignId"]
                        val groupId = notification.getDataMap()["groupId"]
                        val endDate = notification.getDataMap()["endDate"].orEmpty()
                        val date = SimpleDateFormat("yyyy-MM-dd'T'HH:mm").parse(endDate)
                        if (date.before(Date())) {
                            showAlertDialog(itemView.context, "이미 종료된 크라우드 펀딩입니다."){}
                            return@setOnClickListener
                        }
                        when (type) {
                            "12" -> {
                                moveToCrewCampaign(data?.toLong())
                            }
                            "14" -> {
                                moveToCrewCampaignPreview(data?.toLong(), groupId?.toLong())
                            }
                        }
                    }
                    NotiTypeId.EVENT_WIN.id, NotiTypeId.EVENT.id -> {
                        val data = notification.getDataMap()["data"]
                        showWebView(data?.extractUrl())
                    }
                }
            }
        }

        private fun moveToCampaignDetail(campaignId: Long?) {
            campaignId?.let { campaignId ->
                val intent = Intent(BigwalkApplication.context, CampaignDetailActivity::class.java)
                intent.putExtra("campaign_id", campaignId)
                BigwalkApplication.context?.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))

                BigwalkApplication.context?.let {
                    val bundle = Bundle()
                    bundle.putLong("campaign_id", campaignId)
                    FirebaseAnalytics.getInstance(it).logEvent("notification_view_campaign_detail", bundle)
                }
            }
        }

        private fun moveToCrewCampaign(crewCampaignId: Long?) {
            crewCampaignId?.let { campaignId ->
                BigwalkApplication.context?.let {
                    it.startActivity(CrewCampaignDetailActivity.getIntent(it, campaignId).addFlags(FLAG_ACTIVITY_NEW_TASK))
                }
            }
        }

        private fun moveToCrewCampaignPreview(crewCampaignId: Long?, groupId: Long?) {
            ifLet(crewCampaignId, groupId) { (campaignId, crewId) ->
                BigwalkApplication.context?.let {
                    it.startActivity(
                        SupportersCampaignPreviewActivity.getIntent(it, crewId, campaignId, false).addFlags(FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }
        }

        private fun showWebView(url: String?) {
            url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                BigwalkApplication.context?.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_received_notification,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Notification>() {
            override fun areItemsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Notification, newItem: Notification): Boolean {
                return oldItem == newItem
            }

        }
    }
}