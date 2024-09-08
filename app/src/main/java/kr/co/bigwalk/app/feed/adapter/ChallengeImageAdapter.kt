package kr.co.bigwalk.app.feed.adapter

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.feed.dto.ChallengeImageItem
import kr.co.bigwalk.app.databinding.ItemRecyclerChallengePostBinding
import kr.co.bigwalk.app.feed.ChallengeDetailViewModel
import kr.co.bigwalk.app.util.DebugLog

class ChallengeImageAdapter(
    private val viewModel: ChallengeDetailViewModel,
    private val imageWidth: Int,
    private val navigator: BaseNavigator
) : RecyclerView.Adapter<ChallengeImageAdapter.ItemViewHolder>() {

    private var items = mutableListOf<ChallengeImageItem>()

    //private val asyncDiffer = AsyncListDiffer(this, DiffUtilCallback())
    private val asyncDiffer: AsyncListDiffer<ChallengeImageItem> = AsyncListDiffer(this, DIFF_CALLBACK)

    class ItemViewHolder(private val binding: ItemRecyclerChallengePostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var navigator: BaseNavigator

        fun bind(viewModel: ChallengeDetailViewModel, challengeItem: ChallengeImageItem, position: Int, imageWidth: Int) {
            binding.item = challengeItem

            val layoutParams = binding.gridView.layoutParams
            layoutParams.width = imageWidth
            layoutParams.height = imageWidth
            binding.gridView.layoutParams = layoutParams

            Glide.with(navigator.getContext())
                .asBitmap()
                .load(challengeItem.imagePath)
                //.skipMemoryCache(true)
                //.override(imageWidth,imageWidth)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(binding.challengeImage)

            if (challengeItem.imageCount!! > 1L) binding.multiImage.visibility = View.VISIBLE

            itemView.setOnClickListener {
                //viewModel.goFeedDetail()

                var currentPage = position / kr.co.bigwalk.app.feed.ChallengeDetailActivity.PAGE_SIZE
                DebugLog.d("department=0_________currentPage $currentPage $position ${kr.co.bigwalk.app.feed.ChallengeDetailActivity.Companion.PAGE_SIZE}")

                viewModel.challengeId.set(viewModel!!.challengeDetail.get()!!.id)
                viewModel.imageId.set(challengeItem.id!!)
                viewModel.currentPage.set(currentPage)
                viewModel.size.set(kr.co.bigwalk.app.feed.ChallengeDetailActivity.Companion.PAGE_SIZE)

                viewModel!!.challengeDetail.get()!!.id?.let { it1 ->
                    PreferenceManager.saveSelectedFeedInfo(
                        it1, challengeItem.id!!, currentPage,
                        kr.co.bigwalk.app.feed.ChallengeDetailActivity.PAGE_SIZE, position)
                }

                viewModel.goFeedDetail()
                /*
                viewModel!!.challengeDetail.get()!!.id?.let { it1 ->
                    viewModel.requestHitFeedList(
                        it1, challengeItem.id!!, 0, currentPage, PAGE_SIZE)
                }*/
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemViewHolder = ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_challenge_post,
                parent,
                false
            )
        )

        itemViewHolder.navigator = navigator
        return itemViewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun removeItem(position: Int) {
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(viewModel, items[position], position, imageWidth)
    }

    fun setItems(challengeItem: MutableList<ChallengeImageItem>) {
        /*this.items = challengeItem
        notifyDataSetChanged()*/
        this.items = challengeItem
        asyncDiffer.submitList(challengeItem)
    }

    companion object {

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ChallengeImageItem>() {
            override fun areItemsTheSame(oldItem: ChallengeImageItem, newItem: ChallengeImageItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ChallengeImageItem, newItem: ChallengeImageItem): Boolean {
                return oldItem == newItem
            }
        }
    }

    /*class DiffUtilCallback(): DiffUtil.ItemCallback<Feed>(){

        override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem.actionDonationHistoryId == newItem.actionDonationHistoryId
        }

        override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem == newItem
        }
    }*/

    /*private val differCallback = object : DiffUtil.ItemCallback<Feed>(){
        override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return  oldItem.actionDonationHistoryId == newItem.actionDonationHistoryId
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,differCallback)*/
}

