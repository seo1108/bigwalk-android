package kr.co.bigwalk.app.community.share

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityGroupShareBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.community.CommunityInfoActivity


class GroupShareActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGroupShareBinding
    private val groupShareViewModel: GroupShareViewModel by lazy {
        ViewModelProvider(this, GroupShareViewModelFactory(groupId)).get(GroupShareViewModel::class.java)
    }
    private var groupId: Long = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_share)
        binding.lifecycleOwner = this
        FirebaseAnalytics.getInstance(this).logEvent("group_share_view", null)

        groupId = intent.getLongExtra(CommunityInfoActivity.PARM_GROUP_ID, -1L)
        groupShareViewModel
        
        setView()
    }
    
    private fun setView() {
        with(binding) {
            val tabList = resources.getStringArray(R.array.group_share_tab)
            
            groupSharePager.adapter = object : FragmentStateAdapter(
                supportFragmentManager,
                lifecycle
            ) {
                override fun getItemCount(): Int =
                    tabList.size
                
                override fun createFragment(position: Int): Fragment =
                    when (position) {
                        0 -> ShareMyInfluenceFragment.newInstance()
                        1 -> ShareGoalFragment.newInstance()
                        2 -> ShareReportFragment.newInstance()
                        else -> error("Invalid position")
                    }
            }
            
            TabLayoutMediator(bottomTab, groupSharePager) { tab, position ->
                tab.text = tabList[position]
            }.attach()
            
            btnBack.setOnClickListener {
                onBackPressed()
            }
        }
    }
    
    companion object {
        fun getIntent(context: Context, groupId: Long) =
            Intent(context, GroupShareActivity::class.java)
                .putExtra(CommunityInfoActivity.PARM_GROUP_ID, groupId)
    }
}