package kr.co.bigwalk.app.community.create

import android.net.Uri
import androidx.lifecycle.*
import kr.co.bigwalk.app.data.community.dto.create.CrewConcernTagResponse
import kr.co.bigwalk.app.data.community.dto.create.RegisterCrewRequest
import kr.co.bigwalk.app.data.community.repository.CommunityRepository
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.Event
import kr.co.bigwalk.app.util.showToast
import java.io.File

class CreateCommunityViewModel : ViewModel() {

    private val _registerSuccessEvent = MutableLiveData<Event<Long>>()
    val registerSuccessEvent: LiveData<Event<Long>> get() = _registerSuccessEvent

    private val _screenPosition = MutableLiveData<Int>()
    val screenPosition: LiveData<Int> get() = _screenPosition

    private val _createProgressValue = MutableLiveData<Int>()
    val createProgressValue: LiveData<Int> get() = _createProgressValue

    private val _isTitleDuplicate = MutableLiveData<Boolean>()
    val isTitleDuplicate: LiveData<Boolean> get() = _isTitleDuplicate

    val firstCrewImage = MutableLiveData<Pair<Uri, File>>()
    val firstCrewTitle = MutableLiveData<String>()
    val firstCrewSubTitle = MutableLiveData<String>()
    val secondCrewJoinUs = MutableLiveData<String>()
    val secondCrewAddress1 = MutableLiveData<Pair<String, String>>()
    val secondCrewAddress: LiveData<String> = Transformations.map(secondCrewAddress1) {
        if (it.second.isNotEmpty()) it.first + " " + it.second else ""
    }
    val secondCrewConcernList = MutableLiveData<List<CrewConcernTagResponse>>()

    val nextBtnEnable = MediatorLiveData<Boolean>()

    init {
        nextBtnEnable.addSource(screenPosition) { validationCheck() }
        nextBtnEnable.addSource(firstCrewImage) { validationCheck() }
        nextBtnEnable.addSource(firstCrewTitle) { validationCheck() }
        nextBtnEnable.addSource(firstCrewSubTitle) { validationCheck() }
        nextBtnEnable.addSource(isTitleDuplicate) { validationCheck() }
        nextBtnEnable.addSource(secondCrewJoinUs) { validationCheck() }
        nextBtnEnable.addSource(secondCrewAddress) { validationCheck() }
        nextBtnEnable.addSource(secondCrewConcernList) { validationCheck() }
    }

    private fun validationCheck() {
        when (screenPosition.value) {
            0 -> {
                nextBtnEnable.value =
                    isTitleDuplicate.value == false
                        && firstCrewImage.value != null
                        && firstCrewTitle.value.isNullOrEmpty().not()
                        && firstCrewSubTitle.value.isNullOrEmpty().not()
                        && firstCrewTitle.value?.length!! >= 5
                        && firstCrewSubTitle.value?.length!! >= 20
            }
            1 -> {
                nextBtnEnable.value =
                    !secondCrewJoinUs.value.isNullOrEmpty()
                        && !secondCrewAddress.value.isNullOrEmpty()
                        && !secondCrewConcernList.value.isNullOrEmpty()
            }
            2 -> {
                nextBtnEnable.value = true
            }
        }
    }

    fun duplicateCheckForCrewTitle() {
        CommunityRepository.duplicateCheckForCrewTitle(
            crewTitle = firstCrewTitle.value.orEmpty(),
            successCallback = {
                _isTitleDuplicate.value = it?.isDuplicate
                DebugLog.d(it.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }


    fun setFunctionAndViewForScreen(position: Int) {
        _screenPosition.value = position
        when (position) {
            0 -> {
                _createProgressValue.value = 111
            }
            1 -> {
                _createProgressValue.value = 222
            }
            2 -> {
                _createProgressValue.value = 333
            }
        }
    }

    fun setJoinUs(category: String) {
        secondCrewJoinUs.value = category
    }

    fun setCrewAddress(firstAddress: String, secondAddress: String) {
        secondCrewAddress1.value = Pair(firstAddress, secondAddress)
    }

    fun setCrewConcern(concernList: List<CrewConcernTagResponse>) {
        secondCrewConcernList.value = concernList
    }

    fun registerCrew() {
        CommunityRepository.registerCrew(
            request = RegisterCrewRequest(
                groupImage = firstCrewImage.value?.second,
                groupName = firstCrewTitle.value.orEmpty(),
                groupDescription = firstCrewSubTitle.value.orEmpty(),
                groupCategoryId = secondCrewJoinUs.value.orEmpty(),
                groupTagIds = secondCrewConcernList.value?.map { it.id.toLong() }.orEmpty(),
                firstDepthRegion = secondCrewAddress1.value?.first.orEmpty(),
                secondDepthRegion = secondCrewAddress1.value?.second.orEmpty()
            ),
            successCallback = {
                it.first?.let { response -> _registerSuccessEvent.value = Event(response.crewId) }
                showToast("크루 만들기가 완료되었습니다.")
                DebugLog.d(it.toString())
            }, failCallback = {
                DebugLog.d(it)
                showToast(it)
            }
        )
    }
}