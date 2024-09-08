package kr.co.bigwalk.app.community.funding.create

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.funding.RequiredToCreateIds
import kr.co.bigwalk.app.databinding.ActivityCreateFundingBinding

class CreateFundingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateFundingBinding
    private lateinit var createFundingViewModel: CreateFundingViewModel
    private val requiredToCreateIds: RequiredToCreateIds by lazy { intent.getSerializableExtra(KEY_REQUIRED_TO_CREATE_ID) as RequiredToCreateIds }
    private val crewCampaignId: Long by lazy { intent.getLongExtra(KEY_CREW_CAMPAIGN_ID, DEF_LONG_VALUE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_funding)
        binding.lifecycleOwner = this
        overridePendingTransition(R.anim.anim_horizon_enter, R.anim.none)

        createFundingViewModel = ViewModelProvider(this, CreateFundingViewModelFactory(requiredToCreateIds)).get(CreateFundingViewModel::class.java)
        supportFragmentManager.beginTransaction()
            .replace(R.id.create_label_container, CreateLabel1Fragment.newInstance())
            .commit()

        this.window?.apply {
            this.statusBarColor = Color.TRANSPARENT
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        setView()
    }

    private fun setView() {
        with(binding) {
            createFundingViewModel.requestBeforeData(crewCampaignId)
        }
    }

    override fun onBackPressed() {
        val fragment = supportFragmentManager.findFragmentById(R.id.create_label_container)

        if (fragment is CreateLabel2Fragment) {
            createFundingViewModel.requestBeforeData()
        } else if (fragment is CreateLabel1Fragment) {
            if (createFundingViewModel.crewCampaignId != null) setResult(RESULT_OK)
        }
        super.onBackPressed()
        overridePendingTransition(R.anim.none, R.anim.anim_horizon_exit)
    }

    companion object {
        private const val KEY_REQUIRED_TO_CREATE_ID = "REQUIRED_TO_CREATE_ID"
        private const val KEY_CREW_CAMPAIGN_ID = "CREW_CAMPAIGN_ID"
        fun getIntent(context: Context, requiredToCreateIds: RequiredToCreateIds, crewCampaignId: Long? = null) =
            Intent(context, CreateFundingActivity::class.java).apply {
                putExtra(KEY_REQUIRED_TO_CREATE_ID, requiredToCreateIds)
                putExtra(KEY_CREW_CAMPAIGN_ID, crewCampaignId)
            }

    }
}