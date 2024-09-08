package kr.co.bigwalk.app.community.share

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.data.community.StickerType
import kr.co.bigwalk.app.databinding.ItemRecyclerStickerBinding
import java.util.*

class StickerAdapter(
    private val itemClick: (StickerItem) -> Unit
) : ListAdapter<StickerItem, StickerViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StickerViewHolder =
        StickerViewHolder(parent, itemClick)
    
    override fun onBindViewHolder(holder: StickerViewHolder, position: Int) {
        holder.bind(currentList[position])
    }
    
    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StickerItem>() {
            override fun areItemsTheSame(oldItem: StickerItem, newItem: StickerItem): Boolean =
                oldItem.id == newItem.id
            
            override fun areContentsTheSame(oldItem: StickerItem, newItem: StickerItem): Boolean =
                oldItem == newItem
        }
    }
}

class StickerViewHolder(
    parent: ViewGroup,
    private val itemClick: (StickerItem) -> Unit,
    private val binding: ItemRecyclerStickerBinding =
        ItemRecyclerStickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: StickerItem) {
        with(binding) {
            data = item
            root.setOnClickListener {
                itemClick(item)
            }
        }
    }
}

data class StickerItem(
    val id: Int,
    val name: String,
    val imagePath: String,
    val expiredDate: Date? = null,
    val type: StickerType,
    val new: Boolean
)