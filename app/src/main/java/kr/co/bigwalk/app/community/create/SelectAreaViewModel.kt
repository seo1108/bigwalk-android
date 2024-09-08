package kr.co.bigwalk.app.community.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.community.dto.create.CrewAddressResponse
import kr.co.bigwalk.app.data.community.repository.CommunityRepository
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.Event
import kr.co.bigwalk.app.util.showToast

class SelectAreaViewModel: ViewModel() {
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
        CommunityRepository.getCrewAddress(
            successCallback = {
                if (it != null) {
                     address2DepthList.value = it
                    _crewFirstAddressResponse.value = it.map { address -> address.keys.first() }
                }

                DebugLog.d(it.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
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