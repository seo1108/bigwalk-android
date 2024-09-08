@file:Suppress("ConstantConditionIf")

package kr.co.bigwalk.app.util

import android.util.Log
import kr.co.bigwalk.app.BuildConfig

object DebugLog {
    private const val TAG = "BIGWALK_LOG"

    private const val isDebugMode: Boolean = "product" != BuildConfig.FLAVOR

    fun e(e: Exception) {
        if (isDebugMode) Log.e(
            TAG,
            buildLogMsg(e.message)
        )
        
    }

    /**
     * Log Level Warning + Crashlytics.log
     */
    fun w(message: String?) {
        if (isDebugMode) Log.w(
            TAG,
            buildLogMsg(message)
        )
    }

    /**
     * Log Level Information + Crashlytics.log
     */
    fun i(message: String?) {
        if (isDebugMode) Log.i(
            TAG,
            buildLogMsg(message)
        )
    }

    /**
     * Log Level Debug
     */
    fun d(message: String?) {
        if (isDebugMode) Log.d(
            TAG,
            buildLogMsg(message)
        )
    }

    /**
     * Crashlytics set Bool
     */
    fun setBool(key: String, value: Boolean) {
        if (isDebugMode) Log.d(
            TAG,
            buildLogMsg("$key = $value")
        )
    }

    /**
     * Crashlytics set String
     */
    fun setString(key: String, value: String) {
        if (isDebugMode) Log.d(
            TAG,
            buildLogMsg("$key = $value")
        )
    }

    /**
     * Crashlytics set Int
     */
    fun setInt(key: String, value: Int) {
        if (isDebugMode) Log.d(
            TAG,
            buildLogMsg("$key = $value")
        )
    }

    /**
     * Log Level Verbose
     */
    fun v(message: String?) {
        if (isDebugMode) Log.v(
            TAG,
            buildLogMsg(message)
        )
    }

    private fun buildLogMsg(message: String?): String {
        val ste =
            Thread.currentThread().stackTrace[4]
        val sb = StringBuilder()
        sb.append("[")
        sb.append(ste.fileName.replace(".kt", ""))
        sb.append("::")
        sb.append(ste.methodName)
        sb.append("]")
        sb.append(message)
        return sb.toString()
    }

}