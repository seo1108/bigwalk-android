package kr.co.bigwalk.app.my_page

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.mission.WelcomeMissionStatus
import kr.co.bigwalk.app.databinding.ActivityLogoutBinding
import kr.co.bigwalk.app.sign_in.SignInActivity
import kr.co.bigwalk.app.util.DeepLinkStorage
import kr.co.bigwalk.app.util.showToast
import kr.co.bigwalk.app.walk.alarm.AlarmBroadcastReceiver
import kr.co.bigwalk.app.walk.sensor.WalkService
import kr.co.bigwalk.app.walk.sensor.WalkServiceManager

class LogoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLogoutBinding
    lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_logout)
        binding.lifecycleOwner = this

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.setCurrentScreen(this, "splash_view", null)

        logout()
    }
    private fun logout() {
        showToast("다른 기기에서 로그인했거나 세션이 만료되었습니다.")
        PreferenceManager.saveAccessToken(null)
        PreferenceManager.saveDailyStep(0)
        PreferenceManager.saveDonableStep(0)
        PreferenceManager.saveLastMissisonTitle(null)
        PreferenceManager.saveTutorial(false)

        // 웰컴 미션 초기화
        PreferenceManager.saveWelcomeMission1(-1)
        PreferenceManager.saveWelcomeMission1Max(0)
        PreferenceManager.saveWelcomeMission1Completed(false)
        PreferenceManager.saveWelcomeMission1ClearConfirmed(false)
        PreferenceManager.saveWelcomeMission2(-1)
        PreferenceManager.saveWelcomeMission2Max(0)
        PreferenceManager.saveWelcomeMission2Completed(false)
        PreferenceManager.saveWelcomeMission2ClearConfirmed(false)
        PreferenceManager.saveWelcomeMissionStatus(WelcomeMissionStatus.NONE.type)
        AlarmBroadcastReceiver.cancelMissionAlarmManager(this)

        startWalkService(this, 0, 0)
        WalkService.stopService(this)
        val intent = Intent(this, SignInActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun startWalkService(context: Context, dailyStep: Int, donableStep: Int) {
        WalkServiceManager(context).syncStepToForeground(dailyStep, donableStep)
    }
}