package kr.co.bigwalk.app.splash

import android.Manifest
import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.CheckBox
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.gson.Gson
import com.kakao.sdk.common.util.Utility
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.PreferenceManager
import kr.co.bigwalk.app.data.organization.GroupType
import kr.co.bigwalk.app.data.user.dto.AutoLoginResponse
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.databinding.ActivitySplashBinding
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.sign_in.SignInActivity
import kr.co.bigwalk.app.util.BlackUser
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.DeepLinkStorage
import kr.co.bigwalk.app.util.showToast
import kr.co.bigwalk.app.walk.WalkActivity
import java.security.InvalidKeyException
import java.util.*


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    lateinit var firebaseAnalytics: FirebaseAnalytics
    private val remoteConfig = FirebaseRemoteConfig.getInstance()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        binding.lifecycleOwner = this
        
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.setCurrentScreen(this, "splash_view", null)

        showSplashImage()

        //getHashKey()
    }
    
    private fun showSplashImage() {
        binding.splashImage.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                remoteConfigInit()
                DeepLinkStorage(this@SplashActivity).save(intent)
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {
            }

        })
    }


    // 구글 플레이스토어 설치되어 있는지 확인
    private fun checkGooglePlayServices() {
        val googleApiAvailability = GoogleApiAvailability.getInstance()
        val status = googleApiAvailability.isGooglePlayServicesAvailable(this)

        if (status != ConnectionResult.SUCCESS) {
            val dialog = googleApiAvailability.getErrorDialog(this, status, -1)
            dialog.setOnDismissListener { finish() }
            dialog.show()

            googleApiAvailability.showErrorNotification(this, status)
        }
    }

    private fun remoteConfigInit() {
        // 기본 캐쉬 만료시간은 12시간.
        remoteConfig.fetch(0)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    remoteConfig.activate()
                    val mHandler = Handler()
                    mHandler.postDelayed(Runnable {
                        if (!checkIsServiceCheckInProgress()) {
                            checkVersion()
                        }
                    }, 500) // 0.5초후
                } else {
                    showToast("앱 최신 버전을 가져오지 못했습니다. 인터넷 환경 확인 후 다시 실행해주세요.")
                    /*if (!PreferenceManager.getAccessToken().isNullOrBlank()){
                        getUserProfile()
                    } else {
                        FirebaseCrashlytics.getInstance().log("checkVersion()-accessToken-2: ${PreferenceManager.getAccessToken() ?: "empty"}")
                        FirebaseCrashlytics.getInstance().recordException(InvalidKeyException())
                        moveToSignInActivity()
                    }*/
                }
            }
    }

    private fun checkIsServiceCheckInProgress(): Boolean {
        try {
            val serverCheckInProgress: Int = Integer.parseInt(remoteConfig.getString("server_check_in_progress"))
            if (serverCheckInProgress > 0) {
                val serverCheckInProgressMessage: String = remoteConfig.getString("server_check_in_progress_message")
                showServerCheckInProgressDialog(serverCheckInProgressMessage)
                return true
            }
        } catch (exception: NumberFormatException) { }
        return false
    }

    private fun checkVersion() {
        // firebase remote의 버전
        val latestVersion: String = remoteConfig.getString("lastest_version_android")
        // 앱 내부 버전
        val currentVersionName = getAppVersionName(this)

        //DebugLog.d("버전 체크 :  ${latestVersion}___${currentVersionName}")

        try {
            // 첫번째나 두번째 버전이 더 크면 업데이트 (1.19.7)
            if ((latestVersion.split(".")[1].toInt() > currentVersionName.split(".")[1].toInt())
                || (latestVersion.split(".")[0].toInt() > currentVersionName.split(".")[0].toInt())
            ) {
                showForceUpdateDialog()
            } else if (latestVersion.split(".")[2].toInt() > currentVersionName.split(".")[2].toInt()) {
                showUpdateDialog()
            } else {
                if (!PreferenceManager.getAccessToken().isNullOrBlank()) {
                    getUserProfile()
                } else {
                    FirebaseCrashlytics.getInstance()
                        .log("checkVersion()-accessToken-2: ${PreferenceManager.getAccessToken() ?: "empty"}")
                    FirebaseCrashlytics.getInstance().recordException(InvalidKeyException())
                    moveToSignInActivity()
                }
            }
        } catch (e: Exception) {
            if (!PreferenceManager.getAccessToken().isNullOrBlank()) {
                getUserProfile()
            } else {
                FirebaseCrashlytics.getInstance()
                    .log("checkVersion()-accessToken-2: ${PreferenceManager.getAccessToken() ?: "empty"}")
                FirebaseCrashlytics.getInstance().recordException(InvalidKeyException())
                moveToSignInActivity()
            }
        }

        /*if (currentVersionName.replace(".", "").toInt() >= latestVersion.replace(".", "").toInt()) {
            if (!PreferenceManager.getAccessToken().isNullOrBlank()){
                getUserProfile()
            } else {
                FirebaseCrashlytics.getInstance().log("checkVersion()-accessToken-1: ${PreferenceManager.getAccessToken() ?: "empty"}")
                FirebaseCrashlytics.getInstance().recordException(InvalidKeyException())
                moveToSignInActivity()
            }
        } else {
            // 첫번째나 두번째 버전이 더 크면 업데이트 (1.19.7)
            if ( (latestVersion.split(".")[1].toInt() != currentVersionName.split(".")[1].toInt())
                || (latestVersion.split(".")[0].toInt() != currentVersionName.split(".")[0].toInt()) ) {
                showForceUpdateDialog()
            } else if (latestVersion.split(".")[2].toInt() != currentVersionName.split(".")[2].toInt()) {
                showUpdateDialog()
            } else {
                if (!PreferenceManager.getAccessToken().isNullOrBlank()){
                    getUserProfile()
                } else {
                    FirebaseCrashlytics.getInstance().log("checkVersion()-accessToken-2: ${PreferenceManager.getAccessToken() ?: "empty"}")
                    FirebaseCrashlytics.getInstance().recordException(InvalidKeyException())
                    moveToSignInActivity()
                }
            }

            *//*val calToday = Calendar.getInstance()
            val today = Date(calToday.timeInMillis)
            val calLast = Calendar.getInstance()
            if (PreferenceManager.getLastAlertDate() == null) {
                showUpdateDialog()
            } else {
                calLast.time = PreferenceManager.getLastAlertDate()
                calLast.add(Calendar.DAY_OF_MONTH, 3)
                val dateCal = Date(calLast.timeInMillis)
                val compare: Int = today.compareTo(dateCal)
                if (compare > 0) {
                    showUpdateDialog()
                } else {
                    if (!PreferenceManager.getAccessToken().isNullOrBlank()){
                        getUserProfile()
                    } else {
                        FirebaseCrashlytics.getInstance().log("checkVersion()-accessToken-2: ${PreferenceManager.getAccessToken() ?: "empty"}")
                        FirebaseCrashlytics.getInstance().recordException(InvalidKeyException())
                        moveToSignInActivity()
                    }
                }
            }*//*

        }*/
    }

    private fun getAppVersion(context: Context): Int {
        var result = 0

        try {
            result = context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionCode
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("getAppVersion", e.message.orEmpty())
        }

        return result
    }

    private fun getAppVersionName(context: Context): String {
        var result = ""

        try {
            result = context.packageManager
                .getPackageInfo(context.packageName, 0)
                .versionName

            DebugLog.d("버전 체크 1 $result")
            if (result.contains("-")) {
                result = result.split("-")[0]
            }
            DebugLog.d("버전 체크 2 $result")
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("getAppVersion", e.message.orEmpty())
        }

        return result
    }

    private fun showForceUpdateDialog() {
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_app_version, null)
        val checkBox = dialogView.findViewById<View>(R.id.checkBox) as CheckBox
        checkBox.visibility = View.GONE
        val dialog = AlertDialog.Builder(this, R.style.AppUpdateDialogStyle)
            .setTitle(resources.getString(R.string.update_alert_title))
            .setMessage(resources.getString(R.string.update_alert_message))
            .setView(dialogView)
            .setPositiveButton(resources.getString(R.string.update_alert_positive)) { dialog, _ ->
                goToPlayStore()
                finish()
            }
            /*.setNegativeButton(resources.getString(R.string.update_alert_negative)) { dialog, _ ->
                if (checkBox.isChecked) {
                    val calToday = Calendar.getInstance()
                    val today = Date(calToday.timeInMillis)
                    PreferenceManager.saveLastAlertDate(today)
                }

                if (!PreferenceManager.getAccessToken().isNullOrBlank()){
                    getUserProfile()
                } else {
                    FirebaseCrashlytics.getInstance().log("showUpdateDialog()-accessToken-1: ${PreferenceManager.getAccessToken() ?: "empty"}")
                    FirebaseCrashlytics.getInstance().recordException(InvalidKeyException())
                    moveToSignInActivity()
                }
            }*/
            .setCancelable(false)
            .create()

        dialog.show()
        dialog.setOnCancelListener {
            if (checkBox.isChecked) {
                val calToday = Calendar.getInstance()
                val today = Date(calToday.timeInMillis)
                PreferenceManager.saveLastAlertDate(today)
            }

            if (!PreferenceManager.getAccessToken().isNullOrBlank()){
                getUserProfile()
            } else {
                FirebaseCrashlytics.getInstance().log("showUpdateDialog()-accessToken-2: ${PreferenceManager.getAccessToken() ?: "empty"}")
                FirebaseCrashlytics.getInstance().recordException(InvalidKeyException())
                moveToSignInActivity()
            }
        }
    }

    private fun showUpdateDialog() {
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_app_version, null)
        val checkBox = dialogView.findViewById<View>(R.id.checkBox) as CheckBox
        val dialog = AlertDialog.Builder(this, R.style.AppUpdateDialogStyle)
            .setTitle(resources.getString(R.string.update_alert_title))
            .setMessage(resources.getString(R.string.update_alert_message))
            .setView(dialogView)
            .setPositiveButton(resources.getString(R.string.update_alert_positive)) { dialog, _ ->
                goToPlayStore()
                finish()
            }
            .setNegativeButton(resources.getString(R.string.update_alert_negative)) { dialog, _ ->
                if (checkBox.isChecked) {
                    val calToday = Calendar.getInstance()
                    val today = Date(calToday.timeInMillis)
                    PreferenceManager.saveLastAlertDate(today)
                }

                if (!PreferenceManager.getAccessToken().isNullOrBlank()){
                    getUserProfile()
                } else {
                    FirebaseCrashlytics.getInstance().log("showUpdateDialog()-accessToken-1: ${PreferenceManager.getAccessToken() ?: "empty"}")
                    FirebaseCrashlytics.getInstance().recordException(InvalidKeyException())
                    moveToSignInActivity()
                }
            }
            .setCancelable(false)
            .create()

        dialog.show()
        dialog.setOnCancelListener {
            if (checkBox.isChecked) {
                val calToday = Calendar.getInstance()
                val today = Date(calToday.timeInMillis)
                PreferenceManager.saveLastAlertDate(today)
            }

            if (!PreferenceManager.getAccessToken().isNullOrBlank()){
                getUserProfile()
            } else {
                FirebaseCrashlytics.getInstance().log("showUpdateDialog()-accessToken-2: ${PreferenceManager.getAccessToken() ?: "empty"}")
                FirebaseCrashlytics.getInstance().recordException(InvalidKeyException())
                moveToSignInActivity()
            }
        }
    }

    private fun showServerCheckInProgressDialog(message: String) {
        val dialogView: View = layoutInflater.inflate(R.layout.dialog_server_check, null)
        val dialog = AlertDialog.Builder(this, R.style.AppUpdateDialogStyle)
                .setTitle(resources.getString(R.string.server_check_in_progress_alert_title))
                .setMessage(message)
                .setView(dialogView)
                .setPositiveButton(resources.getString(R.string.finish)) { dialog, _ ->
                    finish()
                }
                .create()
        dialog.show()
    }

    private fun goToPlayStore() {
        val marketLaunch = Intent(Intent.ACTION_VIEW)
        marketLaunch.data = Uri.parse("https://play.google.com/store/apps/details?id=kr.co.bigwalk.app")
        startActivity(marketLaunch)
    }

    private fun moveToHomeActivity(userProfile: UserProfileResponse) {
        val intent = Intent(this@SplashActivity, WalkActivity::class.java)
        intent.putExtra(WalkActivity.USER_PROFILE, Gson().toJson(userProfile))
        startActivity(intent)
        finish()
    }

    private fun moveToSignInActivity() {
        FirebaseCrashlytics.getInstance().log("moveToSignInActivity()-accessToken-1: ${PreferenceManager.getAccessToken() ?: "empty"}")
        val intent = Intent(this@SplashActivity, SignInActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun getUserProfile() {
        UserRepository.checkAutoLogin(object : UserDataSource.CheckAutoLoginCallback {
            override fun onSuccess(autoLoginResponse: AutoLoginResponse) {
                autoLoginResponse.megaOrganization?.let { organization ->
                    PreferenceManager.saveOrganization(organization.id?:-1L)
                    organization.name?.let { PreferenceManager.saveOrganizationName(it) }
                    autoLoginResponse.megaDepartment?.let { department ->
                        department.name?.let { PreferenceManager.saveDepartmentName(it) }
                    }
                }
                autoLoginResponse.groups.lastOrNull { group ->
                    group.type == GroupType.SMALL || group.type == GroupType.MEDIUM
                }?.let {
                    PreferenceManager.saveGroupId(it.id)
                } ?: run {
                    PreferenceManager.saveGroupId(-1)
                }
                FirebaseCrashlytics.getInstance().log("name : ${autoLoginResponse.name}")
                PreferenceManager.saveUserId(autoLoginResponse.id)
                PreferenceManager.saveName(autoLoginResponse.name)
                FirebaseCrashlytics.getInstance().setUserId("${autoLoginResponse.id}-${PreferenceManager.getAccessToken() ?: "empty"}")
                FirebaseAnalytics.getInstance(this@SplashActivity).setUserId("${autoLoginResponse.id}")
                FirebaseAnalytics.getInstance(this@SplashActivity).setUserProperty("group_type", autoLoginResponse.getGroupTypeForGA())
                if (autoLoginResponse.characterId == null)
                    PreferenceManager.saveCharacter("1")
                else PreferenceManager.saveCharacter("${autoLoginResponse.characterId}")
                moveToHomeActivity(autoLoginResponse.toUserProfileResponse())
            }

            override fun onBlackUser(autoLoginResponse: AutoLoginResponse) {
                BlackUser(this@SplashActivity).showBlackUserMessageAlert(autoLoginResponse.toUserProfileResponse())
                moveToSignInActivity()
            }

            override fun onFailed(reason: String) {
                FirebaseCrashlytics.getInstance().log("유저 프로필 불러오기 실패 : $reason")
                FirebaseCrashlytics.getInstance().recordException(InvalidKeyException())
                DebugLog.e(UserProfileException("유저 프로필 불러오기 실패: $reason"))
            }

        })
    }

//    private fun getUserProfile() {
//        UserRepository.getMyProfile(object : UserDataSource.GetMyProfileCallback {
//            override fun onSuccess(userProfileResponse: UserProfileResponse) {
//                userProfileResponse.megaOrganization?.let { organization ->
//                    PreferenceManager.saveOrganization(organization.id?:-1L)
//                    organization.name?.let { PreferenceManager.saveOrganizationName(it) }
//                    userProfileResponse.megaDepartment?.let { department ->
//                        department.name?.let { PreferenceManager.saveDepartmentName(it) }
//                    }
//                }
//                userProfileResponse.groups.lastOrNull { group ->
//                    group.type == GroupType.SMALL || group.type == GroupType.MEDIUM
//                }?.let {
//                    PreferenceManager.saveGroupId(it.id)
//                } ?: run {
//                    PreferenceManager.saveGroupId(-1)
//                }
//                FirebaseCrashlytics.getInstance().log("name : ${userProfileResponse.name}")
//                PreferenceManager.saveUserId(userProfileResponse.id)
//                PreferenceManager.saveName(userProfileResponse.name)
//                FirebaseCrashlytics.getInstance().setUserId("${userProfileResponse.id}-${PreferenceManager.getAccessToken() ?: "empty"}")
//                FirebaseAnalytics.getInstance(this@SplashActivity).setUserId("${userProfileResponse.id}")
//                FirebaseAnalytics.getInstance(this@SplashActivity).setUserProperty("group_type", userProfileResponse.getGroupTypeForGA())
//                if (userProfileResponse.characterId == null)
//                    PreferenceManager.saveCharacter("1")
//                else PreferenceManager.saveCharacter("${userProfileResponse.characterId}")
//                moveToHomeActivity(userProfileResponse)
//            }
//
//            override fun onBlackUser(userProfileResponse: UserProfileResponse) {
//                BlackUser(this@SplashActivity).showBlackUserMessageAlert(userProfileResponse)
//                moveToSignInActivity()
//            }
//
//            override fun onFailed(reason: String) {
//                FirebaseCrashlytics.getInstance().log("유저 프로필 불러오기 실패 : $reason")
//                FirebaseCrashlytics.getInstance().recordException(InvalidKeyException())
//                DebugLog.e(UserProfileException("유저 프로필 불러오기 실패: $reason"))
//            }
//
//        })
//    }

    private fun showPromotionDialog(id: String?) {
        AlertDialog.Builder(this)
            .setMessage("Receive promotion code: $id")
            .setPositiveButton("Confirm", null)
            .create().show()
    }

    private fun getHashKey() {
        /*var packageInfo: PackageInfo? = null
        try {
            packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        if (packageInfo == null) Log.e("KeyHash", "KeyHash:null")
        for (signature in packageInfo!!.signatures) {
            try {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT))
            } catch (e: NoSuchAlgorithmException) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=$signature", e)
            }
        }*/

        val keyHash = Utility.getKeyHash(this)
        Log.d("KeyHash : ", "[ $keyHash ]")
    }
}