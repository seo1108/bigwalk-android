package kr.co.bigwalk.app.dialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.signal.repository.SignalRemoteDataSource
import kr.co.bigwalk.app.data.signal.repository.SignalRepository
import kr.co.bigwalk.app.util.DebugLog

class SignalViewModel : ViewModel() {

    val imageSignal = MutableLiveData<Pair<String, String>>()
    val textSignal = MutableLiveData<String>()

    fun requestSignal() {
        SignalRepository.getSignal(
            successCallback = { response ->
                response ?: return@getSignal
                val signal = response!!

                if (signal.isShownShareButton)
                    imageSignal.value = Pair(signal.message, signal.imagePath)
                else
                    textSignal.value = signal.message

                DebugLog.d("signal - ${signal.message}, ${signal.imagePath}, ${signal.isShownShareButton}")
            }
        )
    }
}