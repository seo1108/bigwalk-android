package kr.co.bigwalk.app.story.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.data.story.dto.StoryContentImageResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerStoryImagesViewpagerBinding

class StoryViewPagerRecyclerAdapter: RecyclerView.Adapter<StoryViewPagerViewHolder>() {

    private var mData = emptyList<StoryContentImageResponse>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewPagerViewHolder {
        return StoryViewPagerViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: StoryViewPagerViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun setItems(list: List<StoryContentImageResponse>) {
        mData = list
        notifyDataSetChanged()
    }
}

class StoryViewPagerViewHolder(
    private val binding: ItemRecyclerStoryImagesViewpagerBinding
): RecyclerView.ViewHolder(binding.root) {

    fun bind(data: StoryContentImageResponse) {
        binding.imageUrl = data.url
    }

    companion object {
        fun from(parent: ViewGroup): StoryViewPagerViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRecyclerStoryImagesViewpagerBinding.inflate(layoutInflater, parent, false)
            return StoryViewPagerViewHolder(binding)
        }
    }
}