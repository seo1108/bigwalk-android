package kr.co.bigwalk.app.crowd_funding.adapter

import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.community.PropensityType
import kr.co.bigwalk.app.data.crowd_funding.dto.FundingByHottestResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerStepFundingHottestBinding
import kr.co.bigwalk.app.databinding.ItemRecyclerStepFundingNewestBinding
import kotlin.random.Random

class StepFundingListAdapter(
    private val itemClick: (FundingByHottestResponse) -> Unit
) : PagedListAdapter<FundingByHottestResponse, RecyclerView.ViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                StepFundingListByNewestViewHolder(parent, itemClick)
            }
            else -> {
                StepFundingListByHottestViewHolder(parent, itemClick)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        when (getItemViewType(position)) {
            0 -> {
                (holder as StepFundingListByNewestViewHolder).bind(item)
            }
            1 -> {
                (holder as StepFundingListByHottestViewHolder).bind(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)?.isLiked == null) {
            0
        } else {
            1
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<FundingByHottestResponse>() {
            override fun areItemsTheSame(oldItem: FundingByHottestResponse, newItem: FundingByHottestResponse): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: FundingByHottestResponse, newItem: FundingByHottestResponse): Boolean =
                oldItem == newItem
        }
    }
}

class StepFundingListByNewestViewHolder(
    parent: ViewGroup,
    private val itemClick: (FundingByHottestResponse) -> Unit,
    private val binding: ItemRecyclerStepFundingNewestBinding =
        ItemRecyclerStepFundingNewestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(response: FundingByHottestResponse) {
        with(binding) {
            this.response = response
            root.setOnClickListener {
                itemClick(response)
            }
            val list: TypedArray = when (response.groupPropensity) {
                PropensityType.HEALTH -> {
                    root.context.resources.obtainTypedArray(R.array.health_img)
                }
                PropensityType.ENVIRONMENT -> {
                    root.context.resources.obtainTypedArray(R.array.eco_img)
                }
                PropensityType.RUNNING -> {
                    root.context.resources.obtainTypedArray(R.array.running_img)
                }
                PropensityType.NONE -> {
                    root.context.resources.obtainTypedArray(R.array.health_img)
                }
            }
            this.image = list.getResourceId(Random.nextInt(list.length()), 0)
            list.recycle()
        }
    }
}

class StepFundingListByHottestViewHolder(
    parent: ViewGroup,
    private val itemClick: (FundingByHottestResponse) -> Unit,
    private val binding: ItemRecyclerStepFundingHottestBinding =
        ItemRecyclerStepFundingHottestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(response: FundingByHottestResponse) {
        with(binding) {
            this.response = response
            root.setOnClickListener {
                itemClick(response)
            }
        }
    }
}