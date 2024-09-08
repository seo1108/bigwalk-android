package kr.co.bigwalk.app.profile.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.community.dto.create.CrewAddressResponse
import kr.co.bigwalk.app.util.Event
import kr.co.bigwalk.app.util.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileSelectAreaViewModel: ViewModel() {
    private val _crewFirstAddressResponse = MutableLiveData<List<String>>()
    val crewFirstAddressResponse: LiveData<List<String>> get() = _crewFirstAddressResponse
    private val _crewSecondAddressResponse = MutableLiveData<List<String>>()
    val crewSecondAddressResponse: LiveData<List<String>> get() = _crewSecondAddressResponse

    private val _saveSuccess = MutableLiveData<Event<Pair<String,String>>>()
    val saveSuccess: LiveData<Event<Pair<String,String>>> get() = _saveSuccess

    private var address2DepthList = MutableLiveData<CrewAddressResponse>()
    private var address1Depth = ""
    var address2Depth = ""


    init {
        fetchCrewAddress()
    }

    private fun fetchCrewAddress() {
        RemoteApiManager.getUserApi().getUserAddress(null)
            .enqueue(object : Callback<BaseResponse<CrewAddressResponse>> {
                override fun onResponse(call: Call<BaseResponse<CrewAddressResponse>>, response: Response<BaseResponse<CrewAddressResponse>>) {
                    when(response.body()?.result) {
                        Result.SUCCESS -> {
                            response.body()?.data?.let {
                                address2DepthList.value = it
                                _crewFirstAddressResponse.value = it.map { address -> address.keys.first() }
                            }
                        }
                        Result.FAIL -> {
                            response.body()?.message?.let {
                                showToast(it)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<CrewAddressResponse>>, t: Throwable) {
                    showToast(t.localizedMessage)
                }

            })
    }

    fun validationCheck() {
        if (address1Depth.isNotEmpty() && address2Depth.isNotEmpty()){
            _saveSuccess.value = Event(Pair(address1Depth, address2Depth))
        } else {
            showToast("주 활동지역을 알려주세요.")
        }
    }

    fun filterAddress(key: String) {
        address1Depth = key
        address2Depth = ""
        address2DepthList.value?.map {
            if (it.containsKey(key)) {
                _crewSecondAddressResponse.value = it[key]
                return@map
            }
        }
    }
}