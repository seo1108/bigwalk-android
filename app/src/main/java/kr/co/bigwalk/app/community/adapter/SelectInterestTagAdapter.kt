package kr.co.bigwalk.app.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.community.create.SelectInterestViewModel
import kr.co.bigwalk.app.data.community.dto.create.CrewConcernTagResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerInterestTagBinding
import kr.co.bigwalk.app.util.showToast

class SelectInterestTagAdapter(
    private val selectInterestViewModel: SelectInterestViewModel
) : ListAdapter<CrewConcernTagResponse, SelectInterestTagAdapter.SelectInterestTagViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectInterestTagViewHolder =
        SelectInterestTagViewHolder(parent)

    override fun onBindViewHolder(holder: SelectInterestTagViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    private val selectedItemList = MutableList<Boolean>(SELECTED_MAX_COUNT) { false }
    private var count = 0

    inner class SelectInterestTagViewHolder(
        parent: ViewGroup,
        private val binding: ItemRecyclerInterestTagBinding =
            ItemRecyclerInterestTagBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(response: CrewConcernTagResponse) {
            with(binding) {
                tag.text = response.name
                tag.isChecked = response.selected
                if (response.selected) count++
                tag.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        if (count >= SELECTED_MAX_COUNT) {
                            buttonView.isChecked = false
                            showToast("최대 3개까지 선택 가능합니다.")
                            return@setOnCheckedChangeListener
                        }
                        count++
                        selectInterestViewModel.addSelectConcern(response)
                    } else {
                        count--
                        selectInterestViewModel.removeSelectConcern(response.id)
                    }
                }

            }
        }
    }

    companion object {
        private const val SELECTED_MAX_COUNT = 3
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CrewConcernTagResponse>() {
            override fun areItemsTheSame(oldItem: CrewConcernTagResponse, newItem: CrewConcernTagResponse): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: CrewConcernTagResponse, newItem: CrewConcernTagResponse): Boolean =
                oldItem == newItem
        }
    }
}