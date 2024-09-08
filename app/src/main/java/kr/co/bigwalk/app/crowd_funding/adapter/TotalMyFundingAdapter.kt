package kr.co.bigwalk.app.crowd_funding.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.crowd_funding.dto.FundingState
import kr.co.bigwalk.app.data.crowd_funding.dto.TotalMyFundingResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerMyTotalFundingBinding

class TotalMyFundingAdapter(
    private val itemClick: (TotalMyFundingResponse) -> Unit
) : PagedListAdapter<TotalMyFundingResponse, TotalMyFundingViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TotalMyFundingViewHolder =
        TotalMyFundingViewHolder(parent, itemClick)

    override fun onBindViewHolder(holder: TotalMyFundingViewHolder, position: Int) {
        val item: TotalMyFundingResponse? = getItem(position)
        if (item != null) {
            holder.bind(item)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TotalMyFundingResponse>() {
            override fun areItemsTheSame(oldItem: TotalMyFundingResponse, newItem: TotalMyFundingResponse): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: TotalMyFundingResponse, newItem: TotalMyFundingResponse): Boolean =
                oldItem == newItem
        }
    }
}

class TotalMyFundingViewHolder(
    parent: ViewGroup,
    private val itemClick: (TotalMyFundingResponse) -> Unit,
    private val binding: ItemRecyclerMyTotalFundingBinding =
        ItemRecyclerMyTotalFundingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(response: TotalMyFundingResponse) {
        with(binding) {
            this.response = response
            root.setOnClickListener {
                if (response.remainSteps <= 0) return@setOnClickListener
                itemClick(response)
            }
            this.viewState = when (response.fundingState) {
                FundingState.FUNDING -> {
                    FundingStateViewType.FUNDING
                }
                FundingState.FUNDING_SUCCESS -> {
                    if (response.remainSteps > 0) FundingStateViewType.FUNDING_SUCCESS else FundingStateViewType.FUNDING_SUCCESS_DISABLE
                }
                else -> {
                    if (response.remainSteps > 0) FundingStateViewType.FUNDING_FAIL else FundingStateViewType.FUNDING_FAIL_DISABLE
                }
            }
        }
    }
}

enum class FundingStateViewType(val fundType: String, val stepType: String, @DrawableRes val drawable: Int?) {
    FUNDING("전체 펀딩 걸음 : ", "나의 펀딩 걸음", null),
    FUNDING_SUCCESS("펀딩 성공", "펀딩 성공 걸음", R.drawable.aos_icon_funding_reward),
    FUNDING_SUCCESS_DISABLE("펀딩 성공", "펀딩 성공 걸음", R.drawable.aos_icon_funding_reward_disable),
    FUNDING_FAIL("펀딩 종료", "돌려받는 걸음", R.drawable.aos_icon_funding_returnfund),
    FUNDING_FAIL_DISABLE("펀딩 종료", "돌려받는 걸음", R.drawable.aos_icon_funding_returnfund_disable)
}