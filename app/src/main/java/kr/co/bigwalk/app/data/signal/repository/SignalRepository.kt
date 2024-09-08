package kr.co.bigwalk.app.data.signal.repository

import kr.co.bigwalk.app.data.signal.SignalResponse

object SignalRepository {
    fun getSignal(
        successCallback: (SignalResponse?) -> Unit
    ) {
        return SignalRemoteDataSource.getSignal(successCallback)
    }
}