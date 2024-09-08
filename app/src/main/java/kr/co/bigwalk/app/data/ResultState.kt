package kr.co.bigwalk.app.data

data class ResultState(val result: Boolean, val message: String? = null)
data class ResultCodeState(val result: Boolean, val code: Int = CodeType.SUCCESS_CODE.code, val message: String? = null)
data class ResultDataState(val result: Boolean, val message: String? = null, val data: Any? = null)