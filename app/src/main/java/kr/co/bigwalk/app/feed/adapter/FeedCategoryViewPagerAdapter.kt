package kr.co.bigwalk.app.feed.adapter

import android.view.ViewGroup
import androidx.databinding.ObservableList
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import kr.co.bigwalk.app.feed.category.FeedByCategoryFragment

class FeedCategoryViewPagerAdapter(private val supportManager: FragmentManager) :
    FragmentPagerAdapter(supportManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private var fragments = arrayListOf<Fragment>()

    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        supportManager.beginTransaction().remove(`object` as Fragment).commitNowAllowingStateLoss()
    }

    fun setItems(items: ObservableList<FeedByCategoryFragment>) {
        fragments.addAll(items)
        notifyDataSetChanged()
    }
}