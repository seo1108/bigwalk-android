package kr.co.bigwalk.app.data

import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import kr.co.bigwalk.app.util.DebugLog


data class ErrorMessage(val jsonString: String) {

    companion object {
        @JvmStatic
        fun of(json: String): ErrorMessage {
            return try {
                GsonBuilder().create().fromJson(json, ErrorMessage::class.java)
            } catch (exception: JsonSyntaxException) {
                DebugLog.e(exception)
                ErrorMessage("예상치 못한 에러가 발생했습니다")
            }
        }
    }
}