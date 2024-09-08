package kr.co.bigwalk.app.community.funding.create

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.analytics.FirebaseAnalytics
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.R
import kr.co.bigwalk.app.data.community.GroupMemberRole
import kr.co.bigwalk.app.data.community.repository.CommunityRepository
import kr.co.bigwalk.app.data.funding.LabelSignUpMethod
import kr.co.bigwalk.app.data.funding.RequiredToCreateIds
import kr.co.bigwalk.app.data.funding.dto.*
import kr.co.bigwalk.app.data.funding.repository.FundingRepository
import kr.co.bigwalk.app.util.DebugLog
import kr.co.bigwalk.app.util.Event

data class RegisterOrModifyMoveItem(
    val hasSequence: Boolean,
    val groupId: Long,
    val crewCampaignId: Long,
    val hasActionMission: Boolean
)

class CreateFundingViewModel(private val requiredToCreateIds: RequiredToCreateIds) : ViewModel() {

    private val _myRole = MutableLiveData<GroupMemberRole>()
    val myRole: LiveData<GroupMemberRole> get() = _myRole

    private val _toastMsg = MutableLiveData<Event<Int>>()
    val toastMsg: LiveData<Event<Int>> get() = _toastMsg

    private val _successEvent = MutableLiveData<Event<Unit>>()
    val successEvent: LiveData<Event<Unit>> get() = _successEvent

    private val _failureEvent = MutableLiveData<Event<String>>()
    val failureEvent: LiveData<Event<String>> get() = _failureEvent

    private val _successRegister = MutableLiveData<RegisterOrModifyMoveItem>()
    val successRegister: LiveData<RegisterOrModifyMoveItem> get() = _successRegister

    val questionContainerIsVisible = MutableLiveData<Boolean>()
    val title2CaptionText = MutableLiveData<String>()
    val detail1IsVisible = MutableLiveData<Boolean>(true)
    val detail2IsVisible = MutableLiveData<Boolean>(false)
    val detail3IsVisible = MutableLiveData<Boolean>(false)
    val guide1IsVisible = MutableLiveData<Boolean>(true)
    val guide2IsVisible = MutableLiveData<Boolean>(false)
    val questionIsVisible = MutableLiveData<Boolean>(false)

    private var labelSignUpMethodType: LabelSignUpMethod? = null
    var categoryId: Int? = null
    val previousCategoryId = MutableLiveData<Int>()
    var crewCampaignId: Long? = null

    /* 크루 캠페인 수정 모드 관련 변수 */
    private val _beforeData = MutableLiveData<LabelBeforeDataResponse>()
    val beforeData: LiveData<LabelBeforeDataResponse> get() = _beforeData

    val checkedJoinType = MutableLiveData<Int>()

    init {
        getMyRoleFromGroup()
    }

    private fun getMyRoleFromGroup() {
        CommunityRepository.getMyRoleFromGroup(
            groupId = requiredToCreateIds.groupId,
            successCallback = { response ->
                if (response != null) {
                    _myRole.value = response.myRole
                }
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    fun registerOrModifyCrewCampaign(request: RegisterLabelRequest) {
        if (validationInputForm(request).not()) return
        if (crewCampaignId == null) {
            registerLabel(request)
        } else {
            modifyCrewCampaign(request, crewCampaignId!!)
        }
    }

    fun registerOrModifyChallengeOfCrewCampaign(request: RegisterChallengeOfSupportersRequest) {
        if (validationChallengeInputForm(request).not()) return
        if (beforeData.value?.actionMission == null) {
            registerChallengeOfSupporters(request)
        } else {
            modifyChallengeOfSupporters(request)
        }
    }

    private fun validationInputForm(request: RegisterLabelRequest): Boolean {
        when {
            categoryId == null -> {
                BigwalkApplication.context?.let { FirebaseAnalytics.getInstance(it).logEvent("contest_exhibit_making_pop_up_view", Bundle()) }
                _toastMsg.value = Event(R.string.funding_require_complete_caption1)
                return false
            }
            labelSignUpMethodType == null
                || request.campaignImage == null
                || request.title.isEmpty()
                || request.subTitle.isEmpty()
                || request.description.isEmpty()
                || (labelSignUpMethodType == LabelSignUpMethod.APPROVE && questionIsVisible.value == true && request.question.isEmpty()) -> {
                BigwalkApplication.context?.let { FirebaseAnalytics.getInstance(it).logEvent("contest_exhibit_making_pop_up_view", Bundle()) }
                _toastMsg.value = Event(R.string.funding_require_complete_caption2)
                return false
            }
            request.title.length < 5
                || request.subTitle.length < 10
                || request.description.length < 100
                || (labelSignUpMethodType == LabelSignUpMethod.APPROVE && questionIsVisible.value == true && request.question.length < 10) -> {
                BigwalkApplication.context?.let { FirebaseAnalytics.getInstance(it).logEvent("contest_exhibit_making_pop_up_view", Bundle()) }
                _toastMsg.value = Event(R.string.funding_require_complete_caption2)
                return false
            }
            else -> return true
        }
    }

    private fun validationChallengeInputForm(request: RegisterChallengeOfSupportersRequest): Boolean {
        when {
            request.mainImage == null
                || request.title.length < 5
                || request.content.length < 20
                || request.firstHowToImage == null
                || request.secondHowToImage == null
                || request.firstHowToDescription.length < 5
                || request.secondHowToDescription.length < 5
                || request.firstInvalidImage == null
                || request.secondInvalidImage == null
                || request.firstInvalidDescription.length < 5
                || request.secondInvalidDescription.length < 5 -> {
                _toastMsg.value = Event(R.string.funding_require_complete_caption2)
                BigwalkApplication.context?.let { FirebaseAnalytics.getInstance(it).logEvent("contest_exhibit_making_chall_pop_up_view", Bundle()) }
                return false
            }
            else -> return true
        }
    }

    private fun registerLabel(request: RegisterLabelRequest) {
        FundingRepository.registerLabel(
            groupId = requiredToCreateIds.groupId,
            request = RegisterLabelRequest(
                title = request.title,
                competitionId = requiredToCreateIds.contestId,
                sequence = requiredToCreateIds.sequence,
                categoryId = categoryId,
                campaignImage = request.campaignImage,
                subTitle = request.subTitle,
                description = request.description,
                labelRecruitmentMethod = labelSignUpMethodType,
                question = request.question,
                firstContentTitle = request.firstContentTitle,
                firstContentDescription = request.firstContentDescription,
                firstContentImage = request.firstContentImage,
                secondContentTitle = request.secondContentTitle,
                secondContentDescription = request.secondContentDescription,
                secondContentImage = request.secondContentImage,
                thirdContentTitle = request.thirdContentTitle,
                thirdContentDescription = request.thirdContentDescription,
                thirdContentImage = request.thirdContentImage
            ),
            successCallback = {
                _successEvent.value = Event(Unit)
                crewCampaignId = it?.labelId
            }, failCallback = {
                DebugLog.d(it)
                _failureEvent.value = Event(it)
            })
    }

    private fun registerChallengeOfSupporters(request: RegisterChallengeOfSupportersRequest) {
        FundingRepository.registerChallengeOfSupporters(
            supportersCampaignId = crewCampaignId ?: -1L,
            request = RegisterChallengeOfSupportersRequest(
                title = request.title,
                content = request.content,
                mainImage = request.mainImage,
                firstHowToImage = request.firstHowToImage,
                secondHowToImage = request.secondHowToImage,
                firstHowToDescription = request.firstHowToDescription,
                secondHowToDescription = request.secondHowToDescription,
                firstInvalidImage = request.firstInvalidImage,
                secondInvalidImage = request.secondInvalidImage,
                firstInvalidDescription = request.firstInvalidDescription,
                secondInvalidDescription = request.secondInvalidDescription
            ),
            successCallback = {
                _successRegister.value =
                    RegisterOrModifyMoveItem(
                        requiredToCreateIds.sequence > 0,
                        requiredToCreateIds.groupId,
                        crewCampaignId ?: -1,
                        true
                    )
            }, failCallback = {
                DebugLog.d(it)
                _failureEvent.value = Event(it)
            })
    }


    fun setLabelSignUpMethod(approve: LabelSignUpMethod) {
        labelSignUpMethodType = approve
        questionContainerIsVisible.value = approve == LabelSignUpMethod.APPROVE
        when (approve) {
            LabelSignUpMethod.NOT_APPROVE -> {
                title2CaptionText.value = "가입 버튼을 통해 크루에 즉시 가입됩니다."
                checkedJoinType.value = R.id.not_approve_ratio
            }
            LabelSignUpMethod.APPROVE -> {
                title2CaptionText.value = "마스터가 승인 후 크루에 가입됩니다."
                checkedJoinType.value = R.id.approve_ratio
            }
            LabelSignUpMethod.PRIVATE -> {
                title2CaptionText.value = ""
                checkedJoinType.value = R.id.private_ratio
            }
        }
    }

    fun setQuestionEnable(isEnable: Boolean) {
        questionIsVisible.value = isEnable
    }

    fun setDetail1Visible() {
        detail1IsVisible.value = detail1IsVisible.value?.not()
    }

    fun setDetail2Visible() {
        detail2IsVisible.value = detail2IsVisible.value?.not()
    }

    fun setDetail3Visible() {
        detail3IsVisible.value = detail3IsVisible.value?.not()
    }

    fun setGuide1Visible() {
        guide1IsVisible.value = guide1IsVisible.value?.not()
    }

    fun setGuide2Visible() {
        guide2IsVisible.value = guide2IsVisible.value?.not()
    }

    /* 크루 캠페인 수정 모드 관련 함수 */
    fun requestBeforeData(crewCampaignId: Long) {
        if (crewCampaignId < 0) return
        this.crewCampaignId = crewCampaignId
        FundingRepository.getBeforeLabelData(
            crewCampaignId = crewCampaignId,
            successCallback = { response ->
                _beforeData.value = response
                if (response != null) {
                    setLabelSignUpMethod(response.crewCampaignSignUpMethod)
                    previousCategoryId.value = response.categoryId
                    detail2IsVisible.value = response.campaignContentResponses.size > 1
                    detail3IsVisible.value = response.campaignContentResponses.size > 2
                    guide2IsVisible.value = response.actionMission?.firstInvalidDescription?.isNotEmpty() ?: false
                }
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    fun requestBeforeData() {
        FundingRepository.getBeforeLabelData(
            crewCampaignId = crewCampaignId ?: -1L,
            successCallback = { response ->
                _beforeData.value = response
                if (response != null) {
                    setLabelSignUpMethod(response.crewCampaignSignUpMethod)
                    previousCategoryId.value = response.categoryId
                }
                DebugLog.d(response.toString())
            }, failCallback = {
                DebugLog.d(it)
            }
        )
    }

    private fun modifyCrewCampaign(request: RegisterLabelRequest, crewCampaignId: Long) {
        FundingRepository.modifySupportersCampaign(
            supportersCampaignId = crewCampaignId,
            crewId = requiredToCreateIds.groupId,
            request = ModifySupportersCampaignRequest(
                title = request.title,
                categoryId = categoryId,
                campaignImage = request.campaignImage,
                subTitle = request.subTitle,
                description = request.description,
                labelRecruitmentMethod = labelSignUpMethodType,
                question = request.question,
                firstContentTitle = request.firstContentTitle,
                firstContentDescription = request.firstContentDescription,
                firstContentImage = request.firstContentImage,
                secondContentTitle = request.secondContentTitle,
                secondContentDescription = request.secondContentDescription,
                secondContentImage = request.secondContentImage,
                thirdContentTitle = request.thirdContentTitle,
                thirdContentDescription = request.thirdContentDescription,
                thirdContentImage = request.thirdContentImage
            ),
            successCallback = {
                _successEvent.value = Event(Unit)
            }, failCallback = {
                DebugLog.d(it)
                _failureEvent.value = Event(it)
            })
    }

    private fun modifyChallengeOfSupporters(request: RegisterChallengeOfSupportersRequest) {
        FundingRepository.modifyChallengeOfSupporters(
            supportersCampaignId = crewCampaignId ?: -1L,
            request = ModifyChallengeOfSupportersRequest(
                title = request.title,
                content = request.content,
                mainImage = request.mainImage,
                firstHowToImage = request.firstHowToImage,
                secondHowToImage = request.secondHowToImage,
                firstHowToDescription = request.firstHowToDescription,
                secondHowToDescription = request.secondHowToDescription,
                firstInvalidImage = request.firstInvalidImage,
                secondInvalidImage = request.secondInvalidImage,
                firstInvalidDescription = request.firstInvalidDescription,
                secondInvalidDescription = request.secondInvalidDescription
            ),
            successCallback = {
                _successRegister.value =
                    RegisterOrModifyMoveItem(
                        requiredToCreateIds.sequence > 0,
                        requiredToCreateIds.groupId,
                        crewCampaignId ?: -1,
                        true
                    )
            }, failCallback = {
                DebugLog.d(it)
                _failureEvent.value = Event(it)
            })
    }

    fun moveToPreview() {
        if (beforeData.value?.actionMission == null) {
            _successRegister.value =
                RegisterOrModifyMoveItem(
                    requiredToCreateIds.sequence > 0,
                    requiredToCreateIds.groupId,
                    crewCampaignId ?: -1,
                    false
                )
        } else {
            FundingRepository.deleteChallengeOfSupporters(
                supportersCampaignId = crewCampaignId ?: -1,
                successCallback = {
                    _successRegister.value = RegisterOrModifyMoveItem(
                        requiredToCreateIds.sequence > 0,
                        requiredToCreateIds.groupId,
                        crewCampaignId ?: -1,
                        false
                    )
                }, failCallback = {
                    DebugLog.d(it)
                }
            )
        }
    }


}