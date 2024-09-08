package kr.co.bigwalk.app.data.community

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

data class ModifyCrewRequest(
    val name: String,
    val description: String,
    val mainImage: File? = null
) {
    fun toMultipartBody(): List<MultipartBody.Part> {
        return MultipartBody.Builder().run {
            if (name.isNotEmpty()) addFormDataPart("name", name)
            if (description.isNotEmpty()) addFormDataPart("description", description)
            if (mainImage != null) {
                addFormDataPart(
                    "mainImage",
                    mainImage.name,
                    mainImage.asRequestBody(MultipartBody.FORM)
                )
            }
            build().parts
        }
    }
}