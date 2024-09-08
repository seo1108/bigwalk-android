package kr.co.bigwalk.app.my_page

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.google.firebase.analytics.FirebaseAnalytics
import com.kakao.sdk.user.UserApiClient
//import com.kakao.usermgmt.UserManagement
//import com.kakao.usermgmt.callback.LogoutResponseCallback
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.BaseActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.mission.WelcomeMissionStatus
import kr.co.bigwalk.app.databinding.ActivityMyPageBinding
import kr.co.bigwalk.app.my_page.withdraw.WithdrawActivity
import kr.co.bigwalk.app.sign_in.SignInActivity
import kr.co.bigwalk.app.util.EventObserver
import kr.co.bigwalk.app.util.showToast
import kr.co.bigwalk.app.walk.alarm.AlarmBroadcastReceiver
import kr.co.bigwalk.app.walk.sensor.WalkService
import kr.co.bigwalk.app.walk.sensor.WalkServiceManager

class MyPageActivity : BaseActivity<ActivityMyPageBinding>(R.layout.activity_my_page) {
    private val viewModel by viewModels<MyPageViewModel2>()

    companion object {
        var instance: MyPageActivity? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        instance = this

        setToolbar()
        setView()
        bindViewModel()
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.run {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.aos_icon_20_arrow)
            title = "프로필"
        }
    }

    private fun setView() {
        with(binding) {
            vm = viewModel
            userLogout.setOnClickListener {
                logout()
            }
            userWithdraw.setOnClickListener {
                val intent = Intent(this@MyPageActivity, WithdrawActivity::class.java)
                startActivity(intent)
            }


        }
    }

    private fun bindViewModel() {
        with(viewModel) {
            myProfile.observe(this@MyPageActivity, Observer {
                binding.interestList.removeAllViews()
                it.userConcerns?.forEach { tagList ->
                    val view = LayoutInflater.from(this@MyPageActivity)
                        .inflate(R.layout.view_crew_create_tag, binding.interestList, false)

                    val tagView: TextView = view.findViewById(R.id.tag)
                    tagView.text = tagList.name


                    binding.interestList.addView(view)
                }
            })
            moveEvent.observe(this@MyPageActivity, EventObserver {
                startActivity(MyPageModifyActivity.getIntent(this@MyPageActivity, it))
            })
        }
    }

    private fun logout() {
        showToast("로그아웃 되었습니다.")
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

        /*try {
            UserManagement.getInstance().requestLogout(object : LogoutResponseCallback() {
                override fun onCompleteLogout() {
                    // 로그아웃 완료 후 코드 작성
                }
            })
        } catch (e: Exception) { }*/
        try {
            UserApiClient.instance.logout { error ->
                if (error != null) {
                    Log.e("kakao_logout", "로그아웃 실패. SDK에서 토큰 삭제됨", error)
                } else {
                    Log.i("kakao_logout", "로그아웃 성공. SDK에서 토큰 삭제됨")
                }
            }
        } catch (e: Exception) { }

        startActivity(intent)
    }

    private fun startWalkService(context: Context, dailyStep: Int, donableStep: Int) {
        WalkServiceManager(context).syncStepToForeground(dailyStep, donableStep)
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchMyProfile()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.my_page_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }
            R.id.modify_profile -> {
                viewModel.moveToModifyMyPage()
                FirebaseAnalytics.getInstance(this).logEvent("all_profile_btn_edit_button", Bundle())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        OrganizationSingleton.organization = null
    }
}
//class MyPageActivity : AppCompatActivity(), BaseNavigator {
//    private lateinit var binding: ActivityUserProfileInformationBinding
//    lateinit var viewModel: MyPageViewModel
//
//    companion object{
//        var instance : MyPageActivity? = null
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        instance = this
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_user_profile_information)
//        viewModel = MyPageViewModel(this, supportFragmentManager, this)
//        binding.viewModel = viewModel
//    }
//
//    override fun onResume() {
//        super.onResume()
//        viewModel.getMyProfile()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        OrganizationSingleton.organization = null
//    }
//    override fun getContext(): Activity {
//        return this
//    }
//}