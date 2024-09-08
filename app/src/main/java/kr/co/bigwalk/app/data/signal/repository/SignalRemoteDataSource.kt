package kr.co.bigwalk.app.data.signal.repository

import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.signal.SignalResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object SignalRemoteDataSource {
    fun getSignal(
        successCallback: (SignalResponse?) -> Unit
    ) {
        return RemoteApiManager.getService().getSignal()
            .enqueue(object : Callback<SignalResponse> {
                override fun onResponse(call: Call<SignalResponse>, response: Response<SignalResponse>) {
                    when (response.code()) {
                        200 -> successCallback(response.body())
                        else -> {}
                    }
                }

                override fun onFailure(call: Call<SignalResponse>, t: Throwable) {
                }
            })
    }
}