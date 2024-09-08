package kr.co.bigwalk.app.data.funding.dto

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

data class RegisterChallengeOfSupportersRequest(
    val title: String,
    val content: String,
    val mainImage: File? = null,
    val firstHowToImage: File? = null,
    val secondHowToImage: File? = null,
    val firstHowToDescription: String,
    val secondHowToDescription: String,
    val firstInvalidImage: File? = null,
    val secondInvalidImage: File? = null,
    val firstInvalidDescription: String,
    val secondInvalidDescription: String
) {
    fun toMultipartBody(): List<MultipartBody.Part> {
        return MultipartBody.Builder().run {
            if (title.isNotEmpty()) addFormDataPart("title", title)
            if (content.isNotEmpty()) addFormDataPart("content", content)
            if (firstHowToDescription.isNotEmpty()) addFormDataPart("firstHowToDescription", firstHowToDescription)
            if (secondHowToDescription.isNotEmpty()) addFormDataPart("secondHowToDescription", secondHowToDescription)
            if (firstInvalidDescription.isNotEmpty()) addFormDataPart("firstInvalidDescription", firstInvalidDescription)
            if (secondInvalidDescription.isNotEmpty()) addFormDataPart("secondInvalidDescription", secondInvalidDescription)
            if (mainImage != null) {
                addFormDataPart("mainImage", mainImage.name, mainImage.asRequestBody(MultipartBody.FORM))
            }
            if (firstHowToImage != null) {
                addFormDataPart("firstHowToImage", firstHowToImage.name, firstHowToImage.asRequestBody(MultipartBody.FORM))
            }
            if (secondHowToImage != null) {
                addFormDataPart("secondHowToImage", secondHowToImage.name, secondHowToImage.asRequestBody(MultipartBody.FORM))
            }
            if (firstInvalidImage != null) {
                addFormDataPart("firstInvalidImage", firstInvalidImage.name, firstInvalidImage.asRequestBody(MultipartBody.FORM))
            }
            if (secondInvalidImage != null) {
                addFormDataPart("secondInvalidImage", secondInvalidImage.name, secondInvalidImage.asRequestBody(MultipartBody.FORM))
            }
            build().parts
        }
    }
}

data class ModifyChallengeOfSupportersRequest(
    val title: String,
    val content: String,
    val mainImage: File? = null,
    val firstHowToImage: File? = null,
    val secondHowToImage: File? = null,
    val firstHowToDescription: String,
    val secondHowToDescription: String,
    val firstInvalidImage: File? = null,
    val secondInvalidImage: File? = null,
    val firstInvalidDescription: String,
    val secondInvalidDescription: String
) {
    fun toMultipartBody(): List<MultipartBody.Part> {
        return MultipartBody.Builder().run {
            if (title.isNotEmpty()) addFormDataPart("title", title)
            if (content.isNotEmpty()) addFormDataPart("content", content)
            if (firstHowToDescription.isNotEmpty()) addFormDataPart("firstHowToDescription", firstHowToDescription)
            if (secondHowToDescription.isNotEmpty()) addFormDataPart("secondHowToDescription", secondHowToDescription)
            if (firstInvalidDescription.isNotEmpty()) addFormDataPart("firstInvalidDescription", firstInvalidDescription)
            if (secondInvalidDescription.isNotEmpty()) addFormDataPart("secondInvalidDescription", secondInvalidDescription)
            if (mainImage != null) {
                addFormDataPart("mainImage", mainImage.name, mainImage.asRequestBody(MultipartBody.FORM))
            }
            if (firstHowToImage != null) {
                addFormDataPart("firstHowToImage", firstHowToImage.name, firstHowToImage.asRequestBody(MultipartBody.FORM))
            }
            if (secondHowToImage != null) {
                addFormDataPart("secondHowToImage", secondHowToImage.name, secondHowToImage.asRequestBody(MultipartBody.FORM))
            }
            if (firstInvalidImage != null) {
                addFormDataPart("firstInvalidImage", firstInvalidImage.name, firstInvalidImage.asRequestBody(MultipartBody.FORM))
            }
            if (secondInvalidImage != null) {
                addFormDataPart("secondInvalidImage", secondInvalidImage.name, secondInvalidImage.asRequestBody(MultipartBody.FORM))
            }
            build().parts
        }
    }
}