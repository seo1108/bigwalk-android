package kr.co.bigwalk.app.walk

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.gson.Gson
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.base.SignalBaseActivity
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.notification.NotiTypeId
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.databinding.ActivityWalkBinding
import kr.co.bigwalk.app.extension.extractUrl
import kr.co.bigwalk.app.notification.RealtimeNotification
import kr.co.bigwalk.app.sign_in.organization.OrganizationViewModel
import kr.co.bigwalk.app.util.*
import kr.co.bigwalk.app.walk.sensor.StepCallback
import kr.co.bigwalk.app.walk.sensor.WalkSensor
import kr.co.bigwalk.app.walk.sensor.WalkServiceManager


class WalkActivity : SignalBaseActivity(), WalkNavigator, StepCallback {

    companion object {
        var firebaseAnalytics: FirebaseAnalytics? = null
        const val USER_PROFILE = "USER_PROFILE"
        const val REQUEST_CODE_WELCOME = 1000
        const val KEY_REQ_ORGANIZATION = 100
    }

    private lateinit var binding: ActivityWalkBinding
    lateinit var viewModel: WalkViewModel
    lateinit var organizationViewModel: OrganizationViewModel
    private lateinit var walkFragment1: WalkFragment1
    private lateinit var broadcastReceiver: BroadcastReceiver
    private var isNoticeRequest = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_walk)
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        window?.apply {
            clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            statusBarColor = Color.TRANSPARENT
        }

        viewModel = WalkViewModel(this)
        binding.viewModel = this.viewModel
        binding.homeViewPager.setPageTransformer(HomeTransformer())

        WalkSensor.stepCallback = this

        setProgressPercentage(PreferenceManager.getDonableStepPercentage())

        walkFragment1 = WalkFragment1.newInstance()
        // 추후 이벤트성으로 스와이프 효과를 사용하기 위해 남겨둠.
        binding.homeViewPager.adapter = WalkFragmentStateAdapter(mutableListOf(walkFragment1), this)

        binding.walkBackgroundImage.setImageDrawable(ContextCompat.getDrawable(this, MainRandomViewMaker.getBackgroundResourceByTime()))

        handleEvent()
        checkPermission()
    }

    private fun checkPermission() {
        val permissions = mutableListOf<String>()

        /*// POST_NOTIFICATIONS 권한 체크 (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        // ACTIVITY_RECOGNITION 권한 체크 (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED) {
                permissions.add(Manifest.permission.ACTIVITY_RECOGNITION)
            }
        }

        // FOREGROUND_SERVICE_HEALTH 권한 체크 (Android 14+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE_HEALTH) == PackageManager.PERMISSION_DENIED) {
                permissions.add(Manifest.permission.FOREGROUND_SERVICE_HEALTH)
            }
        }

        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissions.toTypedArray(), 999)
        }*/

        var postNotification =
            ContextCompat.checkSelfPermission(this@WalkActivity, Manifest.permission.POST_NOTIFICATIONS)

        if (postNotification == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this@WalkActivity,
                arrayOf(
                    Manifest.permission.POST_NOTIFICATIONS
                ),
                999
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = ContextCompat.getSystemService(this@WalkActivity, AlarmManager::class.java)
            if (alarmManager?.canScheduleExactAlarms() == false) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    startActivity(intent)
                }
            }
        }
    }

    private fun handleEvent() {
        val data = intent.getStringExtra("data")
        Log.d("handleEvent", data.toString())

        if (intent.hasExtra(USER_PROFILE)) {
            val data = intent.getStringExtra(USER_PROFILE)
            data?.let {
                val userProfile = Gson().fromJson(it, UserProfileResponse::class.java)
                PreferenceManager.saveUserProfile(userProfile)
            }
        }
        // Push notification
        val type = intent.getIntExtra("type", -1)

        if (type == NotiTypeId.CAMPAIGN.id || type == NotiTypeId.STORY.id) {
            val campaignId = intent.getLongExtra("campaign_id", -1L)
            if (campaignId >= 0) {
                viewModel.moveToCampaignDetail(campaignId)
                RealtimeNotification.getInstance().disable()
            }
            return
        } else if (type == NotiTypeId.EVENT.id || type == NotiTypeId.EVENT_WIN.id) {
            val data = intent.getStringExtra("data")
            Log.d("handleEvent", data.toString())
            data?.extractUrl()?.let { url ->
                /*val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                //BigwalkApplication.context?.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                startActivity(intent)*/

                // 웹 페이지를 열 Intent 생성
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                // 시스템에 설치된 브라우저 앱 목록 가져오기
                val packageManager: PackageManager = packageManager
                val browserList = packageManager.queryIntentActivities(intent, 0)

                // 사용자에게 선택할 수 있는 브라우저 목록 생성
                val browserIntents = ArrayList<Intent>()
                for (resolveInfo in browserList) {
                    val packageName = resolveInfo.activityInfo.packageName
                    if (packageName != "kr.co.bigwalk.app") {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        browserIntent.`package` = packageName
                        browserIntents.add(browserIntent)
                    }
                }

                // 사용자에게 선택할 수 있는 브라우저 목록을 표시하는 다이얼로그 생성
                val chooserIntent = Intent.createChooser(intent, "Select Browser")
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, browserIntents.toTypedArray<Parcelable>())
                startActivity(chooserIntent)

                RealtimeNotification.getInstance().disable()
                Log.d("handleEvent 123", data.toString())
            }
            return
        }

        // Deep link
        viewModel.handleDeepLink(this, object : DeepLinkHandleCallback {
            override fun onError() {
                showInvalidAccessAlertDialog()
            }
        })

        // Notice
        //viewModel.requestNotice(supportFragmentManager)

    }

    private fun showInvalidAccessAlertDialog() {
        val dialog = AlertDialog.Builder(this)
            .setMessage(getString(R.string.alert_invalid_access_sub_title))
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
            }
            .create()

        dialog.show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.notifyStepChanged()
        viewModel.requestMissionStatus()
        viewModel.getOccupationInfo()


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d("TTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTTT", "REQUEST PERMISSION" + " " + isPermissionGranted(this, Manifest.permission.FOREGROUND_SERVICE_HEALTH))
            /*if (!isPermissionGranted(this, Manifest.permission.FOREGROUND_SERVICE_HEALTH)) {
                requestPermission(this, Manifest.permission.FOREGROUND_SERVICE_HEALTH)
            }*/

            if (isPermissionGranted(this, Manifest.permission.ACTIVITY_RECOGNITION)) {
                requestPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            } else
                startWalkServiceAndRegisterStepSensor()
        } else
            startWalkServiceAndRegisterStepSensor()

        RealtimeNotification.getInstance().sync()

        if (!isNoticeRequest) {
            isNoticeRequest = true
            viewModel.requestNotice(supportFragmentManager)
        }
    }

    private fun startWalkServiceAndRegisterStepSensor() {
        WalkServiceManager(this, object : WalkServiceManager.SyncBGToFGCallback {
            override fun onSuccess() {
                viewModel.notifyStepChanged()
            }
        }).startWalkService(true)
    }

    override fun setProgressPercentage(percentage: Int) {
        if (isFinishing) return
        var adjustPercentage = percentage
        if (percentage < 0) adjustPercentage = 0
        if (percentage > 100) adjustPercentage = 100
        val level = 10000 * adjustPercentage / 100
        binding.walkBottomProgress.background.level = level
    }

    override fun getContext(): Activity {
        return this
    }

    override fun onStepChanged(dailyStep: Int, donableStep: Int) {
        viewModel.notifyStepChanged()
    }

    override fun onBackPressed() {
        finishAffinity()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.isEmpty()) return
            permissions.forEach {
                if (it == Manifest.permission.ACTIVITY_RECOGNITION) {
                    if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                        startWalkServiceAndRegisterStepSensor()
                    } else {
                        showToast("걸음을 측정하기 위해 신체활동 권한 허가가 필요합니다.")
                        return
                    }
                }
            }
        }
    }

    /*override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            KEY_REQ_ORGANIZATION -> {
                if (resultCode == Activity.RESULT_OK) {
                    if (data?.getSerializableExtra(OrganizationViewModel.KEY_CORPORATE_MEMBER_FORM) != null) {
                        val test = data.getSerializableExtra(OrganizationViewModel.KEY_CORPORATE_MEMBER_FORM) as CorporateMemberDeliveryInputForm
                        organizationViewModel.afterSelectOrganization(test)
                    }
                }
            }
        }
    }*/
}