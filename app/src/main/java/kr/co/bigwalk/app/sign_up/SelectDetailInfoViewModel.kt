package kr.co.bigwalk.app.sign_up

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kr.co.bigwalk.app.data.community.dto.create.CrewConcernTagResponse

class SelectDetailInfoViewModel: ViewModel() {

    private val area = MutableLiveData<Pair<String, String>>()
    val selectArea: LiveData<String> = Transformations.map(area) {
        if (it.second.isNotEmpty()) it.first + " " + it.second else ""
    }
    val selectConcernList = MutableLiveData<List<CrewConcernTagResponse>>()

    fun setArea(area: Pair<String, String>) {
        this.area.value = area
    }

    fun setConcern(concernList: List<CrewConcernTagResponse>) {
        selectConcernList.value = concernList
    }
}