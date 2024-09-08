package kr.co.bigwalk.app.event

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BaseNavigator
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.BroadcastManager
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.mission.dto.WelcomeMissionCompleteRequest
import kr.co.bigwalk.app.databinding.ActivityEventBinding
import kr.co.bigwalk.app.event.my.MyMissionFragment
import kr.co.bigwalk.app.util.DebugLog

class EventActivity : AppCompatActivity(), BaseNavigator {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    lateinit var viewModel: EventViewModel
    private lateinit var broadcastReceiver: BroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
//        firebaseAnalytics.logEvent("event_view", Bundle())
        val binding: ActivityEventBinding = DataBindingUtil.setContentView(this, R.layout.activity_event)

        viewModel = EventViewModel(this, firebaseAnalytics)
        binding.viewModel = viewModel
        val myMissionFragment = MyMissionFragment.newInstance(this, firebaseAnalytics)
        supportFragmentManager.beginTransaction().add(R.id.event_list_frame, myMissionFragment).commit()
    }

    override fun onResume() {
        super.onResume()
        viewModel.requestMissions()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        viewModel.requestMissions()
    }

    override fun getContext(): Activity {
        return this
    }
}