package kr.co.bigwalk.app.data

enum class CodeType(val code: Int) {
    SUCCESS_CODE(1),
    INVALID_AUTHORITY_GROUP_IS_FULL(205),
    INVALID_AUTHORITY_ALREADY_JOINED_GROUP(206),
    ETC_ERROR_CODE(-1);

    companion object {
        fun fromInt(code: Int) = CodeType.values().first { it.code == code }
    }
}