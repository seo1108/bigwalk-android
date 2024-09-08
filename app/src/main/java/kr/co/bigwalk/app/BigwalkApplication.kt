package kr.co.bigwalk.app

import android.app.Activity
import android.app.Application
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
import com.facebook.stetho.Stetho
//import com.kakao.auth.KakaoSDK
import com.kakao.sdk.common.KakaoSdk


class BigwalkApplication : Application(), LifecycleObserver {

    var progressDialog: AppCompatDialog? = null

    init {
        context = this
    }

    companion object {
        var isForeground = false
        var context: BigwalkApplication? = null
            private set

    }

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)

        KakaoSdk.init(this, getString(R.string.kakao_app_key))
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true)
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
        }

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)

    }

    //TODO base activity 만들고 current activity를 계속 등록해두면 intercepter에서 사용할 수 있도록 가능할 듯, 용
    // - 추가 애니메이션은 직접 코드로 그리기보다는 애니메이션 이미지 화면 전체로 두고 사이즈 조절해서 적용
    fun showLoadingAnimation(activity: Activity?) {
        if (activity == null || activity.isFinishing) return
        if (progressDialog == null || !progressDialog?.isShowing!!) {
            progressDialog = AppCompatDialog(activity)
            progressDialog!!.setCancelable(true)
            progressDialog!!.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
            progressDialog!!.setContentView(R.layout.animation_dialog)
            progressDialog!!.show()
        }
    }

    fun dismissLoadingAnimation() {
        if (progressDialog != null && progressDialog!!.isShowing) progressDialog!!.dismiss()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() { isForeground = false }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() { isForeground = true}
}