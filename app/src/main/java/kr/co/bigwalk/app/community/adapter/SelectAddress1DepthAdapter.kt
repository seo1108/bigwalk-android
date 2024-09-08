package kr.co.bigwalk.app.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.databinding.ItemRecyclerAddress1DepthBinding

class SelectAddress1DepthAdapter(
    private val itemClick: (String) -> Unit
) : ListAdapter<String, SelectAddress1DepthAdapter.SelectAddress1DepthViewHolder>(DIFF_CALLBACK) {

    private var selectedItemPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectAddress1DepthViewHolder =
        SelectAddress1DepthViewHolder(parent)

    override fun onBindViewHolder(holder: SelectAddress1DepthViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    fun setItemClick(first: String) {
        val position = currentList.indexOf(first)
        selectedItemPosition = position
        notifyItemChanged(position)
        itemClick(first)
    }

    inner class SelectAddress1DepthViewHolder(
        parent: ViewGroup,
        private val binding: ItemRecyclerAddress1DepthBinding =
            ItemRecyclerAddress1DepthBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(response: String) {
            with(binding) {
                title.text = response
                isSelected = selectedItemPosition == adapterPosition
                itemView.setOnClickListener {
                    if (selectedItemPosition == adapterPosition) return@setOnClickListener
                    if (selectedItemPosition != -1) {
                        notifyItemChanged(selectedItemPosition)
                    }
                    selectedItemPosition = adapterPosition
                    notifyItemChanged(adapterPosition)
                    itemClick(response)
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<String>() {
            override fun areItemsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem

            override fun areContentsTheSame(oldItem: String, newItem: String): Boolean =
                oldItem == newItem
        }
    }
}

