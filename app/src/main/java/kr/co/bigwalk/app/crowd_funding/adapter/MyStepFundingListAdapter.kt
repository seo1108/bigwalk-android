package kr.co.bigwalk.app.crowd_funding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.data.crowd_funding.dto.MyFundingByContestResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerMyFundingByContestBinding

class MyStepFundingListAdapter(
    private val itemClick: (MyFundingByContestResponse) -> Unit
) : PagedListAdapter<MyFundingByContestResponse, MyStepFundingViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyStepFundingViewHolder =
        MyStepFundingViewHolder(parent, itemClick)

    override fun onBindViewHolder(holder: MyStepFundingViewHolder, position: Int) {
        val item: MyFundingByContestResponse? = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MyFundingByContestResponse>() {
            override fun areItemsTheSame(oldItem: MyFundingByContestResponse, newItem: MyFundingByContestResponse): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MyFundingByContestResponse, newItem: MyFundingByContestResponse): Boolean =
                oldItem == newItem
        }
    }
}

class MyStepFundingViewHolder(
    parent: ViewGroup,
    private val itemClick: (MyFundingByContestResponse) -> Unit,
    private val binding: ItemRecyclerMyFundingByContestBinding =
        ItemRecyclerMyFundingByContestBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(response: MyFundingByContestResponse) {
        with(binding) {
            this.response = response
            root.setOnClickListener {
                itemClick(response)
            }
        }
    }
}