package kr.co.bigwalk.app.sign_in

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.api.AuthenticatePhoneNumberRequest
import kr.co.bigwalk.app.data.api.VerificationCodeRequest
import kr.co.bigwalk.app.util.Event
import kr.co.bigwalk.app.util.PHONE_PATTERN
import kr.co.bigwalk.app.util.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.regex.Pattern

class SelfCertificationViewModel: ViewModel() {
    private val _dialogMsg = MutableLiveData<Event<String>>()
    val dialogMsg: LiveData<Event<String>> get() = _dialogMsg

    private val _actionDialogMsg = MutableLiveData<Event<String>>()
    val actionDialogMsg: LiveData<Event<String>> get() = _actionDialogMsg

    val phoneNumber = MutableLiveData<String>()
    val certificationCode = MutableLiveData<String>()
    val isCertifyFocus = MutableLiveData<Boolean>(false)
    val isSamePhonePattern = MutableLiveData<Boolean>(true)

    fun sendCertificationCode() {
        isSamePhonePattern.value = Pattern.matches(PHONE_PATTERN, phoneNumber.value.orEmpty())
        if (isSamePhonePattern.value == false) return

        RemoteApiManager.getUserApi().requestVerificationCode(
            VerificationCodeRequest(phoneNumber.value.orEmpty())
        ).enqueue(object : Callback<BaseResponse<Nothing>> {
            override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                when(response.body()?.result) {
                    Result.SUCCESS -> {
                        isCertifyFocus.value = true
                        _dialogMsg.value = Event("인증번호가 전송되었습니다")
                    }
                    Result.FAIL -> {
                        response.body()?.message?.let {
                            _dialogMsg.value = Event(it)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                showToast(t.localizedMessage)
            }
        })
    }

    fun certifyMobileNumber(authenticationNumber: String) {
        RemoteApiManager.getUserApi().authenticatePhoneNumber(
            AuthenticatePhoneNumberRequest(
                phoneNumber = phoneNumber.value.orEmpty(),
                authenticationNumber = authenticationNumber
            )
        ).enqueue(object : Callback<BaseResponse<Nothing>> {
            override fun onResponse(call: Call<BaseResponse<Nothing>>, response: Response<BaseResponse<Nothing>>) {
                when(response.body()?.result) {
                    Result.SUCCESS -> {
                        _actionDialogMsg.value = Event("인증이 완료되었습니다")
                    }
                    Result.FAIL -> {
                        response.body()?.message?.let {
                            _dialogMsg.value = Event(it)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<BaseResponse<Nothing>>, t: Throwable) {
                showToast(t.localizedMessage)
            }
        })
    }
}