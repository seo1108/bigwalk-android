package kr.co.bigwalk.app.campaign.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import kr.co.bigwalk.app.util.DebugLog

class RankingReportAdapter(fragment: Fragment): FragmentStateAdapter(fragment.childFragmentManager, fragment.lifecycle) {
    private val items: MutableList<Fragment> = ArrayList()
    private val pageIds: MutableList<Long> = ArrayList()

    override fun getItemCount(): Int {
        return items.size
    }

    override fun createFragment(position: Int): Fragment {
        return items[position]
    }

    fun addFragment(fragment: Fragment) {
        items.add(fragment)
        pageIds.add(fragment.hashCode().toLong())
        notifyItemInserted(itemCount)
    }

    fun clearFragment() {
        val itemCount = this.itemCount
        items.clear()
        pageIds.clear()
        notifyItemRangeRemoved(0, itemCount)
    }

    override fun getItemId(position: Int): Long {
        return pageIds[position]
    }

    override fun containsItem(itemId: Long): Boolean {
        return pageIds.contains(itemId)
    }
}