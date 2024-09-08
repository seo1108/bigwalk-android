package kr.co.bigwalk.app.campaign.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignViewModel
import kr.co.bigwalk.app.data.story.dto.Story
import kr.co.bigwalk.app.databinding.ItemRecyclerStoryForReadyBinding

class StoryForReadyAdapter(private val viewModel: CampaignViewModel) : PagedListAdapter<Story, StoryForReadyAdapter.ViewHolder>(DIFF_CALLBACK) {

    class ViewHolder(private val binding: ItemRecyclerStoryForReadyBinding, private val viewModel: CampaignViewModel) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(story: Story) {
            binding.story = story
            itemView.setOnClickListener {
                viewModel.moveToStoryTab()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_story_for_ready,
                parent,
                false
            ),
            viewModel
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
                return oldItem == newItem
            }

        }
    }
}