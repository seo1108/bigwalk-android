package kr.co.bigwalk.app.data.blame.dto

import okhttp3.MultipartBody

data class BlameFeedCommentRequest(
    val blameId: Long,
    val blameMessage: String
) {
    fun toMultipartBody(): List<MultipartBody.Part> {
        return MultipartBody.Builder().run {
            if (blameId > -1) addFormDataPart("blameId", blameId.toString())
            if (blameMessage.isNotEmpty()) addFormDataPart("blameMessage", blameMessage)
            build().parts
        }
    }
}
