package kr.co.bigwalk.app.campaign.donation.report

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityDonationReportBinding

class DonationReportActivity : AppCompatActivity(),
    BaseNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding : ActivityDonationReportBinding = DataBindingUtil.setContentView(this, R.layout.activity_donation_report)
        binding.viewModel = DonationReportViewModel(this)
    }

    override fun getContext(): Activity {
        return this
    }

}