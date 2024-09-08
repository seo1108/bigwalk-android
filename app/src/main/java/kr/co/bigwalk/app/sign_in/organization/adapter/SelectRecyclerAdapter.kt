package kr.co.bigwalk.app.sign_in.organization.adapter

import android.app.Activity.RESULT_OK
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.organization.Department
import kr.co.bigwalk.app.data.organization.Organization
import kr.co.bigwalk.app.data.organization.OrganizationItem
import kr.co.bigwalk.app.databinding.ItemRecyclerSelectBinding

class SelectRecyclerAdapter(private val navigator: BaseNavigator, private val isSelectOrganization: Boolean): RecyclerView.Adapter<SelectRecyclerAdapter.ItemViewHolder>() {

    private var items = listOf<OrganizationItem>()

    class ItemViewHolder (
        private val binding: ItemRecyclerSelectBinding,
        private val navigator: BaseNavigator,
        private val isSelectOrganization: Boolean
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(organizationItem: OrganizationItem) {
            binding.item = organizationItem
            itemView.setOnClickListener {
                if (isSelectOrganization && organizationItem is Organization) {
                    val intent = navigator.getContext().intent
                    intent.putExtra("OrganizationItem", organizationItem)
                    navigator.getContext().setResult(RESULT_OK, intent)
                    navigator.getContext().finish()
                } else {
                    val intent = navigator.getContext().intent
                    intent.putExtra("DepartmentItem", organizationItem as Department)
                    navigator.getContext().setResult(RESULT_OK, intent)
                    navigator.getContext().finish()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_recycler_select,
            parent,
            false
        ), navigator
        , isSelectOrganization)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun setItems(organizationItems: List<OrganizationItem>) {
        this.items = organizationItems
        notifyDataSetChanged()
    }

}