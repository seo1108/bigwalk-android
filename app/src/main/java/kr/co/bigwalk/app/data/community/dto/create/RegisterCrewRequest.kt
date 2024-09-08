package kr.co.bigwalk.app.data.community.dto.create

import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

data class RegisterCrewRequest(
    val groupImage: File? = null,
    val groupName: String,
    val groupDescription: String,
    val groupTagIds: List<Long>,
    val groupCategoryId: String,
    val firstDepthRegion: String,
    val secondDepthRegion: String
) {
    fun toMultipartBody(): List<MultipartBody.Part> {
        return MultipartBody.Builder().run {
            if (groupName.isNotEmpty()) addFormDataPart("groupName", groupName)
            if (groupDescription.isNotEmpty()) addFormDataPart("groupDescription", groupDescription)
            if (groupCategoryId.isNotEmpty()) addFormDataPart("groupCategoryId", groupCategoryId)
            if (firstDepthRegion.isNotEmpty()) addFormDataPart("firstDepthRegion", firstDepthRegion)
            if (secondDepthRegion.isNotEmpty()) addFormDataPart("secondDepthRegion", secondDepthRegion)
            if (groupImage != null) {
                addFormDataPart("groupImage", groupImage.name, groupImage.asRequestBody(MultipartBody.FORM))
            }
            if (groupTagIds.isNotEmpty()) {
                groupTagIds.forEach { addFormDataPart("groupTagIds", it.toString()) }
            }


            build().parts
        }
    }
}