package kr.co.bigwalk.app.data.funding.dto

import com.google.gson.annotations.SerializedName
import kr.co.bigwalk.app.data.funding.LabelSignUpMethod

data class QuestionForCrewCampaignResponse(
    @SerializedName("question")
    val question: String,
    @SerializedName("signUpMethod")
    val signUpMethod: LabelSignUpMethod
)