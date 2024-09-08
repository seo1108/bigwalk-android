package kr.co.bigwalk.app.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.databinding.ItemRecyclerAddress2DepthBinding

class SelectAddress2DepthAdapter(
    private val itemClick: (String) -> Unit
) : ListAdapter<String, SelectAddress2DepthAdapter.SelectAddress2DepthViewHolder>(DIFF_CALLBACK) {

    private var selectedItemPosition = -1
    private var initItemClick = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectAddress2DepthViewHolder =
        SelectAddress2DepthViewHolder(parent)
    
    override fun onBindViewHolder(holder: SelectAddress2DepthViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    fun reselectPosition(){
        selectedItemPosition = -1
        notifyDataSetChanged()
    }

    fun setItemClick(second: String) {
        if (initItemClick.not()) {
            val position = currentList.indexOf(second)
            selectedItemPosition = position
            notifyItemChanged(position)
            itemClick(second)
            initItemClick = true
        }
    }

    inner class SelectAddress2DepthViewHolder(
        parent: ViewGroup,
        private val binding: ItemRecyclerAddress2DepthBinding =
            ItemRecyclerAddress2DepthBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(response: String) {
            with(binding) {
                title.text = response
                isSelected = selectedItemPosition == adapterPosition
                itemView.setOnClickListener {
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