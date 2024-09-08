package kr.co.bigwalk.app.walk.manage

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class WalkWorkManage(context: Context, params: WorkerParameters) : Worker(context, params) {
    private val sensorContext = context

    override fun doWork(): Result {
        receiveIntent?.let { ContextCompat.startForegroundService(sensorContext, it) }
        return Result.success()
    }
    companion object {
        var receiveIntent: Intent? = null
    }
}