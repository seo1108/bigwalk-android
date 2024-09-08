package kr.co.bigwalk.app.feed_home.adapter

import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.feedHome.dto.ChallengeInfoResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerChallengeHomeBinding
import kr.co.bigwalk.app.extension.dpToPx
import kr.co.bigwalk.app.extension.dpToPxFloat
import kr.co.bigwalk.app.feed_home.ChallengeHomeViewModel
import kr.co.bigwalk.app.util.showToast

class ChallengeActiveListAdapter(
        private val viewModel: ChallengeHomeViewModel,
        private val navigator: BaseNavigator
    ): RecyclerView.Adapter<ChallengeActiveListAdapter.ItemViewHolder>() {

    private var items = listOf<ChallengeInfoResponse>()

    class ItemViewHolder(private val binding: ItemRecyclerChallengeHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var navigator: BaseNavigator
        fun bind(viewModel: ChallengeHomeViewModel, challengeHomeItem: ChallengeInfoResponse) {
            val displayMetrics = DisplayMetrics()
            navigator.getContext().windowManager.defaultDisplay.getMetrics(displayMetrics)
            // 좌우 margin 고려
            var screenWidth = displayMetrics.widthPixels - 18.dpToPx()

            val layoutParams = binding.rootChallengeHomeItem.layoutParams
            layoutParams.width = screenWidth/3 - (5.33f).dpToPxFloat()
            layoutParams.height = ((screenWidth/3)*1.46).toInt()
            binding.rootChallengeHomeItem.layoutParams = layoutParams

            val layoutParams1 = binding.blurText.layoutParams
            layoutParams1.width = screenWidth/3 - (5.33f).dpToPxFloat()
            layoutParams1.height = ((screenWidth/3)*0.73).toInt()
            binding.blurText.layoutParams = layoutParams1

            val layoutParams2 = binding.blurScreen.layoutParams
            layoutParams2.width = screenWidth/3 - (5.33f).dpToPxFloat()
            layoutParams2.height = ((screenWidth/3)*1.46).toInt()
            binding.blurScreen.layoutParams = layoutParams2

            binding.item = challengeHomeItem
            challengeHomeItem.open = challengeHomeItem.challengeType == "open"
            itemView.setOnClickListener {
                //showToast("id " + challengeHomeItem.id)
                viewModel.goChallengeDetail(challengeHomeItem.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemViewHolder = ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_challenge_home,
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

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(viewModel, items[position])
    }

    fun setItems(challengeHomeItem: List<ChallengeInfoResponse>) {
        this.items = challengeHomeItem
        notifyDataSetChanged()
    }
}