package kr.co.bigwalk.app.lock_screen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kr.co.bigwalk.app.BigwalkApplication
import org.apache.commons.lang3.StringUtils

class LockScreenReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
//        if (!StringUtils.isBlank(intent?.action)) {
//            if (intent?.action == Intent.ACTION_SCREEN_OFF) { //Receiver에서 구분해야 어플리케이션 재부팅 없이 설정을 적용 할 수 있다.
//                val lockIntent = Intent(BigwalkApplication.context, LockScreenActivity::class.java)
//                lockIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
//                lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                context?.startActivity(lockIntent)
//            }
//        }
    }
}