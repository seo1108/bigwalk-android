package kr.co.bigwalk.app.util

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor

class MainThreadExecutor : Executor {

    override fun execute(command: Runnable?) {
        if (command != null) {
            Handler(Looper.getMainLooper()).post(command)
        }
    }

}