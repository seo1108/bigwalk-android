package kr.co.bigwalk.app.community.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.BaseResponse
import kr.co.bigwalk.app.data.RemoteApiManager
import kr.co.bigwalk.app.data.Result
import kr.co.bigwalk.app.data.community.dto.create.CrewConcernResponse
import kr.co.bigwalk.app.data.community.dto.create.CrewConcernTagResponse
import kr.co.bigwalk.app.data.community.repository.CommunityRepository
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.Event
import kr.co.bigwalk.app.util.showToast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectInterestViewModel: ViewModel() {
    private val _crewConcernList = MutableLiveData<List<CrewConcernResponse>>()
    val crewConcernList: LiveData<List<CrewConcernResponse>> get() = _crewConcernList

    private val _saveSuccess = MutableLiveData<Event<List<CrewConcernTagResponse>>>()
    val saveSuccess: LiveData<Event<List<CrewConcernTagResponse>>> get() = _saveSuccess

    private val selectConcernList = mutableListOf<CrewConcernTagResponse>()

    fun fetchCrewConcern(selectTagList: List<CrewConcernTagResponse>) {
        selectConcernList.addAll(selectTagList)
        CommunityRepository.getCrewConcern(
            groupId = null,
            successCallback = {
                val list = mutableListOf<CrewConcernResponse>()
                it?.forEach { crewConcernResponse ->
                    list.add(
                        CrewConcernResponse(
                            characteristic = crewConcernResponse.characteristic,
                            tags = crewConcernResponse.tags.map { tagResponse ->
                                tagResponse.conversionOfSelectionToTag(selectTagList)
                            }
                        )
                    )
                }
                _crewConcernList.value = list

                DebugLog.d(it.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    fun fetchCrewConcern2(selectTagList: List<CrewConcernTagResponse>) {
        selectConcernList.addAll(selectTagList)
        RemoteApiManager.getUserApi().getUserConcern(null)
            .enqueue(object : Callback<BaseResponse<List<CrewConcernResponse>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<CrewConcernResponse>>>,
                    response: Response<BaseResponse<List<CrewConcernResponse>>>
                ) {
                    when (response.body()?.result) {
                        Result.SUCCESS -> {
                            response.body()?.data?.let {
                                val list = mutableListOf<CrewConcernResponse>()
                                it.forEach { crewConcernResponse ->
                                    list.add(
                                        CrewConcernResponse(
                                            characteristic = crewConcernResponse.characteristic,
                                            tags = crewConcernResponse.tags.map { tagResponse ->
                                                tagResponse.conversionOfSelectionToTag(selectTagList)
                                            }
                                        )
                                    )
                                }
                                _crewConcernList.value = list
                            }
                        }
                        Result.FAIL -> {
                            response.body()?.message?.let {
                                showToast(it)
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<CrewConcernResponse>>>, t: Throwable) {
                    showToast(t.localizedMessage)
                }

            })
    }
    fun addSelectConcern(response: CrewConcernTagResponse) {
        selectConcernList.add(response)
    }

    fun removeSelectConcern(removeId: Int) {
        selectConcernList.remove(selectConcernList.find {
            it.id == removeId
        })
    }

    fun validationCheck() {
        if (selectConcernList.isNotEmpty()){
            _saveSuccess.value = Event(selectConcernList)
        } else {
            showToast("관심사를 알려주세요.")
        }
    }

//    fun setSelectedConcernList(list: List<CrewConcernTagResponse>) {
//        selectConcernList.clear()
//        selectedConcernList.addAll(list)
//    }
}