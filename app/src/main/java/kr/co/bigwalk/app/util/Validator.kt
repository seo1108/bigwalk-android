package kr.co.bigwalk.app.util

fun nicknameValidator(nickname: String?) : Boolean {
    if (nickname.isNullOrBlank()) return false
    return nickname.length in 2..10
}

const val PHONE_PATTERN = "^01([0|1|6|7|8|9])-?([0-9]{4})-?([0-9]{4})$"
const val BIRTH_PATTERN = "^(19[0-9][0-9]|20\\d{2})(0[0-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])\$"