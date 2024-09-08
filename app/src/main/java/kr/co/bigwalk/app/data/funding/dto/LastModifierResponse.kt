package kr.co.bigwalk.app.data.funding.dto

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.*

data class LastModifierResponse(
    @SerializedName("lastModifiedBy")
    val lastModifiedBy: String,
    @SerializedName("lastModifiedDate")
    val lastModifiedDate: Date
) {
    fun getLastModifiedDateToString(): String {
        return SimpleDateFormat("yy년MM월dd일").format(lastModifiedDate)
    }
}
