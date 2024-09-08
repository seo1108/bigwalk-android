package kr.co.bigwalk.app.community.funding

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.funding.dto.ContestResponse
import kr.co.bigwalk.app.data.funding.repository.FundingRepository
import kr.co.bigwalk.app.util.DebugLog
import java.util.*

class ContestViewModel : ViewModel() {

    private val _evenContestList = MutableLiveData<List<ContestResponse>>()
    val evenContestList: LiveData<List<ContestResponse>> get() = _evenContestList

    private val _oddContestList = MutableLiveData<List<ContestResponse>>()
    val oddContestList: LiveData<List<ContestResponse>> get() = _oddContestList

    init {
        getContestList()
    }

    private fun getDefaultList(): List<ContestResponse> {
        return listOf(
            ContestResponse(
                id = -1L,
                title = null,
                mainImagePath = null,
                startDate = Date(),
                endDate = Date(),
                fontColor = "",
                logoImagePath = "",
                mainColor = "#f2f2f2"
            ),
            ContestResponse(
                id = -1L,
                title = null,
                mainImagePath = null,
                startDate = Date(),
                endDate = Date(),
                fontColor = "",
                logoImagePath = "",
                mainColor = "#f2f2f2"
            ),
            ContestResponse(
                id = -1L,
                title = null,
                mainImagePath = null,
                startDate = Date(),
                endDate = Date(),
                fontColor = "",
                logoImagePath = "",
                mainColor = "#f2f2f2"
            ),
            ContestResponse(
                id = -1L,
                title = null,
                mainImagePath = null,
                startDate = Date(),
                endDate = Date(),
                fontColor = "",
                logoImagePath = "",
                mainColor = "#f2f2f2"
            )
        )
    }

    private fun getContestList() {
        FundingRepository.getContestList(
            successCallback = {
                if (it != null) {
                    positioningData(it.data)
                }
                DebugLog.d(it.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    private fun positioningData(data: List<ContestResponse>) {
        val list = mutableListOf<ContestResponse>()
        list.addAll(data)
        when(data.size) {
            0 -> { list.addAll(getDefaultList().subList(0, 4)) }
            1 -> { list.addAll(getDefaultList().subList(1, 4)) }
            2 -> { list.addAll(getDefaultList().subList(2, 4)) }
            3 -> { list.addAll(getDefaultList().subList(3, 4)) }
        }
        _evenContestList.value = list.filterIndexed { index, _ ->
            index % 2 == 0
        }
        _oddContestList.value = list.filterIndexed { index, _ ->
            index % 2 != 0
        }
    }
}