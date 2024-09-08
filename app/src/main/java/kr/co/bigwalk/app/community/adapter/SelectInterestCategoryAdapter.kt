package kr.co.bigwalk.app.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kr.co.bigwalk.app.community.create.SelectInterestViewModel
import kr.co.bigwalk.app.data.community.dto.create.CrewConcernResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerInterestCategoryBinding

class SelectInterestCategoryAdapter(
    private val selectInterestViewModel: SelectInterestViewModel
) : ListAdapter<CrewConcernResponse, SelectInterestCategoryAdapter.SelectInterestCategoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectInterestCategoryViewHolder =
        SelectInterestCategoryViewHolder(parent)

    override fun onBindViewHolder(holder: SelectInterestCategoryViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    inner class SelectInterestCategoryViewHolder(
        parent: ViewGroup,
        private val binding: ItemRecyclerInterestCategoryBinding =
            ItemRecyclerInterestCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(response: CrewConcernResponse) {
            with(binding) {
                category.text = response.characteristic
                tagList.adapter = SelectInterestTagAdapter(selectInterestViewModel).apply {
                    this.submitList(response.tags)
                }
                tagList.layoutManager = FlexboxLayoutManager(itemView.context).apply {
                    flexWrap = FlexWrap.WRAP
                    flexDirection = FlexDirection.ROW
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CrewConcernResponse>() {
            override fun areItemsTheSame(oldItem: CrewConcernResponse, newItem: CrewConcernResponse): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: CrewConcernResponse, newItem: CrewConcernResponse): Boolean =
                oldItem == newItem
        }
    }
}