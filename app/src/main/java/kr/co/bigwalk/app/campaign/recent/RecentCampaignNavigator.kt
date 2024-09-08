package kr.co.bigwalk.app.campaign.recent

import androidx.fragment.app.FragmentManager
import kr.co.bigwalk.app.BaseNavigator

interface RecentCampaignNavigator : BaseNavigator{
    fun getActivityFragmentManager() : FragmentManager
}