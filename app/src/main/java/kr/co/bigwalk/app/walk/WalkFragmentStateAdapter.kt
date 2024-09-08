package kr.co.bigwalk.app.walk

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class WalkFragmentStateAdapter (private val walkFragments : List<Fragment>, fragmentActivity : FragmentActivity) : FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return walkFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return walkFragments[position]
    }

}
