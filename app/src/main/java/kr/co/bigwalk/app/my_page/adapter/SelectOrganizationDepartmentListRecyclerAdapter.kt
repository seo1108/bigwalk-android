package kr.co.bigwalk.app.my_page.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.organization.Department
import kr.co.bigwalk.app.databinding.ItemRecyclerSelectDepartmentBinding
import kr.co.bigwalk.app.databinding.ItemRecyclerSelectOrganizationDepartmentBinding
import kr.co.bigwalk.app.my_page.DepartmentSingleton
import kr.co.bigwalk.app.my_page.MyPageViewModel

class SelectOrganizationDepartmentListRecyclerAdapter(private val navigator: BaseNavigator, private val viewModel: MyPageViewModel) : RecyclerView.Adapter<SelectOrganizationDepartmentListRecyclerAdapter.ItemViewHolder>(), Filterable {

    private var items = listOf<Department>()
    private var searchedItems = listOf<Department>()

    class ItemViewHolder(private val binding: ItemRecyclerSelectOrganizationDepartmentBinding, private val viewModel: MyPageViewModel) :
        RecyclerView.ViewHolder(binding.root) {
        lateinit var navigator: BaseNavigator
        fun bind(department: Department) {
            binding.item = department
            itemView.setOnClickListener {
                viewModel.department.set(department)
                viewModel.departmentName.set(department.name)
                DepartmentSingleton.department = department
                navigator.finish()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemViewHolder = ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_select_organization_department,
                parent,
                false
            ), viewModel
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

    fun setItems(departments: List<Department>) {
        this.items = departments
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
                    val filteredList = ArrayList<Department>()
                    //이부분에서 원하는 데이터를 검색할 수 있음
                    for (row in items) {
                        if (row.name.toLowerCase().contains(charString.toLowerCase())) {
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
                searchedItems = filterResults.values as ArrayList<Department>
                notifyDataSetChanged()
            }
        }
    }

}