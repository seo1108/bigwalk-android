package kr.co.bigwalk.app.lock_screen

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class LockScreenFragmentStateAdapter (private val lockScreenFragments : List<Fragment>, fragmentActivity : FragmentActivity) : FragmentStateAdapter(fragmentActivity) {


    override fun getItemCount(): Int {
        return lockScreenFragments .size
    }

    override fun createFragment(position: Int): Fragment {
        return lockScreenFragments [position]
    }


}
