package kr.co.bigwalk.app.notification

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.iid.FirebaseInstanceId
import kr.co.bigwalk.app.data.user.dto.SaveTokenRequest
import kr.co.bigwalk.app.data.user.repository.UserDataSource
import kr.co.bigwalk.app.data.user.repository.UserRepository
import kr.co.bigwalk.app.exception.NotificationException
import kr.co.bigwalk.app.exception.UserProfileException
import kr.co.bigwalk.app.util.DebugLog

object BigwalkFcmServiceManager {

    fun retrieveCurrentToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    DebugLog.e(NotificationException(task.exception?.localizedMessage))
                    return@OnCompleteListener
                }

                val token = task.result?.token
                token?.let { sendRegistrationToServer(it) }
            })
    }

    fun sendRegistrationToServer(token: String) {
        UserRepository.sendPushToken(SaveTokenRequest(token), object : UserDataSource.SendPushTokenCallback {
            override fun onSuccess() {
                DebugLog.d("토큰 전송 성공: $token")
            }

            override fun onFailed(reason: String) {
                DebugLog.e(UserProfileException(reason))
            }

        })

    }
}