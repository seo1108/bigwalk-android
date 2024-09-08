package kr.co.bigwalk.app.campaign.recent

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignDonateDataManager
import kr.co.bigwalk.app.campaign.adapter.RecentCampaignRecyclerAdapter
import kr.co.bigwalk.app.databinding.ActivityRecentCampaignBinding
import kr.co.bigwalk.app.util.RecyclerDecoration

class RecentCampaignActivity : AppCompatActivity(), RecentCampaignNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityRecentCampaignBinding = DataBindingUtil.setContentView(this, R.layout.activity_recent_campaign)

        val viewModelFactory = RecentCampaignViewModelFactory(this)
        val viewModel = ViewModelProvider(this, viewModelFactory).get(RecentCampaignViewModel::class.java)
        val recentAdapter = RecentCampaignRecyclerAdapter(viewModel)
        binding.recentRecycler.adapter = recentAdapter
        binding.recentRecycler.addItemDecoration(RecyclerDecoration(32))
        binding.viewModel = viewModel
        
        CampaignDonateDataManager.updateCampaignDonateData.observe(this, Observer {
            recentAdapter.replaceItem(it.first, it.second, it.third)
        })
    }

    override fun getActivityFragmentManager(): FragmentManager {
        return supportFragmentManager
    }

    override fun getContext(): Activity {
        return this
    }
}