package kr.co.bigwalk.app.community.funding.request.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.community.CrewRequestResponse
import kr.co.bigwalk.app.databinding.ItemRecyclerCrewRequestListBinding
import kr.co.bigwalk.app.loadImage

class CrewRequestListAdapter(
    private val itemClick: (CrewRequestResponse) -> Unit
) : ListAdapter<CrewRequestResponse, CrewRequestListViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewRequestListViewHolder =
        CrewRequestListViewHolder(parent, itemClick)

    override fun onBindViewHolder(holder: CrewRequestListViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CrewRequestResponse>() {
            override fun areItemsTheSame(oldItem: CrewRequestResponse, newItem: CrewRequestResponse): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: CrewRequestResponse, newItem: CrewRequestResponse): Boolean =
                oldItem == newItem
        }
    }
}

class CrewRequestListViewHolder(
    val parent: ViewGroup,
    private val itemClick: (CrewRequestResponse) -> Unit,
    private val binding: ItemRecyclerCrewRequestListBinding =
        ItemRecyclerCrewRequestListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(response: CrewRequestResponse) {
        with(binding) {
            data = response
            loadImage(crewRequestProfile, null)
            crewRequestProfile.clipToOutline = true
            requestConfirmButton.setOnClickListener {
                itemClick(response)
            }
        }
    }
}