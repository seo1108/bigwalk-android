package kr.co.bigwalk.app.campaign.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.paging.PagedList
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.campaign.CampaignDonateDataManager
import kr.co.bigwalk.app.campaign.adapter.CampaignCategoryRecyclerAdapter
import kr.co.bigwalk.app.data.campaign.dto.ResponseCampaign
import kr.co.bigwalk.app.databinding.FrameCampaignByCategoryBinding

class CampaignByCategoryFragment: Fragment() {

    companion object {
        fun newInstance(categoryId: Long): CampaignByCategoryFragment {
            val bundle = Bundle()
            bundle.putLong("categoryId", categoryId)
            val fragment = CampaignByCategoryFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FrameCampaignByCategoryBinding = DataBindingUtil.inflate(inflater, R.layout.frame_campaign_by_category, container, false)
        val viewModel = arguments?.getLong("categoryId")?.let { CampaignByCategoryViewModel(it, childFragmentManager) }
        binding.viewModel = viewModel
        val adapter = viewModel?.let { CampaignCategoryRecyclerAdapter(it) }
        viewModel?.campaignsByCategory?.observe(this, Observer<PagedList<ResponseCampaign>> { pagedList ->
            adapter?.submitList(pagedList)
        })
        binding.campaignCategoryRecycler.adapter = adapter
        
        CampaignDonateDataManager.updateCampaignDonateData.observe(viewLifecycleOwner, Observer {
            viewModel?.invalidateDataSource()
        })

        return binding.root
    }

}