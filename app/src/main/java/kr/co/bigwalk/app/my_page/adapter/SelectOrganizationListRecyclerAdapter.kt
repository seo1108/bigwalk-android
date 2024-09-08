package kr.co.bigwalk.app.my_page.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.databinding.ItemRecyclerSelectOrganizationBinding
import kr.co.bigwalk.app.my_page.OrganizationSingleton
import kr.co.bigwalk.app.my_page.SelectOrganizationViewModel

class SelectOrganizationListRecyclerAdapter(private val viewModel: SelectOrganizationViewModel, private val navigator: BaseNavigator): RecyclerView.Adapter<SelectOrganizationListRecyclerAdapter.ItemViewHolder>() {

    private var items = listOf<Organization>()

    class ItemViewHolder(
        private val binding: ItemRecyclerSelectOrganizationBinding) : RecyclerView.ViewHolder(binding.root) {
        lateinit var viewModel: SelectOrganizationViewModel
        lateinit var navigator: BaseNavigator
        fun bind(organization: Organization) {
            binding.item = organization
            itemView.setOnClickListener {
                OrganizationSingleton.organization = organization
                navigator.finish()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val itemViewHolder = ItemViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_recycler_select_organization,
                parent,
                false
            )
        )
        itemViewHolder.viewModel = viewModel
        itemViewHolder.navigator = navigator
        return itemViewHolder
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setItems(organizationItem: List<Organization>) {
        this.items = organizationItem
        notifyDataSetChanged()
    }

}