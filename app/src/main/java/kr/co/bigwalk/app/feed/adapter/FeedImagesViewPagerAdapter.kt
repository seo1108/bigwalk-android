package kr.co.bigwalk.app.feed.adapter

import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.databinding.ItemRecyclerFeedImagesViewpagerBinding

class FeedImagesViewPagerAdapter(
    private var mData: List<String> = emptyList<String>(),
    private val isLike: Boolean,
    private val doubleTabCallback: () -> Unit
): RecyclerView.Adapter<FeedImagesViewPagerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FeedImagesViewPagerViewHolder {
        return FeedImagesViewPagerViewHolder.from(parent, isLike, doubleTabCallback)
    }

    override fun onBindViewHolder(holder: FeedImagesViewPagerViewHolder, position: Int) {
        holder.bind(mData[position])
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    fun setItems(list: List<String>, position: Int = 0) {
        mData = list
        notifyItemChanged(position)
    }

}

class FeedImagesViewPagerViewHolder(
    private val binding: ItemRecyclerFeedImagesViewpagerBinding,
    private val isLike: Boolean,
    private val callback: () -> Unit
): RecyclerView.ViewHolder(binding.root) {

    fun bind(url: String) {
        binding.imageUrl = url

        var doubleClick = false

        binding.ivFeedImagesViewpagerImage.setOnClickListener {
            if (doubleClick) {
                callback.invoke()
            }
            doubleClick = true
            Handler(Looper.getMainLooper()).postDelayed({ doubleClick = false }, 500)
        }

    }

    companion object {
        fun from(parent: ViewGroup, isLike: Boolean, callback: () -> Unit): FeedImagesViewPagerViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemRecyclerFeedImagesViewpagerBinding.inflate(layoutInflater, parent, false)
            return FeedImagesViewPagerViewHolder(binding, isLike, callback)
        }
    }
}