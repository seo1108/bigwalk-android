package kr.co.bigwalk.app.crowd_funding.detail.crew

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.DEF_LONG_VALUE
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.databinding.ActivityCrewJoinFromBinding
import kr.co.bigwalk.app.util.EventObserver
import kr.co.bigwalk.app.util.showToast

class CrewJoinFromActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCrewJoinFromBinding
    private val crewJoinFromViewModel by lazy {
        ViewModelProvider(this, CrewJoinFormViewModelFactory(crewCampaignId)).get(CrewJoinFromViewModel::class.java)
    }
    private val crewCampaignId: Long by lazy { intent.getLongExtra(KEY_CREW_CAMPAIGN_ID, DEF_LONG_VALUE) }
    private val question: String by lazy { intent.getStringExtra(KEY_QUESTION).orEmpty() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crew_join_from)
        binding.lifecycleOwner = this

        FirebaseAnalytics.getInstance(this).logEvent("crew_join_request_view", Bundle())

        setView()
        bindView()
    }

    private fun setView() {
        with(binding) {
            questionTitle.text = "Q. $question"
            exitButton.setOnClickListener {
                finish()
            }
            applyBtn.setOnClickListener {
                FirebaseAnalytics.getInstance(this@CrewJoinFromActivity).logEvent("crew_join_request_button_apply_button", Bundle())
                crewJoinFromViewModel.applyForCrewCampaign(inputView.contentView.text.toString())
            }
        }
    }


    private fun bindView() {
        with(crewJoinFromViewModel) {
            toastMessage.observe(this@CrewJoinFromActivity, EventObserver {
                showToast(it)
            })
            successEvent.observe(this@CrewJoinFromActivity, EventObserver {
                finish()
            })
        }
    }

    companion object {
        private const val KEY_QUESTION = "QUESTION"
        private const val KEY_CREW_CAMPAIGN_ID = "CREW_CAMPAIGN_ID"
        fun getIntent(context: Context, question: String, crewCampaignId: Long) =
            Intent(context, CrewJoinFromActivity::class.java).apply {
                putExtra(KEY_QUESTION, question)
                putExtra(KEY_CREW_CAMPAIGN_ID, crewCampaignId)
            }
    }
}