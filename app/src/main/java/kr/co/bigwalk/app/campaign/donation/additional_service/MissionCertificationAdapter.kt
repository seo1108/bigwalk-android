package kr.co.bigwalk.app.campaign.donation.additional_service

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.databinding.ItemRecyclerMissionCertificationBinding
import kr.co.bigwalk.app.extension.formatToSimple
import kr.co.bigwalk.app.util.DebugLog
import java.util.*

class MissionCertificationAdapter: RecyclerView.Adapter<MissionCertificationViewHolder>() {

    private var mData = emptyList<Uri>()
    private var mMissionDonationData: MissionDonationData? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MissionCertificationViewHolder {
        return MissionCertificationViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MissionCertificationViewHolder, position: Int) {
        holder.bind(mData[position], mMissionDonationData)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun setItems(data: List<Uri>, missionData: MissionDonationData?) {
        mData = data
        mMissionDonationData = missionData
        notifyDataSetChanged()
    }

}

class MissionCertificationViewHolder(
    private val binding: ItemRecyclerMissionCertificationBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(uri: Uri, missionData: MissionDonationData?) {
        DebugLog.d("bind uri=$uri , missionData=$missionData")
        binding.imageUri = uri.toString()
        if (adapterPosition == 0) {
            binding.shareCampaignNameLabel2.text = missionData?.actionMission?.title
            binding.tvCertificatedDate.text = Calendar.getInstance().formatToSimple()
        }
        binding.shareCampaignNameLabel2.isVisible = adapterPosition == 0 && missionData != null
        binding.tvCertificatedDate.isVisible = adapterPosition == 0 && missionData != null
        binding.imageView8.isVisible = adapterPosition == 0 && missionData != null
    }
    companion object {
        fun from(parent: ViewGroup): MissionCertificationViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRecyclerMissionCertificationBinding.inflate(layoutInflater, parent, false)
            return MissionCertificationViewHolder(binding)
        }
    }

}