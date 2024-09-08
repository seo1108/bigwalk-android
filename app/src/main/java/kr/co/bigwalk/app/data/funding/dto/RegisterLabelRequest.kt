package kr.co.bigwalk.app.data.funding.dto

import kr.co.bigwalk.app.data.funding.LabelSignUpMethod
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

data class RegisterLabelRequest(
    val title: String,
    val competitionId: Long,
    val sequence: Int,
    val categoryId: Int?,
    val campaignImage: File? = null,
    val subTitle: String,
    val description: String,
    val labelRecruitmentMethod: LabelSignUpMethod? = null,
    val question: String,
    val firstContentTitle: String,
    val firstContentDescription: String,
    val firstContentImage: File? = null,
    val secondContentTitle: String,
    val secondContentDescription: String,
    val secondContentImage: File? = null,
    val thirdContentTitle: String,
    val thirdContentDescription: String,
    val thirdContentImage: File? = null
) {
    fun toMultipartBody(): List<MultipartBody.Part> {
        return MultipartBody.Builder().run {
            val competitionId = this@RegisterLabelRequest.competitionId.toString()
            val sequence = this@RegisterLabelRequest.sequence.toString()
            if (title.isNotEmpty()) addFormDataPart("title", title)
            if (competitionId.isNotEmpty()) addFormDataPart("competitionId", competitionId)
            if (sequence.isNotEmpty()) addFormDataPart("sequence", sequence)
            if (categoryId != null) addFormDataPart("categoryId", categoryId.toString())
            if (subTitle.isNotEmpty()) addFormDataPart("subTitle", subTitle)
            if (description.isNotEmpty()) addFormDataPart("description", description)
            if (question.isNotEmpty()) addFormDataPart("question", question)
            if (firstContentTitle.isNotEmpty()) addFormDataPart("firstContentTitle", firstContentTitle)
            if (firstContentDescription.isNotEmpty()) addFormDataPart("firstContentDescription", firstContentDescription)
            if (secondContentTitle.isNotEmpty()) addFormDataPart("secondContentTitle", secondContentTitle)
            if (secondContentDescription.isNotEmpty()) addFormDataPart("secondContentDescription", secondContentDescription)
            if (thirdContentTitle.isNotEmpty()) addFormDataPart("thirdContentTitle", thirdContentTitle)
            if (thirdContentDescription.isNotEmpty()) addFormDataPart("thirdContentDescription", thirdContentDescription)
            if (description.isNotEmpty()) addFormDataPart("description", description)
            if (labelRecruitmentMethod != null) addFormDataPart("crewCampaignSignUpMethod", labelRecruitmentMethod.name)
            if (campaignImage != null) {
                addFormDataPart("campaignImage", campaignImage.name, campaignImage.asRequestBody(MultipartBody.FORM))
            }
            if (firstContentImage != null) {
                addFormDataPart("firstContentImage", firstContentImage.name, firstContentImage.asRequestBody(MultipartBody.FORM))
            }
            if (secondContentImage != null) {
                addFormDataPart("secondContentImage", secondContentImage.name, secondContentImage.asRequestBody(MultipartBody.FORM))
            }
            if (thirdContentImage != null) {
                addFormDataPart("thirdContentImage", thirdContentImage.name, thirdContentImage.asRequestBody(MultipartBody.FORM))
            }
            build().parts
        }
    }
}