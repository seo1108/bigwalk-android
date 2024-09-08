package kr.co.bigwalk.app.campaign.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.campaign.dto.CampaignContentType
import kr.co.bigwalk.app.data.campaign.dto.CampaignContentResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerCampaignContentImagesBinding
import kr.co.bigwalk.app.databinding.ItemRecyclerCampaignContentNoneBinding
import kr.co.bigwalk.app.databinding.ItemRecyclerCampaignContentVideoBinding

class CampaignContentRecyclerAdapter(private val lifeCycle: Lifecycle) : RecyclerView.Adapter<CampaignContentRecyclerAdapter.BaseViewHolder<*>>() {

    private var contents = listOf<CampaignContentResponse>()


    abstract class BaseViewHolder<T>(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(item: T)
    }

    class ContentImagesViewHolder (private val binding: ItemRecyclerCampaignContentImagesBinding) : BaseViewHolder<CampaignContentResponse>(binding.root) {
        override fun bind(item: CampaignContentResponse) {
            binding.content = item
        }
    }

    class ContentVideoViewHolder (private val binding: ItemRecyclerCampaignContentVideoBinding, private val lifeCycle: Lifecycle) : BaseViewHolder<CampaignContentResponse>(binding.root) {
        override fun bind(item: CampaignContentResponse) {
            binding.content = item
            lifeCycle.addObserver(binding.youtubePlayerView)
        }
    }

    class ContentNoneViewHolder (private val binding: ItemRecyclerCampaignContentNoneBinding) : BaseViewHolder<CampaignContentResponse>(binding.root) {
        override fun bind(item: CampaignContentResponse) {
            binding.content = item
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        return when (viewType) {
            TYPE_VIDEO -> ContentVideoViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_recycler_campaign_content_video,
                    parent,
                    false
                ), lifeCycle
            )
            TYPE_IMAGE -> ContentImagesViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_recycler_campaign_content_images,
                    parent,
                    false
                )
            )
            else -> ContentNoneViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(parent.context),
                    R.layout.item_recycler_campaign_content_none,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return contents.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        when (holder) {
            is ContentVideoViewHolder -> holder.bind(contents[position])
            is ContentImagesViewHolder -> holder.bind(contents[position])
            is ContentNoneViewHolder -> holder.bind(contents[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (contents[position].campaignContentType) {
            CampaignContentType.IMAGE, CampaignContentType.IMAGES -> TYPE_IMAGE
            CampaignContentType.VIDEO -> TYPE_VIDEO
            else -> TYPE_NONE
        }
    }

    fun setItems(postedContents: List<CampaignContentResponse>) {
        contents = postedContents
        notifyDataSetChanged()
    }

    companion object {
        const val TYPE_IMAGE = 0
        const val TYPE_VIDEO = 1
        const val TYPE_NONE = 2
    }
}