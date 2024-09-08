package kr.co.bigwalk.app.sign_in.organization.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.organization.Content
import kr.co.bigwalk.app.data.organization.Department
import kr.co.bigwalk.app.databinding.ItemRecyclerSelectKeywordBinding

class SelectSearchKeywordAdapter(private val navigator: BaseNavigator) : RecyclerView.Adapter<SelectSearchKeywordAdapter.ItemViewHolder>(), Filterable {
    private var items = listOf<Content>()
    private var searchedItems = listOf<Content>()

    class ItemViewHolder(private val binding: ItemRecyclerSelectKeywordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var navigator: BaseNavigator
        fun bind(content: Content) {
            binding.item = content
            itemView.setOnClickListener {
                val intent = navigator.getContext().intent
                intent.putExtra("ContentItem", content)
                navigator.getContext().setResult(Activity.RESULT_OK, intent)
                navigator.getContext().finish()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemViewHolder = ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_select_keyword,
                parent,
                false
            )
        )
        itemViewHolder.navigator = navigator
        return itemViewHolder
    }

    override fun getItemCount(): Int {
        return searchedItems.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(searchedItems[position])
    }

    fun setItems(contents: List<Content>) {
        this.items = contents
        searchedItems = items
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    searchedItems = items
                } else {
                    val filteredList = ArrayList<Content>()
                    //이부분에서 원하는 데이터를 검색할 수 있음
                    for (row in items) {
                        if (row.content.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row)
                        }
                    }
                    searchedItems = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = searchedItems
                return filterResults
            }
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                searchedItems = filterResults.values as ArrayList<Content>
                notifyDataSetChanged()
            }
        }
    }
}