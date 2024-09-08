package kr.co.bigwalk.app.data

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kr.co.bigwalk.app.BigwalkApplication
import kr.co.bigwalk.app.data.feed.dto.Feed
import kr.co.bigwalk.app.data.mission.WelcomeMissionStatus
import kr.co.bigwalk.app.data.user.dto.UserProfileResponse
import kr.co.bigwalk.app.extension.getDisplayEndDate
import kr.co.bigwalk.app.util.DeepLinkData
import kr.co.bigwalk.app.util.DeepLinkType
import kr.co.bigwalk.app.util.SafetyDeepLinkType
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max

/***
 * 지금 이 구조는 말도 안되는 구조
 * 걸음 수를 Preference에 저장을 한다?
 * 그냥 걸음 수 유실하겠다는 의미
 * 왜냐하면 Preference는 저장용량의 한계가 있는데
 * 이걸 왜 내부 데이터베이스에 저장을 할 생각을 안했을까..
 * 당장은 Preference를 분리해서 해결을 하고 있지만
 * 나중에 DB로 마이그레이션 진행을 해야한다.
 * 추가적으로 Preference에는 작은 데이터만 저장해야함 ex) 알림 설정 등
 */
object PreferenceManager {

    private val preference: SharedPreferences = BigwalkApplication.context?.getSharedPreferences("preference", Context.MODE_PRIVATE)!!

    private val preferenceHaveSeen: SharedPreferences =
        BigwalkApplication.context?.getSharedPreferences("preference_have_seen", Context.MODE_PRIVATE)!!

    private val preferenceMissions: SharedPreferences =
        BigwalkApplication.context?.getSharedPreferences("preference_missions", Context.MODE_PRIVATE)!!

    private val preferenceNotification: SharedPreferences =
        BigwalkApplication.context?.getSharedPreferences("preference_notification", Context.MODE_PRIVATE)!!

    private val preferenceDeepLink: SharedPreferences =
        BigwalkApplication.context?.getSharedPreferences("preference_deep_link", Context.MODE_PRIVATE)!!

    private enum class Key {
        LAST_ALERT_DATE,// 마지막으로 업데이트 알러트 띄워준 날짜
        ACCESS_TOKEN,
        DAILY_STEP,// 하루 단위로 갱신되는 걸음 수를 저장
        TIMELY_STEP,// 한시간 단위로 갱신되는 걸음 수를 저장
        RECENT_STEP,// 센서로 들어오는 raw한 걸음 수를 저장
        RECENT_STEP_TIME, // 센서로 들어오는 raw한 걸음 수를 저장한 시간
        CURRENT_DAY,//최근 저장 날짜 (지금이랑 다를 경우, 오늘 걸음수 초기화), 1~31
        CURRENT_HOUR, //최근 저장 시간 (지금이랑 다를 경우, 지난 시간 걸음 수 저장), 0~23
        DONABLE_STEP,// 기부가능한 걸음 수를 저장
        ORGANIZATION, // 유저 기업 정보
        ORGANIZATION_NAME, // 유저 기업 이름
        DEPARTMENT_NAME, // 유저 부서 이름
        CHARACTER, // 유저 캐릭터
        NAME,
        NOTIFICATION,
        MISSION, // 캠페인 id 별로 true, false 저장
        LAST_UPLOAD_MISSION_TITLE,
        LAST_UPLOAD_MISSION_END_DATE,
        LAST_UPLOAD_MISSION_CAMPAIGN_ID,
        LAST_UPLOAD_MISSION_ORGANIZATION_ID,
        USER_ID,
        PROFILE_PATH,
        NOTIFICATION_COUNT,
        RANKING_,
        CAMPAIGN_100,
        GROUP,
        GROUP_RANK,
        STICKER_ID_LIST,
        SELECTED_CHALLENGE_ID,
        SELECTED_IMAGE_ID,
        SELECTED_PAGE,
        SELECTED_SIZE,
        SELECTED_POSITION,
        FEED_ADAPTER_POSITION,
        FEED_ID,
        FEED_LIKE_COUNT,
        FEED_IS_LIKE,
        FEED_COMMENT_COUNT,
        FEED_COMMENT,
        FEED_IS_DELETE,
        FEED_ADDED,
        FEED_ADDED_ID,
        FEED_TOTAL_PAGE,
        FEED_TOTAL_COUNT
        ;
        enum class DeepLink { //preferenceDeepLink
            DEEP_LINK_TYPE,
            DEEP_LINK_GROUP_ID,
            DEEP_LINK_CAMPAIGN_ID,
            DEEP_LINK_CREW_CAMPAIGN_ID,
            DEEP_LINK_DATA
        }

        enum class Notification { //preferenceNotification
            CAMPAIGN,
            STORY,
            RANKING,
            WALK,
            MARKETING
        }

        enum class Missions { //preferenceMissions
            WELCOME_MISSION1,
            WELCOME_MISSION1_MAX,
            WELCOME_MISSION1_COMPLETED,
            WELCOME_MISSION1_CLEAR_CONFIRMED,
            WELCOME_MISSION2,
            WELCOME_MISSION2_MAX,
            WELCOME_MISSION2_COMPLETED,
            WELCOME_MISSION2_CLEAR_CONFIRMED,
            WELCOME_MISSION_STATUS
        }

        enum class CrewCampaign { //preferenceHaveSeen
            CREW_CAMPAIGN,
            TOOLTIP,
            GUIDE_TOOLTIP,
            CROWD_FUNDING_GUIDE,
            STEP_FUNDING_GUIDE
        }

        enum class HaveSeen { //preferenceHaveSeen
            TUTORIAL, // 튜토리얼
            RANKING_TUTORIAL,
            NOTICE
        }
    }

    private const val MAXIMUM_DONABLE_STEP = 10000

    private val dateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.KOREA)

    fun saveLastAlertDate(date: Date?) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putString(Key.LAST_ALERT_DATE.name, dateFormat.format(date))
        editor.apply()
    }

    fun getLastAlertDate(): Date? {
        return if (preference.getString(Key.LAST_ALERT_DATE.name, null) != null) {
            dateFormat.parse(preference.getString(Key.LAST_ALERT_DATE.name, null))
        } else {
            null
        }

    }

    fun saveAccessToken(accessToken: String?) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putString(Key.ACCESS_TOKEN.name, accessToken)
        editor.apply()
        FirebaseCrashlytics.getInstance().setCustomKey("saveAccessToken()-accessToken", accessToken ?: "empty")
    }

    fun getAccessToken(): String? {
        return preference.getString(Key.ACCESS_TOKEN.name, null)
    }

    fun saveRecentStep(step: Int) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putInt(Key.RECENT_STEP.name, step)
        editor.apply()
    }

    fun getRecentStep(): Int {
        return preference.getInt(Key.RECENT_STEP.name, 0)
    }

    fun saveRecentStepTime(timestamp: Long) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putLong(Key.RECENT_STEP_TIME.name, timestamp)
        editor.apply()
    }

    fun getRecentStepTime(): Long {
        return preference.getLong(Key.RECENT_STEP_TIME.name, 0)
    }


    fun saveDailyStep(step: Int) {
        if (step < 0)
            return
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putInt(Key.DAILY_STEP.name, step)
        editor.apply()
    }

    fun getDailyStep(): Int {
        return preference.getInt(Key.DAILY_STEP.name, 0)
    }

    fun getKcalorie(): Int{
        return preference.getInt(Key.DAILY_STEP.name, 0) / 28
    }

    fun getDonableStepPercentage() :Int{
        return preference.getInt(Key.DONABLE_STEP.name, 0) * 100 / MAXIMUM_DONABLE_STEP
    }

    fun saveTimelyStep(step: Int) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putInt(Key.TIMELY_STEP.name, step)
        editor.apply()
    }

    fun getTimelyStep(): Int {
        return preference.getInt(Key.TIMELY_STEP.name, -1)
    }

    fun saveDonableStep(step: Int) {
        val donableStep = if (step > MAXIMUM_DONABLE_STEP) MAXIMUM_DONABLE_STEP else step
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putInt(Key.DONABLE_STEP.name, max(donableStep, 0))
        editor.apply()
    }

    fun getDonableStep(): Int {
        return preference.getInt(Key.DONABLE_STEP.name, 0)
    }

    fun saveCurrentDay(dayOfMonth: Int){
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putInt(Key.CURRENT_DAY.name, dayOfMonth)
        editor.apply()
    }

    fun getCurrentDay() : Int {
        return preference.getInt(Key.CURRENT_DAY.name, -1)
    }

    fun saveCurrentHour(hour: Int){
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putInt(Key.CURRENT_HOUR.name, hour)
        editor.apply()
    }

    fun getCurrentHour() : Int{
        return preference.getInt(Key.CURRENT_HOUR.name, 0)
    }

    fun saveCampaignPushSetting(boolean: Boolean){
        val editor: SharedPreferences.Editor = preferenceNotification.edit()
        editor.putBoolean(Key.Notification.CAMPAIGN.name, boolean)
        editor.apply()
    }

    fun getCampaignPushSetting(): Boolean{
        return preferenceNotification.getBoolean(Key.Notification.CAMPAIGN.name,true)
    }

    fun saveStoryPushSetting(boolean: Boolean){
        val editor: SharedPreferences.Editor = preferenceNotification.edit()
        editor.putBoolean(Key.Notification.STORY.name, boolean)
        editor.apply()
    }

    fun getStoryPushSetting(): Boolean{
        return preferenceNotification.getBoolean(Key.Notification.STORY.name,true)
    }

    fun saveRankingPushSetting(boolean: Boolean){
        val editor: SharedPreferences.Editor = preferenceNotification.edit()
        editor.putBoolean(Key.Notification.RANKING.name, boolean)
        editor.apply()
    }

    fun getRankingPushSetting(): Boolean{
        return preferenceNotification.getBoolean(Key.Notification.RANKING.name,true)
    }

    fun saveWalkPushSetting(boolean: Boolean){
        val editor: SharedPreferences.Editor = preferenceNotification.edit()
        editor.putBoolean(Key.Notification.WALK.name, boolean)
        editor.apply()
    }

    fun getWalkPushSetting(): Boolean{
        return preferenceNotification.getBoolean(Key.Notification.WALK.name,true)
    }

    fun saveMarketingPushSetting(boolean: Boolean){
        val editor: SharedPreferences.Editor = preferenceNotification.edit()
        editor.putBoolean(Key.Notification.MARKETING.name, boolean)
        editor.apply()
    }

    fun getMarketingPushSetting(): Boolean{
        return preferenceNotification.getBoolean(Key.Notification.MARKETING.name,true)
    }

    fun saveOrganization(id: Long) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putLong(Key.ORGANIZATION.name, id)
        editor.apply()
    }

    fun getOrganization(): Long{
        return preference.getLong(Key.ORGANIZATION.name, -1L)
    }

    fun saveGroupId(id: Long) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putLong(Key.GROUP.name, id)
        editor.apply()
    }

    fun getGroupId(): Long{
        return preference.getLong(Key.GROUP.name, -1L)
    }

    fun saveGroupRank(rank: Long, groupId: Long) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putLong(Key.GROUP_RANK.name + "$groupId", rank)
        editor.apply()
    }

    fun getGroupRank(groupId: Long): Long{
        return preference.getLong(Key.GROUP_RANK.name + "$groupId", -1L)
    }

    fun saveOrganizationName(organizationName: String?) {
        if (organizationName.isNullOrEmpty()) return
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putString(Key.ORGANIZATION_NAME.name, organizationName)
        editor.apply()
    }

    fun getOrganizationName(): String {
        return preference.getString(Key.ORGANIZATION_NAME.name, "").orEmpty()
    }

    fun clearOrganization() {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.apply {
            putLong(Key.ORGANIZATION.name, -1L)
            putString(Key.ORGANIZATION_NAME.name, "")
        }.apply()
    }

    fun saveDepartmentName(departmentName: String) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putString(Key.DEPARTMENT_NAME.name, departmentName)
        editor.apply()
    }

    fun getDepartmentName(): String {
        return preference.getString(Key.DEPARTMENT_NAME.name, "").orEmpty()
    }

    fun clearDepartmentName() {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putString(Key.DEPARTMENT_NAME.name, "")
        editor.apply()
    }

    fun saveCharacter(id: String) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putString(Key.CHARACTER.name, id)
        editor.apply()
    }

    fun getCharacter(): String {// 기본 캐릭터값은 bigker = 1
        return when {
            preference.getString(Key.CHARACTER.name, "1").isNullOrBlank() -> {
                "1"
            }
            preference.getString(Key.CHARACTER.name, "1")!!.contains("http") -> {
                "1"
            }
            else -> {
                preference.getString(Key.CHARACTER.name, "1")!!
            }
        }
    }

    fun saveTutorial(boolean: Boolean) {
        val editor: SharedPreferences.Editor = preferenceHaveSeen.edit()
        editor.putBoolean(Key.HaveSeen.TUTORIAL.name, boolean)
        editor.apply()
    }

    fun haveSeenTutorial(): Boolean {
        return preferenceHaveSeen.getBoolean(Key.HaveSeen.TUTORIAL.name, false)
    }

    fun saveUserId(userId: Long) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putLong(Key.USER_ID.name, userId)
        editor.apply()
    }

    fun getUserId(): Long{
        return preference.getLong(Key.USER_ID.name, -1)
    }

    fun saveName(name: String) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putString(Key.NAME.name, name)
        editor.apply()
    }

    fun getName(): String{
        return preference.getString(Key.NAME.name, "").orEmpty()
    }

    fun saveDeepLink(type: DeepLinkType, campaignId: Long?, groupId: Long?, crewCampaignId: Long?, data: String? = null) {
        val editor: SharedPreferences.Editor = preferenceDeepLink.edit()
        editor.apply {
            putString(Key.DeepLink.DEEP_LINK_TYPE.name, type.value)
            putLong(Key.DeepLink.DEEP_LINK_GROUP_ID.name, groupId ?: DeepLinkData.ID_NULL)
            putLong(Key.DeepLink.DEEP_LINK_CAMPAIGN_ID.name, campaignId ?: DeepLinkData.ID_NULL)
            putLong(Key.DeepLink.DEEP_LINK_CREW_CAMPAIGN_ID.name, crewCampaignId ?: DeepLinkData.ID_NULL)
            putString(Key.DeepLink.DEEP_LINK_DATA.name, data ?: "")
        }.apply()
    }

    fun clearDeepLink() {
        val editor: SharedPreferences.Editor = preferenceDeepLink.edit()
        editor.apply {
            putString(Key.DeepLink.DEEP_LINK_TYPE.name, "")
            putLong(Key.DeepLink.DEEP_LINK_GROUP_ID.name, DeepLinkData.ID_NULL)
            putLong(Key.DeepLink.DEEP_LINK_CAMPAIGN_ID.name, DeepLinkData.ID_NULL)
            putLong(Key.DeepLink.DEEP_LINK_CREW_CAMPAIGN_ID.name, DeepLinkData.ID_NULL)
            putString(Key.DeepLink.DEEP_LINK_DATA.name, "")
        }.apply()
    }

    fun getDeepLinkData(): DeepLinkData {
        return DeepLinkData(
            SafetyDeepLinkType.from(preferenceDeepLink.getString(Key.DeepLink.DEEP_LINK_TYPE.name, "").orEmpty()),
            preferenceDeepLink.getLong(Key.DeepLink.DEEP_LINK_CAMPAIGN_ID.name, DeepLinkData.ID_NULL),
            preferenceDeepLink.getLong(Key.DeepLink.DEEP_LINK_GROUP_ID.name, DeepLinkData.ID_NULL),
            preferenceDeepLink.getLong(Key.DeepLink.DEEP_LINK_CREW_CAMPAIGN_ID.name, DeepLinkData.ID_NULL),
            preferenceDeepLink.getString(Key.DeepLink.DEEP_LINK_DATA.name, "").orEmpty())
    }

    fun enableNotification() {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putBoolean(Key.NOTIFICATION.name, true)
        editor.apply()
    }

    fun disableNotification() {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putBoolean(Key.NOTIFICATION.name, false)
        editor.apply()
    }

    fun isNotification(): Boolean {
        return preference.getBoolean(Key.NOTIFICATION.name, false)
    }

    fun isNotShowingNotice(noticeId: Long): Boolean {
        return preferenceHaveSeen.getBoolean("${Key.HaveSeen.NOTICE.name}-$noticeId", false)
    }

    fun setNotShowingNotice(noticeId: Long) {
        val editor: SharedPreferences.Editor = preferenceHaveSeen.edit()
        editor.putBoolean("${Key.HaveSeen.NOTICE.name}-$noticeId", true)
        editor.apply()
    }

    fun hasSeenMission(campaignId: Long): Boolean {
        return preference.getBoolean("${Key.MISSION.name}-$campaignId", false)
    }

    /**
     * 기부하고 참여하기 bottomSheet
     * camaignId 비교해서 챌린지 view 노출/비화성화
     */
    fun sawMission(campaignId: Long) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putBoolean("${Key.MISSION.name}-$campaignId", true)
        editor.apply()
    }

    /**
     * 바로 걸음 기부하는 bottomSheet
     */
    fun sawCampaign(campaignId: Long) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putBoolean("${Key.MISSION.name}-$campaignId", false)
        editor.apply()
    }

    /**
     * 사진 업로드 성공후 해당 챌린지 제목, campaignId, organizationId 저장
     */
    fun saveLastMission(title: String, endDate: String, campaignId: Long, organizationId: Long) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putString(Key.LAST_UPLOAD_MISSION_TITLE.name, title)
        editor.putString(Key.LAST_UPLOAD_MISSION_END_DATE.name, endDate)
        editor.putLong(Key.LAST_UPLOAD_MISSION_CAMPAIGN_ID.name, campaignId)
        editor.putLong(Key.LAST_UPLOAD_MISSION_ORGANIZATION_ID.name, organizationId)
        editor.apply()
    }

    fun saveLastMissisonTitle(title: String?) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putString(Key.LAST_UPLOAD_MISSION_TITLE.name, title)
        editor.apply()
    }

    fun getLastMissisonTitle(): String {
        return preference.getString(Key.LAST_UPLOAD_MISSION_TITLE.name, "").orEmpty()
    }

    fun getLastMissionEndDate(): String {
        val endDate = preference.getString(Key.LAST_UPLOAD_MISSION_END_DATE.name, "").orEmpty()
        if (endDate.isNullOrEmpty()) return ""
        return getDisplayEndDate(endDate)
    }

    fun getLastMissionCampaignId(): Long {
        return preference.getLong(Key.LAST_UPLOAD_MISSION_CAMPAIGN_ID.name, -1)
    }

    fun getLastMissionOrganizationId(): Long {
        return preference.getLong(Key.LAST_UPLOAD_MISSION_ORGANIZATION_ID.name, -1)
    }

    fun saveProfilePath(path: String) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putString(Key.PROFILE_PATH.name, path)
        editor.apply()
    }

    fun getProfilePath(): String {
        return preference.getString(Key.PROFILE_PATH.name, "").orEmpty()
    }

    fun saveUserProfile(userProfileResponse: UserProfileResponse?) {
        userProfileResponse?.megaOrganization?.let { organization ->
            saveOrganization(organization.id?:-1L)
            organization.name.let { saveOrganizationName(it) }
        }
        userProfileResponse?.megaDepartment?.let { department ->
            saveDepartmentName(department.name?: "")
        }
        userProfileResponse?.name?.let {
            saveName(it)
        }
        userProfileResponse?.characterId?.let {
            saveCharacter("$it")
        }
        userProfileResponse?.profilePath?.let {
            saveProfilePath(it)
        }
    }

    fun increaseNotificationCount() {
        var count = preference.getInt(Key.NOTIFICATION_COUNT.name, 0)
        count+=1
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putInt(Key.NOTIFICATION_COUNT.name, count)
        editor.apply()
    }

    fun getNotificationCount(): Int {
        return preference.getInt(Key.NOTIFICATION_COUNT.name, 0)
    }

    fun resetNotificationCount() {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putInt(Key.NOTIFICATION_COUNT.name, 0)
        editor.apply()
    }

    fun saveRankingTutorial(boolean: Boolean) {
        val editor: SharedPreferences.Editor = preferenceHaveSeen.edit()
        editor.putBoolean(Key.HaveSeen.RANKING_TUTORIAL.name, boolean)
        editor.apply()
    }

    fun haveSeenRankingTutorial(): Boolean {
        return preferenceHaveSeen.getBoolean(Key.HaveSeen.RANKING_TUTORIAL.name, false)
    }

    fun saveCrewCampaignGuide(boolean: Boolean) {
        val editor: SharedPreferences.Editor = preferenceHaveSeen.edit()
        editor.putBoolean(Key.CrewCampaign.CREW_CAMPAIGN.name, boolean)
        editor.apply()
    }

    fun haveSeenCrewCampaignGuide(): Boolean {
        return preferenceHaveSeen.getBoolean(Key.CrewCampaign.CREW_CAMPAIGN.name,false)
    }

    fun saveCrewCampaignTooltip(boolean: Boolean, groupId: Long) {
        val editor: SharedPreferences.Editor = preferenceHaveSeen.edit()
        editor.putBoolean(Key.CrewCampaign.TOOLTIP.name + "$groupId", boolean)
        editor.apply()
    }

    fun haveSeenCrewCampaignTooltip(groupId: Long): Boolean {
        return preferenceHaveSeen.getBoolean(Key.CrewCampaign.TOOLTIP.name + "$groupId",false)
    }

    fun saveCrewCampaignGuideTooltip(boolean: Boolean, groupId: Long) {
        val editor: SharedPreferences.Editor = preferenceHaveSeen.edit()
        editor.putBoolean(Key.CrewCampaign.GUIDE_TOOLTIP.name + "$groupId", boolean)
        editor.apply()
    }

    fun haveSeenCrewCampaignGuideTooltip(groupId: Long): Boolean {
        return preferenceHaveSeen.getBoolean(Key.CrewCampaign.GUIDE_TOOLTIP.name + "$groupId",false)
    }

    fun saveCrowdFundingGuide(boolean: Boolean) {
        val editor: SharedPreferences.Editor = preferenceHaveSeen.edit()
        editor.putBoolean(Key.CrewCampaign.CROWD_FUNDING_GUIDE.name, boolean)
        editor.apply()
    }

    fun haveSeenCrowdFundingGuide(): Boolean {
        return preferenceHaveSeen.getBoolean(Key.CrewCampaign.CROWD_FUNDING_GUIDE.name,false)
    }

    fun saveStepFundingGuide(boolean: Boolean) {
        val editor: SharedPreferences.Editor = preferenceHaveSeen.edit()
        editor.putBoolean(Key.CrewCampaign.STEP_FUNDING_GUIDE.name, boolean)
        editor.apply()
    }

    fun haveSeenStepFundingGuide(): Boolean {
        return preferenceHaveSeen.getBoolean(Key.CrewCampaign.STEP_FUNDING_GUIDE.name,false)
    }

    fun getRankNumber(key: String): String? {
        return preference.getString(Key.RANKING_.name + key, null)
    }

    fun saveRankNumber(key: String, rankNumber: String) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putString(Key.RANKING_.name + key, rankNumber)
        editor.apply()
    }

    fun removeRankNumber(key: String) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.remove(Key.RANKING_.name + key)
        editor.apply()
    }

    fun hasSeenCampaign100(campaignId: String): Boolean {
        val set = preference.getStringSet(Key.CAMPAIGN_100.name, mutableSetOf())
        return set!!.contains(campaignId)
    }

    fun saveCampaign100(campaignId: String) {
        val set = preference.getStringSet(Key.CAMPAIGN_100.name, mutableSetOf())
        set!!.add(campaignId)

        val editor: SharedPreferences.Editor = preference.edit()
        editor.putStringSet(Key.CAMPAIGN_100.name, set)
        editor.apply()
    }

    fun getWelcomeMission1(): Int {
        return preferenceMissions.getInt(Key.Missions.WELCOME_MISSION1.name, -1)
    }

    fun saveWelcomeMission1(step: Int) {        // 웰컴 미션1의 현재 값을 저장합니다.
        val editor: SharedPreferences.Editor = preferenceMissions.edit()
        editor.putInt(Key.Missions.WELCOME_MISSION1.name, step)
        editor.apply()
    }

    fun getWelcomeMission2(): Int {
        return preferenceMissions.getInt(Key.Missions.WELCOME_MISSION2.name, -1)
    }

    fun saveWelcomeMission2(count: Int) {        // 웰컴 미션2의 현재 값을 저장합니다.
        val editor: SharedPreferences.Editor = preferenceMissions.edit()
        editor.putInt(Key.Missions.WELCOME_MISSION2.name, count)
        editor.apply()
    }

    fun getWelcomeMission1Max(): Int {
        return preferenceMissions.getInt(Key.Missions.WELCOME_MISSION1_MAX.name, 0)
    }

    fun saveWelcomeMission1Max(max: Int) {        // 웰컴 미션1의 최대 값을 저장합니다.
        val editor: SharedPreferences.Editor = preferenceMissions.edit()
        editor.putInt(Key.Missions.WELCOME_MISSION1_MAX.name, max)
        editor.apply()
    }

    fun getWelcomeMission2Max(): Int {
        return preferenceMissions.getInt(Key.Missions.WELCOME_MISSION2_MAX.name, 0)
    }

    fun saveWelcomeMission2Max(max: Int) {        // 웰컴 미션1의 최대 값을 저장합니다.
        val editor: SharedPreferences.Editor = preferenceMissions.edit()
        editor.putInt(Key.Missions.WELCOME_MISSION2_MAX.name, max)
        editor.apply()
    }

    fun getWelcomeMission1Completed(): Boolean {
        return preferenceMissions.getBoolean(Key.Missions.WELCOME_MISSION1_COMPLETED.name, false)
    }

    fun saveWelcomeMission1Completed(clear: Boolean) {        // 웰컴 미션1의 성공을 저장합니다.
        val editor: SharedPreferences.Editor = preferenceMissions.edit()
        editor.putBoolean(Key.Missions.WELCOME_MISSION1_COMPLETED.name, clear)
        editor.apply()
    }

    fun getWelcomeMission2Completed(): Boolean {
        return preferenceMissions.getBoolean(Key.Missions.WELCOME_MISSION2_COMPLETED.name, false)
    }

    fun saveWelcomeMission2Completed(clear: Boolean) {        // 웰컴 미션2의 성공을 저장합니다.
        val editor: SharedPreferences.Editor = preferenceMissions.edit()
        editor.putBoolean(Key.Missions.WELCOME_MISSION2_COMPLETED.name, clear)
        editor.apply()
    }

    fun getWelcomeMission1ClearConfirmed(): Boolean {
        return preferenceMissions.getBoolean(Key.Missions.WELCOME_MISSION1_CLEAR_CONFIRMED.name, false)
    }

    fun saveWelcomeMission1ClearConfirmed(clear: Boolean) {        // 웰컴 미션1의 성공 확인 여부를 저장합니다.
        val editor: SharedPreferences.Editor = preferenceMissions.edit()
        editor.putBoolean(Key.Missions.WELCOME_MISSION1_CLEAR_CONFIRMED.name, clear)
        editor.apply()
    }

    fun getWelcomeMission2ClearConfirmed(): Boolean {
        return preferenceMissions.getBoolean(Key.Missions.WELCOME_MISSION2_CLEAR_CONFIRMED.name, false)
    }

    fun saveWelcomeMission2ClearConfirmed(clear: Boolean) {        // 웰컴 미션2의 성공 확인 여부를 저장합니다.
        val editor: SharedPreferences.Editor = preferenceMissions.edit()
        editor.putBoolean(Key.Missions.WELCOME_MISSION2_CLEAR_CONFIRMED.name, clear)
        editor.apply()
    }

    fun saveWelcomeMissionStatus(status: String) {        // 웰컴 미션의 상태 값을 저장
        val editor: SharedPreferences.Editor = preferenceMissions.edit()
        editor.putString(Key.Missions.WELCOME_MISSION_STATUS.name, status)
        editor.apply()
        if (status == WelcomeMissionStatus.NONE.type)
            initWelcomeMission()
    }

    fun getWelcomeMissionStatus(): String {
        return preferenceMissions.getString(Key.Missions.WELCOME_MISSION_STATUS.name, "none") ?: "none"
    }

    private fun initWelcomeMission() {
        saveWelcomeMission1(0)
        saveWelcomeMission2(0)
        saveWelcomeMission1Max(0)
        saveWelcomeMission2Max(0)
        saveWelcomeMission1Completed(false)
        saveWelcomeMission2Completed(false)
        saveWelcomeMission1ClearConfirmed(false)
        saveWelcomeMission2ClearConfirmed(false)
    }

    fun saveStickerIds(list: ArrayList<Int>, groupId: Long) {
        val arr: ArrayList<Int> = ArrayList()
        val jsonArr = JSONArray()
        list.forEachIndexed { index, id ->
            arr.add(index, id)
        }
        arr.forEach {
            jsonArr.put(it)
        }
        val result = jsonArr.toString()

        val editor: SharedPreferences.Editor = preference.edit()
        editor.putString(Key.STICKER_ID_LIST.name + "$groupId", result)
        editor.apply()

    }

    fun getStickerIds(groupId: Long): ArrayList<Int> {
        val shared = preference.getString(Key.STICKER_ID_LIST.name + "$groupId", "").orEmpty()
        val resultArr: ArrayList<Int> = ArrayList()
        if (shared.isNotEmpty()) {
            val arrJson = JSONArray(shared)
            for (i in 0 until arrJson.length()) {
                resultArr.add(arrJson.optInt(i))
            }
        }
        return resultArr
    }

    fun saveSelectedFeedInfo(challengeId: Long, imageId: Long, page: Int, size: Int, pos: Int) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putLong(Key.SELECTED_CHALLENGE_ID.name, challengeId)
        editor.putLong(Key.SELECTED_IMAGE_ID.name, imageId)
        editor.putInt(Key.SELECTED_PAGE.name, page)
        editor.putInt(Key.SELECTED_SIZE.name, size)
        editor.putInt(Key.SELECTED_POSITION.name, pos)
        editor.apply()
    }

    fun getSelectedChallengeId() : Long {
        return preference.getLong(Key.SELECTED_CHALLENGE_ID.name, -1)
    }

    fun getSelectedImageId() : Long {
        return preference.getLong(Key.SELECTED_IMAGE_ID.name, -1)
    }

    fun getSelectedPage() : Int {
        return preference.getInt(Key.SELECTED_PAGE.name, -1)
    }

    fun getSelectedSize() : Int {
        return preference.getInt(Key.SELECTED_SIZE.name, -1)
    }

    fun getSelectedPosition() : Int {
        return preference.getInt(Key.SELECTED_POSITION.name, -1)
    }

    fun saveFeedInfo(id: Long, likeCount: Long, isLike: Boolean, commentCount: Long, comment: String?, isDelete: Boolean) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putLong(Key.FEED_ID.name, id)
        editor.putLong(Key.FEED_LIKE_COUNT.name, likeCount)
        editor.putBoolean(Key.FEED_IS_LIKE.name, isLike)
        editor.putLong(Key.FEED_COMMENT_COUNT.name, commentCount)
        editor.putString(Key.FEED_COMMENT.name, comment)
        editor.putBoolean(Key.FEED_IS_DELETE.name, isDelete)
        editor.apply()
    }



    fun saveFeedComment(id: Long, comment: String) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putLong(Key.FEED_ID.name, id)
        editor.putString(Key.FEED_COMMENT.name, comment)
        editor.apply()
    }

    fun saveFeedCommentCount(id: Long, commentCount: Long) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putLong(Key.FEED_ID.name, id)
        editor.putLong(Key.FEED_COMMENT_COUNT.name, commentCount)
        editor.apply()
    }

    fun saveFeedTotalPage(count: Int) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putInt(Key.FEED_TOTAL_PAGE.name, count)
        editor.apply()
    }

    fun saveFeedTotalCount(count: Int) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putInt(Key.FEED_TOTAL_COUNT.name, count)
        editor.apply()
    }

    fun saveFeedUpload(added: Boolean, id: Long) {
        val editor: SharedPreferences.Editor = preference.edit()
        editor.putBoolean(Key.FEED_ADDED.name, added)
        editor.putLong(Key.FEED_ADDED_ID.name, id)
        editor.apply()
    }

    fun getFeedTotalPage() : Int {
        return preference.getInt(Key.FEED_TOTAL_PAGE.name, 0)
    }

    fun getFeedTotalCount() : Int {
        return preference.getInt(Key.FEED_TOTAL_COUNT.name, 0)
    }

    fun getFeedId() : Long {
        return preference.getLong(Key.FEED_ID.name, -1L)
    }

    /*fun getFeedAdapterPosition() : Int {
        return preference.getInt(Key.FEED_ADAPTER_POSITION.name, -1)
    }*/

    fun getFeedLikeCount() : Long {
        return preference.getLong(Key.FEED_LIKE_COUNT.name, -1L)
    }

    fun getFeedIsLike() : Boolean {
        return preference.getBoolean(Key.FEED_IS_LIKE.name, false)
    }

    fun getFeedCommentCount() : Long {
        return preference.getLong(Key.FEED_COMMENT_COUNT.name, -1L)
    }

    fun getFeedComment() : String? {
        return preference.getString(Key.FEED_COMMENT.name, "")
    }

    fun getFeedIsDelete() : Boolean {
        return preference.getBoolean(Key.FEED_IS_DELETE.name, false)
    }

    fun isFeedUpload() : Boolean {
        return preference.getBoolean(Key.FEED_ADDED.name, false)
    }

    fun addedFeedId() : Long {
        return preference.getLong(Key.FEED_ADDED_ID.name, -1L)
    }

}